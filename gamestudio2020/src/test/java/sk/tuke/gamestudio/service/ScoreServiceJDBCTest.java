package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Score;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScoreServiceJDBCTest {

    private final ScoreService scoreService = new ScoreServiceJDBC();
    private final String gameName = "SCORE_TEST";
    private final String playerName = "MAC";
    private final int score = 600;

    @Test
    void addScore() {
        scoreService.addScore(new Score(gameName, score, playerName, new java.util.Date()));

        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());

        String scoreString =
                "Score{game='" + gameName +
                "', player='" + playerName +
                "', points=" + score +
                ", playedOn=" + formattedDate +
                " 00:00:00.0}";

        List<Score> scores = scoreService.getTopScores(gameName);
        String dbString = scores.get(scores.size() - 1).toString();

        assertEquals(scoreString, dbString);
    }
}