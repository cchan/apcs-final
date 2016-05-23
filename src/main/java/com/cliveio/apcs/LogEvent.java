package com.cliveio.apcs;

public class LogEvent extends Event{
  private String color;
  public String getColor(){return color;}
  public void setColor(String color){this.color = sanitize(color);}
  
  private String text;
  public String getText(){return text;}
  public void setText(String text){this.text = sanitize(text);}
}
