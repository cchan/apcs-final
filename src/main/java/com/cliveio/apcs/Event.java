package com.cliveio.apcs;
import java.util.Date;

public class Event{
  protected Date timestamp; // Server received time
  public Date getTimestamp(){return timestamp;}
  
  public Event(){timestamp = new Date();}
}
