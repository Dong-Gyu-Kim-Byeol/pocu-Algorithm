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

            final HashMap<Task, Integer> pathSumMap = new HashMap<>(this.graph.nodeCount());
            final LinkedList<GraphNode<Task>> bfsQueue = new LinkedList<>();
            {
                final GraphNode<Task> startNode = graph.get(startData);
                assert (!scc.containsKey(startNode.getData()));

                bfsQueue.addLast(startNode);
                pathSumMap.put(startData, startData.getEstimate());
            }

            while (!bfsQueue.isEmpty()) {
                final GraphNode<Task> node = bfsQueue.poll();
                final Task nodeData = node.getData();
                if (node.getEdges().size() == 0) {
                    max = Math.max(max, pathSumMap.get(nodeData));
                }

                for (final GraphEdge<Task> edge : node.getEdges().values()) {
                    final GraphNode<Task> nextNode = edge.getNode2();
                    final Task nextData = nextNode.getData();

                    if (scc.containsKey(nextData)) {
                        continue;
                    }

                    bfsQueue.addLast(nextNode);
                    pathSumMap.put(nextData, pathSumMap.get(nodeData) + nextData.getEstimate());
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
            final ArrayList<Integer> leafNodeToGhostEdgeWeightArray = new ArrayList<>(ghostEdgeDataArray.size());
            for (final Task edgeData : ghostEdgeDataArray) {
                ghostEdgeWeightArray.add(edgeData.getEstimate());
                leafNodeToGhostEdgeWeightArray.add(leafCapacitySum);
            }

            ghostData = new Task("GHOST", leafCapacitySum);
            this.graph.addTransposedNode(ghostData, ghostEdgeDataArray, ghostEdgeWeightArray, leafNodeToGhostEdgeWeightArray);
        }

//        final int maxBonusCount = this.maxFlow(skinData, ghostData);
        final int maxBonusCount = this.graph.maxFlow(false, skinData, ghostData, false);

        this.graph.removeTransposedNode(ghostData);

        return maxBonusCount;
    }

    // max flow
    public final int maxFlow(final Task source,
                             final Task sink) {

        final HashMap<Task, Integer> dataIndex = this.graph.getIndexMap();
        final HashMap<Task, GraphNode<Task>> mainGraph = this.graph.getGraph();
        final HashMap<Task, GraphNode<Task>> transposedGraph = this.graph.getTransposedGraph();

        int outTotalFlow = 0;

        final int BACK_FLOW_CAPACITY = 0;
        final HashMap<GraphEdge<Task>, Integer> mainFlow = new HashMap<>(dataIndex.size());
        {
            {
                final boolean[] isDiscovered = new boolean[dataIndex.size()];

                for (final GraphNode<Task> startNode : mainGraph.values()) {
                    final LinkedList<GraphNode<Task>> bfsQueue = new LinkedList<>();

                    {
                        if (isDiscovered[dataIndex.get(startNode.getData())]) {
                            continue;
                        }

                        isDiscovered[dataIndex.get(startNode.getData())] = true;
                        bfsQueue.addLast(startNode);
                    }

                    while (!bfsQueue.isEmpty()) {
                        final GraphNode<Task> node = bfsQueue.poll();

                        for (final GraphEdge<Task> nextEdge : node.getEdges().values()) {
                            mainFlow.put(nextEdge, 0);

                            if (isDiscovered[dataIndex.get(nextEdge.getNode2().getData())]) {
                                continue;
                            }

                            isDiscovered[dataIndex.get(nextEdge.getNode2().getData())] = true;
                            bfsQueue.addLast(nextEdge.getNode2());
                        }
                    }
                }
            }
        }

        final HashMap<GraphEdge<Task>, Integer> transposedFlow = new HashMap<>(dataIndex.size());
        {
            {
                final boolean[] isDiscovered = new boolean[dataIndex.size()];

                for (final GraphNode<Task> startNode : transposedGraph.values()) {
                    final LinkedList<GraphNode<Task>> bfsQueue = new LinkedList<>();

                    {
                        if (isDiscovered[dataIndex.get(startNode.getData())]) {
                            continue;
                        }

                        isDiscovered[dataIndex.get(startNode.getData())] = true;
                        bfsQueue.addLast(startNode);
                    }

                    while (!bfsQueue.isEmpty()) {
                        final GraphNode<Task> node = bfsQueue.poll();

                        for (final GraphEdge<Task> nextEdge : node.getEdges().values()) {
                            transposedFlow.put(nextEdge, 0);

                            if (isDiscovered[dataIndex.get(nextEdge.getNode2().getData())]) {
                                continue;
                            }

                            isDiscovered[dataIndex.get(nextEdge.getNode2().getData())] = true;
                            bfsQueue.addLast(nextEdge.getNode2());
                        }
                    }
                }
            }
        }

        final int[] nodeFlowArray = new int[dataIndex.size()];
        final int[] nodeCapacityArray = new int[dataIndex.size()];
        {
            for (final GraphNode<Task> from : mainGraph.values()) {
                final Task fromData = from.getData();
                final int iFrom = dataIndex.get(fromData);
                nodeCapacityArray[iFrom] = fromData.getEstimate();
            }
        }

        final LinkedList<GraphEdge<Task>> bfsEdgeQueue = new LinkedList<>();
        final HashMap<GraphEdge<Task>, GraphEdge<Task>> preEdgeMap = new HashMap<>(dataIndex.size());

        while (true) {

            final boolean[] isDiscovered = new boolean[dataIndex.size()];
            bfsEdgeQueue.clear();
            preEdgeMap.clear();

            {
                isDiscovered[dataIndex.get(source)] = true;
                bfsEdgeQueue.addLast(new GraphEdge<>(false, 0, null, mainGraph.get(source)));
            }

            GraphEdge<Task> lastEdge = null;
            // bfs
            while (!bfsEdgeQueue.isEmpty()) {
                final GraphEdge<Task> nowIsTransposedFlow = bfsEdgeQueue.poll();
                final Task nodeData = nowIsTransposedFlow.getNode2().getData();
                final int iNodeData = dataIndex.get(nodeData);

                if (nodeData.equals(sink)) {
                    lastEdge = nowIsTransposedFlow;
                    break;
                }

                final GraphNode<Task> transposedNode = transposedGraph.get(nodeData);
                for (final GraphEdge<Task> nextTransposedEdge : transposedNode.getEdges().values()) {
                    final GraphNode<Task> nextTransposedNode = nextTransposedEdge.getNode2();
                    final Task nextTransposedData = nextTransposedNode.getData();

                    assert (!nextTransposedData.equals(nodeData));

                    final int edgeTransposedFlow = transposedFlow.get(nextTransposedEdge);
                    final int edgeTransposedRemain = BACK_FLOW_CAPACITY - edgeTransposedFlow;

                    assert (edgeTransposedFlow <= 0);
                    assert (edgeTransposedRemain >= 0);

                    if (edgeTransposedRemain <= 0) {
                        continue;
                    }

                    bfsEdgeQueue.addLast(nextTransposedEdge);
                    preEdgeMap.put(nextTransposedEdge, nowIsTransposedFlow);
                }


                final GraphNode<Task> node = mainGraph.get(nodeData);
                final int nodeFlow = nodeFlowArray[iNodeData];
                final int nodeCap = nodeCapacityArray[iNodeData];
                final int nodeRemain = nodeCap - nodeFlow;

                assert (nodeFlow >= 0);
                assert (nodeRemain >= 0);

                if (nodeRemain <= 0 && !nowIsTransposedFlow.isTransposedEdge()) {
                    continue;
                }

                for (final GraphEdge<Task> nextEdge : node.getEdges().values()) {
                    final GraphNode<Task> nextNode = nextEdge.getNode2();
                    final Task nextData = nextNode.getData();
                    final int iNextData = dataIndex.get(nextData);

                    assert (!nextData.equals(nodeData));

                    final int edgeFlow = mainFlow.get(nextEdge);
                    final int edgeCap = nextEdge.getWeight();
                    final int edgeRemain = edgeCap - edgeFlow;

                    assert (edgeFlow >= 0);
                    assert (edgeRemain >= 0);

                    if (edgeRemain <= 0) {
                        continue;
                    }

                    if (isDiscovered[iNextData]) {
                        continue;
                    }

                    isDiscovered[iNextData] = true;

                    bfsEdgeQueue.addLast(nextEdge);
                    preEdgeMap.put(nextEdge, nowIsTransposedFlow);
                }
            } // end bfs

            if (lastEdge == null) {
                break;
            }

            int minRemainCapacity = Integer.MAX_VALUE;


            for (GraphEdge<Task> edge = lastEdge; edge.getNode1() != null; edge = preEdgeMap.get(edge)) {
                final GraphNode<Task> from = edge.getNode1();
                final Task fromData = from.getData();
                final int iFromData = dataIndex.get(fromData);

                if (edge.isTransposedEdge()) {
                    final int edgeTransposedFlow = transposedFlow.get(edge);
                    assert (edgeTransposedFlow < 0);

                    final int edgeTransposedRemain = BACK_FLOW_CAPACITY - edgeTransposedFlow;
                    assert (edgeTransposedRemain > 0);

                    minRemainCapacity = Math.min(minRemainCapacity, edgeTransposedRemain);
                } else {
                    final int edgeCapacity = edge.getWeight();

                    final int edgeFlow = mainFlow.get(edge);
                    assert (edgeFlow >= 0);

                    final int edgeRemain = edgeCapacity - edgeFlow;
                    assert (edgeRemain > 0);

                    minRemainCapacity = Math.min(minRemainCapacity, edgeRemain);

                    if (!preEdgeMap.get(edge).isTransposedEdge()) {
                        final int nodeCap = nodeCapacityArray[iFromData];

                        final int nodeFlow = nodeFlowArray[iFromData];
                        assert (nodeFlow >= 0);

                        final int nodeRemain = nodeCap - nodeFlow;
                        assert (nodeRemain > 0);

                        minRemainCapacity = Math.min(minRemainCapacity, nodeRemain);
                    }
                }
            }

            for (GraphEdge<Task> edge = lastEdge; edge.getNode1() != null; edge = preEdgeMap.get(edge)) {
                final GraphNode<Task> from = edge.getNode1();
                final Task fromData = from.getData();
                final int iFromData = dataIndex.get(fromData);

                if (edge.isTransposedEdge()) {
                    transposedFlow.put(edge, transposedFlow.get(edge) + minRemainCapacity);
                    final GraphEdge<Task> mainEdge = mainGraph.get(edge.getNode2().getData()).getEdges().get(edge.getNode1().getData());
                    mainFlow.put(mainEdge, mainFlow.get(mainEdge) - minRemainCapacity);
                } else {
                    mainFlow.put(edge, mainFlow.get(edge) + minRemainCapacity);
                    final GraphEdge<Task> transposedEdge = transposedGraph.get(edge.getNode2().getData()).getEdges().get(edge.getNode1().getData());
                    transposedFlow.put(transposedEdge, transposedFlow.get(transposedEdge) - minRemainCapacity);
                }

                if (!edge.isTransposedEdge()) {
                    nodeFlowArray[iFromData] += minRemainCapacity;
                    nodeFlowArray[iFromData] = Math.min(nodeCapacityArray[iFromData], nodeFlowArray[iFromData]);
                }
            }

            outTotalFlow += minRemainCapacity;

        }

        return outTotalFlow;
    }
}