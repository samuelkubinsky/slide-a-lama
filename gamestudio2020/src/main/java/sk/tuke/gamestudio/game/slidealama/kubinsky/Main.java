package sk.tuke.gamestudio.game.slidealama.kubinsky;

import sk.tuke.gamestudio.game.slidealama.kubinsky.consoleui.ConsoleUI;
import sk.tuke.gamestudio.game.slidealama.kubinsky.core.Field;
import sk.tuke.gamestudio.service.CommentException;
import sk.tuke.gamestudio.service.RatingException;

public class Main {
    public static void main(String[] args) throws RatingException, CommentException {
        Field field = new Field();
        ConsoleUI console = new ConsoleUI(field);
        console.play();
    }
}
