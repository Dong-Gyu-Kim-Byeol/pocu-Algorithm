package academy.pocu.comp3500.lab8;

import academy.pocu.comp3500.lab8.maze.Point;

import java.util.ArrayList;
import java.util.List;

public final class MazeSolver {
    public static List<Point> findPath(final char[][] maze, final Point start) {
        return findPathBfs(maze, start);
//        return findPathDfs(maze, start);
    }

    public static List<Point> findPathBfs(final char[][] maze, final Point start) {
        if (maze[start.getY()][start.getX()] == 'E') {
            final ArrayList<Point> path = new ArrayList<Point>(1);
            path.add(start);
            return path;
        }

        final CircularQueue<Path> pathBfsQueue = new CircularQueue<Path>(1);
        pathBfsQueue.enqueue(new Path(start));

        // top left : (0, 0)
        while (pathBfsQueue.size() != 0) {
            final Path nowPath = pathBfsQueue.dequeue();

            final Point nowPos = nowPath.getNowPosition();
            for (int moveType = 0; moveType < 4; ++moveType) {
                final int UP_MOVE_TYPE = 0;
                final int DOWN_MOVE_TYPE = 1;
                final int RIGHT_MOVE_TYPE = 2;
                final int LEFT_MOVE_TYPE = 3;

                final Point nextPos;
                switch (moveType) {
                    case DOWN_MOVE_TYPE:
                        nextPos = new Point(nowPos.getX(), nowPos.getY() + 1);
                        break;
                    case UP_MOVE_TYPE:
                        nextPos = new Point(nowPos.getX(), nowPos.getY() - 1);
                        break;
                    case LEFT_MOVE_TYPE:
                        nextPos = new Point(nowPos.getX() - 1, nowPos.getY());
                        break;
                    case RIGHT_MOVE_TYPE:
                        nextPos = new Point(nowPos.getX() + 1, nowPos.getY());
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown move type");
                }

                if (0 > nextPos.getY() || nextPos.getY() >= maze.length
                        || 0 > nextPos.getX() || nextPos.getX() >= maze[0].length) {
                    continue;
                }

                final Point prePosOrNull = nowPath.getPrePositionOrNull();
                if (prePosOrNull != null) {
                    if (prePosOrNull.getX() == nextPos.getX() && prePosOrNull.getY() == nextPos.getY()) {
                        continue;
                    }
                }


                switch (maze[nextPos.getY()][nextPos.getX()]) {
                    case 'E':
                        nowPath.move(nextPos);
                        return nowPath.getPath();
                    case ' ':
                        final Path nextPath = new Path(nowPath);
                        nextPath.move(nextPos);

                        pathBfsQueue.enqueue(nextPath);
                        break;
                    case 'x':
                        continue;
                    default:
                        throw new IllegalArgumentException("Unknown type");
                }
            }
        }

        assert (false);
        return new ArrayList<Point>(0);
    }

    public static List<Point> findPathDfs(final char[][] maze, final Point start) {
        final Stack<Point> pointOrNullDfsStack = new Stack<>(2);

        final ArrayList<Point> outPath = new ArrayList<>();
        final Stack<Integer> pathIndex = new Stack<>(2);

        pointOrNullDfsStack.push(null);
        pointOrNullDfsStack.push(start);
        // top left : (0, 0)
        while (!pointOrNullDfsStack.isEmpty()) {
            final Point nowPos = pointOrNullDfsStack.pop();
            if (nowPos == null) {
                final int removeIndex = pathIndex.pop();
                outPath.remove(removeIndex);
                continue;
            }

            switch (maze[nowPos.getY()][nowPos.getX()]) {
                case 'E':
                    pathIndex.push(outPath.size());
                    outPath.add(nowPos);
                    return outPath;
                case ' ':
                    pathIndex.push(outPath.size());
                    outPath.add(nowPos);
                    break;
                case 'x':
                    assert (false);
                    continue;
                default:
                    throw new IllegalArgumentException("Unknown type");
            }

            pointOrNullDfsStack.push(null);
            for (int moveType = 0; moveType < 4; ++moveType) {
                final int UP_MOVE_TYPE = 0;
                final int DOWN_MOVE_TYPE = 1;
                final int RIGHT_MOVE_TYPE = 2;
                final int LEFT_MOVE_TYPE = 3;

                final Point nextPos;
                switch (moveType) {
                    case DOWN_MOVE_TYPE:
                        nextPos = new Point(nowPos.getX(), nowPos.getY() + 1);
                        break;
                    case UP_MOVE_TYPE:
                        nextPos = new Point(nowPos.getX(), nowPos.getY() - 1);
                        break;
                    case LEFT_MOVE_TYPE:
                        nextPos = new Point(nowPos.getX() - 1, nowPos.getY());
                        break;
                    case RIGHT_MOVE_TYPE:
                        nextPos = new Point(nowPos.getX() + 1, nowPos.getY());
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown move type");
                }

                if (0 > nextPos.getY() || nextPos.getY() >= maze.length
                        || 0 > nextPos.getX() || nextPos.getX() >= maze[0].length) {
                    continue;
                }

                if (maze[nextPos.getY()][nextPos.getX()] == 'x') {
                    continue;
                }

                assert (maze[nowPos.getY()][nowPos.getX()] == ' ');
                if (outPath.size() >= 2) {
                    final Point prePos = outPath.get(outPath.size() - 2);
                    if (prePos.getX() == nextPos.getX() && prePos.getY() == nextPos.getY()) {
                        continue;
                    }
                }

                pointOrNullDfsStack.push(nextPos);
            }
        }

        assert (false);
        return outPath;
    }
}