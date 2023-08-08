package GamePackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameMenu extends JFrame {

    // Constructor
    public GameMenu() {
        initUI();
    }

    // Initialize the User Interface
    private void initUI() {
        setTitle("Game Menu");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window on the screen
        setLayout(new BorderLayout());

        createMenuPanel();
        

        setVisible(true);
    }

    // Create the main menu panel
    private void createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(3, 1, 10, 10)); // 3 rows, 1 column, 10 pixels gap between components

        JButton newGameButton = new JButton("New Game");
        JButton selectLevelButton = new JButton("Higher Difficulty");
        JButton exitButton = new JButton("Exit");

        // Add action listeners to the buttons
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call a method to start a new game
            	showGameScreen();
            }
        });

        selectLevelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call a method to start a game with higher difficulty
            	//showGameScreen();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call a method to exit the game
                exitGame();
            }
        });

        // Add buttons to the panel
        menuPanel.add(newGameButton);
        menuPanel.add(selectLevelButton);
        menuPanel.add(exitButton);

        add(menuPanel, BorderLayout.CENTER);
    }

    // Method to exit the game
    private void exitGame() {
        // Code to handle game exit goes here
        // For now, just close the application
        System.exit(0);
    }

    // Method to switch to the game screen
    private void showGameScreen() {
        // Close the current menu screen and show the game screen
        dispose(); // Dispose of the current frame
        new GameWindow(); // Create a new GameWindow object to show the game screen
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameMenu();
        });
    }
    
}