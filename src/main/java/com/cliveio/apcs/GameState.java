package com.cliveio.apcs;

public class GameState{
  //Constants for width and height should be here
  
  private int tick = 0;
  public int getTick(){return tick;}
  public void advanceTick(){tick++;}

  private TupleList<NewSnakeEvent, List<TurnEvent>, SnakeDeathEvent> snakeData; //TupleList isn't a thing
                                                // Or instead of indexing on snake, we can index on tick
  public void addNewSnake(NewSnakeEvent ns){
    newSnakes.push(ns);
  }
  //We can't really have a removeSnake event, can we? Two different scenarios:
    //1) The player just leaves.
    //2) The player dies.
  //In both cases, the player immediately hits zero length.
  //This should be a SnakeDeathEvent.

  public void addTurn(TurnEvent te){
    Find Tuple that has the right snake //TODO: make it SocketIO-ID-based, not name-based
    list.push(te);
  }

  public void killSnake(SnakeDeathEvent sd){
    //So I guess we just add this in on the event chain?
  }

  public void getCurrentBoard(){
    //For the sake of using Java, do this computation and send this data back with the rest of the GameState.
  }
}
