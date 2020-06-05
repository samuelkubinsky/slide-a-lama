package sk.tuke.gamestudio.game.slidealama.kubinsky.core;

public class Player {

    private String name;
    private int score;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore(int by) {
        score += by;
    }

    public void setScore(int to) {
        score = to;
    }

    public void setName(String to) {
        name = to;
    }

}
