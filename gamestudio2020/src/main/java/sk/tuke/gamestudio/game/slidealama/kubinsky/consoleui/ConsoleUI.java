package sk.tuke.gamestudio.game.slidealama.kubinsky.consoleui;

import org.springframework.beans.factory.annotation.Autowired;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.game.slidealama.kubinsky.core.tiles.*;
import sk.tuke.gamestudio.game.slidealama.kubinsky.core.Field;
import sk.tuke.gamestudio.game.slidealama.kubinsky.core.GameState;
import sk.tuke.gamestudio.game.slidealama.kubinsky.core.Player;
//import sk.tuke.gamestudio.game.slidealama.kubinsky.core.Services;
import sk.tuke.gamestudio.service.*;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private static final String GAME_NAME = "SLIDE-A-LAMA";

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private CommentService commentService;

    private final Scanner scanner = new Scanner(System.in);
//    private final Services services = new Services();

    private Field field;

    public ConsoleUI(Field field) {
        this.field = field;
        printInstructions();
    }

    public void play() throws RatingException, CommentException {
        // before game
        Tile thisTurnTile = field.getRandomTile();
        Tile nextTurnTile = field.getRandomTile();

        // game
        while (field.getGameState() == GameState.PLAYING) {
            // print round separator
            System.out.println("\n__________________________________");

            // print field
            printField(thisTurnTile, nextTurnTile);

            // get input and insert if correct
            while (!field.insertTile(readLine(), thisTurnTile));

            // check same kinds and gravity
            do field.checkGravity();
            while (field.checkAdjacentTiles());

            // check end game
            field.checkIfSolved();
            field.incrementRound();

            // switch tiles and get new
            thisTurnTile = nextTurnTile;
            nextTurnTile = field.getRandomTile();
        }

        // after game
        printEnd();
    }

    private String readLine() {
        return scanner.nextLine().toUpperCase();
    }

    private void printTile(Tile tile) {
        System.out.print(tile.getColor() + tile.getShortcut() + tile.getResetColor());
    }

    private void printField(Tile thisTurnTile, Tile nextTurnTile) {
        printMatrix();
        printPlayerScores();
        printTurnTiles(thisTurnTile, nextTurnTile);
        printTurn();
    }

    private void printMatrix() {
        Tile[][] matrix = field.getMatrix();

        System.out.print("\n     ");

        for (int index = 0; index < 5; index++) {
            System.out.print((char)('F' + index) + " \t ");
        }

        System.out.println("\n    ___\t___\t___\t___\t___\t");


        for (int row = 0; row < 5; row++) {
            System.out.print((char)('E' - row) + "  |");

            for (int column = 0; column < 5; column++) {
                printTile(matrix[row][column]);
                if (column < 4) System.out.print("\t");
            }

            System.out.println("|  " + (char)('K' + row));
        }
    }

    private void printPlayerScores() {
        System.out.println("\n" + field.getPlayerOne().getName() + "'s score: " + field.getPlayerOne().getScore());
        System.out.println(field.getPlayerTwo().getName() + "'s score: " + field.getPlayerTwo().getScore());
    }

    private void printTurnTiles(Tile thisTurnTile, Tile nextTurnTile) {
        System.out.print("\nNext turn: ");
        printTile(nextTurnTile);
        System.out.print("\nThis turn: ");
        printTile(thisTurnTile);
        System.out.println();
    }

    private void printTurn() {
        if (field.getRoundCount() % 2 == 0) {
            System.out.print("\n" + field.getPlayerOne().getName() + "'s turn: ");
        } else {
            System.out.print("\n" + field.getPlayerTwo().getName() + "'s turn: ");
        }
    }

    private void printInstructions() {
        printRules();
        printControls();
    }

    private void printRules() {
        Tile[] arrayOfTiles = {new Seven(), new Bar(), new Cherry(), new Pear(), new Plum(), new Banana(), new Bell()};

        System.out.println(
            "\n" +
            "-----------------\n" +
            "      RULES      \n" +
            "-----------------"
        );

        for (Tile tile: arrayOfTiles) {
            for (int i = 0; i < 3; i++) {
                printTile(tile);
                System.out.print(" ");
            }

            System.out.println("- " + tile.getScoreForThree());
        }

        System.out.println(
            "4 of a kind - x2\n" +
            "5 of a kind - x3"
        );
    }

    private void printControls() {
        System.out.println(
            "-----------------\n" +
            "     CONTROLS    \n" +
            "-----------------\n" +
            "A - O: Play\n" +
            "X: Exit\n" +
            "-----------------"
        );
    }

    private void printEnd() throws RatingException, CommentException {
        Player playerOne = field.getPlayerOne();
        Player playerTwo = field.getPlayerTwo();
        Player winner = (playerOne.getScore() > playerTwo.getScore()) ? (playerOne) : (playerTwo);

        printCongratulations(winner);
        addBestScore(winner);
        endMenu(winner);
    }


    private void printCongratulations(Player player) {
        System.out.println("Congratulations, " + player.getName() + " won with " + player.getScore() + " score!\n");
    }

    private void endMenu(Player player) throws CommentException, RatingException {
        printMenu();
        String input;

        do {
            System.out.print("What now?: ");
            input = readLine();
        } while (!handleMenuInput(input, player));

        field = new Field();
        play();
    }

    private void printMenu() {
        System.out.println(
            "-----------------\n" +
            "       MENU      \n" +
            "-----------------\n" +
            "A: Play again\n" +
            "B: Print best scores\n" +
            "C: Add rating\n" +
            "D: Print avg rating\n" +
            "E: Add comment\n" +
            "F: Print comments\n" +
            "X: Exit\n" +
            "-----------------"
        );
    }

    private boolean handleMenuInput(String menuInput, Player player) throws RatingException, CommentException {
        switch (menuInput) {
            case "A":
                return true;
            case "B":
                printBestScores();
                return false;
            case "C":
                addRating(player);
                return false;
            case "D":
                printAverageRating();
                return false;
            case "E":
                addComment(player);
                return false;
            case "F":
                printLastComments();
                return false;
            case "X":
                System.exit(0);
            default:
                return false;
        }
    }

    public void addBestScore(Player player) {
        scoreService.addScore(new Score(player.getName(), player.getScore(), GAME_NAME, new java.util.Date()));
    }

    public void printBestScores() {
        List<Score> scores = scoreService.getTopScores(GAME_NAME);

        System.out.println(
                "\n" +
                "  TOP 10 SCORES\n" +
                "-----------------"
        );

        for (Score s : scores) {
            System.out.println(s);
        }

        System.out.println();
    }

    public void addRating(Player player) throws RatingException {
        float rating;

        do {
            System.out.print("Rate (1 - 5): ");
            rating = scanner.nextFloat();
        } while (rating < 1 || rating > 5);

        ratingService.setRating(new Rating(player.getName(), GAME_NAME, Math.round(rating), new java.util.Date()));
    }

    public void printAverageRating() throws RatingException {
        System.out.println("Average rating is: " + ratingService.getAverageRating(GAME_NAME));
    }

    public void addComment(Player player) throws CommentException {
        System.out.print("Comment: ");
//        scanner.skip("[\r\n]");
        String comment = scanner.nextLine();

        if (!comment.isBlank()) {
            commentService.addComment(new Comment(player.getName(), GAME_NAME, comment, new java.util.Date()));
        }
    }

    public void printLastComments() throws CommentException {
        List<Comment> comments = commentService.getComments(GAME_NAME);

        System.out.println(
                "\n" +
                " LAST 10 COMMENTS\n" +
                "-----------------"
        );

        for (Comment c : comments) {
            System.out.println(c);
        }

        System.out.println();
    }

}
