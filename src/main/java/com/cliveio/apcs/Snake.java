package com.cliveio.apcs;
import java.util.List;
import java.util.ArrayList;

public class Snake{
  private int length = 10;
  private NewSnakeEvent nsk = null; //initial tick, name, x, y, dir
  private List<TurnEvent> turns = new ArrayList<TurnEvent>(); //each turn: tick, direction
  private GameEvent death = null; //death tick

  public Snake(NewSnakeEvent nsk){this.nsk = nsk;}
  public void turn(TurnEvent te){turns.add(te);}
  public void kill(GameEvent sd){this.death = sd;}

  public String getName(){return nsk.getName();}
  public int getLength(){return length;}
}
