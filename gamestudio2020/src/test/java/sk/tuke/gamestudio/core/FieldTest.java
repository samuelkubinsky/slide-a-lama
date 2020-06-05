package sk.tuke.gamestudio.core;

import sk.tuke.gamestudio.game.slidealama.kubinsky.core.Field;
import sk.tuke.gamestudio.game.slidealama.kubinsky.core.GameState;
import sk.tuke.gamestudio.game.slidealama.kubinsky.core.tiles.Banana;
import sk.tuke.gamestudio.game.slidealama.kubinsky.core.tiles.Seven;
import sk.tuke.gamestudio.game.slidealama.kubinsky.core.tiles.Tile;
import org.junit.jupiter.api.Test;
import sk.tuke.gamestudio.game.slidealama.kubinsky.core.tiles.Empty;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FieldTest {

    private final Tile[][] matrix;
    private Field field;

    FieldTest() {
        field = new Field("Peter", "Jan");
        matrix = field.getMatrix();
    }

    @Test
    void checkSameKinds() {
        assertEquals(false, field.checkAdjacentTiles());
    }

    @Test
    void checkEmptyTiles() {
        int emptyCount = 0;

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                if (matrix[y][x] instanceof Empty) {
                    emptyCount++;
                }
            }
        }

        assertEquals(0, emptyCount);
    }

    @Test
    void checkScoreIncrementation() {
        matrix[4][0] = new Seven();
        matrix[4][1] = new Banana();
        matrix[4][2] = new Banana();
        matrix[4][3] = new Banana();
        matrix[4][4] = new Banana();

        field.getPlayerOne().setScore(0);
        field.checkAdjacentTiles();

        assertEquals(40, field.getPlayerOne().getScore());
    }

    @Test
    void checkGameState() {
        field.getPlayerOne().incrementScore(300);
        field.checkIfSolved();
        assertEquals(GameState.SOLVED, field.getGameState());
    }

    @Test
    void checkGravity() {
        matrix[0][0] = new Seven();
        matrix[1][0] = new Empty();
        matrix[2][0] = new Empty();
        matrix[3][0] = new Empty();
        matrix[4][0] = new Empty();

        field.checkGravity();

        assertEquals(Seven.class, matrix[4][0].getClass());
    }

    @Test
    void checkTileInsertion() {
        field.insertTile("O", new Seven());
        assertEquals(Seven.class, matrix[4][4].getClass());
    }

}