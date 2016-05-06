package com.cliveio.apcs;

import junit.framework.TestCase;

public class SocketServerTest extends TestCase {
  public SocketServerTest(String name){
    super(name);
  }
  public void testSomething() throws Exception {
    System.out.println("Port: " + SocketServer.PORT);
    assertTrue(SocketServer.PORT >= 0 && SocketServer.PORT < 65536);
  }
}
