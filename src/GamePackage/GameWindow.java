package GamePackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GameWindow extends JFrame {

    // Constants for labyrinth cells
    private static final char EMPTY = ' ';
    private static final char WALL = '#';
    private static final char PLAYER = 'P';
    private static final char GOLD_COIN = '$';
    private static final char EXIT = 'E';
    private static final char POWER_UP = 'U';

    private char[][] labyrinth;
    private int labyrinthWidth;
    private int labyrinthHeight;

    private int playerRow;
    private int playerCol;
    private int exitRow;
    private int exitCol;

    private GamePanel gamePanel;
    private Snake snake;
    public int score;
    private int numGoldCoins;
    private int powerUpDuration;

    private static final int DEFAULT_PLAYER_SPEED = 1;
    private int playerSpeed = DEFAULT_PLAYER_SPEED;
    
    private boolean isGameOver = false;
    private GameOverPanel gameOverPanel;
    private HighScorePanel highScorePanel;
    public boolean hasPlayerWon = false;

    private ArrayList<Integer> highScores = new ArrayList<>();
    private static final String HIGH_SCORES_FILE = "highscores.txt";
    private JLabel scoreLabel;
   
    public GameWindow() {
        initUI();
        loadHighScores(); 
        generateLabyrinth();
        placeGoldCoins();
        placeExit();
        placePowerUps();
        initializeSnake();
        startGameLoop();
    }

    private void initUI() {
        setTitle("Game");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
     // Create the HighScorePanel object
        highScorePanel = new HighScorePanel();

        // Add the HighScorePanel to the layout
        add(highScorePanel, BorderLayout.EAST);
        
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                handlePlayerMovement(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });

        updateStatusBar();

        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);

        setFocusable(true);
        setVisible(true);
        
        
    }

    
    
    private class GamePanel extends JPanel {
        private static final int CELL_SIZE = 30;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawGame(g);
        }

        private void drawGame(Graphics g) {
            for (int row = 0; row < labyrinthHeight; row++) {
                for (int col = 0; col < labyrinthWidth; col++) {
                    int x = col * CELL_SIZE;
                    int y = row * CELL_SIZE;

                    switch (labyrinth[row][col]) {
                        case EMPTY:
                            g.setColor(Color.WHITE);
                            break;
                        case WALL:
                            g.setColor(Color.BLACK);
                            break;
                        case PLAYER:
                            g.setColor(Color.GREEN);
                            break;
                        case GOLD_COIN:
                            g.setColor(Color.YELLOW);
                            break;
                        case EXIT:
                            g.setColor(Color.BLUE);
                            break;
                        case POWER_UP:
                            g.setColor(Color.RED);
                            break;
                    }

                    g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                }
            }

            g.setColor(Color.ORANGE);
            for (int i = 0; i < snake.length; i++) {
                int x = snake.bodyCol[i] * CELL_SIZE;
                int y = snake.bodyRow[i] * CELL_SIZE;
                g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    private void generateLabyrinth() {
        labyrinthWidth = 20;
        labyrinthHeight = 15;

        labyrinth = new char[labyrinthHeight][labyrinthWidth];
        for (int row = 0; row < labyrinthHeight; row++) {
            for (int col = 0; col < labyrinthWidth; col++) {
                labyrinth[row][col] = WALL;
            }
        }

        generateLabyrinthLayout(1, 1);

        labyrinth[1][1] = PLAYER;
        labyrinth[labyrinthHeight - 2][labyrinthWidth - 2] = EXIT;

        playerRow = 1;
        playerCol = 1;

        exitRow = labyrinthHeight - 2;
        exitCol = labyrinthWidth - 2;
    }

    private void generateLabyrinthLayout(int row, int col) {
        labyrinth[row][col] = EMPTY;

        int[] directions = {0, 1, 2, 3};
        shuffleArray(directions);

        for (int direction : directions) {
            int newRow = row + dr[direction];
            int newCol = col + dc[direction];

            if (isInBounds(newRow, newCol) && labyrinth[newRow][newCol] == WALL) {
                int wallCount = countWallsAround(newRow, newCol);

                if (wallCount >= 3) {
                    labyrinth[newRow][newCol] = EMPTY;
                    generateLabyrinthLayout(newRow, newCol);
                }
            }
        }
    }

    private void shuffleArray(int[] arr) {
        Random rnd = new Random();
        for (int i = arr.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int temp = arr[index];
            arr[index] = arr[i];
            arr[i] = temp;
        }
    }

    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < labyrinthHeight && col >= 0 && col < labyrinthWidth;
    }

    private int countWallsAround(int row, int col) {
        int wallCount = 0;
        for (int i = 0; i < dr.length; i++) {
            int newRow = row + dr[i];
            int newCol = col + dc[i];
            if (isInBounds(newRow, newCol) && labyrinth[newRow][newCol] == WALL) {
                wallCount++;
            }
        }
        return wallCount;
    }

    private final int[] dr = {-1, 1, 0, 0};
    private final int[] dc = {0, 0, -1, 1};

    private void handlePlayerMovement(int keyCode) {
        int newRow = playerRow;
        int newCol = playerCol;
        
        if (hasPlayerWon ||isGameOver) {
            return; // If the game is over, do not allow the player to move
        }

        switch (keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                newRow -= 1;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                newRow += 1;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                newCol -= 1;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                newCol += 1;
                break;
            default:
                return;
        }

        if (isInBounds(newRow, newCol) && labyrinth[newRow][newCol] != WALL) {
            if (labyrinth[newRow][newCol] == GOLD_COIN) {
                labyrinth[newRow][newCol] = EMPTY;
                numGoldCoins++;
                score += 10;
                System.out.println("Collected a gold coin! Score: " + score);
            }

            if (newRow == exitRow && newCol == exitCol) {
                System.out.println("Level completed!");
            }

            labyrinth[playerRow][playerCol] = EMPTY;
            labyrinth[newRow][newCol] = PLAYER;

            playerRow = newRow;
            playerCol = newCol;

            gamePanel.repaint();
        }
    }

    private void placeGoldCoins() {
        int numGoldCoins = 5;
        int placedCoins = 0;

        Random random = new Random();

        while (placedCoins < numGoldCoins) {
            int row = random.nextInt(labyrinthHeight);
            int col = random.nextInt(labyrinthWidth);

            if (labyrinth[row][col] == EMPTY) {
                labyrinth[row][col] = GOLD_COIN;
                placedCoins++;
            }
        }
    }

    private void placeExit() {
        Random random = new Random();
        int row, col;
        do {
            row = random.nextInt(labyrinthHeight);
            col = random.nextInt(labyrinthWidth);
        } while (labyrinth[row][col] != EMPTY);

        labyrinth[row][col] = EXIT;
        exitRow = row;
        exitCol = col;
    }

    private void placePowerUps() {
        int numPowerUps = 3;
        int placedPowerUps = 0;

        Random random = new Random();

        while (placedPowerUps < numPowerUps) {
            int row = random.nextInt(labyrinthHeight);
            int col = random.nextInt(labyrinthWidth);

            if (labyrinth[row][col] == EMPTY) {
                labyrinth[row][col] = POWER_UP;
                placedPowerUps++;
            }
        }
    }

    private void initializeSnake() {
        Random random = new Random();
        int startRow, startCol;
        do {
            startRow = random.nextInt(labyrinthHeight);
            startCol = random.nextInt(labyrinthWidth);
        } while (labyrinth[startRow][startCol] != EMPTY);

        snake = new Snake(3, startRow, startCol);
    }

    private void startGameLoop() {
        Timer timer = new Timer(800, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gameLoop();
            }
        });
        timer.start();
    }

    private void gameLoop() {
    	if (!hasPlayerWon) {
            snake.moveTowardsPlayer(playerRow, playerCol);
            checkCollisions();
            handlePowerUpTimer();
        }
        checkCollisions();
        handlePowerUpTimer();
        gamePanel.repaint();
        highScorePanel.updateHighScores(highScores);
    }
    
    private void checkCollisions() {
    	if (snake.collidesWithPlayer(playerRow, playerCol)) {
            System.out.println("Game Over!");
            isGameOver = true;
            showGameOverScreen(); // Show the GameOverPanel
            // Handle game over scenario
        }
    	if (playerRow == exitRow && playerCol == exitCol) {
            System.out.println("Congratulations! You won the game!");
            hasPlayerWon = true;
            showGameOverScreen(); // Show the GameOverPanel with win message
        }
    }

    private void handlePowerUpTimer() {
        if (powerUpDuration > 0) {
            powerUpDuration--;
            if (powerUpDuration == 0) {
                playerSpeed = DEFAULT_PLAYER_SPEED;
                System.out.println("Power-up effect expired.");
            }
        }
    }
    
   
    public void resetGame() {
    	getContentPane().remove(gameOverPanel); // Remove the GameOverPanel
    	hasPlayerWon = false;
        if (highScorePanel != null) {
            getContentPane().remove(highScorePanel);
            highScorePanel = null;
        }
        highScorePanel = new HighScorePanel();
        add(highScorePanel, BorderLayout.EAST);
        
        isGameOver = false;
        score = 0;
        numGoldCoins = 0;
        powerUpDuration = 0;

        generateLabyrinth();
        placeGoldCoins();
        placeExit();
        placePowerUps();
        initializeSnake();

        // Remove the current gamePanel
        remove(gamePanel);

        // Create a new GamePanel object
        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER); // Add the new GamePanel

        // Update the status bar with the legend after resetting the game
        updateStatusBar();
        
        // Reset the game panel
        gamePanel.repaint();
        
        revalidate(); // Revalidate the frame to apply the changes
        
    }
    
 // Save high scores to a file
    private void saveHighScores() {
        try {
            File file = new File(HIGH_SCORES_FILE);
            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (int score : highScores) {
                writer.write(String.valueOf(score));
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load high scores from a file
    private void loadHighScores() {
        try {
            File file = new File(HIGH_SCORES_FILE);
            if (!file.exists()) {
                return; // If the file doesn't exist, there are no high scores to load
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                int highScore = Integer.parseInt(line);
                highScores.add(highScore);
            }
            reader.close();

            Collections.sort(highScores, Comparator.reverseOrder());

            if (highScorePanel != null) {
                highScorePanel.updateHighScores(highScores);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void showGameOverScreen() {
    	gameOverPanel = new GameOverPanel(this, score,hasPlayerWon); // Create a new GameOverPanel
        getContentPane().removeAll(); // Remove all components from the content pane
        getContentPane().add(gameOverPanel, BorderLayout.CENTER); // Add the GameOverPanel
        isGameOver = true;
        revalidate(); // Revalidate the frame to apply the changes
        repaint();

        // Add the score to the high scores list
        highScores.add(score);

        // Save the updated high scores to the file
        saveHighScores();
    }

    private void updateStatusBar() {
        JPanel statusBar = new JPanel();
        statusBar.setLayout(new BorderLayout());

        JLabel instructionsLabel = new JLabel("Instructions: Use arrow keys or WASD to move the player. Avoid the snake and collect gold coins to reach the exit.");
        statusBar.add(instructionsLabel, BorderLayout.CENTER);

        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JLabel legendLabel = new JLabel("Legend: ");
        legendLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

        JLabel emptyLabel = new JLabel("Empty");
        emptyLabel.setForeground(Color.BLACK);
        emptyLabel.setBackground(Color.WHITE);

        JLabel wallLabel = new JLabel("Wall");
        wallLabel.setForeground(Color.WHITE);
        wallLabel.setBackground(Color.BLACK);
        wallLabel.setOpaque(true);

        JLabel playerLabel = new JLabel("Player");
        playerLabel.setForeground(Color.BLACK);
        playerLabel.setBackground(Color.GREEN);
        playerLabel.setOpaque(true);

        JLabel snakeLabel = new JLabel("Snake");
        snakeLabel.setForeground(Color.BLACK);
        snakeLabel.setBackground(Color.ORANGE);
        snakeLabel.setOpaque(true);

        JLabel goldCoinLabel = new JLabel("Gold Coin");
        goldCoinLabel.setForeground(Color.BLACK);
        goldCoinLabel.setBackground(Color.YELLOW);
        goldCoinLabel.setOpaque(true);

        JLabel exitLabel = new JLabel("Exit");
        exitLabel.setForeground(Color.WHITE);
        exitLabel.setBackground(Color.BLUE);
        exitLabel.setOpaque(true);

        JLabel powerUpLabel = new JLabel("Power-Up");
        powerUpLabel.setForeground(Color.BLACK);
        powerUpLabel.setBackground(Color.RED);
        powerUpLabel.setOpaque(true);

        legendPanel.add(legendLabel);
        legendPanel.add(emptyLabel);
        legendPanel.add(wallLabel);
        legendPanel.add(playerLabel);
        legendPanel.add(snakeLabel);
        legendPanel.add(goldCoinLabel);
        legendPanel.add(exitLabel);
        legendPanel.add(powerUpLabel);

        statusBar.add(legendPanel, BorderLayout.SOUTH);

        add(statusBar, BorderLayout.NORTH);
    }
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            new GameWindow(difficulty);
//        });
//    }
}