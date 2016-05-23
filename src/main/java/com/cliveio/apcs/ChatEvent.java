package com.cliveio.apcs;

public class ChatEvent extends Event{
  private String author;
  public String getAuthor(){return author;}
  public void setAuthor(String author){this.author = sanitize(author);}
  
  private String message;
  public String getMessage(){return message;}
  public void setMessage(String message){this.message = sanitize(message);}
}
