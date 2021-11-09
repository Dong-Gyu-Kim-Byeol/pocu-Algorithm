package academy.pocu.comp3500.assignment3;

public final class ScoreMove {
    private static final int MIN_POSITION = -1;
    private static final int MAX_POSITION = 8;

    // position : y << 4 ^ x
    private byte from;
    private byte to;

    private short score;
    private char piece;

    public ScoreMove() {
        this(-1, -1, -1, -1, 0, (char) 0);
    }

    public ScoreMove(final int fromX, final int fromY, final int toX, final int toY, final int score, final char piece) {
        init(fromX, fromY, toX, toY, score, piece);
    }

    public int fromX() {
        final int ret = from & 0x0f;
        assert (MIN_POSITION <= ret && ret < MAX_POSITION);
        return ret;
    }

    public int fromY() {
        final int ret = from >>> 4;
        assert (MIN_POSITION <= ret && ret < MAX_POSITION);
        return ret;
    }

    public int toX() {
        final int ret = to & 0x0f;
        assert (MIN_POSITION <= ret && ret < MAX_POSITION);
        return ret;
    }

    public int toY() {
        final int ret = to >>> 4;
        assert (MIN_POSITION <= ret && ret < MAX_POSITION);
        return ret;
    }

    public char piece() {
        return piece;
    }

    public int score() {
        return score;
    }

    public void init(final ScoreMove other) {
        this.from = other.from;
        this.to = other.to;
        this.score = other.score;
        this.piece = other.piece;
    }

    public void init(final int fromX, final int fromY, final int toX, final int toY, final int score, final char piece) {
        assert (ScoreMove.MIN_POSITION <= fromX && fromX < ScoreMove.MAX_POSITION);
        assert (ScoreMove.MIN_POSITION <= fromY && fromY < ScoreMove.MAX_POSITION);
        assert (ScoreMove.MIN_POSITION <= toX && toX < ScoreMove.MAX_POSITION);
        assert (ScoreMove.MIN_POSITION <= toY && toY < ScoreMove.MAX_POSITION);

        assert (Short.MIN_VALUE < score && score < Short.MAX_VALUE);
        assert (0 <= piece);

        this.from = (byte) (fromY << 4 ^ fromX);
        this.to = (byte) (toY << 4 ^ toX);

        this.score = (short) score;
        this.piece = piece;
    }

    public void init(final int fromX, final int fromY, final int toX, final int toY, final int score) {
        init(fromX, fromY, toX, toY, score, (char) 0);
    }
}
