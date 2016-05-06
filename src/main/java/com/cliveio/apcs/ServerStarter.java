package com.cliveio.apcs;

public class ServerStarter {
  public static void main(String[] args){
    try{
      HTTPServer hs = new HTTPServer(8000);
      SocketServer ss = new SocketServer(1238);
      ss.start();
      hs.start();
      Thread.sleep(1000);
      ss.interrupt();
      hs.interrupt();
    }catch(Exception e){
      System.out.println("EXCEPTION: " + e.getMessage());
    }
  }
}
