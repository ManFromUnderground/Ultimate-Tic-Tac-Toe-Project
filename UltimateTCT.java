
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Map;
import java.util.Random;
import javax.swing.BorderFactory;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * A window program that plays a game of ultimate Tic tac toe. 
 * The program itself will enforce the rules of the game.
 * Allows the player to both reset and undo a move.
 * Players can also have the ability to display various stats
 * such as Win percentage, the total number of games, and
 * avergae moves per win.
 * @author Trey Castle
 *
 */

public class UltimateTCT extends JFrame {
    // Borders
    Border redline = BorderFactory.createLineBorder(Color.red);
    Border blackline = BorderFactory.createLineBorder(Color.black);
    Border yellowline = BorderFactory.createLineBorder(Color.yellow, 3);
    Border blueline = BorderFactory.createLineBorder(Color.blue);
    
    // table and model
    JTable table;
    DefaultTableModel model;
    
    // buttons display
    JButton player1Btn = new JButton("Player O");
    JButton player2Btn = new JButton("Player X");
    JButton restartBtn = new JButton("Restart");
    JButton undoBtn = new JButton("Undo");
    JButton playingStatsBtn = new JButton("Playing Stats");
    
    // tracking button history
    JButton lastButtonClicked;
    int buttonPositionOfLastClick;
    int boardWithButtonOfLastClick;
    public Map<Integer, Integer> boardAndButtonPosition;
    
    // labels
    JLabel nowPlayingLbl = new JLabel("Now Playing:");
    JLabel manageGameLbl = new JLabel("Manage Game:");
    JLabel winPercentageLbl = new JLabel("Win %:");
    JLabel totalGamesLbl = new JLabel("Total # of games:");
    JLabel averageMovesLbl = new JLabel("Average # of moves per win:");
    
    // panels
    // UI structure
    JPanel center;
    JPanel top;
    JPanel bottom;
    // Boxes at bottom
    JPanel winPercentagePanel = new JPanel();
    JPanel totalGamesPanel = new JPanel();
    JPanel averageMovesPanel = new JPanel();
    
    // Boards
    public static Board[] boards;
      
    // Players
    public static Player player1;
    public static Player player2;
    
    // vars for panels at bottom
    public int totalGames;
   
    public UltimateTCT() {
        createPlayers();
        createTable();
        decideOrder();
    }
    
    // UI
    /**
     * creates and formats the table
     */
    public void createTable() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // create table
        model = new DefaultTableModel();
        table = new JTable(model);
        JTableHeader header = table.getTableHeader();
       
        // top
        top();
        // center
        createBoards();
        // bottom
        bottom();
        
