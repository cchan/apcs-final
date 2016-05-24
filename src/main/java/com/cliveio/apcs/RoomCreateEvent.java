package com.cliveio.apcs;

public class RoomCreateEvent extends Event{
  private String name;
  public String getName(){return name;}
  public void setName(String name){this.name = sanitize(name);}
}
