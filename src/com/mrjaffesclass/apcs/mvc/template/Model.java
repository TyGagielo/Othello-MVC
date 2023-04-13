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
    for(int row=0; row<this.board.length; row++) {
      for (int col=0; col<this.board[0].length; col++){
        this.board[row][col] = Constants.BLANK;
      }
    }
    
    this.board[3][3] = Constants.WHITE;
    this.board[3][4] = Constants.BLACK;
    this.board[4][3] = Constants.BLACK;
    this.board[4][4] = Constants.WHITE;
    
    this.whoseMove = false;
    this.gameOver = false;
  }
  
  private int step(int direction, int row, int col, int rowdir, int coldir,
          String color, String oppColor, int firstRow, int firstCol){
      
      if (!inBounds(row, col, rowdir, coldir) || board[row+rowdir][col+coldir].equals(Constants.BLANK)){
          callFor(direction, row, col, firstRow, firstCol, oppColor);
          return 1;
      }
      
      if (board[row][col].equals(board[row+rowdir][col+coldir])){
          return 1;
      } else{
          row += rowdir;
          col += coldir;
          if (inBounds(row, col, rowdir, coldir)){
              board[row][col] = color;
          }
      }
      return 0;
  }
  
  private void flipPieces(int direction, String color, String oppColor, int row, int col){
      int firstRow = row;
      int firstCol = col;
      
      int count = 0;
      //right
      while(direction == 0){
          direction += step(direction, row, col+count, 0, 1, color, oppColor, firstRow, firstCol);
          count++;
      }
      count = 0;
      //left
      while(direction == 1){
          direction += step(direction, row, col-count, 0, -1, color, oppColor, firstRow, firstCol);
          count++;
      }
      count = 0;
      //up
      while(direction == 2){
          direction += step(direction, row-count, col, -1, 0, color, oppColor, firstRow, firstCol);
          count++;
      }
      count = 0;
      //down
      while(direction == 3){
          direction += step(direction, row+count, col, 1, 0, color, oppColor, firstRow, firstCol);
          count++;
      }
      count = 0;
      //down-right
      while(direction == 4){
          direction += step(direction, row+count, col+count, 1, 1, color, oppColor, firstRow, firstCol);
          count++;
      }
      count = 0;
      //down-left
      while(direction == 5){
          direction += step(direction, row+count, col-count, 1, -1, color, oppColor, firstRow, firstCol);
          count++;
      }
      count = 0;
      //up-left
      while(direction == 6){
          direction += step(direction, row-count, col-count, -1, -1, color, oppColor, firstRow, firstCol);
          count++;
      }
      count = 0;
      //up-right
      while(direction == 7){
          direction += step(direction, row-count, col+count, -1, 1, color, oppColor, firstRow, firstCol);
          count++;
      }
  }
  
  private void callFor(int d, int row, int col, int firstRow, int firstCol, String oppColor){
      if (d == 0){
        for (int i = col; i > firstCol; i--){
            board[firstRow][i] = oppColor;
        }
      }
      
      if (d == 1){
        for (int i = col; i < firstCol; i++){
            board[firstRow][i] = oppColor;
        }
      }
      
      if (d == 2){
        for (int i = row; i < firstRow; i++){
              board[i][firstCol] = oppColor;
          }
      }
      
      if (d == 3){
        for (int i = row; i > firstRow; i--){
              board[i][firstCol] = oppColor;
          }
      }
      
      if (d == 4){
        int j = col;
        for (int i = row; i > firstRow && j > -1; i--){
          board[i][j] = oppColor;
          j--;
        }
      }
      
      if (d == 5){
        int j = col;
        for (int i = row; i > firstRow && j < 8; i--){
            board[i][j] = oppColor;
            j++;
          }
      }
      
      if (d == 6){
        int j = col;
          for (int i = row; i < firstRow && j < 8; i++){
            board[i][j] = oppColor;
            j++;
          }
      }
      
      if (d == 7){
        int j = col;
        for (int i = row; i < firstRow && j > -1; i++){
          board[i][j] = oppColor;
          j--;
        }
      }
  }
  
  private void countPieces(){
      int bnum = 0, wnum = 0;
       
       for(String[] i : this.board){
           for (String s : i){
               if (s.equals(Constants.WHITE)){wnum++;}
               if (s.equals(Constants.BLACK)){bnum++;}
           }
       }
       this.mvcMessaging.notify("countWhite", wnum);
       this.mvcMessaging.notify("countBlack", bnum);
  }
  
   private boolean boardFull(){
       int bnum = 0, wnum = 0;
       
       for(String[] i : this.board){
           for (String s : i){
               if(s.equals(Constants.BLANK)){return false;}
               if (s.equals(Constants.WHITE)){wnum++;}
               if (s.equals(Constants.BLACK)){bnum++;}
           }
       }
       this.mvcMessaging.notify("countWhite", wnum);
       this.mvcMessaging.notify("countBlack", bnum);
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
   
   private boolean isLegal(int row, int col, String piece){
        // Check empty
        if (!board[row][col].equals(Constants.BLANK))
            return false;
        
        String opponent = Constants.WHITE;
        if (piece.equals(Constants.WHITE))
            opponent = Constants.BLACK;
        
        // up 
        if (row-1>-1 && legalStep(row - 1, col, -1, 0, piece, opponent)){
            return true;}
        //down
        if (row+1<8 && legalStep(row + 1, col, 1, 0, piece, opponent)){
            return true;}
        //left
        if (col-1>-1 && legalStep(row, col - 1, 0, -1, piece, opponent)){
            return true;}
        //right
        if (col+1<8 && legalStep(row, col + 1, 0, 1, piece, opponent)){
            return true;}
        // up-left
        if (row-1>-1 && col-1>-1 && legalStep(row - 1, col - 1, -1, -1, piece, opponent)){
            return true;}
        // down-left
        if (row+1<8 && col-1>-1 && legalStep(row + 1, col - 1, 1, -1, piece, opponent)){
            return true;}
        // up-right
        if (row-1>-1 && col+1<8 && legalStep(row - 1, col + 1, -1, 1, piece, opponent)){
            return true;}
        // down-right
        if (row+1<8 && col+1<8 && legalStep(row + 1, col + 1, 1, 1, piece, opponent)){
            return true;}
        
        return false;
    }
   
    private boolean legalStep(int row, int col, int rowdir, int coldir, String color, String oppColor){
        if (board[row][col].equals(oppColor)){
            while (inBounds(row, col, rowdir, coldir)){
                row += rowdir;
                col += coldir;
                if (board[row][col].equals(Constants.BLANK))
                    return false;
                if (board[row][col].equals(color))
                    return true;
            }
        }
        return false;
    }
    
    private boolean noLegal(String color){
      for(int row=0; row<this.board.length; row++) {
        for (int col=0; col<this.board[0].length; col++){
               if(board[row][col].equals(Constants.BLANK) && isLegal(row, col, color)){
                    return false;
               }
           }
       }
       whoseMove = !whoseMove;
       return true;
    }
   
    private String currentColor(){
        return (whoseMove) ? Constants.WHITE : Constants.BLACK;
    }
    
    private boolean inBounds(int row, int col, int rd, int cd){
        boolean reted = false;
        if((row+rd > -1) && (row+rd < 8) && (col+cd > -1) && (col+cd < 8)){reted = true;}
        return reted;
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
//        legalLoop();
        this.mvcMessaging.notify("boardChange", this.board);
    }
    
    // playerMove message handler
    if (messageName.equals("playerMove")) {
      // Get the position string and convert to row and col
      String position = (String)messagePayload;
      Integer row = Integer.valueOf(position.substring(0,1));
      Integer col = Integer.valueOf(position.substring(1,2));
      
      // If square is blank...
      if (this.board[row][col].equals(Constants.BLANK) && !noLegal(currentColor())) {
        // ... then set @ or O depending on whose move it is
        if (this.whoseMove && isLegal(row, col, Constants.WHITE)) {
          this.mvcMessaging.notify("gameOver", Constants.BLACK + "MOVE");
          this.board[row][col] = Constants.WHITE;
          whoseMove = !this.whoseMove;
          flipPieces(0, Constants.WHITE, Constants.BLACK, row, col);
        }
        else if (!this.whoseMove && isLegal(row, col, Constants.BLACK)) {
          this.mvcMessaging.notify("gameOver", Constants.WHITE + "MOVE");
          this.board[row][col] = Constants.BLACK;
          whoseMove = !this.whoseMove;
          flipPieces(0, Constants.BLACK, Constants.WHITE, row, col);
        }
        else{
            this.mvcMessaging.notify("gameOver", "INVALID MOVE");
        }
        
        countPieces();
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
