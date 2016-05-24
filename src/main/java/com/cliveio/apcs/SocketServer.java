package com.cliveio.apcs;

import java.io.*;
import java.net.*;
import java.util.*;

import com.corundumstudio.socketio.listener.*;
import com.corundumstudio.socketio.*;

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
    System.out.println(text);
  }
  public List<String> getAllNamespaces(){
    List<String> namespaces = new ArrayList<String>();
    for(SocketIONamespace ns : server.getAllNamespaces())
      namespaces.add(ns.getName());
    return namespaces;
  }
  @Override
  public void run(){
    server = new SocketIOServer(config);
    server.addEventListener("ChatEvent", ChatEvent.class, new DataListener<ChatEvent>(){
      @Override
      public void onData(SocketIOClient client, ChatEvent data, AckRequest ackrequest){
        log("yellow", data.getAuthor() + ": " + data.getMessage());
      }
    });
    server.addEventListener("ConnectEvent", ConnectEvent.class, new DataListener<ConnectEvent>(){
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
          server.addNamespace(data.getName());
          log("cyan", "Game '" + data.getName() + "' has been created");
        }else{
          log("red", "Game '" + data.getName() + "' already exists");
        }
        ackrequest.sendAckData(getAllNamespaces());
      }
    });
    
    server.start();
    System.out.println("Listening *:"+PORT);
    try{
      Thread.sleep(Integer.MAX_VALUE);
    }
    catch(InterruptedException e){
      server.stop();
    }
  }
}
