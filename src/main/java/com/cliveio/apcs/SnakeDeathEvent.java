package com.cliveio.apcs;

public class SnakeDeathEvent extends GameEvent{
  private String player; //IS THERE A WAY TO DO PLAYER IDs -- extract it from the SocketIOClient object and add it to the event
  public String getPlayer(){return player;}
  public void setPlayer(String player){this.player = sanitize(player);}
}
