package academy.pocu.comp3500.lab8;

import academy.pocu.comp3500.lab8.maze.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public final class MazeSolver {
    public static List<Point> findPath(final char[][] maze, final Point start) {
//        return myGenericFindPath(maze, start);
        return javaGenericFindPath(maze, start);
    }


    public static List<Point> myGenericFindPath(final char[][] maze, final Point start) {
        Point lastPosOrNull = null;

        final boolean[][] isVisit = new boolean[maze.length][maze[0].length];
        final HashMap<Point, Point> prePosMap = new HashMap<>();

        final CircularQueue<Point> bfsQueue = new CircularQueue<Point>(maze.length * maze[0].length);
        bfsQueue.enqueue(start);
        isVisit[start.getY()][start.getX()] = true;
        prePosMap.put(start, null);

        // top left : (0, 0)
        force_break:
        while (bfsQueue.size() != 0) {
            final Point nowPos = bfsQueue.dequeue();

            switch (maze[nowPos.getY()][nowPos.getX()]) {
                case 'E':
                    lastPosOrNull = nowPos;
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
                prePosMap.put(nextPos, nowPos);
                bfsQueue.enqueue(nextPos);
            }
        }

        assert (bfsQueue.capacity() == maze.length * maze[0].length);

        final Stack<Point> reverse = new Stack<Point>(maze.length * maze[0].length);
        if (lastPosOrNull != null) {
            Point nextPos = lastPosOrNull;
            reverse.push(nextPos);

            while (prePosMap.get(nextPos) != null) {
                final Point prePos = prePosMap.get(nextPos);
                reverse.push(prePos);

                nextPos = prePos;
            }
        }

        final ArrayList<Point> outPath = new ArrayList<>(reverse.size());
        while (!reverse.isEmpty()) {
            outPath.add(reverse.pop());
        }

        return outPath;
    }


    public static List<Point> javaGenericFindPath(final char[][] maze, final Point start) {
        Point lastPosOrNull = null;

        final boolean[][] isVisit = new boolean[maze.length][maze[0].length];
        final HashMap<Point, Point> prePosMap = new HashMap<>();

        final LinkedList<Point> bfs = new LinkedList<Point>();
        bfs.add(start);
        isVisit[start.getY()][start.getX()] = true;
        prePosMap.put(start, null);

        // top left : (0, 0)
        force_break:
        while (bfs.size() != 0) {
            final Point nowPos = bfs.poll();

            switch (maze[nowPos.getY()][nowPos.getX()]) {
                case 'E':
                    lastPosOrNull = nowPos;
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
                prePosMap.put(nextPos, nowPos);
                bfs.add(nextPos);
            }
        }

        final LinkedList<Point> outPath = new LinkedList<Point>();


        if (lastPosOrNull != null) {
            Point nextPos = lastPosOrNull;
            outPath.addFirst(nextPos);

            while (prePosMap.get(nextPos) != null) {
                final Point prePos = prePosMap.get(nextPos);
                outPath.addFirst(prePos);

                nextPos = prePos;
            }
        }

        return outPath;
    }
}
