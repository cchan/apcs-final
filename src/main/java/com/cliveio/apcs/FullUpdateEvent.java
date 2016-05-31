package com.cliveio.apcs;
import java.util.Map;
import java.util.UUID;

public class FullUpdateEvent extends GameEvent{
  String name;
  public String getName(){return name;}
  Map<UUID, Snake> snakes;
  public Map<UUID, Snake> getSnakes(){return snakes;}
  FullUpdateEvent(Game g){
    super();
    this.name = g.getName();
    this.snakes = g.getSnakes();
    this.setTick(g.getTick());
    for(Snake s : this.snakes.values())
      s.getQueue(g.getTick()); //idk, somehow make getQueue get caught by SocketIO and sent
  }
}
