package academy.pocu.comp3500.lab8.app;

import academy.pocu.comp3500.lab8.MazeSolver;
import academy.pocu.comp3500.lab8.maze.Point;

import java.util.List;

public class Program {

    public static void main(String[] args) {

        char[][] maze3x3 = new char[][]{
                {'x', 'x', 'x'},
                {'x', 'E', 'x'},
                {'x', 'x', 'x'}
        };

        List<Point> result = MazeSolver.findPath(maze3x3, new Point(1, 1));

        assert (result.size() == 1);
        assert (result.get(0).getX() == 1 && result.get(0).getY() == 1);

        char[][] maze3x5 = new char[][]{
                {'x', 'x', 'x'},
                {'x', ' ', 'x'},
                {'x', ' ', 'x'},
                {'x', 'E', 'x'},
                {'x', 'x', 'x'}
        };

        result = MazeSolver.findPath(maze3x5, new Point(1, 1));

        assert (result.size() == 3);
        assert (result.get(0).getX() == 1 && result.get(0).getY() == 1);
        assert (result.get(1).getX() == 1 && result.get(1).getY() == 2);
        assert (result.get(2).getX() == 1 && result.get(2).getY() == 3);

        char[][] maze7x5 = new char[][]{
                {'x', 'x', 'x', 'x', 'x', 'x', 'x'},
                {'x', 'E', 'x', ' ', ' ', ' ', 'x'},
                {'x', ' ', 'x', ' ', 'x', ' ', 'x'},
                {'x', ' ', ' ', ' ', 'x', ' ', 'x'},
                {'x', 'x', 'x', 'x', 'x', 'x', 'x'}
        };

        result = MazeSolver.findPath(maze7x5, new Point(5, 3));

        assert (result.size() == 11);
        assert (result.get(0).getX() == 5 && result.get(0).getY() == 3);
        assert (result.get(1).getX() == 5 && result.get(1).getY() == 2);
        assert (result.get(2).getX() == 5 && result.get(2).getY() == 1);
        assert (result.get(3).getX() == 4 && result.get(3).getY() == 1);
        assert (result.get(4).getX() == 3 && result.get(4).getY() == 1);
        assert (result.get(5).getX() == 3 && result.get(5).getY() == 2);
        assert (result.get(6).getX() == 3 && result.get(6).getY() == 3);
        assert (result.get(7).getX() == 2 && result.get(7).getY() == 3);
        assert (result.get(8).getX() == 1 && result.get(8).getY() == 3);
        assert (result.get(9).getX() == 1 && result.get(9).getY() == 2);
        assert (result.get(10).getX() == 1 && result.get(10).getY() == 1);

        char[][] maze8x6 = new char[][]{
                {'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x'},
                {'x', 'x', ' ', 'x', ' ', 'E', ' ', 'x'},
                {'x', 'x', ' ', 'x', ' ', 'x', 'x', 'x'},
                {'x', ' ', ' ', ' ', ' ', 'x', ' ', 'x'},
                {'x', 'x', ' ', 'x', ' ', ' ', ' ', 'x'},
                {'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x'}
        };

        result = MazeSolver.findPath(maze8x6, new Point(2, 2));

        assert (result.size() == 7);
        assert (result.get(0).getX() == 2 && result.get(0).getY() == 2);
        assert (result.get(6).getX() == 5 && result.get(6).getY() == 1);
    }
}