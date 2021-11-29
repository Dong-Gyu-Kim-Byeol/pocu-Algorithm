package academy.pocu.comp3500.lab11;

import academy.pocu.comp3500.lab11.data.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BallBoy {
    public static List<Point> findPath(final Point[] points) {
        // create graph
        // create taskDataArray
        final DirectedGraph<Point> graph;
        {
            final ArrayList<Point> pointArray = new ArrayList<>(points.length);

            for (final Point point : points) {
                pointArray.add(point);
            }

            final HashMap<Point, ArrayList<Point>> pointEdgeArrayMap = new HashMap<>(points.length);
            final HashMap<Point, ArrayList<Integer>> weightEdgeArrayMap = new HashMap<>(points.length);

            for (final Point point : pointArray) {

                final ArrayList<Point> pointEdgeArray = new ArrayList<>(points.length);
                pointEdgeArrayMap.put(point, pointEdgeArray);

                final ArrayList<Integer> weightEdgeArray = new ArrayList<>(points.length);
                weightEdgeArrayMap.put(point, weightEdgeArray);

                for (final Point edgePoint : pointArray) {
                    if (edgePoint.equals(point)) {
                        continue;
                    }

                    pointEdgeArray.add(edgePoint);
                    weightEdgeArray.add(getDistance(point, edgePoint));
                }
            }

            graph = new DirectedGraph<>(pointArray, pointEdgeArrayMap, weightEdgeArrayMap);
        }




        return null;
    }

    // ---

    private static int getDistance(final Point p1, final Point p2) {
        return Math.abs(p1.getX() - p2.getX()) + Math.abs(p1.getY() - p2.getY());
    }
}