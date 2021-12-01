package academy.pocu.comp3500.assignment4;

import academy.pocu.comp3500.assignment4.project.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public final class Project {
    private final HashMap<String, TaskData> taskDataMap;
    private final Graph<TaskData> graph;

    // ---

    public Project(final Task[] tasks) {
        this.taskDataMap = new HashMap<>(tasks.length);

        // create graph
        {
            final ArrayList<TaskData> taskDataArray = new ArrayList<>(tasks.length);
            final HashMap<Task, TaskData> tempTaskDataMap = new HashMap<>(tasks.length);

            // create taskDataArray
            for (final Task task : tasks) {
                final TaskData taskData = new TaskData(task.getTitle(), task.getEstimate());

                this.taskDataMap.put(taskData.getTitle(), taskData);

                taskDataArray.add(taskData);
                tempTaskDataMap.put(task, taskData);
            }

            final HashMap<TaskData, ArrayList<TaskData>> edgeArrayMap = new HashMap<>(tasks.length);
            final HashMap<TaskData, ArrayList<Integer>> edgeWeightArrayMap = new HashMap<>(tasks.length);

            for (final Task task : tasks) {
                assert (tempTaskDataMap.containsKey(task));

                final ArrayList<TaskData> edgeArray = new ArrayList<>(task.getPredecessors().size());
                edgeArrayMap.put(tempTaskDataMap.get(task), edgeArray);

                final ArrayList<Integer> edgeWeightArray = new ArrayList<>(task.getPredecessors().size());
                edgeWeightArrayMap.put(tempTaskDataMap.get(task), edgeWeightArray);

                for (final Task predecessor : task.getPredecessors()) {
                    assert (tempTaskDataMap.containsKey(predecessor));

                    final TaskData preTaskData = tempTaskDataMap.get(predecessor);
                    edgeArray.add(preTaskData);

                    edgeWeightArray.add(task.getEstimate());
                }
            }

            this.graph = new Graph<>(true, taskDataArray, edgeArrayMap, edgeWeightArrayMap);
        }
    }

    // ---

    public final int findTotalManMonths(final String task) {
        assert (this.taskDataMap.containsKey(task));
        final TaskData taskNode = this.taskDataMap.get(task);

        final LinkedList<GraphNode<TaskData>> searchNodes = new LinkedList<>();
        this.graph.bfs(true, taskNode, false, searchNodes);

        int manMonths = 0;

        for (final GraphNode<TaskData> node : searchNodes) {
            final TaskData data = node.getData();
            manMonths += data.getEstimate();
        }

        return manMonths;
    }

    public final int findMinDuration(final String task) {
        assert (this.taskDataMap.containsKey(task));
        final TaskData startData = this.taskDataMap.get(task);

        final LinkedList<WeightNode<GraphNode<TaskData>>> sums = new LinkedList<>();
        {
            final HashMap<TaskData, Boolean> scc = this.graph.getDataScc();
            final HashMap<TaskData, GraphNode<TaskData>> graph = this.graph.getGraph();

            final LinkedList<WeightNode<GraphNode<TaskData>>> bfsQueue = new LinkedList<>();
            {
                final GraphNode<TaskData> startNode = graph.get(startData);
                assert (!scc.containsKey(startNode.getData()));

                bfsQueue.addLast(new WeightNode<>(startData.getEstimate(), startNode));
            }

            while (!bfsQueue.isEmpty()) {
                final WeightNode<GraphNode<TaskData>> weightNode = bfsQueue.poll();

                final GraphNode<TaskData> node = weightNode.getData();
                if (node.getEdges().size() == 0) {
                    sums.add(weightNode);
                }

                for (final GraphEdge<TaskData> edge : node.getEdges().values()) {
                    final GraphNode<TaskData> nextNode = edge.getNode2();
                    final TaskData nextData = nextNode.getData();

                    if (scc.containsKey(nextData)) {
                        continue;
                    }

                    bfsQueue.addLast(new WeightNode<>(weightNode.getWeight() + nextData.getEstimate(), nextNode));
                }
            }
        }

        int max = 0;
        for (final WeightNode<GraphNode<TaskData>> snm : sums) {
            max = Math.max(max, snm.getWeight());
        }

        return max;
    }

    public final int findMaxBonusCount(final String task) {
        assert (this.taskDataMap.containsKey(task));
        final TaskData skinData = this.taskDataMap.get(task);

        final ArrayList<TaskData> ghostEdgeDataArray;
        final TaskData ghostData;
        {

            final LinkedList<GraphNode<TaskData>> searchNodes = new LinkedList<>();
            this.graph.bfs(true, skinData, false, searchNodes);

            int leafCapacitySum = 0;
            ghostEdgeDataArray = new ArrayList<>(searchNodes.size());
            for (final GraphNode<TaskData> node : searchNodes) {
                if (node.getEdges().size() == 0) {
                    ghostEdgeDataArray.add(node.getData());
                    leafCapacitySum += node.getData().getEstimate();
                }
            }

            final ArrayList<Integer> ghostEdgeWeightArray = new ArrayList<>(ghostEdgeDataArray.size());
            for (final TaskData edgeData : ghostEdgeDataArray) {
                ghostEdgeWeightArray.add(edgeData.getEstimate());
            }

            ghostData = new TaskData("GHOST", leafCapacitySum);
            this.graph.addTransposedNode(ghostData, ghostEdgeDataArray, ghostEdgeWeightArray, ghostEdgeWeightArray);
        }

//        final int maxBonusCount = this.graph.maxFlow(true, ghostData, skinData, true);
        final int maxBonusCount = this.graph.maxFlow(true, skinData, ghostData, false);

        this.graph.removeTransposedNode(ghostData, ghostEdgeDataArray);

        return maxBonusCount;
    }
}