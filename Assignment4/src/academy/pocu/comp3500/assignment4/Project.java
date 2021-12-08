package academy.pocu.comp3500.assignment4;

import academy.pocu.comp3500.assignment4.project.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public final class Project {
    private final HashMap<String, Task> taskDataMap;
    private final Graph<Task> graph;

    // ---

    public Project(final Task[] tasks) {
        this.taskDataMap = new HashMap<>(tasks.length);

        // create graph
        {
            final ArrayList<Task> taskDataArray = new ArrayList<>(tasks.length);

            // create taskDataArray
            for (final Task task : tasks) {
                this.taskDataMap.put(task.getTitle(), task);

                taskDataArray.add(task);
            }

            final HashMap<Task, ArrayList<Task>> edgeArrayMap = new HashMap<>(tasks.length);
            final HashMap<Task, ArrayList<Integer>> edgeWeightArrayMap = new HashMap<>(tasks.length);

            for (final Task task : tasks) {
                final ArrayList<Task> edgeArray = new ArrayList<>(task.getPredecessors().size());
                edgeArrayMap.put(task, edgeArray);

                final ArrayList<Integer> edgeWeightArray = new ArrayList<>(task.getPredecessors().size());
                edgeWeightArrayMap.put(task, edgeWeightArray);

                for (final Task predecessor : task.getPredecessors()) {
                    edgeArray.add(predecessor);
                    edgeWeightArray.add(predecessor.getEstimate());
                }
            }

            this.graph = new Graph<>(true, taskDataArray, edgeArrayMap, edgeWeightArrayMap);
        }
    }

    // ---

    public final int findTotalManMonths(final String task) {
        assert (this.taskDataMap.containsKey(task));
        final Task taskNode = this.taskDataMap.get(task);

        final LinkedList<Task> searchNodes = new LinkedList<>();
        this.graph.bfs(true, taskNode, false, searchNodes);

        int manMonths = 0;

        for (final Task data : searchNodes) {
            manMonths += data.getEstimate();
        }

        return manMonths;
    }

    public final int findMinDuration(final String task) {
        assert (this.taskDataMap.containsKey(task));
        final Task startData = this.taskDataMap.get(task);

        int max = this.graph.maxDistInDag(true, startData, false);

        max += startData.getEstimate();
        return max;
    }

//    public final int findMinDuration(final String task) {
//        assert (this.taskDataMap.containsKey(task));
//        final Task startData = this.taskDataMap.get(task);
//
//        final HashMap<Task, Task> prevMap = new HashMap<>(this.graph.nodeCount());
//        final HashMap<Task, Integer> maxDistMap = this.graph.dijkstraMaxPath(true, startData, false, prevMap);
//
//        int max = 0;
//        for (final int dist : maxDistMap.values()) {
//            max = Math.max(dist, max);
//        }
//
//        max += startData.getEstimate();
//        return max;
//    }

    public final int findMaxBonusCount(final String task) {
        assert (this.taskDataMap.containsKey(task));
        final Task skinData = this.taskDataMap.get(task);

        final Task ghostData;
        {
            final LinkedList<Task> searchNodes = new LinkedList<>();
            this.graph.bfs(true, skinData, false, searchNodes);
            final HashMap<Task, GraphNode<Task>> mainGraph = this.graph.getGraph();

            int leafCapacitySum = 0;
            final ArrayList<Task> ghostEdgeDataArray = new ArrayList<>(searchNodes.size());
            for (final Task data : searchNodes) {
                if (mainGraph.get(data).getEdges().size() == 0) {
                    ghostEdgeDataArray.add(data);
                    leafCapacitySum += data.getEstimate();
                }
            }

            final ArrayList<Integer> ghostEdgeWeightArray = new ArrayList<>(ghostEdgeDataArray.size());
            for (int i = 0; i < ghostEdgeDataArray.size(); ++i) {
                ghostEdgeWeightArray.add(leafCapacitySum);
            }

            ghostData = new Task("GHOST", leafCapacitySum);
            this.graph.addTransposedNode(ghostData, ghostEdgeDataArray, ghostEdgeWeightArray);
        }

        final int maxBonusCount = this.graph.maxFlowAndNodeCapacity(false, skinData, ghostData, false, Task::getEstimate);
//        final int maxBonusCount = this.graph.maxFlowAndNodeCapacity(true, ghostData, skinData, true, Task::getEstimate);

        this.graph.removeTransposedNode(ghostData);

        return maxBonusCount;
    }
}