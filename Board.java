
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * A class that handles and represents the game boards for the ultimate TCT game
 * @author Trey Castle
 *
 */

public class Board {
    // Borders
    Border redline = BorderFactory.createLineBorder(Color.red);
    Border blackline = BorderFactory.createLineBorder(Color.black);
    Border yellowline = BorderFactory.createLineBorder(Color.yellow, 3);
    Border blueline = BorderFactory.createLineBorder(Color.blue);
    
    // Misc
    public JPanel board;
    public int boardNumber;
    public int buttonPosition;
    public int count = 0;
    public String letter;
    public int totalGames;
    
    // flags
    public boolean lockBoard;
    public boolean boardWon = false;
    public boolean firstMove;
    
    // Players
    public Player player1;
    public Player player2;
    public JButton player1Btn;
    public JButton player2Btn;
    
    // Buttons
    public List<JButton> buttonList = new ArrayList<>();
    public JButton lastButtonClicked;
    
    // Board instances
    public static Board[] boards;
    
    public Board() {
        // Create board instance
        board = new JPanel();
        board.setBorder(redline);
        board.setLayout(new GridLayout(0,3));
        
        // Board always created as unlocked (playable)
        this.lockBoard = false;
        
        // Add buttons to board, method contains button listener
        populateButtons();
    }
    
    public void setTotalGames(int v) {
        totalGames = v;
    }
    
    // boards array created in TicTacToe
    // Needed to update all boards' status (locked and/or won)
    public void setBoards(Board[] boards) {
        this.boards = boards;
    }
    
    // Board and TicTacToe have visibility to same Player objects
    public void setPlayers(Player p1, Player p2) {
        this.player1 = p1;
        this.player2 = p2;
    }
    
    // Board and TicTacToe have visibility to same instance of Player's buttons
    // Need to toggle player's turn active status
    public void setButtons(JButton player1Btn, JButton player2Btn) {
        this.player1Btn = player1Btn;
        this.player2Btn = player2Btn;
    }
    
    /*
     *  Populate board with buttons and define logic for each button's listener
     */
    public void populateButtons() {
        for(int i = 0; i < 9; i++) {
            JButton button = new JButton();
            board.add(button);
            buttonList.add(button);
            // button listener
            button.addActionListener(e-> {
                buttonPosition = buttonList.indexOf(button);
                if(!this.lockBoard && !this.boardWon && button.getText().isEmpty()) {
                    if(player1.IsActive()) {
                        letter = "O";
                        button.setForeground(Color.green);

                        player1.setIsActive(false);
                        player1Btn.setBorder(blackline);
                        player1.incrementMoves();
                        
                        player2.setIsActive(true);
                        player2Btn.setBorder(yellowline);
                        
                        player1.setPositionOfLastButtonClicked(buttonPosition);
                        player1.setLastButtonClicked(button);
                        player1.setBoardOfLastButtonClicked(this.boardNumber);
                    } else {
                        letter = "X";
                        button.setForeground(Color.red);
                        
                        player2.setIsActive(false);
                        player2Btn.setBorder(blackline);
                        player2.incrementMoves();
                        
                        player1.setIsActive(true);
                        player1Btn.setBorder(yellowline);
                        
                        player2.setPositionOfLastButtonClicked(buttonPosition);
                        player2.setLastButtonClicked(button);
                        player2.setBoardOfLastButtonClicked(this.boardNumber);
                    }
                    
                    // set button text properties
                    button.setText(letter);
                    
                    // if game won, lock board
                    if(gameWon()) {
                        this.boardWon = true;
                        this.lockBoard = true;
                    }
                    
                    // restrict other boards
                    restrictOtherBoards(buttonPosition);

                    if(ultimateGameWon()) {
                        String winner = this.letter.equalsIgnoreCase("O") ? "Player 1" : "Player 2";
                        System.out.println(winner + " won the game");
                        player1.incrementTotalGames();
                        player2.incrementTotalGames();
                        if(this.letter.equalsIgnoreCase("O")) {
                            player1.incrementGamesWon();
                        } else {
                            player2.incrementGamesWon();
                        } 
                    }
                } else {
                    
                }
                });   
        }
    }
    
    /*
     *  Locks other boards if player has won board or if board is not correct nth board
     */
    public void restrictOtherBoards(int btn) {
        // restrict other boards
        buttonPosition = btn;
        boolean boardWon = boards[buttonPosition].getBoardWon();
        
        // if player wins board, all boards without winner determined are activated
        if(boardWon) {
            for(Board cb : boards) {
                if(!cb.getBoardWon()) {
                    cb.setBoardLocked(false);
                }
            }
        } 
        // if player move does not result in win, lock all boards and then unlock nth board (depending on nth button clicked)
        else {
            for(Board cb : boards) {
                cb.setBoardLocked(true);
            }
            boards[buttonPosition].setBoardLocked(false);
        }
    }
    
    /*
     *  Open all boards (needed for undo button)
     *  Differes from restart button logic since only the board is unlocked, winner is not cleared
     */
    public void openAllBoards() {
        for(Board cb : boards) {
            cb.setBoardLocked(false);
        }
    }
    
