package com.cliveio.apcs;

public class FullUpdateEvent extends GameEvent{
  Game g;
  FullUpdateEvent(Game g, int tick){super();this.g=g;this.setTick(tick);}
}
