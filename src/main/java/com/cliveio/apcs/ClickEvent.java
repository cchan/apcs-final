package com.cliveio.apcs;

public class ClickEvent{
  private int x;
  public int getX(){return x;}
  public void setX(int x){this.x=x;}
  
  private int y;
  public int getY(){return y;}
  public void setY(int y){this.y=y;}
  
  public ClickEvent(){}
  public ClickEvent(int x, int y){this.x = x; this.y = y;}
}