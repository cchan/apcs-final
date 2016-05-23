package com.cliveio.apcs;

public class ConnectEvent extends Event{
  private String name;
  public String getName(){return name;}
  public void setName(String name){this.name = sanitize(name);}
  
  public ConnectEvent(String name){super(); this.name = sanitize(name);}
}
