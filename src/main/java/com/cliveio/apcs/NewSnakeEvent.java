package com.cliveio.apcs;

public class NewSnakeEvent extends GameEvent{
  private String name;
  public String getName(){return name;}
  public void setName(String name){this.name = sanitize(name);}

  private int x;
  public int getX(){return x;}
  public void setX(int x){this.x = x;}

  private int y;
  public int getY(){return y;}
  public void setY(int y){this.y = y;}

  private int dir;
  public int getDir(){return dir;}
  public void setDir(int dir){this.dir = dir;}
}
