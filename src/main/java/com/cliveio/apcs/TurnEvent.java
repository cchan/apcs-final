package com.cliveio.apcs;

public class TurnEvent extends GameEvent{
  private String player; //IS THERE A WAY TO DO PLAYER IDs
  public String getPlayer(){return player;}
  public void setPlayer(String player){this.player = sanitize(player);}

  private int twist; //+1 or -1 (or zero maybe)
  public int getTwist(){return twist;}
  public void setTwist(int twist){this.twist = twist > 0 ? 1 : twist < 0 ? -1 : 0;}
}
