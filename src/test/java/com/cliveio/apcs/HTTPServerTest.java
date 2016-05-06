package com.cliveio.apcs;

import junit.framework.TestCase;
import org.apache.commons.io.*;
import java.net.*;
import java.io.*;

public class HTTPServerTest extends TestCase {
  public HTTPServerTest(String name){
    super(name);
  }
  public void testSomething() throws Exception {
    int PORT = 8000;
    HTTPServer hs = new HTTPServer(PORT);
    hs.start();
    
    InputStream is1 = (new URL("http://127.0.0.1:"+PORT+"/")).openStream();
    
    InputStream is2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("client/index.html");
    
    IOUtils.contentEquals(is1, is2);
    
    hs.interrupt();
  }
}
