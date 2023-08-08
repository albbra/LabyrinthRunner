package GamePackage;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class HighScorePanel extends JPanel {

    private static final int PANEL_WIDTH = 300;
    private static final int PANEL_HEIGHT = 150;

    private DefaultListModel<String> highScoreListModel;
    private JList<String> highScoreList;

    private ArrayList<Integer> highScores;
    private static final String HIGH_SCORES_FILE = "highscores.txt";

    public HighScorePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(new BorderLayout());

        JLabel highScoreLabel = new JLabel("High Scores");
        highScoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        highScoreLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

        highScoreListModel = new DefaultListModel<>();
        highScoreList = new JList<>(highScoreListModel);
        highScoreList.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        highScoreList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(highScoreList);

        add(highScoreLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadHighScores(); // Load high scores from file on initialization
    }

    // Update and display the high scores
    public void updateHighScores(ArrayList<Integer> scores) {
        highScores = scores;
        saveHighScores(); // Save updated high scores to the file
        updateHighScoreListModel();
    }

    // Load high scores from a file
    private void loadHighScores() {
        highScores = new ArrayList<>();
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateHighScoreListModel();
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

    // Update the JList model with the high scores
    private void updateHighScoreListModel() {
        highScoreListModel.clear();
        for (int i = 0; i < highScores.size(); i++) {
            highScoreListModel.addElement((i + 1) + ". " + highScores.get(i));
        }
    }
}