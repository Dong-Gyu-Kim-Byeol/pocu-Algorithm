package academy.pocu.comp3500.lab8;

import academy.pocu.comp3500.lab8.maze.Point;

public class NowAndPrePosition {
    private final Point nowPos;
    private final NowAndPrePosition prePosOrNull;

    public NowAndPrePosition(final Point nowPos, final NowAndPrePosition prePosOrNull) {
        this.nowPos = nowPos;
        this.prePosOrNull = prePosOrNull;
    }

    public Point getNowPos() {
        return nowPos;
    }

    public NowAndPrePosition getPrePosOrNull() {
        return prePosOrNull;
    }
}
