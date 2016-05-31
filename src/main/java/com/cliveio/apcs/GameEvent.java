package com.cliveio.apcs;

public abstract class GameEvent extends Event{
  private int tick;
  public int getTick(){return tick;}
  public void setTick(int tick){this.tick = tick;}
}
