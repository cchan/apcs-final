package com.cliveio.apcs;
import java.util.*;

public class Snake{
  public static Random rand = new Random();

  private int length = 10;
  private NewSnakeEvent birth; //initial tick, name
  private Position initPos;
  private int initDir;
  private SortedSet<TurnEvent> turns = new TreeSet<TurnEvent>(); //each turn: tick, direction
  private GameEvent death = null; //death tick
  private Queue<Position> queue = new LinkedList<Position>();
  private int lastUpdatedQueueTick;
  private int dir;

  public Snake(NewSnakeEvent birth){
    this.birth = birth;
    this.lastUpdatedQueueTick = birth.getTick();
    queue.offer(this.initPos = new Position(rand.nextInt(Game.width), rand.nextInt(Game.height)));
    this.dir = this.initDir = rand.nextInt(4);
  }
  public void turn(TurnEvent te){turns.add(te);}
  public void kill(GameEvent sd){this.death = sd;}

  public String getName(){return birth.getName();}
  public int getLength(){return length;}
  public NewSnakeEvent getBirth(){return birth;}
  public SortedSet<TurnEvent> getTurns(){return turns;}
  public GameEvent getDeath(){return death;}
  public Queue<Position> getQueue(int tick){
    for(int i = lastUpdatedQueueTick; i < tick; i++)
      ;//??
    return queue;
  }
  public int getDir(){return dir;}
}
class Position{
  public int x, y;
  public Position(int x, int y){this.x = x; this.y = y;}
}