        // Configure window
        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        setSize(1400,700);
        setVisible(true);
    }
    
    /**
     * handles the top panel
     */
    public void top() {
        // create top
        top = new JPanel();
        top.setLayout(new FlowLayout());
        
        // LHS of top
        top.add(nowPlayingLbl);
        top.add(player1Btn);
        top.add(player2Btn);
        JLabel space = new JLabel("                       ");
        top.add(space);
        
        // Highlight active player in yellow
        if(player1.IsActive()) {
            player1Btn.setBorder(yellowline);
            player2Btn.setBorder(blackline);
        } else {
            player1Btn.setBorder(blackline);
            player2Btn.setBorder(yellowline);
        }
        
        // RHS of top
        top.add(manageGameLbl);
        top.add(restartBtn);
        top.add(undoBtn);
        
        // Listeners for buttons
        restartListener();
        undoListener();
    }
    
    /**
     * handles the greater board itself and adds the smaller boards
     */
    public void createBoards() {
        totalGames = 0;
        center = new JPanel();
        center.setLayout(new GridLayout(0,3));
        
        // create boards and populate each board with 9 buttons
        boards = new Board[9];
        for(int i = 0; i < boards.length; i++) {
            boards[i] = new Board();
            boards[i].setBoardNumber(i);
            boards[i].setPlayers(player1, player2);
            boards[i].setButtons(player1Btn, player2Btn);
            boards[i].setTotalGames(totalGames);
            }

        // add boards to center panel
        for(Board currentBoard : boards) {
            center.add(currentBoard.getBoard());
            currentBoard.setBoards(boards);
        }
        
//        player1.setTotalGames(this.totalGames);
//        player2.setTotalGames(this.totalGames);
    }
    
    /**
     * handles the bottom panel
     */
    public void bottom() {
        bottom = new JPanel();
        bottom.setLayout(new FlowLayout());
        
        // Set dimensions for boxes at bottom (win%, total games, avg moves/win)
        configurePanels();
        
        bottom.add(playingStatsBtn);
        bottom.add(winPercentageLbl);
        bottom.add(winPercentagePanel);
        
        bottom.add(totalGamesLbl);
        bottom.add(totalGamesPanel);
        
        bottom.add(averageMovesLbl);
        bottom.add(averageMovesPanel);
        
        // listener for playing stats btn
        playingStatsListener();
    }
    
    /**
     * configures the panels in the bottom panel
     */
    public void configurePanels() {
        winPercentagePanel.setPreferredSize(new Dimension(300, 30));
        winPercentagePanel.setBorder(blackline);
        
        totalGamesPanel.setPreferredSize(new Dimension(120, 30));
        totalGamesPanel.setBorder(blackline);
        
        averageMovesPanel.setPreferredSize(new Dimension(300, 30));
        averageMovesPanel.setBorder(blackline);   
    }
    

    /**
     * sets up the players
     */
    public void createPlayers() {
        player1 =  new Player();
        player1.setLetter("O");
        player2 = new Player();
        player2.setLetter("X");
    }
    
    /** 
     * Decide who starts off the game
     */
    public void decideOrder() {
        int r = new Random().nextInt(2);
        
        if(r == 0) {
            player1.setIsActive(true);
            player2.setIsActive(false);
            player1Btn.setBorder(yellowline);
            player2Btn.setBorder(blackline);
            
        } else {
            player1.setIsActive(false);
            player2.setIsActive(true);
            player1Btn.setBorder(blackline);
            player2Btn.setBorder(yellowline);
        }
    }
    
    // Listeners
    /**
     * sets up the listener for undo
     */
    public void undoListener() {
        undoBtn.addActionListener(e-> {
            // Undo does not function if game has been won. User must click on Restart to start new game
            if(new Board().ultimateGameWon()) { return; }
            int size;
            // if player 1 active, player 2 made last move
            if(player1.IsActive()) {
                
                // if size = 0, nothing to undo
                size = player2.getLastButtonClicked().size();
                
                if(size > 0) {
                // Erase last button's text
                lastButtonClicked = player2.getLastButtonClicked().get(size-1);
                lastButtonClicked.setText("");
                
                //buttonPositionOfLastClick = player2.getPositionOfLastButtonClicked().get(size-1);
                
                // get board number that contained the button
                // reduce # of buttons clicked for that board by one
                boardWithButtonOfLastClick = player2.getBoardOfLastButtonClicked().get(size-1);
                boards[boardWithButtonOfLastClick].setCount(boards[boardWithButtonOfLastClick].getCount()-1);
                
                // if player undoes winning move, set boardWon flag to false and update players list of boards won
                if(boards[boardWithButtonOfLastClick].getBoardWon()) {
                    boards[boardWithButtonOfLastClick].setBoardWon(false);
                    player2.updateBoardsWon(boardWithButtonOfLastClick, 1, 0);
                }
                
                // update players list that tracks move history and decrease their total moves 
                player2.getLastButtonClicked().remove(size-1);
                player2.getPositionOfLastButtonClicked().remove(size-1);
                player2.getBoardOfLastButtonClicked().remove(size-1);
                player2.decrementMoves();
                
                // if last button was clicked then open all boards
                // handles edge case for first button clicked in game
                if(player2.getLastButtonClicked().isEmpty() && player1.getLastButtonClicked().isEmpty()) {
                    new Board().openAllBoards();
                } else {
                new Board().restrictOtherBoards(boardWithButtonOfLastClick);
                }
                
                // update active status
                player1.setIsActive(false);
                player2.setIsActive(true);
                player1Btn.setBorder(blackline);
                player2Btn.setBorder(yellowline);
                } 
                else {} // end if
                
            } else {
                size = player1.getLastButtonClicked().size();
                
                if(size > 0) {
                // Erase last button's text
                lastButtonClicked = player1.getLastButtonClicked().get(size-1);
                lastButtonClicked.setText("");
                
                
                //buttonPositionOfLastClick = player1.getPositionOfLastButtonClicked().get(size-1);
                
                // get board number that contained the button
                // reduce # of buttons clicked for that board by one
                boardWithButtonOfLastClick = player1.getBoardOfLastButtonClicked().get(size-1);
                boards[boardWithButtonOfLastClick].setCount(boards[boardWithButtonOfLastClick].getCount()-1);
                
                // if player undoes winning move, set boardWon flag to false and update players list of boards won
                if(boards[boardWithButtonOfLastClick].getBoardWon()) {
                    boards[boardWithButtonOfLastClick].setBoardWon(false);
                    player1.updateBoardsWon(boardWithButtonOfLastClick, 1, 0);
                }
                
                // update players list that tracks move history and decrease their total moves
                player1.getLastButtonClicked().remove(size-1);
                player1.getPositionOfLastButtonClicked().remove(size-1);
                player1.getBoardOfLastButtonClicked().remove(size-1);
                player1.decrementMoves();
                
                // if last button was clicked then open all boards
                // handles edge case for first button clicked in game
                if(player1.getLastButtonClicked().isEmpty() && player2.getLastButtonClicked().isEmpty()) {
                    new Board().openAllBoards();
                } else {
                new Board().restrictOtherBoards(boardWithButtonOfLastClick);
                }
                
                // update active status
                player1.setIsActive(true);
                player2.setIsActive(false);
                player1Btn.setBorder(yellowline);
                player2Btn.setBorder(blackline);
                } 
                else {} // end if
            }
            
        });
    }
    
    /**
     * sets up the listener for restart
     */
    public void restartListener() {
       
        restartBtn.addActionListener(e-> {
            for(Board currentBoard : boards) {
                currentBoard.setBoardLocked(false);
                currentBoard.setBoardWon(false);
                currentBoard.setCount(0);
                for(JButton currentButton : currentBoard.getButtonList()) {
                    currentButton.setText("");
                }
            }
            
            // Clear all button and board history
            player1.getLastButtonClicked().clear();
            player1.getPositionOfLastButtonClicked().clear();
            player1.getBoardOfLastButtonClicked().clear();
            
            player2.getLastButtonClicked().clear();
            player2.getPositionOfLastButtonClicked().clear();
            player2.getBoardOfLastButtonClicked().clear();

            // Clear and reset players list that tracks which boards they won
            player1.resetBoardsWonList();
            player2.resetBoardsWonList();
            
            // Randomly choose who starts new game
            decideOrder();
        });
    }
    
    /**
     * sets up listener for stats button
     */
    public void playingStatsListener() {
        JLabel winLabel = new JLabel();
        JLabel totalLabel = new JLabel();
        JLabel avgMoves = new JLabel();
            playingStatsBtn.addActionListener(e-> {
            
            winLabel.setText("Player 1: " + player1.getWinningPercentage() + "      Player 2: " + player2.getWinningPercentage());
            winPercentagePanel.add(winLabel);
            winPercentagePanel.validate();
            
            totalLabel.setText(Integer.toString(player1.getTotalGames()));
            totalGamesPanel.add(totalLabel);
            totalGamesPanel.validate();
            
            avgMoves.setText("Player 1: " + player1.getAverageNumberOfMovesPerWin() + "      Player 2: " + player2.getAverageNumberOfMovesPerWin());
            averageMovesPanel.add(avgMoves);
            averageMovesPanel.validate();
        });
    }
    
 
    
    
}


