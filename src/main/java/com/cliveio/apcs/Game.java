package com.cliveio.apcs;

import com.corundumstudio.socketio.listener.*;
import com.corundumstudio.socketio.*;
import java.util.*;

public class Game{ //can be treated as a GameEvent
                   //Need to synchronize on this object
  public static final int width = 25;
  public static final int height = 25;

  private int tick = 0;
  public int getTick(){return tick;}
  public void advanceTick(){tick++;}

  private String name;
  public String getName(){return name;}

  private Map<UUID, Snake> snakes = new HashMap<UUID, Snake>();

  private final Timer timer = new Timer();
  private final SocketIONamespace ns;
  private final SocketIOServer server;

  public Game(final SocketIOServer server, String gameName){
    this.server = server;
    this.ns = server.addNamespace("/"+gameName);
    this.name = gameName;

    ns.addEventListener("NewSnakeEvent", NewSnakeEvent.class, new DataListener<NewSnakeEvent>(){
      @Override
      public void onData(SocketIOClient client, NewSnakeEvent data, AckRequest ackrequest){
        data.setTick(tick);
        snakes.put(client.getSessionId(), new Snake(data));
        ackrequest.sendAckData(new FullUpdateEvent(Game.this, tick));
        ns.getBroadcastOperations().sendEvent("NewSnakeEvent", data);
      }
    });
    ns.addEventListener("TurnEvent", TurnEvent.class, new DataListener<TurnEvent>(){
      @Override
      public void onData(SocketIOClient client, TurnEvent data, AckRequest ackrequest){
        snakes.get(client.getSessionId()).turn(data);
        ns.getBroadcastOperations().sendEvent("TurnEvent", data);
      }
    });
    ns.addEventListener("SnakeDeathEvent", GameEvent.class, new DataListener<GameEvent>(){
      //Two different scenarios:
        //1) The player just leaves.
        //2) The player dies.
      //In both cases, the player immediately hits zero length.
      @Override
      public void onData(SocketIOClient client, GameEvent data, AckRequest ackrequest){
        snakes.get(client.getSessionId()).kill(data); //WHAT IF someone wants to come back and play again in the same room?
        ns.getBroadcastOperations().sendEvent("SnakeDeathEvent", data);
      }
    });
    ns.addEventListener("ChatEvent", ChatEvent.class, new DataListener<ChatEvent>(){
      @Override
      public void onData(SocketIOClient client, ChatEvent data, AckRequest ackrequest){
        ns.getBroadcastOperations().sendEvent("ChatEvent", data);
      }
    });

    timer.schedule(new TimerTask(){
      @Override
      public void run(){
        Game.this.tick ++;
        if(Game.this.tick % 10 == 0)
          ns.getBroadcastOperations().sendEvent("FullUpdateEvent", new FullUpdateEvent(Game.this, Game.this.tick));
      }
    }, 200, 200); //every 0.2 seconds, starting in 0.2 seconds
  }

  public void close(){
    timer.cancel();
    server.removeNamespace(ns.getName());
  }
}
