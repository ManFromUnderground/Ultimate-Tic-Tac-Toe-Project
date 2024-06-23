
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;

/**
 * A class that represents a player in the Ultimate TCT game
 * @author Trey Castle
 */
public class Player {
    public Player player; 
    public String letter;
    public boolean isActive;
    public boolean mustRestart;
    
    // stats
    public int gamesWon;
    public int totalGames;
    public int winningPercentage;
    public int moves;
    public double averageNumberOfMoves;
    public List<Integer> numberOfMovesUntilWin = new ArrayList<>();
    DecimalFormat pct;
    DecimalFormat format;
    
    // button history info
    public List<JButton> lastButtonClickedList = new ArrayList<>();
    public List<Integer> positionOfLastButtonClickedList = new ArrayList<>();
    public List<Integer> boardOfLastButtonClickedList = new ArrayList<>();
    public int positionOfLastButtonClicked = -1;
    
    // board history
    public Map<Integer, Integer> boardsWon = new HashMap<>();
    
    public Player() {
        this.gamesWon = 0;
        this.moves = 0;
        this.averageNumberOfMoves = 0;
        this.totalGames = 0;
        
        // key will be board position
        // value will be 0 for boards not won, 1 for boards won
        for(int i = 0; i < 9; i++) {
            this.boardsWon.put(i, 0);
        }
    }
    
    /* 0 for not won, 1 for won
     * called in method updateUltimateGame() in Board class to update with a win
     * called in undo listener in UltimateTCT class to reverse a win (if a user undo's a winning move)
     */
    public void updateBoardsWon(int board, int originalValue, int newValue) {
        this.boardsWon.replace(board, originalValue, newValue);
    }
    public Map<Integer, Integer> getBoardsWon() {
        return this.boardsWon;
    }
    public void resetBoardsWonList() {
        this.boardsWon.clear();
        for(int i = 0; i < 9; i++) {
            this.boardsWon.put(i, 0);
        } 
    }
    
    
    public void setLetter(String n) {
        this.letter = n;
    }
    
    
    /*
     *  Methods to keep track of players turns
     */
    public void setIsActive(boolean status) {
        this.isActive = status;
    }
    public boolean IsActive() {
        return this.isActive;
    }
    
    
    /*
     * Methods to keep track of # of moves, wins, and total games played
     */
    public void incrementMoves() {
        this.moves++;
    }
    public void decrementMoves() {
        this.moves--;
    }
    public void incrementGamesWon() {
        this.gamesWon++;
        String winner = this.letter.equalsIgnoreCase("X") ? "Player 2" : "Player 1";
        System.out.println("Games " + winner + " has won: " + this.gamesWon);
        System.out.println("Total games played: " + this.totalGames);
        
        setAverageMovesArray();  
    } 
    public void incrementTotalGames() {
        this.totalGames++;
    } 
    public void setAverageMovesArray() {
        this.numberOfMovesUntilWin.add(this.moves);
        this.moves = 0;
    }
    public String getAverageNumberOfMovesPerWin() {
        format = new DecimalFormat("##.##");
        int total = 0;
        int counter = 0;
        
        for(Integer currentInt : this.numberOfMovesUntilWin) {
            total = total + currentInt;
            counter++;
        }
        
        if(total != 0) {
        double avg = (double)total/counter;
        BigDecimal avgRounded = new BigDecimal(avg);
        avgRounded = avgRounded.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        return avgRounded.toString();
        }
        return "no wins";
    } 
    public void setTotalGames(int v) {
        this.totalGames = v;
    } 
    public int getTotalGames() {
        return this.totalGames;
    }  
    public String getWinningPercentage() {
        pct = new DecimalFormat("##.##%");
        if(this.totalGames != 0) {
        double value = (double)this.gamesWon/this.totalGames;
        BigDecimal valueRounded = new BigDecimal(value);
        valueRounded = valueRounded.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        return pct.format(valueRounded).toString();
        }
        
        return "0%";
    }
    

    /*
     *  Methods for tracking game history
     */
    public void setLastButtonClicked(JButton button) {
        this.lastButtonClickedList.add(button);
    }
    public List<JButton> getLastButtonClicked() {
        return lastButtonClickedList;
    }
    public void setPositionOfLastButtonClicked(int p) {
        this.positionOfLastButtonClickedList.add(p);
    }   
    public List<Integer> getPositionOfLastButtonClicked() {
        return positionOfLastButtonClickedList;
    }
    public void setBoardOfLastButtonClicked(int b) {
        this.boardOfLastButtonClickedList.add(b);
    }
    public List<Integer> getBoardOfLastButtonClicked() {
        return boardOfLastButtonClickedList;
    }
           
}
