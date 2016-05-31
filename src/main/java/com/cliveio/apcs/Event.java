package com.cliveio.apcs;
import java.util.Date;
import java.text.Normalizer;

public abstract class Event{
  protected Date timestamp; // Server received time
  public Date getTimestamp(){return timestamp;}

  protected String sanitize(String dirty){
    return Normalizer //http://stackoverflow.com/a/4122207/1181387
      .normalize(dirty, Normalizer.Form.NFD)
      .replaceAll("[^ -~]", "")
      .replace("&","&amp;").replace(">","&gt;").replace("<","&lt;");
  }

  public Event(){timestamp = new Date();}
}
