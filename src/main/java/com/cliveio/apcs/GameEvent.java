package com.cliveio.apcs;

public abstract class GameEvent extends Event{
  private int tick;
  public int getTick(){return name;}
  public void setTick(int tick){this.tick = tick;}
}
