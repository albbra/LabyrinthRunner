package GamePackage;

public class Snake {
    public int length;
    public int[] bodyRow;
    public int[] bodyCol;

    public Snake(int initialLength, int startRow, int startCol) {
        length = initialLength;
        bodyRow = new int[length];
        bodyCol = new int[length];

        for (int i = 0; i < length; i++) {
            bodyRow[i] = startRow;
            bodyCol[i] = startCol - i;
        }
    }

    public void moveTowardsPlayer(int playerRow, int playerCol) {
        int newHeadRow = bodyRow[0];
        int newHeadCol = bodyCol[0];

        if (playerRow < newHeadRow) {
            newHeadRow--;
        } else if (playerRow > newHeadRow) {
            newHeadRow++;
        }

        if (playerCol < newHeadCol) {
            newHeadCol--;
        } else if (playerCol > newHeadCol) {
            newHeadCol++;
        }

        for (int i = length - 1; i > 0; i--) {
            bodyRow[i] = bodyRow[i - 1];
            bodyCol[i] = bodyCol[i - 1];
        }

        bodyRow[0] = newHeadRow;
        bodyCol[0] = newHeadCol;
    }

    public boolean collidesWithPlayer(int playerRow, int playerCol) {
        return bodyRow[0] == playerRow && bodyCol[0] == playerCol;
    }
}