package academy.pocu.comp3500.assignment3;

public final class ScoreMove extends CompactMove {
    private short score;
    private char piece;

    public ScoreMove() {
    }

    public ScoreMove(final int fromX, final int fromY, final int toX, final int toY, final int score) {
        this(fromX, fromY, toX, toY, score, (char) 0);
    }

    public ScoreMove(final int fromX, final int fromY, final int toX, final int toY, final int score, final char piece) {
        init(fromX, fromY, toX, toY, score, piece);
    }

    public char piece() {
        return piece;
    }

    public int score() {
        return score;
    }

    public void init(final int fromX, final int fromY, final int toX, final int toY, final int score, final char piece) {
        assert (Short.MIN_VALUE < score && score < Short.MAX_VALUE);
        assert (0 <= piece);

        super.init(fromX, fromY, toX, toY);
        this.score = (short) score;
        this.piece = piece;
    }

    public void init(final int fromX, final int fromY, final int toX, final int toY, final int score) {
        init(fromX, fromY, toX, toY, score, (char) 0);
    }
}
