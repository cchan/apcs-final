package com.cliveio.apcs;

import junit.framework.TestCase;

public class HTTPServerTest extends TestCase {
  public HTTPServerTest(String name){
    super(name);
  }
  public void testSomething() throws Exception {
    int PORT = 8000;
    HTTPServer hs = new HTTPServer(PORT);
    assertTrue(hs.PORT == PORT);
  }
}
