package academy.pocu.comp3500.assignment3;

public class ScoreMove {
    public int fromX;
    public int fromY;
    public int toX;
    public int toY;
    public int score;

    public ScoreMove() {
    }

    public ScoreMove(final int fromX, final int fromY, final int toX, final int toY, final int score) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.score = score;
    }
}
