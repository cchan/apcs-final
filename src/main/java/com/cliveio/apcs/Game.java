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

  private String name;
  public String getName(){return name;}

  private Map<UUID, Snake> snakes = new HashMap<UUID, Snake>();
  public Map<UUID, Snake> getSnakes(){return snakes;}

  private final Timer timer = new Timer();
  private final SocketIONamespace ns;
  private final SocketIOServer server;

  public void log(String color, String text){
    LogEvent le = new LogEvent();
    le.setColor(color);
    le.setText(text);
    ns.getBroadcastOperations().sendEvent("LogEvent", le);
    System.out.println("SOCKETSERVER"+this.ns.getName()+" LOG: " + text);
  }

  public Game(final SocketIOServer server, String gameName){
    this.server = server;
    this.ns = server.addNamespace("/"+gameName);
    this.name = gameName;

    ns.addEventListener("NewSnakeEvent", NewSnakeEvent.class, new DataListener<NewSnakeEvent>(){
      @Override
      public void onData(SocketIOClient client, NewSnakeEvent data, AckRequest ackrequest){
        log("teal","Received NewSnakeEvent");
        data.setTick(tick);
        data.setID(client.getSessionId());
        snakes.put(client.getSessionId(), new Snake(data));
        ackrequest.sendAckData(new FullUpdateEvent(Game.this));
        ns.getBroadcastOperations().sendEvent("NewSnakeEvent", data);
      }
    });
    ns.addEventListener("TurnEvent", TurnEvent.class, new DataListener<TurnEvent>(){
      @Override
      public void onData(SocketIOClient client, TurnEvent data, AckRequest ackrequest){
        data.setID(client.getSessionId());
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
        data.setID(client.getSessionId());
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
    ns.addEventListener("disconnect", Object.class, new DataListener<Object>(){
      @Override
      public void onData(SocketIOClient client, Object data, AckRequest ackrequest){
        GameEvent ndata = new GameEvent();
        ndata.setID(client.getSessionId());
        ndata.setTick(tick);
        snakes.get(client.getSessionId()).kill(ndata); //WHAT IF someone wants to come back and play again in the same room?
        ns.getBroadcastOperations().sendEvent("SnakeDeathEvent", ndata);
      }
    });

    timer.schedule(new TimerTask(){
      @Override
      public void run(){
        Game.this.tick ++;
        if(Game.this.tick % 10 == 0)
          ns.getBroadcastOperations().sendEvent("FullUpdateEvent", new FullUpdateEvent(Game.this));
      }
    }, 200, 200); //every 0.2 seconds, starting in 0.2 seconds
  }

  public void close(){
    timer.cancel();
    server.removeNamespace(ns.getName());
  }
}
