package com.cliveio.apcs;

public class TurnEvent extends GameEvent{
  private int twist; //+1 or -1 (or zero maybe)
  public int getTwist(){return twist;}
  public void setTwist(int twist){this.twist = twist > 0 ? 1 : twist < 0 ? -1 : 0;}
}
