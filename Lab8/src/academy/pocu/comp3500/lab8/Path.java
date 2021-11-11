package academy.pocu.comp3500.lab8;

import academy.pocu.comp3500.lab8.maze.Point;

import java.util.ArrayList;

public class Path {
    private Point nowPosition;
    private final ArrayList<Point> path;

    public Path(final Point startPosition) {
        this.nowPosition = startPosition;

        this.path = new ArrayList<Point>();
        this.path.add(startPosition);
    }

    public Path(final Path other) {
        this.nowPosition = other.nowPosition;

        this.path = new ArrayList<Point>(other.path.size());
        for (final Point point : other.path) {
            this.path.add(point);
        }
    }

    public void move(final Point nextPosition) {
        assert (this.nowPosition.getX() != nextPosition.getX() || this.nowPosition.getY() != nextPosition.getY());
        this.path.add(nextPosition);
        this.nowPosition = nextPosition;
    }

    public Point getNowPosition() {
        return nowPosition;
    }

    public ArrayList<Point> getPath() {
        return path;
    }
}
