package academy.pocu.comp3500.lab8;

import academy.pocu.comp3500.lab8.maze.Point;

public class Node {
    private final Point nowPos;
    private final Node prePosOrNull;

    public Node(final Point nowPos, final Node prePosOrNull) {
        this.nowPos = nowPos;
        this.prePosOrNull = prePosOrNull;
    }

    public Point getNowPos() {
        return nowPos;
    }

    public Node getPrePosOrNull() {
        return prePosOrNull;
    }
}
