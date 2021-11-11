package academy.pocu.comp3500.lab8;

import academy.pocu.comp3500.lab8.maze.Point;

import java.util.ArrayList;
import java.util.List;

public final class MazeSolver {
    public static List<Point> findPath(final char[][] maze, final Point start) {
        return myGenericFindPath(maze, start);
    }


    public static List<Point> myGenericFindPath(final char[][] maze, final Point start) {
        Node lastNodeOrNull = null;

        final boolean[][] isVisit = new boolean[maze.length][maze[0].length];

        final CircularQueue<Node> bfsQueue = new CircularQueue<Node>(maze.length * maze[0].length);
        bfsQueue.enqueue(new Node(start, null));
        isVisit[start.getY()][start.getX()] = true;

        // top left : (0, 0)
        force_break:
        while (bfsQueue.size() != 0) {
            final Node nowNode = bfsQueue.dequeue();

            final Point nowPos = nowNode.getNowPos();

            switch (maze[nowPos.getY()][nowPos.getX()]) {
                case 'E':
                    lastNodeOrNull = nowNode;
                    break force_break;
                case ' ':
                    break;
                case 'x':
                    continue;
                default:
                    throw new IllegalArgumentException("Unknown type");
            }


            for (int moveType = 0; moveType < 4; ++moveType) {
                final int UP_MOVE_TYPE = 0;
                final int DOWN_MOVE_TYPE = 1;
                final int RIGHT_MOVE_TYPE = 2;
                final int LEFT_MOVE_TYPE = 3;

                final int nextX;
                final int nextY;
                switch (moveType) {
                    case DOWN_MOVE_TYPE:
                        nextX = nowPos.getX();
                        nextY = nowPos.getY() + 1;
                        break;
                    case UP_MOVE_TYPE:
                        nextX = nowPos.getX();
                        nextY = nowPos.getY() - 1;
                        break;
                    case LEFT_MOVE_TYPE:
                        nextX = nowPos.getX() - 1;
                        nextY = nowPos.getY();
                        break;
                    case RIGHT_MOVE_TYPE:
                        nextX = nowPos.getX() + 1;
                        nextY = nowPos.getY();
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown move type");
                }

                if (0 > nextY || nextY >= maze.length
                        || 0 > nextX || nextX >= maze[0].length) {
                    continue;
                }

                if (isVisit[nextY][nextX]) {
                    continue;
                }

                isVisit[nextY][nextX] = true;


                final Point nextPos = new Point(nextX, nextY);
                final Node nextNowAndPrePosition = new Node(nextPos, nowNode);
                bfsQueue.enqueue(nextNowAndPrePosition);
            }
        }

        assert (bfsQueue.capacity() == maze.length * maze[0].length);

        final Stack<Point> reverse = new Stack<Point>(maze.length * maze[0].length);
        if (lastNodeOrNull != null) {
            Node nowAndPrePosition = lastNodeOrNull;
            reverse.push(nowAndPrePosition.getNowPos());

            while (nowAndPrePosition.getPrePosOrNull() != null) {
                final Point prePos = nowAndPrePosition.getPrePosOrNull().getNowPos();
                reverse.push(prePos);

                nowAndPrePosition = nowAndPrePosition.getPrePosOrNull();
            }
        }

        final ArrayList<Point> outPath = new ArrayList<>(reverse.size());
        while (!reverse.isEmpty()) {
            outPath.add(reverse.pop());
        }

        return outPath;
    }
}
