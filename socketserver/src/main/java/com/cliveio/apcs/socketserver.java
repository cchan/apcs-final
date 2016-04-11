package com.cliveio.apcs;

import java.io.*;
import java.net.*;

import com.corundumstudio.socketio.listener.*;
import com.corundumstudio.socketio.*;

public class socketserver {
  public static void main(String[] args) throws Exception {
    Configuration config = new Configuration();
    config.setHostname("0.0.0.0");
    config.setPort(1234);
    
    final SocketIOServer server = new SocketIOServer(config);
    server.addEventListener("clickevent", ClickEvent.class, new DataListener<ClickEvent>(){
      @Override
      public void onData(SocketIOClient client, ClickEvent data, AckRequest ackrequest){
        System.out.println("ClickEvent received");
      }
    });
    
    server.start();
    System.out.println("Listening *:1234");
    Thread.sleep(Integer.MAX_VALUE);
    server.stop();
  }
}
