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
    this.mvcMessaging.subscribe("legalMoves", this);
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
      }
    }
    
    this.board[3][3] = Constants.WHITE;
    this.board[3][4] = Constants.BLACK;
    this.board[4][3] = Constants.BLACK;
    this.board[4][4] = Constants.WHITE;
    
    this.board[2][3] = Constants.LEGAL;
    this.board[3][2] = Constants.LEGAL;
    this.board[4][5] = Constants.LEGAL;
    this.board[5][4] = Constants.LEGAL;
    
    this.whoseMove = false;
    this.gameOver = false;
  }

  private int flipPieces(int direction, String color, String oppColor, int row, int col){
      int firstRow = row;
      int firstCol = col;
      
      if (direction == 8){return 0;}
      
      //CARDINAL DIRECtIONS
      //flip to the right
      while (direction == 0 && col < 7 && !shouldntFlip(direction, firstRow, firstCol)){
          if (board[row][col+1].equals(Constants.BLANK)){
              for (int i = col; i > firstCol; i--){
                  board[firstRow][i] = oppColor;
              }
              return flipPieces(direction+1, color, oppColor, firstRow, firstCol);
          }
          if (board[row][col].equals(board[row][col+1])){
              return flipPieces(direction+1, color, oppColor, firstRow, firstCol);
          } 
          if (!board[row][col].equals(board[row][col+1])){
              col++;
              if (col != 7){
                  board[row][col] = color;
              }
          }
      }
      direction = (direction == 0) ? 1 : direction;
      
      row = firstRow;
      col = firstCol;
      //flip to the left
      while (direction == 1 && col > 0 && !shouldntFlip(direction, firstRow, firstCol)){
          if (board[row][col-1].equals(Constants.BLANK)){
              for (int i = col; i < firstCol; i++){
                  board[firstRow][i] = oppColor;
              }
              return flipPieces(direction+1, color, oppColor, firstRow, firstCol);
          }
          if (board[row][col].equals(board[row][col-1])){
              return flipPieces(direction+1, color, oppColor, firstRow, firstCol);
          } 
          if (!board[row][col].equals(board[row][col-1])){
              col--;
              if (col != 0){
                board[row][col] = color;
              }
          }
      }
      direction = (direction == 1) ? 2 : direction;
      
      row = firstRow;
      col = firstCol;
      //flip up
      while (direction == 2 && row > 0 && !shouldntFlip(direction, firstRow, firstCol)){
          if (board[row-1][col].equals(Constants.BLANK)){
              for (int i = row; i < firstRow; i++){
                  board[i][firstCol] = oppColor;
              }
              return flipPieces(direction+1, color, oppColor, firstRow, firstCol);
          }
          if (board[row][col].equals(board[row-1][col])){
              return flipPieces(direction+1, color, oppColor, firstRow, firstCol);
          } 
          if (!board[row][col].equals(board[row-1][col])){
              row--;
              if (row != 0){
                board[row][col] = color;
              }
          }
      }
      direction = (direction == 2) ? 3 : direction;
      
      row = firstRow;
      col = firstCol;
      //flip down
      while (direction == 3 && row < 7 && !shouldntFlip(direction, firstRow, firstCol)){
          if (board[row+1][col].equals(Constants.BLANK)){
              for (int i = row; i > firstRow; i--){
                  board[i][firstCol] = oppColor;
              }
              return flipPieces(direction+1, color, oppColor, firstRow, firstCol);
          }
          if (board[row][col].equals(board[row+1][col]) ){
              return flipPieces(direction+1, color, oppColor, firstRow, firstCol);
          } 
          if (!board[row][col].equals(board[row+1][col])){
              row++;
              if (row != 7){
                board[row][col] = color;
              }
          }
      }
      direction = (direction == 3) ? 4 : direction;
      
      //DIAGONALS
      row = firstRow;
      col = firstCol;
      //diag down/right
      while (direction == 4 && row < 7 && col < 7 && !shouldntFlip(direction, firstRow, firstCol)){
          if (board[row+1][col+1].equals(Constants.BLANK)){
              int j = col;
              for (int i = row; i > firstRow && j > -1; i--){
                board[i][j] = oppColor;
                j--;
              }
              return flipPieces(direction+1, color, oppColor, firstRow, firstCol);
          }
          if (board[row][col].equals(board[row+1][col+1]) ){
              return flipPieces(direction+1, color, oppColor, firstRow, firstCol);
          } 
          if (!board[row][col].equals(board[row+1][col+1])){
              row++;
              col++;
              if (row != 7 && col != 7){
                board[row][col] = color;
              }
          }
      }
      direction = (direction == 4) ? 5 : direction;
      
      row = firstRow;
      col = firstCol;
      //diag down/left
      while (direction == 5 && row < 7 && col > 0 && !shouldntFlip(direction, firstRow, firstCol)){
          if (board[row+1][col-1].equals(Constants.BLANK)){
              int j = col;
              for (int i = row; i > firstRow && j < 8; i--){
                board[i][j] = oppColor;
                j++;
              }
              return flipPieces(direction+1, color, oppColor, firstRow, firstCol);
          }
          if (board[row][col].equals(board[row+1][col-1]) ){
              return flipPieces(direction+1, color, oppColor, firstRow, firstCol);
          } 
          if (!board[row][col].equals(board[row+1][col-1])){
              row++;
              col--;
              if (row != 7 && col != 0){
                board[row][col] = color;
              }
          }
      }
      direction = (direction == 5) ? 6 : direction;
      
      row = firstRow;
      col = firstCol;
      //diag up/left
      while (direction == 6 && row > 0 && col > 0 && !shouldntFlip(direction, firstRow, firstCol)){
          if (board[row-1][col-1].equals(Constants.BLANK)){
              int j = col;
              for (int i = row; i < firstRow && j < 8; i++){
                board[i][j] = oppColor;
                j++;
              }
              return flipPieces(direction+1, color, oppColor, firstRow, firstCol);
          }
          if (board[row][col].equals(board[row-1][col-1]) ){
              return flipPieces(direction+1, color, oppColor, firstRow, firstCol);
          } 
          if (!board[row][col].equals(board[row-1][col-1])){
              row--;
              col--;
              if (row != 7 && col != 0){
                board[row][col] = color;
              }
          }
      }
      direction = (direction == 6) ? 7 : direction;
      
      row = firstRow;
      col = firstCol;
      //diag up/right
      while (direction == 7 && row > 0 && col < 7 && !shouldntFlip(direction, firstRow, firstCol)){
          if (board[row-1][col+1].equals(Constants.BLANK)){
              int j = col;
              for (int i = row; i < firstRow && j > -1; i++){
                board[i][j] = oppColor;
                j--;
              }
              return flipPieces(direction+1, color, oppColor, firstRow, firstCol);
          }
          if (board[row][col].equals(board[row-1][col+1]) ){
              return flipPieces(direction+1, color, oppColor, firstRow, firstCol);
          } 
          if (!board[row][col].equals(board[row-1][col+1])){
              row--;
              col++;
              if (row != 7 && col != 0){
                board[row][col] = color;
              }
          }
      }
      return 1;
  }
  
   private boolean shouldntFlip(int dir, int firstRow, int firstCol){
      int count = 0;
      //right
      if (dir == 0){
          for (int i = 7; i > firstCol; --i){
              count = (board[firstRow][i].equals(board[firstRow][7])) ? count+1 : count;
          }
          if (count == 7-firstCol){return true;}
      }
      //left
      if (dir == 1){
          for (int i = 0; i < firstCol; ++i){
              count = (board[firstRow][i].equals(board[firstRow][0])) ? count+1 : count;
          }
          if (count == firstCol){return true;}
      }
      //up
      if (dir == 2){
          for (int i = 0; i < firstRow; ++i){
              count = (board[i][firstCol].equals(board[0][firstCol])) ? count+1 : count;
          }
          if (count == firstRow){return true;}
      }
      //down
      if (dir == 3){
          for (int i = 7; i > firstRow; --i){
              count = (board[i][firstCol].equals(board[7][firstCol])) ? count+1 : count;
          }
          if (count == 7-firstRow){return true;}
      }
      
      //DIAGANOLS
      //down/right
      if(dir == 4){
          int j = 7;
          for (int i = 7; i > firstRow && j > firstCol; --i){
              j--;
              count = (board[i][j].equals(board[7][7])) ? count+1:count;
          }
          if (count == 7-firstRow || count == 7-firstCol){return true;}
      }
      
      //down/left
      if(dir == 5){
          int j = 0;
          for (int i = 7; i > firstRow && j < firstCol; --i){
              j++;
              count = (board[i][j].equals(board[7][0])) ? count+1:count;
          }
          if (count == 7-firstRow || count == firstCol){return true;}
      }
      
      //up/left
      if(dir == 6){
          int j = 0;
          for (int i = 0; i < firstRow && j < firstCol; ++i){
              j++;
              count = (board[i][j].equals(board[0][0])) ? count+1:count;
          }
          if (count == firstRow || count == firstCol){return true;}
      }
      
      //up/right
      if(dir == 7){
          int j = 7;
          for (int i = 0; i < firstRow && j > firstCol; ++i){
              j--;
              count = (board[i][j].equals(board[0][7])) ? count+1:count;
          }
          if (count == firstRow || count == 7-firstCol){return true;}
      }
    return false;
  }
  
   private boolean boardFull(){
       for(String[] i : this.board){
           for (String s : i){
               if(s.equals(Constants.BLANK)){return false;}
           }
       }
       return true;
   }
   private String findWinner(){
       int b = 0; int w = 0;
       for(String[] i : this.board){
           for (String s : i){
               b += (s.equals(Constants.BLACK)) ? 1:0;
               w += (s.equals(Constants.WHITE)) ? 1:0;
           }
       }
       
       if (b>w){return "BLACk WINS";} 
       else if (b == w){return "TIE";}
       else {return "WHITE WINS";}
   }
   
   private int findLegalMoves(int direction, String color, String oppColor, int row, int col){
      int firstRow = row;
      int firstCol = col;
      
      //CARDINAL DIRECtIONS
      //flip to the right
      while (direction == 0 && col < 7){
          if (board[row][col].equals(board[row][col+1])){
              return findLegalMoves(direction+1, color, oppColor, firstRow, firstCol);
          } 
          if (!board[row][col].equals(board[row][col+1])){
              col++;
          }
          if (board[row][col].equals(Constants.BLANK)){
              board[row][col] = Constants.LEGAL;
              return findLegalMoves(direction+1, color, oppColor, firstRow, firstCol);
          }
      }
      direction = (direction == 0) ? 1 : direction;
      
      row = firstRow;
      col = firstCol;
      //flip to the left
      while (direction == 1 && col > 0){
          if (board[row][col].equals(board[row][col-1])){
              return findLegalMoves(direction+1, color, oppColor, firstRow, firstCol);
          } 
          if (!board[row][col].equals(board[row][col-1])){
              col--;
          }
          if (board[row][col].equals(Constants.BLANK)){
              board[row][col] = Constants.LEGAL;
              return findLegalMoves(direction+1, color, oppColor, firstRow, firstCol);
          }
      }
      direction = (direction == 1) ? 2 : direction;
      
      row = firstRow;
      col = firstCol;
      //flip up
      while (direction == 2 && row > 0){
          if (board[row][col].equals(board[row-1][col])){
              return findLegalMoves(direction+1, color, oppColor, firstRow, firstCol);
          } 
          if (!board[row][col].equals(board[row-1][col])){
              row--;
          }
          if (board[row][col].equals(Constants.BLANK)){
              board[row][col] = Constants.LEGAL;
              return findLegalMoves(direction+1, color, oppColor, firstRow, firstCol);
          }
      }
      direction = (direction == 2) ? 3 : direction;
      
      row = firstRow;
      col = firstCol;
      //flip down
      while (direction == 3 && row < 7){
          if (board[row][col].equals(board[row+1][col])){
              return findLegalMoves(direction+1, color, oppColor, firstRow, firstCol);
          } 
          if (!board[row][col].equals(board[row+1][col])){
              row++;
          }
          if (board[row][col].equals(Constants.BLANK)){
              board[row][col] = Constants.LEGAL;
              return findLegalMoves(direction+1, color, oppColor, firstRow, firstCol);
          }
      }
      direction = (direction == 3) ? 4 : direction;
      
      //DIAGONALS
      row = firstRow;
      col = firstCol;
      //diag down/right
      while (direction == 4 && row < 7 && col < 7){
          if (board[row][col].equals(board[row+1][col+1])){
              return findLegalMoves(direction+1, color, oppColor, firstRow, firstCol);
          } 
          if (!board[row][col].equals(board[row+1][col+1])){
              col++;
              row++;
          }
          if (board[row][col].equals(Constants.BLANK)){
              board[row][col] = Constants.LEGAL;
              return findLegalMoves(direction+1, color, oppColor, firstRow, firstCol);
          }
      }
      direction = (direction == 4) ? 5 : direction;
      
      row = firstRow;
      col = firstCol;
      //diag down/left
      while (direction == 5 && row < 7 && col > 0){
          if (board[row][col].equals(board[row+1][col-1])){
              return findLegalMoves(direction+1, color, oppColor, firstRow, firstCol);
          } 
          if (!board[row][col].equals(board[row+1][col-1])){
              col--;
              row++;
          }
          if (board[row][col].equals(Constants.BLANK)){
              board[row][col] = Constants.LEGAL;
              return findLegalMoves(direction+1, color, oppColor, firstRow, firstCol);
          }
      }
      direction = (direction == 5) ? 6 : direction;
      
      row = firstRow;
      col = firstCol;
      //diag up/left
      while (direction == 6 && row > 0 && col > 0){
          if (board[row][col].equals(board[row-1][col-1])){
              return findLegalMoves(direction+1, color, oppColor, firstRow, firstCol);
          } 
          if (!board[row][col].equals(board[row-1][col-1])){
              col--;
              row--;
          }
          if (board[row][col].equals(Constants.BLANK)){
              board[row][col] = Constants.LEGAL;
              return findLegalMoves(direction+1, color, oppColor, firstRow, firstCol);
          }
      }
//      direction = (direction == 6) ? 7 : direction;
//      
//      row = firstRow;
//      col = firstCol;
//      //diag up/right
//      while (direction == 7 && row > 0 && col < 7){
//          if (board[row][col].equals(board[row-1][col+1])){
//              return findLegalMoves(direction+1, color, oppColor, firstRow, firstCol);
//          } 
//          if (board[row][col].equals(board[row-1][col+1])){
//              col++;
//              row--;
//          }
//          if (board[row][col].equals(Constants.BLANK)){
//              board[row][col] = Constants.LEGAL;
//              return findLegalMoves(direction+1, color, oppColor, firstRow, firstCol);
//          }
//      }
      return 1;
   }
   
   public void callLegalMoves(){
      this.mvcMessaging.notify("legalMoves", this);
  }
   private void legalLoop(){
       //find Legal Moves
      for (int i = 0; i < this.board.length; i++){
          for (int j = 0; j < this.board.length; j++){
              if (board[i][j].equals(Constants.LEGAL)){board[i][j] = Constants.BLANK;}
              if (this.whoseMove && board[i][j].equals(Constants.WHITE)){
                  findLegalMoves(0, Constants.WHITE, Constants.BLACK, i, j);
              }
              if (!this.whoseMove && this.board[i][j].equals(Constants.BLACK)){
                  findLegalMoves(0, Constants.BLACK, Constants.WHITE, i, j);
              }
          }
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
    
    if(messageName.equals("legalMoves")){
        legalLoop();
        this.mvcMessaging.notify("boardChange", this.board);
    }
    
    // playerMove message handler
    if (messageName.equals("playerMove")) {
      // Get the position string and convert to row and col
      String position = (String)messagePayload;
      Integer row = Integer.valueOf(position.substring(0,1));
      Integer col = Integer.valueOf(position.substring(1,2));
      
      // If square is blank...
      if (this.board[row][col].equals(Constants.LEGAL)) {
        // ... then set @ or O depending on whose move it is
        if (this.whoseMove) {
          this.board[row][col] = Constants.WHITE;
          flipPieces(0, Constants.WHITE, Constants.BLACK, row, col);
        } else {
          this.board[row][col] = Constants.BLACK;
          flipPieces(0, Constants.BLACK, Constants.WHITE, row, col);
        }
        whoseMove = !this.whoseMove;
        // Send the boardChange message along with the new board 
        this.mvcMessaging.notify("boardChange", this.board);
      }
      
    // newGame message handler
    } else if (messageName.equals("newGame")) {
      // Reset the game
      this.newGame();
      // Send the boardChange message along with the new board 
      this.mvcMessaging.notify("boardChange", this.board);
    }
    
    if (boardFull()){
          this.gameOver = true;
          this.mvcMessaging.notify("gameOver", findWinner());
    }
  }
}
