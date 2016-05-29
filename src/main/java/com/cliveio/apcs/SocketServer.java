package com.cliveio.apcs;

import java.io.*;
import java.net.*;
import java.util.*;

import com.corundumstudio.socketio.listener.*;
import com.corundumstudio.socketio.*;
import org.apache.log4j.BasicConfigurator;

public class SocketServer extends Thread{
  public int PORT;
  public Configuration config;
  public SocketIOServer server;
  public SocketServer(int PORT) throws Exception {
    this.PORT = PORT;
    config = new Configuration();
    config.setHostname("0.0.0.0");
    config.setPort(PORT);
  }
  public void log(String color, String text){
    LogEvent le = new LogEvent();
    le.setColor(color);
    le.setText(text);
    server.getBroadcastOperations().sendEvent("LogEvent", le);
    System.out.println("SOCKETSERVER LOG: " + text);
  }
  public List<String> getAllNamespaces(){
    List<String> namespaces = new ArrayList<String>();
    for(SocketIONamespace ns : server.getAllNamespaces())
      namespaces.add(ns.getName());
    return namespaces;
  }
  
  public void initGameRoom(final SocketIONamespace ns){
    ns.addEventListener("TurnEvent", TurnEvent.class, new DataListener<TurnEvent>(){
      @Override
      public void onData(SocketIOClient client, TurnEvent data, AckRequest ackrequest){
        ns.getBroadcastOperations().sendEvent("TurnEvent", data);
      }
    });
    ns.addEventListener("NewSnakeEvent", NewSnakeEvent.class, new DataListener<NewSnakeEvent>(){
      @Override
      public void onData(SocketIOClient client, NewSnakeEvent data, AckRequest ackrequest){
        data.setTick(/*currentTick*/);
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

    java.util.Timer.setInterval(function(){send FullUpdateEvent});

    //EVERY TWO SECONDS, EMIT A FullUpdateEvent.

    log("cyan", "Game '" + ns.getName() + "' has been created");
  }
  @Override
  public void run(){
    BasicConfigurator.configure();

    server = new SocketIOServer(config);

    server.addEventListener("ConnectEvent", ConnectEvent.class, new DataListener<ConnectEvent>(){
      //TODO: disallow duplicate names?
      @Override
      public void onData(SocketIOClient client, ConnectEvent data, AckRequest ackrequest){
        log("green", data.getName() + " has connected");
        ackrequest.sendAckData(getAllNamespaces());
      }
    });
    server.addEventListener("RoomCreateEvent", RoomCreateEvent.class, new DataListener<RoomCreateEvent>(){
      @Override
      public void onData(SocketIOClient client, RoomCreateEvent data, AckRequest ackrequest){
        if(server.getNamespace(data.getName()) == null){
          final SocketIONamespace ns = server.addNamespace("/"+data.getName());
          initGameRoom(ns);
        }else{
          log("red", "Game '" + data.getName() + "' already exists - maybe you want to join it?");
        }
        ackrequest.sendAckData(getAllNamespaces());
      }
    });
    server.addEventListener("RoomRefresh", Object.class, new DataListener<Object>(){
      //TODO: this doesn't actually work
      @Override
      public void onData(SocketIOClient client, Object data, AckRequest ackrequest){
        log("orange", "received room list refresh request");
        ackrequest.sendAckData(getAllNamespaces());
      }
    });

    server.start();
    System.out.println("SocketServer listening *:"+PORT);
    try{
      Thread.sleep(Integer.MAX_VALUE);
    }
    catch(InterruptedException e){
      server.stop();
    }
  }
}
