package com.mrjaffesclass.apcs.mvc.template;

import com.mrjaffesclass.apcs.messenger.*;

/**
 * The model represents the data that the app uses.
 * @author Roger Jaffe
 * @version 1.0
 */
public class Model implements MessageHandler {

  // Messaging system for the MVC
  private final Messenger mvcMessaging;

  // Model's data variables
  private boolean whoseMove;
  private boolean gameOver;
  private String[][] board;

  /**
   * Model constructor: Create the data representation of the program
   * @param messages Messaging class instantiated by the Controller for 
   *   local messages between Model, View, and controller
   */
  public Model(Messenger messages) {
    mvcMessaging = messages;
    this.whoseMove = true;
    this.gameOver = false;
    this.board = new String[8][8];
  }
  
  /**
   * Initialize the model here and subscribe to any required messages
   */
  public void init() {
    this.newGame();
    this.mvcMessaging.subscribe("playerMove", this);
    this.mvcMessaging.subscribe("newGame", this);
    this.mvcMessaging.notify("boardChange", this.board);
  }
  
  public void callNewGame(){
      this.mvcMessaging.notify("newGame", this);
  }
    /**
   * Reset the state for a new game
   */
  private void newGame() {
      boolean xoro = true;
      
    for(int row=0; row<this.board.length; row++) {
      for (int col=0; col<this.board[0].length; col++) {
        this.board[row][col] = Constants.BLANK;
        
        for (int i = 0; i < 2; i++){
            
            for (int j = 0; j < 2; j++){
                
                if (row == i+3 && col == j+3){
                  if (xoro == true){
                      this.board[row][col] = Constants.WHITE;
                  } else{
                      this.board[row][col] = Constants.BLACK;
                  }
                  xoro = !xoro;
                }
            }
            xoro = !xoro;
        }
      }
    }
    this.whoseMove = false;
    this.gameOver = false;
  }

  private void flipPieces(int direction, String color){
      if (direction == 0){
          
          
          
      }
  }
  
  @Override
  public void messageHandler(String messageName, Object messagePayload) {
      // Display the message to the console for debugging
    if (messagePayload != null) {
      System.out.println("MSG: received by model: "+messageName+" | "+messagePayload.toString());
    } else {
      System.out.println("MSG: received by model: "+messageName+" | No data sent");
    }
    
    // playerMove message handler
    if (messageName.equals("playerMove")) {
      // Get the position string and convert to row and col
      String position = (String)messagePayload;
      Integer row = Integer.valueOf(position.substring(0,1));
      Integer col = Integer.valueOf(position.substring(1,2));
      // If square is blank...
      if (this.board[row][col].equals(Constants.BLANK)) {
        // ... then set X or O depending on whose move it is
        if (this.whoseMove) {
          this.board[row][col] = Constants.WHITE;
        } else {
          this.board[row][col] = Constants.BLACK;
        }
        
        flipPieces(0, Constants.WHITE);
        
        whoseMove = !this.whoseMove;
        // Send the boardChange message along with the new board 
        this.mvcMessaging.notify("boardChange", this.board);
      }
      
    // newGame message handler
    } else if (messageName.equals("newGame")) {
      // Reset the app state
      this.newGame();
      // Send the boardChange message along with the new board 
      this.mvcMessaging.notify("boardChange", this.board);
    }
  }
}
