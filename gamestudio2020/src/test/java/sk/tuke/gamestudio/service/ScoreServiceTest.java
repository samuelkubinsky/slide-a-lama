package sk.tuke.gamestudio.service;

import org.junit.Test;
import sk.tuke.gamestudio.entity.Score;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ScoreServiceTest {
    private ScoreService service = new ScoreServiceFile();
    //private ScoreService service = new ScoreServiceJDBC();

    @Test
    public void addScore() {
        Score score = new Score("Zuzka", 200, "mines", new Date());
        service.addScore(score);
        List<Score> scores = service.getTopScores("mines");

        assertEquals(1, scores.size());
        assertEquals("Zuzka", scores.get(0).getPlayer());
        assertEquals(200, scores.get(0).getPoints());
        assertEquals("mines", scores.get(0).getGame());
    }
}
