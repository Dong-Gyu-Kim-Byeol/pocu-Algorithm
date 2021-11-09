package academy.pocu.comp3500.assignment3;

public class ScoreMove {
    public int fromX;
    public int fromY;
    public int toX;
    public int toY;
    public int score;
    public char piece;

    public ScoreMove() {
    }

    public ScoreMove(final int fromX, final int fromY, final int toX, final int toY, final int score) {
        this(fromX, fromY, toX, toY, score, (char) 0);
    }

    public ScoreMove(final int fromX, final int fromY, final int toX, final int toY, final int score, final char piece) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.score = score;
        this.piece = piece;
    }
}
