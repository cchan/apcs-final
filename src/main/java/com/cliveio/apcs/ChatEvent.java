package com.cliveio.apcs;
import java.util.Date;

public class ChatEvent extends Event{
  private String author;
  public String getAuthor(){return author;}
  public void setAuthor(String author){this.author = sanitize(author);}
  
  private String message;
  public String getMessage(){return message;}
  public void setMessage(String message){this.message = sanitize(message);}
  
  public ChatEvent(){}
  public ChatEvent(String author, String message){super(); this.author = sanitize(author); this.message = sanitize(message);}
}
