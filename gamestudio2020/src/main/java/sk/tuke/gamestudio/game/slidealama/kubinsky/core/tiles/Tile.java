package sk.tuke.gamestudio.game.slidealama.kubinsky.core.tiles;

public abstract class Tile {
    private final int scoreForThree;
    private final String shortcut;
    private final String color;
    private final String resetColor;
    private final String imageName;

    public Tile(int scoreForThree, String shortcut, int colorCode, String imageName) {
        this.scoreForThree = scoreForThree;
        this.shortcut = shortcut;
        this.color = "\u001B[" + colorCode + "m";
        this.resetColor = "\u001B[0m";
        this.imageName = imageName;
    }

    public int getScoreForThree() {
        return scoreForThree;
    }

    public String getShortcut() {
        return shortcut;
    }

    public String getColor() {
        return color;
    }

    public String getResetColor() {
        return resetColor;
    }

    public String getImageName() {
        return imageName;
    }

}
