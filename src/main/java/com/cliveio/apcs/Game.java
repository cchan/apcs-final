package com.cliveio.apcs;

import com.corundumstudio.socketio.listener.*;
import com.corundumstudio.socketio.*;

public class Game{ //can be treated as a GameEvent
  public static final int width = 25;
  public static final int height = 25;

  private int tick = 0;
  public int getTick(){return tick;}
  public void advanceTick(){tick++;}

  //private TupleList<NewSnakeEvent, List<TurnEvent>, SnakeDeathEvent> snakeData; //TupleList isn't a thing
                                                // Or instead of indexing on snake, we can index on tick
  public void addNewSnake(NewSnakeEvent ns){
    //newSnakes.push(ns);
  }
  //We can't really have a removeSnake event, can we? Two different scenarios:
    //1) The player just leaves.
    //2) The player dies.
  //In both cases, the player immediately hits zero length.
  //This should be a SnakeDeathEvent.

  public void addTurn(TurnEvent te){
    //Find Tuple that has the right snake //TODO: make it SocketIO-ID-based, not name-based
    //list.push(te);
  }

  public void killSnake(SnakeDeathEvent sd){
    //So I guess we just add this in on the event chain?
  }

  public void getCurrentBoard(){
    //For the sake of using Java, do this computation and send this data back with the rest of the GameState.
  }

  public Game(final SocketIONamespace ns){
    ns.addEventListener("TurnEvent", TurnEvent.class, new DataListener<TurnEvent>(){
      @Override
      public void onData(SocketIOClient client, TurnEvent data, AckRequest ackrequest){
        ns.getBroadcastOperations().sendEvent("TurnEvent", data);
      }
    });
    ns.addEventListener("NewSnakeEvent", NewSnakeEvent.class, new DataListener<NewSnakeEvent>(){
      @Override
      public void onData(SocketIOClient client, NewSnakeEvent data, AckRequest ackrequest){
        data.setTick(0 /*currentTick*/);
      }
    });
    ns.addEventListener("FullUpdateRequest", Object.class, new DataListener<Object>(){
      //TODO: this shouldn't be necessary
      @Override
      public void onData(SocketIOClient client, Object data, AckRequest ackrequest){
        //client.emit(gameState);
      }
    });
    ns.addEventListener("ChatEvent", ChatEvent.class, new DataListener<ChatEvent>(){
      @Override
      public void onData(SocketIOClient client, ChatEvent data, AckRequest ackrequest){
        ns.getBroadcastOperations().sendEvent("ChatEvent", data);
      }
    });

    //java.util.Timer.setInterval(function(){send FullUpdateEvent}, 2 sec);
  }
}
