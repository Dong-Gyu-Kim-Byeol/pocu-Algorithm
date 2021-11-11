package academy.pocu.comp3500.lab8;

import academy.pocu.comp3500.lab8.maze.Point;

import java.util.ArrayList;
import java.util.List;

public final class MazeSolver {
    public static List<Point> findPath(final char[][] maze, final Point start) {
        final boolean[][] isVisit = new boolean[maze.length][maze[0].length];

        final CircularQueue<Path> pathBfsQueue = new CircularQueue<Path>(maze.length * maze[0].length);
        pathBfsQueue.enqueue(new Path(start));

        // top left : (0, 0)
        while (pathBfsQueue.size() != 0) {
            final Path nowPath = pathBfsQueue.dequeue();
            final Point nowPos = nowPath.getNowPosition();

            if (isVisit[nowPos.getY()][nowPos.getX()]) {
                continue;
            }

            isVisit[nowPos.getY()][nowPos.getX()] = true;

            switch (maze[nowPos.getY()][nowPos.getX()]) {
                case 'E':
                    return nowPath.getPath();
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


                final Point nextPos = new Point(nextX, nextY);
                final Path nextPath = new Path(nowPath);
                nextPath.move(nextPos);
                pathBfsQueue.enqueue(nextPath);
            }
        }

        return new ArrayList<Point>(0);
    }
}