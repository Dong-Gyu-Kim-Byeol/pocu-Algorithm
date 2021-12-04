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

        final ArrayList<Task> ghostEdgeDataArray;
        final Task ghostData;
        {

            final LinkedList<GraphNode<Task>> searchNodes = new LinkedList<>();
            this.graph.bfs(true, skinData, false, searchNodes);

            int leafCapacitySum = 0;
            ghostEdgeDataArray = new ArrayList<>(searchNodes.size());
            for (final GraphNode<Task> node : searchNodes) {
                if (node.getEdges().size() == 0) {
                    ghostEdgeDataArray.add(node.getData());
                    leafCapacitySum += node.getData().getEstimate();
                }
            }

            final ArrayList<Integer> ghostEdgeWeightArray = new ArrayList<>(ghostEdgeDataArray.size());
            for (final Task edgeData : ghostEdgeDataArray) {
                ghostEdgeWeightArray.add(edgeData.getEstimate());
            }

            ghostData = new Task("GHOST", leafCapacitySum);
            this.graph.addTransposedNode(ghostData, ghostEdgeDataArray, ghostEdgeWeightArray, ghostEdgeWeightArray);
        }

        final int maxBonusCount = this.maxFlow(skinData, ghostData);

        this.graph.removeTransposedNode(ghostData, ghostEdgeDataArray);

        return maxBonusCount;
    }

    // max flow
    public final int maxFlow(final Task source,
                             final Task sink) {

        final HashMap<Task, Integer> dataIndex = this.graph.getDataIndex();
        final HashMap<Task, GraphNode<Task>> mainGraph = this.graph.getGraph();
        final HashMap<Task, GraphNode<Task>> transposedGraph = this.graph.getTransposedGraph();

        int outTotalFlow = 0;

        final int BACK_FLOW_CAPACITY = 0;
        final int[][] flow = new int[dataIndex.size()][dataIndex.size()];
        final int[] nodeFlowArray = new int[dataIndex.size()];
        final int[] nodeCapacityArray = new int[dataIndex.size()];
        {
            for (final GraphNode<Task> from : mainGraph.values()) {
                final Task fromData = from.getData();
                final int iFrom = dataIndex.get(fromData);
                nodeCapacityArray[iFrom] = fromData.getEstimate();
            }
        }

        final LinkedList<IsTransposedEdge<Task>> bfsEdgeQueue = new LinkedList<>();
        final HashMap<IsTransposedEdge<Task>, IsTransposedEdge<Task>> preEdgeMap = new HashMap<>();

        while (true) {
            {
                final boolean[] isDiscovered = new boolean[dataIndex.size()];
                bfsEdgeQueue.clear();
                preEdgeMap.clear();

                {
                    isDiscovered[dataIndex.get(source)] = true;
                    bfsEdgeQueue.addLast(new IsTransposedEdge<>(false, new GraphEdge<>(0, null, mainGraph.get(source))));
                }

                IsTransposedEdge<Task> lastEdge = null;
                // bfs
                while (!bfsEdgeQueue.isEmpty()) {
                    final IsTransposedEdge<Task> nowIsTransposedFlow = bfsEdgeQueue.poll();
                    final Task nodeData = nowIsTransposedFlow.getEdge().getNode2().getData();
                    final int iNodeData = dataIndex.get(nodeData);

                    if (nodeData.equals(sink)) {
                        lastEdge = nowIsTransposedFlow;
                        break;
                    }

                    final GraphNode<Task> transposedNode = transposedGraph.get(nodeData);
                    for (final GraphEdge<Task> nextTransposedEdge : transposedNode.getEdges().values()) {
                        final GraphNode<Task> nextTransposedNode = nextTransposedEdge.getNode2();
                        final Task nextTransposedData = nextTransposedNode.getData();
                        final int iNextTransposedData = dataIndex.get(nextTransposedData);

                        assert (!nextTransposedData.equals(nodeData));

//                        if (isSkipScc) {
//                            if (this.dataScc.containsKey(nextTransposedNode.getData())) {
//                                continue;
//                            }
//                        }

                        final int edgeTransposedFlow = flow[iNodeData][iNextTransposedData];
                        final int edgeTransposedRemain = BACK_FLOW_CAPACITY - edgeTransposedFlow;

                        assert (edgeTransposedFlow <= 0);
                        assert (edgeTransposedRemain >= 0);

                        if (edgeTransposedRemain <= 0) {
                            continue;
                        }

//                        if (isDiscovered[iNextTransposedData]) {
//                            continue;
//                        }
//
//                        isDiscovered[dataIndex.get(nodeData)] = true;

                        final IsTransposedEdge<Task> nextIsTransposedFlow = new IsTransposedEdge<>(true, nextTransposedEdge);
                        bfsEdgeQueue.addLast(nextIsTransposedFlow);
                        preEdgeMap.put(nextIsTransposedFlow, nowIsTransposedFlow);
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

//                        if (isSkipScc) {
//                            if (this.dataScc.containsKey(nextData)) {
//                                continue;
//                            }
//                        }

                        final int edgeFlow = flow[iNodeData][iNextData];
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

                        final IsTransposedEdge<Task> nextIsTransposedFlow = new IsTransposedEdge<>(false, nextEdge);
                        bfsEdgeQueue.addLast(nextIsTransposedFlow);
                        preEdgeMap.put(nextIsTransposedFlow, nowIsTransposedFlow);
                    }
                } // end bfs

                if (lastEdge == null) {
                    break;
                }

                int minRemainCapacity = Integer.MAX_VALUE;


                for (IsTransposedEdge<Task> isTransposedEdge = lastEdge; isTransposedEdge.getEdge().getNode1() != null; isTransposedEdge = preEdgeMap.get(isTransposedEdge)) {
                    final GraphEdge<Task> edge = isTransposedEdge.getEdge();

                    final GraphNode<Task> from = edge.getNode1();
                    final Task fromData = from.getData();
                    final int iFromData = dataIndex.get(fromData);

                    final GraphNode<Task> to = edge.getNode2();
                    final Task toData = to.getData();
                    final int iToData = dataIndex.get(toData);

                    if (isTransposedEdge.isTransposedEdge()) {
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

                        if (!preEdgeMap.get(isTransposedEdge).isTransposedEdge()) {
                            final int nodeCap = nodeCapacityArray[iFromData];

                            final int nodeFlow = nodeFlowArray[iFromData];
                            assert (nodeFlow >= 0);

                            final int nodeRemain = nodeCap - nodeFlow;
                            assert (nodeRemain > 0);

                            minRemainCapacity = Math.min(minRemainCapacity, nodeRemain);
                        }
                    }
                }

                for (IsTransposedEdge<Task> isTransposedFlow = lastEdge; isTransposedFlow.getEdge().getNode1() != null; isTransposedFlow = preEdgeMap.get(isTransposedFlow)) {
                    final GraphEdge<Task> edge = isTransposedFlow.getEdge();

                    final GraphNode<Task> from = edge.getNode1();
                    final Task fromData = from.getData();
                    final int iFromData = dataIndex.get(fromData);

                    final GraphNode<Task> to = edge.getNode2();
                    final Task toData = to.getData();
                    final int iToData = dataIndex.get(toData);

                    flow[iFromData][iToData] += minRemainCapacity;
                    flow[iToData][iFromData] -= minRemainCapacity;

                    if (!isTransposedFlow.isTransposedEdge()) {
                        nodeFlowArray[iFromData] += minRemainCapacity;
                        nodeFlowArray[iFromData] = Math.min(nodeCapacityArray[iFromData], nodeFlowArray[iFromData]);
                    }
                }

                outTotalFlow += minRemainCapacity;
            }
        }

        return outTotalFlow;
    }
}