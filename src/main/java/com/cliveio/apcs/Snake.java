package com.cliveio.apcs;
import java.util.*;
import java.lang.Math;

public class Snake{
  public static Random rand = new Random();

  private int length = 10;
  private NewSnakeEvent birth; //initial tick, name, x, y, d
  private TreeMap<Integer, Integer> turns = new TreeMap<Integer, Integer>(); //each turn: tick => direction
                                                                             //Additionally, TreeMap is helpful by disallowing multiple turns in one tick
  private GameEvent death = null; //collision tick
  private Deque<Position> queue = new LinkedList<Position>();
  private int x, y, dir;
  private int nextUnqueuedTick;

  public Snake(NewSnakeEvent birth){ //Modifies this parameter
    birth.setX(rand.nextInt(Game.width));
    birth.setY(rand.nextInt(Game.height));
    birth.setDir(this.dir = rand.nextInt(4));
    this.birth = birth;

    this.nextUnqueuedTick = birth.getTick() + 1;

    queue.offer(new Position(birth.getX(), birth.getY()));
  }
  public void turn(TurnEvent te){
    if(te.getTwist() != 0){
      dir = (dir + te.getTwist() + 4) % 4;
      turns.put(te.getTick(), te.getTwist());
      this.nextUnqueuedTick = Math.min(this.nextUnqueuedTick, te.getTick());
    }
  }
  public void kill(GameEvent sd){this.death = sd;}

  public boolean isAlive(){return death == null;}
  public String getName(){return birth.getName();}
  public int getLength(){return length;}
  public NewSnakeEvent getBirth(){return birth;}
  public TreeMap<Integer, Integer> getTurns(){return turns;}
  public GameEvent getDeath(){return death;}
  public int getX(){return x;}
  public int getY(){return y;}
  public int getDir(){return dir;}
  public Queue<Position> getQueue(int tick){
    queue.clear();
    if(length == 0) return queue;
    int height = Game.height, width = Game.width;

    //Work backwards to get current position
    int currX = x, currY = y, currDir = dir;

    for(int currTick = tick; currTick > tick - length; currTick--){
      queue.offerFirst(new Position(currX, currY));

      if(turns.containsKey(currTick))
        currDir = (currDir - turns.get(currTick) + 4) % 4;

      if(currDir == 0)currY = (currY + 1) % height;
      else if(currDir == 1)currX = (currX - 1 + width) % width;
      else if(currDir == 2)currY = (currY - 1 + height) % height;
      else if(currDir == 3)currX = (currX + 1) % width;
    }
    return queue;
  }
}
class Position{
  public int x, y;
  public Position(int x, int y){this.x = x; this.y = y;}
}
