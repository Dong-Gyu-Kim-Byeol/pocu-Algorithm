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

        final LinkedList<GraphNode<Task>> searchNodes = new LinkedList<>();
        this.graph.bfs(true, taskNode, false, searchNodes);

        int manMonths = 0;

        for (final GraphNode<Task> node : searchNodes) {
            final Task data = node.getData();
            manMonths += data.getEstimate();
        }

        return manMonths;
    }

    public final int findMinDuration(final String task) {
        assert (this.taskDataMap.containsKey(task));
        final Task startData = this.taskDataMap.get(task);

        int max = 0;
        {
            final HashMap<Task, Boolean> scc = this.graph.getDataScc();
            final HashMap<Task, GraphNode<Task>> graph = this.graph.getGraph();

            final LinkedList<WeightNode<GraphNode<Task>>> bfsQueue = new LinkedList<>();
            {
                final GraphNode<Task> startNode = graph.get(startData);
                assert (!scc.containsKey(startNode.getData()));

                bfsQueue.addLast(new WeightNode<>(startData.getEstimate(), startNode));
            }

            while (!bfsQueue.isEmpty()) {
                final WeightNode<GraphNode<Task>> weightNode = bfsQueue.poll();

                final GraphNode<Task> node = weightNode.getData();
                if (node.getEdges().size() == 0) {
                    max = Math.max(max, weightNode.getWeight());
                }

                for (final GraphEdge<Task> edge : node.getEdges().values()) {
                    final GraphNode<Task> nextNode = edge.getNode2();
                    final Task nextData = nextNode.getData();

                    if (scc.containsKey(nextData)) {
                        continue;
                    }

                    bfsQueue.addLast(new WeightNode<>(weightNode.getWeight() + nextData.getEstimate(), nextNode));
                }
            }
        }

        return max;
    }

    public final int findMaxBonusCount(final String task) {
        assert (this.taskDataMap.containsKey(task));
        final Task skinData = this.taskDataMap.get(task);

        final Task ghostData;
        {
            final LinkedList<GraphNode<Task>> searchNodes = new LinkedList<>();
            this.graph.bfs(true, skinData, false, searchNodes);

            int leafCapacitySum = 0;
            final ArrayList<Task> ghostEdgeDataArray = new ArrayList<>(searchNodes.size());
            for (final GraphNode<Task> node : searchNodes) {
                if (node.getEdges().size() == 0) {
                    ghostEdgeDataArray.add(node.getData());
                    leafCapacitySum += node.getData().getEstimate();
                }
            }

            final ArrayList<Integer> ghostEdgeWeightArray = new ArrayList<>(ghostEdgeDataArray.size());
            for (final Task edgeData : ghostEdgeDataArray) {
                ghostEdgeWeightArray.add(leafCapacitySum);
            }

            ghostData = new Task("GHOST", leafCapacitySum);
            this.graph.addTransposedNode(ghostData, ghostEdgeDataArray, ghostEdgeWeightArray);
        }

//        final int maxBonusCount = this.graph.maxFlowAndNodeCapacity(false, skinData, ghostData, false, Task::getEstimate);
        final int maxBonusCount = this.graph.maxFlowAndNodeCapacity(true, ghostData, skinData, true, Task::getEstimate);

        this.graph.removeTransposedNode(ghostData);

        return maxBonusCount;
    }
}