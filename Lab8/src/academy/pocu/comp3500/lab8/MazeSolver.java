package academy.pocu.comp3500.lab8;

import academy.pocu.comp3500.lab8.maze.Point;

import java.util.List;
import java.util.Stack;

public final class MazeSolver {
    public static List<Point> findPath(final char[][] maze, final Point start) {
        final Stack<Point> pointStack = new Stack<>();
        pointStack.push(start);

        while (!pointStack.empty()) {
            final Point nowPos = pointStack.pop();

            for (int moveType = 0; moveType < 4; ++moveType) {
                final int UP_MOVE_TYPE = 0;
                final int DOWN_MOVE_TYPE = 1;
                final int RIGHT_MOVE_TYPE = 2;
                final int LEFT_MOVE_TYPE = 3;

                switch (moveType) {
                    case UP_MOVE_TYPE:
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown move type");
                }
            }


            return null;
        }
    }