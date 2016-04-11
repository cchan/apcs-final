package com.cliveio;

import java.io.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;

import com.sun.net.httpserver.*;

public class socketserver {
  public static final int PORT = 8000;
  public static final String resp404 = "404 not found";
  public static void main(String[] args) throws Exception {
    HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
    server.createContext("/", new MyHandler());
    server.setExecutor(null); // creates a default executor
    server.start();
    System.out.println("Listening on *:"+PORT);
  }
  static class MyHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
      String path = t.getRequestURI().getPath();
      path = path.replace("/","").replace("\\","").replace("..","").replace("~","");
      System.out.print("Requested ["+path+"] ");
      if(path.equals(""))
        path="index.html";
      
      File f = new File("../client/"+path);
      OutputStream os = t.getResponseBody();
      if(f.exists() && !f.isDirectory()){
        t.sendResponseHeaders(200, f.length());
        Files.copy(f.toPath(), os);
        System.out.println("200");
      }else{
        t.sendResponseHeaders(404,resp404.length());
        os.write(resp404.getBytes());
        System.out.println("404");
      }
      os.close();      
    }
  }
}
