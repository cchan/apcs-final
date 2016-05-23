package com.cliveio.apcs;

import java.io.*;
import java.net.*;

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
  @Override
  public void run(){
    server = new SocketIOServer(config);
    server.addEventListener("ChatEvent", ChatEvent.class, new DataListener<ChatEvent>(){
      @Override
      public void onData(SocketIOClient client, ChatEvent data, AckRequest ackrequest){
        log("yellow", data.getAuthor() + ": " + data.getMessage());
        server.getBroadcastOperations().sendEvent("LogEvent", data);
      }
    });
    server.addEventListener("ConnectEvent", ConnectEvent.class, new DataListener<ConnectEvent>(){
      @Override
      public void onData(SocketIOClient client, ConnectEvent data, AckRequest ackrequest){
        log("green", data.getName() + " has connected");
        server.getBroadcastOperations().sendEvent("LogEvent", data);
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