    /*
     *  Checks to see if most recent move results in win of small board
     */
    public boolean gameWon() {
        // first row
        if(!buttonList.get(0).getText().isEmpty() && buttonList.get(0).getText().equals(buttonList.get(1).getText())
                && buttonList.get(0).getText().equals(buttonList.get(2).getText())) {
            String value = buttonList.get(0).getText();
            if(value.equalsIgnoreCase("O")) {
                player1.updateBoardsWon(this.boardNumber, 0, 1);
            } else {
                player2.updateBoardsWon(this.boardNumber, 0, 1);
            }
            return true;
        }
        
        // second row
        if(!buttonList.get(3).getText().isEmpty() && buttonList.get(3).getText().equals(buttonList.get(4).getText())
                && buttonList.get(3).getText().equals(buttonList.get(5).getText())) {
            String value = buttonList.get(0).getText();
            if(value.equalsIgnoreCase("O")) {
                player1.updateBoardsWon(this.boardNumber, 0, 1);
            } else {
                player2.updateBoardsWon(this.boardNumber, 0, 1);
            }
            return true;
        }
        
        // third row
        if(!buttonList.get(6).getText().isEmpty() && buttonList.get(6).getText().equals(buttonList.get(7).getText())
                && buttonList.get(6).getText().equals(buttonList.get(8).getText())) {
            String value = buttonList.get(0).getText();
            if(value.equalsIgnoreCase("O")) {
                player1.updateBoardsWon(this.boardNumber, 0, 1);
            } else {
                player2.updateBoardsWon(this.boardNumber, 0, 1);
            }
            return true;
        }
        
        // first column
        if(!buttonList.get(0).getText().isEmpty() && buttonList.get(0).getText().equals(buttonList.get(3).getText())
                && buttonList.get(0).getText().equals(buttonList.get(6).getText())) {
            String value = buttonList.get(0).getText();
            if(value.equalsIgnoreCase("O")) {
                player1.updateBoardsWon(this.boardNumber, 0, 1);
            } else {
                player2.updateBoardsWon(this.boardNumber, 0, 1);
            }
            return true;
        }
        
        // second column
        if(!buttonList.get(1).getText().isEmpty() && buttonList.get(1).getText().equals(buttonList.get(4).getText())
                && buttonList.get(1).getText().equals(buttonList.get(7).getText())) {
            String value = buttonList.get(0).getText();
            if(value.equalsIgnoreCase("O")) {
                player1.updateBoardsWon(this.boardNumber, 0, 1);
            } else {
                player2.updateBoardsWon(this.boardNumber, 0, 1);
            }
            return true;
        }
        
        // third column
        if(!buttonList.get(2).getText().isEmpty() && buttonList.get(2).getText().equals(buttonList.get(5).getText())
                && buttonList.get(2).getText().equals(buttonList.get(8).getText())) {
            String value = buttonList.get(0).getText();
            if(value.equalsIgnoreCase("O")) {
                player1.updateBoardsWon(this.boardNumber, 0, 1);
            } else {
                player2.updateBoardsWon(this.boardNumber, 0, 1);
            }
            return true;
        }
        
        // diagonal 0 -> 8
        if(!buttonList.get(0).getText().isEmpty() && buttonList.get(0).getText().equals(buttonList.get(4).getText())
                && buttonList.get(0).getText().equals(buttonList.get(8).getText())) {
            String value = buttonList.get(0).getText();
            if(value.equalsIgnoreCase("O")) {
                player1.updateBoardsWon(this.boardNumber, 0, 1);
            } else {
                player2.updateBoardsWon(this.boardNumber, 0, 1);
            }
            return true;
        }
        
        // diagonal 2 -> 6
        if(!buttonList.get(2).getText().isEmpty() && buttonList.get(2).getText().equals(buttonList.get(4).getText())
                && buttonList.get(2).getText().equals(buttonList.get(6).getText())) {
            String value = buttonList.get(0).getText();
            if(value.equalsIgnoreCase("O")) {
                player1.updateBoardsWon(this.boardNumber, 0, 1);
            } else {
                player2.updateBoardsWon(this.boardNumber, 0, 1);
            }
            return true;
        }  
        return false;
    }
    
    /*
     *  Checks to see if most recent move results in overall win
     */
    public boolean ultimateGameWon() {
        // first row
        if(boards[0].getBoardWon() && boards[1].getBoardWon() && boards[2].getBoardWon()) {
            return true;
        }
        
        // second row
        if(boards[3].getBoardWon() && boards[4].getBoardWon() && boards[5].getBoardWon()) {
            return true;
        }
        
        // third row
        if(boards[6].getBoardWon() && boards[7].getBoardWon() && boards[8].getBoardWon()) {
            return true;
        }
        
        // first column
        if(boards[0].getBoardWon() && boards[3].getBoardWon() && boards[6].getBoardWon()) {

            return true;
        }
        
        // second column
        if(boards[1].getBoardWon() && boards[4].getBoardWon() && boards[7].getBoardWon()) {

            return true;
        }
        
        // third column
        if(boards[2].getBoardWon() && boards[5].getBoardWon() && boards[8].getBoardWon()) {

            return true;
        }
        
        // diagonal 0 -> 8
        if(boards[0].getBoardWon() && boards[4].getBoardWon() && boards[8].getBoardWon()) {

            return true;
        }
        
        // diagonal 2 -> 6
        if(boards[2].getBoardWon() && boards[4].getBoardWon() && boards[2].getBoardWon()) {

            return true;
        }  
        return false;
    }
    
    /*
     *  Methods related to the board (won, locked)
     */
    public JPanel getBoard() {
        return  board;
    }
    
    public void setBoardLocked(boolean v) {
        this.lockBoard = v;
    }
    
    public boolean getBoardLocked() {
        return lockBoard;
    }
    
    public void setBoardWon(boolean v) {
        this.boardWon = v;
    }
    
    public boolean getBoardWon() {
        return boardWon;
    }
    
    public void setBoardNumber(int n) {
        this.boardNumber = n;
    }
    
    /*
     *  Methods related to last button clicked (last button clicked, position of last button clicked)
     */
    public List<JButton> getButtonList() {
        return buttonList;
    }
    
    public int getButtonPostion() {
        return buttonPosition;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    public int getCount() {
        return this.count;
    }
    
}
