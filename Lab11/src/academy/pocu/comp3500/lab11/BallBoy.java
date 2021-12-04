package academy.pocu.comp3500.lab11;

import academy.pocu.comp3500.lab11.data.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BallBoy {
    public static List<Point> findPath(final Point[] points) {
        // 모든 n과 e는 원래의 완전 그래프 기준
        //
        // 그래프 데이터 준비
        // O(n + e)
        //
        // 그래프 만들기
        // O(n + e)
        //
        // mst edge 준비
        // O(e)
        //
        // mst
        // O(e정렬) + O(e * disjoint set ( = 실질적으로 e ))
        // https://en.wikipedia.org/wiki/Disjoint-set_data_structure#Time_complexity
        //
        // mst 그래프 데이터 준비
        // O(n + n( = mst e)) (mst e == g n-1)
        //
        // mst 그래프 만들기
        // O(n + n( = mst e)) (mst e == g n-1)
        //
        // mst 그래프 해밀턴 순회를 위한 dfs
        // O(n + n( = mst e))
        //
        // 해밀턴 순회를 위한 중복 제거
        // O(n)
        //
        // 노드에서 포인터 추출
        // O(n)


        final Point startPoint = new Point(0, 0);

        // create graph
        final Graph<Point> graph;
        final ArrayList<Point> pointArray;
        final HashMap<Point, ArrayList<Point>> pointEdgeArrayMap;
        final HashMap<Point, ArrayList<Integer>> weightEdgeArrayMap;
        {
            // create pointArray
            pointArray = new ArrayList<>(points.length + 1);
            pointArray.add(startPoint);

            for (final Point point : points) {
                pointArray.add(point);
            }
            // end create pointArray

            pointEdgeArrayMap = new HashMap<>(pointArray.size());
            weightEdgeArrayMap = new HashMap<>(pointArray.size());

            for (final Point point : pointArray) {

                final ArrayList<Point> pointEdgeArray = new ArrayList<>(pointArray.size());
                pointEdgeArrayMap.put(point, pointEdgeArray);

                final ArrayList<Integer> weightEdgeArray = new ArrayList<>(pointArray.size());
                weightEdgeArrayMap.put(point, weightEdgeArray);

                for (final Point edgePoint : pointArray) {
                    if (edgePoint.equals(point)) {
                        continue;
                    }

                    pointEdgeArray.add(edgePoint);
                    weightEdgeArray.add(getDistance(point, edgePoint));
                }
            }

            graph = new Graph<Point>(false, pointArray, pointEdgeArrayMap, weightEdgeArrayMap);
        }
        // end create graph

        final ArrayList<GraphNode<Point>> tspNodeList = graph.tsp2Approximation(false, startPoint);
        final ArrayList<Point> tspList = new ArrayList<>(points.length + 2);
        for (final GraphNode<Point> tspNode : tspNodeList) {
            final Point tspData = tspNode.getData();
            tspList.add(tspData);
        }

        return tspList;
    }

    // ---

    private static int getDistance(final Point p1, final Point p2) {
        return Math.abs(p1.getX() - p2.getX()) + Math.abs(p1.getY() - p2.getY());
    }
}