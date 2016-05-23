package com.cliveio.apcs;

public class DisconnectEvent extends Event{
  private String name;
  public String getName(){return name;}
  public void setName(String name){this.name = sanitize(name);}
  
  public DisconnectEvent(){super();}
}
