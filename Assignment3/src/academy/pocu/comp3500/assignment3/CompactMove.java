package academy.pocu.comp3500.assignment3;

public class CompactMove {
    private static final int MIN_POSITION = -1;
    private static final int MAX_POSITION = 8;

    // position : y << 4 ^ x
    private byte from;
    private byte to;

    public CompactMove() {
    }

    public CompactMove(final int fromX, final int fromY, final int toX, final int toY) {
        init(fromX, fromY, toX, toY);
    }

    public final int fromX() {
        final int ret = from & 0x0f;
        assert (MIN_POSITION <= ret && ret < MAX_POSITION);
        return ret;
    }

    public final int fromY() {
        final int ret = from >>> 4;
        assert (MIN_POSITION <= ret && ret < MAX_POSITION);
        return ret;
    }

    public final int toX() {
        final int ret = to & 0x0f;
        assert (MIN_POSITION <= ret && ret < MAX_POSITION);
        return ret;
    }

    public final int toY() {
        final int ret = to >>> 4;
        assert (MIN_POSITION <= ret && ret < MAX_POSITION);
        return ret;
    }

    public final void init(final int fromX, final int fromY, final int toX, final int toY) {
        assert (CompactMove.MIN_POSITION <= fromX && fromX < CompactMove.MAX_POSITION);
        assert (CompactMove.MIN_POSITION <= fromY && fromY < CompactMove.MAX_POSITION);
        assert (CompactMove.MIN_POSITION <= toX && toX < CompactMove.MAX_POSITION);
        assert (CompactMove.MIN_POSITION <= toY && toY < CompactMove.MAX_POSITION);

        this.from = (byte) (fromY << 4 ^ fromX);
        this.to = (byte) (toY << 4 ^ toX);
    }
}
