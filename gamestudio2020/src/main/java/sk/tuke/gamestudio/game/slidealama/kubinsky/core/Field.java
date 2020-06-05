package sk.tuke.gamestudio.game.slidealama.kubinsky.core;

import sk.tuke.gamestudio.game.slidealama.kubinsky.core.tiles.*;

import java.util.Random;
import java.util.Scanner;

public class Field {

    private final Tile[][] matrix = new Tile[5][5];
    private final Scanner scanner = new Scanner(System.in);
    private final Random random = new Random();
    private GameState gameState = GameState.PLAYING;

    private Player playerOne;
    private Player playerTwo;
    private int roundCount;

    public Field() {
        getPlayers();
        generate();
        playerOne.setScore(0);
        playerTwo.setScore(0);
    }

    public Field(String playerOneName, String playerTwoName) {
        playerOne = new Player(playerOneName);
        playerTwo = new Player(playerTwoName);
        generate();
        playerOne.setScore(0);
        playerTwo.setScore(0);
    }

    private String readLine() {
        return scanner.nextLine().toUpperCase();
    }

    private void getPlayers() {
        System.out.print("\nEnter Player One name: ");
        String playerOneName = readLine();
        System.out.print("Enter Player Two name: ");
        String playerTwoName = readLine();

        while (playerOneName.equals(playerTwoName)) {
            System.out.print("Choose different name - Player Two: ");
            playerTwoName = readLine();
        }

        playerOne = new Player(playerOneName);
        playerTwo = new Player(playerTwoName);
    }

    public void checkIfSolved() {
        if (playerOne.getScore() >= 300 || playerTwo.getScore() >= 300) {
            gameState = GameState.SOLVED;
        }
    }

    public void incrementScore(int score) {
        if (roundCount % 2 == 0) {
            playerOne.incrementScore(score);
        } else {
            playerTwo.incrementScore(score);
        }
    }

    public void incrementRound() {
        roundCount++;
    }

    public Tile getRandomTile() {
        int randomNumber = (random.nextInt() & 0xff) % 7;

        switch (randomNumber) {
            case 0:
                return new Bell();
            case 1:
                return new Banana();
            case 2:
                return new Plum();
            case 3:
                return new Pear();
            case 4:
                return new Cherry();
            case 5:
                return new Bar();
            case 6:
                return new Seven();
            default:
                throw new IllegalStateException("Unexpected value: " + randomNumber);
        }
    }

    private void generate() {
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                matrix[y][x] = getRandomTile();
            }
        }

        for (boolean iterate = true; iterate;) {
            for (iterate = false; checkAdjacentTiles(); iterate = true) {
                checkGravity();
            }

            if (iterate) {
                for (int y = 0; y < 5; y++) {
                    for (int x = 0; x < 5; x++) {
                        if (matrix[y][x] instanceof Empty) {
                            matrix[y][x] = getRandomTile();
                        }
                    }
                }
            }
        }
    }

    public boolean checkAdjacentTiles() {
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                if (matrix[y][x] instanceof Empty) continue;

                Class tileClass = matrix[y][x].getClass();
                int count = 0;

                // check same kinds on Y axis
                for (int tempY = y; (y < 3) && (tempY < 5); tempY++) {
                    if (tileClass == matrix[tempY][x].getClass()) count++;
                    else break;
                }

                if (count > 2) {
                    incrementScore(getScore(count, y, x, true));
                    return true;
                } else count = 0;

                // check same kinds on X axis
                for (int tempX = x; (x < 3) && (tempX < 5); tempX++) {
                    if (tileClass == matrix[y][tempX].getClass()) count++;
                    else break;
                }

                if (count > 2) {
                    incrementScore(getScore(count, y, x, false));
                    return true;
                }
            }
        }

        return false;
    }

    public void checkGravity() {
        // start on bottom
        for (int Y = 4; Y > 0; Y--) {
            // start on left
            for (int X = 0; X < 5; X++) {
                // if tile is Empty
                if (matrix[Y][X] instanceof Empty) {
                    // find first non-Empty tile above
                    for (int tempY = Y - 1; tempY >= 0; tempY--) {
                        // drop tile
                        if (!(matrix[tempY][X] instanceof Empty)) {
                            matrix[Y][X] = matrix[tempY][X];
                            removeTile(tempY, X);
                            break;
                        }
                    }
                }
            }
        }
    }

    private int getScore(int count, int y, int x, boolean column) {
        int score = 0;

        // add score
        score = (count - 2) * matrix[y][x].getScoreForThree();

        // remove tiles
        if (column) {
            while (count > 0) {
                removeTile(y + count - 1, x);
                count--;
            }
        } else {
            while (count > 0) {
                removeTile(y, x + count - 1);
                count--;
            }
        }

        return score;
    }

    private void removeTile(int y, int x) {
        matrix[y][x] = new Empty();
    }

    public boolean insertTile(String tilePosition, Tile tile) {
        switch (tilePosition) {
            case "A":
                shiftTiles(4, 0, false, tile);
                return true;
            case "B":
                shiftTiles(3, 0, false, tile);
                return true;
            case "C":
                shiftTiles(2, 0, false, tile);
                return true;
            case "D":
                shiftTiles(1, 0, false, tile);
                return true;
            case "E":
                shiftTiles(0, 0, false, tile);
                return true;
            case "F":
                shiftTiles(0, 0, true, tile);
                return true;
            case "G":
                shiftTiles(0, 1, true, tile);
                return true;
            case "H":
                shiftTiles(0, 2, true, tile);
                return true;
            case "I":
                shiftTiles(0, 3, true, tile);
                return true;
            case "J":
                shiftTiles(0, 4, true, tile);
                return true;
            case "K":
                shiftTiles(0, 4, false, tile);
                return true;
            case "L":
                shiftTiles(1, 4, false, tile);
                return true;
            case "M":
                shiftTiles(2, 4, false, tile);
                return true;
            case "N":
                shiftTiles(3, 4, false, tile);
                return true;
            case "O":
                shiftTiles(4, 4, false, tile);
                return true;
            case "X":
                System.exit(0);
            default:
                System.out.print("Choose correct position (A - O): ");
                return false;
        }
    }

    private void shiftTiles(int y, int x, boolean column, Tile tile) {
        if (!(matrix[y][x] instanceof Empty)) {
            if (column) { // top side F-J
                for (int tempY = 4; tempY > 0; tempY--) {
                    matrix[tempY][x] = matrix[tempY - 1][x];
                }
            } else {
                Tile firstTemp = matrix[y][x];
                Tile secondTemp;

                if (x == 0) {
                    // left side A-E
                    for (int tempX = 0; tempX < 4; tempX++) {
                        secondTemp = matrix[y][tempX + 1];
                        matrix[y][tempX + 1] = firstTemp;
                        if (secondTemp instanceof Empty) break;
                        firstTemp = secondTemp;
                    }
                } else if (x == 4) {
                    // right side K-O
                    for (int tempX = 4; tempX > 0; tempX--) {
                        secondTemp = matrix[y][tempX - 1];
                        matrix[y][tempX - 1] = firstTemp;
                        if (secondTemp instanceof Empty) break;
                        firstTemp = secondTemp;
                    }
                }
            }
        }

        // insert
        matrix[y][x] = tile;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getRoundCount() {
        return roundCount;
    }

    public Tile[][] getMatrix() {
        return matrix;
    }

    public Tile getTile(int y, int x) {
        return matrix[y][x];
    }

}
