package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Score;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreServiceFile implements ScoreService {
    private static final String FILE_NAME = "score.db";

    @Override
    public void addScore(Score score) {
        List<Score> scores = loadScore();
        scores.add(score);
        Collections.sort(scores);
        saveScore(scores);
    }

    @Override
    public List<Score> getTopScores(String gameName) {
        List<Score> scores = loadScore();
        //TODO: vyber podla gameName
        return scores;
    }

    private void saveScore(List<Score> scores) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            os.writeObject(scores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Score> loadScore() {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Score>) is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
