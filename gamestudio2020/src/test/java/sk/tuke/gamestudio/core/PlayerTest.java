package sk.tuke.gamestudio.core;

import sk.tuke.gamestudio.game.slidealama.kubinsky.core.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    Player player = new Player("JOHN");

    @Test
    void getName() {
        assertEquals("JOHN", player.getName());
    }

    @Test
    void getScore() {
        player.setScore(60);
        assertEquals(60, player.getScore());
    }

    @Test
    void incrementScore() {
        player.setScore(20);
        player.incrementScore(150);
        assertEquals(170, player.getScore());
    }

    @Test
    void setScore() {
        player.setScore(40);
        assertEquals(40, player.getScore());
    }
}