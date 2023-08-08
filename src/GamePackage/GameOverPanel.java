package GamePackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameOverPanel extends JPanel {

	 	private static final int PANEL_WIDTH = 300;
	    private static final int PANEL_HEIGHT = 150;

	    private int score;
	    private GameWindow gameWindow;
	    private boolean hasPlayerWon;
	    
	    public JLabel scoreLabel = new JLabel();

	    public GameOverPanel(GameWindow gameWindow, int score, boolean hasPlayerWon) {
	        this.gameWindow = gameWindow;
	        this.score = score;
	        this.hasPlayerWon = hasPlayerWon;
	        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
	        setLayout(new BorderLayout());

	        String gameOverMessage = hasPlayerWon ? "Congratulations! You won the game!" : "Game Over!";
	        JLabel gameOverLabel = new JLabel(gameOverMessage);
	        gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
	        gameOverLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));

	        JLabel finalScoreLabel = new JLabel("Final Score: " + score);
	        finalScoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
	        finalScoreLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));

	        JButton playAgainButton = new JButton("Play Again");
	        playAgainButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                gameWindow.resetGame();
	            }
	        });
	        JPanel buttonPanel = new JPanel();
	        buttonPanel.add(playAgainButton);

	        add(gameOverLabel, BorderLayout.NORTH);
	        add(finalScoreLabel, BorderLayout.CENTER);
	        add(buttonPanel, BorderLayout.SOUTH);
	        
	        scoreLabel.setText("Final Score: " + score);
	    }
	    public void updateScore(int newScore) {
	        score = newScore;
	        scoreLabel.setText("Final Score: " + score);
	    }
	}