package com.cliveio.apcs;
import java.lang.Comparable;
import java.util.UUID;

public abstract class GameEvent extends Event implements Comparable{
  private UUID id;
  public UUID getID(){return id;}
  public void setID(UUID id){this.id=id;}

  private int tick;
  public int getTick(){return tick;}
  public void setTick(int tick){this.tick = tick;}

  @Override
  public int compareTo(Object o) {
    if(this.tick > ((GameEvent)o).getTick()) return 1;
    else if(this.tick < ((GameEvent)o).getTick()) return -1;
    else if(this.getTimestamp().after(((Event)o).getTimestamp())) return 1;
    else if(this.getTimestamp().before(((Event)o).getTimestamp())) return -1;
    else return 0;
  }
}
