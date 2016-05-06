package com.cliveio.apcs;

import java.io.*;
import java.net.*;

import com.corundumstudio.socketio.listener.*;
import com.corundumstudio.socketio.*;

public class SocketServer extends Thread{
  public int PORT;
  public Configuration config;
  public SocketServer(int PORT) throws Exception {
    this.PORT = PORT;
    config = new Configuration();
    config.setHostname("0.0.0.0");
    config.setPort(PORT);
  }
  @Override
  public void run(){
    final SocketIOServer server = new SocketIOServer(config);
    server.addEventListener("clickevent", ClickEvent.class, new DataListener<ClickEvent>(){
      @Override
      public void onData(SocketIOClient client, ClickEvent data, AckRequest ackrequest){
        System.out.println("ClickEvent received");
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
