package com.cliveio.apcs;

import junit.framework.TestCase;

public class SocketServerTest extends TestCase {
  public SocketServerTest(String name){
    super(name);
  }
  public void testSomething() throws Exception {
    int PORT = 8056;
    SocketServer hs = new SocketServer(PORT);
    assertTrue(hs.PORT == PORT);
  }
}