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

        int max = 0;
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
                    max = Math.max(max, weightNode.getWeight() + nextData.getEstimate());
                }
            }
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
        final int maxBonusCount = this.maxFlow(skinData, ghostData);

        this.graph.removeTransposedNode(ghostData, ghostEdgeDataArray);

        return maxBonusCount;
    }

    // max flow
    public final int maxFlow(final TaskData source,
                             final TaskData sink) {

        final HashMap<TaskData, Integer> dataIndex = this.graph.getDataIndex();
        final HashMap<TaskData, Boolean> scc = this.graph.getDataScc();

        int outTotalFlow = 0;

        final int BACK_FLOW_CAPACITY = 0;

        final HashMap<TaskData, GraphNode<TaskData>> mainGraph = this.graph.getGraph();
        final HashMap<TaskData, GraphNode<TaskData>> transposedGraph = this.graph.getTransposedGraph();

        final int[][] flow = new int[dataIndex.size()][dataIndex.size()];
        final int[] capacity = new int[dataIndex.size()];
        for (final GraphNode<TaskData> node : mainGraph.values()) {
            final TaskData data = node.getData();
            final int iData = dataIndex.get(data);
            capacity[iData] = data.getEstimate();
        }
        final LinkedList<IsTransposedEdge<TaskData>> path = new LinkedList<>();

        final LinkedList<TaskData> bfsQueue = new LinkedList<>();
        final LinkedList<IsTransposedEdge<TaskData>> bfsEdgeQueue = new LinkedList<>();
        final HashMap<IsTransposedEdge<TaskData>, IsTransposedEdge<TaskData>> prePathMap = new HashMap<>();

        while (true) {
            {
                final boolean[] isDiscovered = new boolean[dataIndex.size()];
                bfsQueue.clear();
                bfsEdgeQueue.clear();
                prePathMap.clear();

                {
                    isDiscovered[dataIndex.get(source)] = true;

                    bfsQueue.addLast(source);
                    bfsEdgeQueue.addLast(null);
                }

                IsTransposedEdge<TaskData> lastEdge = null;
                // bfs
                while (!bfsQueue.isEmpty()) {
                    assert (bfsQueue.size() == bfsEdgeQueue.size());

                    final TaskData nodeData = bfsQueue.poll();
                    final int iNodeData = dataIndex.get(nodeData);

                    final GraphNode<TaskData> node = mainGraph.get(nodeData);
                    final GraphNode<TaskData> transposedNode = transposedGraph.get(nodeData);

                    final IsTransposedEdge<TaskData> nowIsTransposedFlow = bfsEdgeQueue.poll();

                    assert (bfsQueue.size() == bfsEdgeQueue.size());

                    if (nodeData.equals(sink)) {
                        lastEdge = nowIsTransposedFlow;
                        break;
                    }

                    for (final GraphEdge<TaskData> nextEdge : node.getEdges().values()) {
                        final GraphNode<TaskData> nextNode = nextEdge.getNode2();
                        final TaskData nextData = nextNode.getData();
                        final int iNextData = dataIndex.get(nextData);

                        assert (!nextData.equals(nodeData));

                        if (scc.containsKey(nextData)) {
                            continue;
                        }

                        if (isDiscovered[iNextData]) {
                            continue;
                        }

                        final int edgeFlow = flow[iNodeData][iNextData];
                        final int edgeCap = capacity[iNodeData];
                        final int edgeRemain = edgeCap - edgeFlow;

                        assert (edgeFlow >= 0);
                        assert (edgeRemain >= 0);

                        if (edgeRemain <= 0) {
                            continue;
                        }

                        isDiscovered[iNextData] = true;
                        bfsQueue.addLast(nextData);

                        final IsTransposedEdge<TaskData> nextIsTransposedFlow = new IsTransposedEdge<>(false, nextEdge);
                        bfsEdgeQueue.addLast(nextIsTransposedFlow);
                        prePathMap.put(nextIsTransposedFlow, nowIsTransposedFlow);
                    }

                    for (final GraphEdge<TaskData> nextTransposedEdge : transposedNode.getEdges().values()) {
                        final GraphNode<TaskData> nextTransposedNode = nextTransposedEdge.getNode2();
                        final TaskData nextTransposedData = nextTransposedNode.getData();
                        final int iNextTransposedData = dataIndex.get(nextTransposedData);

                        assert (!nextTransposedData.equals(nodeData));

                        if (scc.containsKey(nextTransposedNode.getData())) {
                            continue;
                        }

                        final int edgeTransposedFlow = flow[iNodeData][iNextTransposedData];
                        final int edgeTransposedRemain = BACK_FLOW_CAPACITY - edgeTransposedFlow;

                        assert (edgeTransposedFlow <= 0);
                        assert (edgeTransposedRemain >= 0);

                        if (edgeTransposedRemain <= 0) {
                            continue;
                        }

                        if (isDiscovered[iNextTransposedData]) {
                            continue;
                        }

                        isDiscovered[dataIndex.get(nodeData)] = true;
                        bfsQueue.addLast(nextTransposedData);

                        final IsTransposedEdge<TaskData> nextIsTransposedFlow = new IsTransposedEdge<>(true, nextTransposedEdge);
                        bfsEdgeQueue.addLast(nextIsTransposedFlow);
                        prePathMap.put(nextIsTransposedFlow, nowIsTransposedFlow);
                    }
                } // end bfs

                if (lastEdge == null) {
                    break;
                }

                assert (path.isEmpty());
                int minRemainCapacity = Integer.MAX_VALUE;

                while (lastEdge != null) {
                    final GraphEdge<TaskData> edge = lastEdge.getEdge();

                    final GraphNode<TaskData> from = edge.getNode1();
                    final TaskData fromData = from.getData();
                    final int iFromData = dataIndex.get(fromData);

                    final GraphNode<TaskData> to = edge.getNode2();
                    final TaskData toData = to.getData();
                    final int iToData = dataIndex.get(toData);

                    if (lastEdge.isTransposedEdge()) {
                        final int edgeTransposedFlow = flow[iFromData][iToData];
                        assert (edgeTransposedFlow < 0);

                        final int edgeTransposedRemain = BACK_FLOW_CAPACITY - edgeTransposedFlow;
                        assert (edgeTransposedRemain > 0);

                        minRemainCapacity = Math.min(minRemainCapacity, edgeTransposedRemain);
                    } else {
                        final int edgeCapacity = edge.getWeight();

                        final int edgeFlow = flow[iFromData][iToData];
                        assert (edgeFlow >= 0);

                        final int edgeRemain = edgeCapacity - edgeFlow;
                        assert (edgeRemain > 0);

                        minRemainCapacity = Math.min(minRemainCapacity, edgeRemain);
                    }
                    path.addFirst(lastEdge);

                    lastEdge = prePathMap.get(lastEdge);
                }

                while (!path.isEmpty()) {
                    final IsTransposedEdge<TaskData> isTransposedFlow = path.poll();
                    final GraphEdge<TaskData> edge = isTransposedFlow.getEdge();

                    final GraphNode<TaskData> from = edge.getNode1();
                    final TaskData fromData = from.getData();
                    final int iFromData = dataIndex.get(fromData);

                    final GraphNode<TaskData> to = edge.getNode2();
                    final TaskData toData = to.getData();
                    final int iToData = dataIndex.get(toData);

                    flow[iFromData][iToData] += minRemainCapacity;
                    flow[iToData][iFromData] -= minRemainCapacity;

//                    if (isTransposedFlow.isTransposedEdge()) {
//                        capacity[iFromData] += minRemainCapacity;
//                    } else {
//                        capacity[iFromData] -= minRemainCapacity;
//                    }
                }

                outTotalFlow += minRemainCapacity;
            }
        }

        return outTotalFlow;
    }
}