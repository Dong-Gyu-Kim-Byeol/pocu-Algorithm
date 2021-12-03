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

                    edgeWeightArray.add(predecessor.getEstimate());
                }
            }

            this.graph = new Graph<>(true, taskDataArray, edgeArrayMap, edgeWeightArrayMap);
        }
    }

    // ---

    public final int findTotalManMonths(final String task) {
        assert (this.taskDataMap.containsKey(task));
        final TaskData startData = this.taskDataMap.get(task);

        int sum = 0;
        {
            final HashMap<TaskData, Boolean> scc = this.graph.getDataScc();
            final HashMap<TaskData, GraphNode<TaskData>> graph = this.graph.getGraph();
            final HashMap<TaskData, Integer> indexMap = this.graph.getIndexMap();


            final boolean[] isDiscovered = new boolean[indexMap.size()];
            final LinkedList<WeightNode<GraphNode<TaskData>>> bfsQueue = new LinkedList<>();
            {
                final GraphNode<TaskData> startNode = graph.get(startData);
                assert (!scc.containsKey(startNode.getData()));

                bfsQueue.addLast(new WeightNode<>(startData.getEstimate(), startNode));
                isDiscovered[indexMap.get(startData)] = true;
            }

            while (!bfsQueue.isEmpty()) {
                final WeightNode<GraphNode<TaskData>> weightNode = bfsQueue.poll();
                final GraphNode<TaskData> node = weightNode.getData();
                final TaskData data = node.getData();

                sum += data.getEstimate();

                for (final GraphNode<TaskData> nextNode : node.getNodes()) {
                    final TaskData nextData = nextNode.getData();

                    if (scc.containsKey(nextData)) {
                        continue;
                    }

                    if (isDiscovered[indexMap.get(nextData)]) {
                        continue;
                    }

                    isDiscovered[indexMap.get(nextData)] = true;
                    bfsQueue.addLast(new WeightNode<>(weightNode.getWeight() + nextData.getEstimate(), nextNode));
                }
            }
        }

        return sum;
    }

    public final int findMinDuration(final String task) {
        assert (this.taskDataMap.containsKey(task));
        final TaskData startData = this.taskDataMap.get(task);

        int max = 0;
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
                if (node.getNodes().size() == 0) {
                    max = Math.max(max, weightNode.getWeight());
                }

                for (final GraphNode<TaskData> nextNode : node.getNodes()) {
                    final TaskData nextData = nextNode.getData();

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
        final TaskData skinData = this.taskDataMap.get(task);

        final ArrayList<TaskData> ghostEdgeDataArray;
        final TaskData ghostData;
        {

            final LinkedList<GraphNode<TaskData>> searchNodes = new LinkedList<>();
            this.graph.bfs(true, skinData, false, searchNodes);

            int leafCapacitySum = 0;
            ghostEdgeDataArray = new ArrayList<>(searchNodes.size());
            for (final GraphNode<TaskData> node : searchNodes) {
                if (node.getNodes().size() == 0) {
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

//        final int maxBonusCount = this.graph.maxFlow(false, skinData, ghostData, false);
        final int maxBonusCount = this.graph.maxFlow(false, ghostData, skinData, true);
//        final int maxBonusCount = this.maxFlow(skinData, ghostData);

        this.graph.removeTransposedNode(ghostData);

        return maxBonusCount;
    }

//    // max flow
//    public final int maxFlow(final TaskData source,
//                             final TaskData sink) {
//
//        final HashMap<TaskData, Integer> dataIndex = this.graph.getDataIndex();
//
//        int outTotalFlow = 0;
//
//        final int BACK_FLOW_CAPACITY = 0;
//
//        final HashMap<TaskData, AdjacencyListGraphNode<TaskData>> mainGraph = this.graph.getTransposedGraph();
//        final HashMap<TaskData, AdjacencyListGraphNode<TaskData>> transposedGraph = this.graph.getGraph();
////        final HashMap<TaskData, GraphNode<TaskData>> mainGraph = this.graph.getGraph();
////        final HashMap<TaskData, GraphNode<TaskData>> transposedGraph = this.graph.getTransposedGraph();
//
//        final int[][] flow = new int[dataIndex.size()][dataIndex.size()];
//        final int[] nodeFlowArray = new int[dataIndex.size()];
//        final int[] nodeBackFlowArray = new int[dataIndex.size()];
//
//        final int[] capacity = new int[dataIndex.size()];
//        for (final AdjacencyListGraphNode<TaskData> node : mainGraph.values()) {
//            final TaskData data = node.getData();
//            final int iData = dataIndex.get(data);
//            capacity[iData] = data.getEstimate();
//        }
//        final LinkedList<IsTransposedEdge<TaskData>> path = new LinkedList<>();
//
//        final LinkedList<TaskData> bfsQueue = new LinkedList<>();
//        final LinkedList<IsTransposedEdge<TaskData>> bfsEdgeQueue = new LinkedList<>();
//        final HashMap<IsTransposedEdge<TaskData>, IsTransposedEdge<TaskData>> prePathMap = new HashMap<>();
//
//        while (true) {
//            {
//                final boolean[] isDiscovered = new boolean[dataIndex.size()];
//                bfsQueue.clear();
//                bfsEdgeQueue.clear();
//                prePathMap.clear();
//
//                {
//                    bfsQueue.addLast(source);
//                    bfsEdgeQueue.addLast(null);
//                }
//
//                IsTransposedEdge<TaskData> lastEdge = null;
//                // bfs
//                while (!bfsQueue.isEmpty()) {
//                    assert (bfsQueue.size() == bfsEdgeQueue.size());
//
//                    final TaskData nodeData = bfsQueue.poll();
//                    final int iNodeData = dataIndex.get(nodeData);
//
//                    final AdjacencyListGraphNode<TaskData> node = mainGraph.get(nodeData);
//                    final AdjacencyListGraphNode<TaskData> transposedNode = transposedGraph.get(nodeData);
//
//                    final IsTransposedEdge<TaskData> nowIsTransposedFlow = bfsEdgeQueue.poll();
//
//                    assert (bfsQueue.size() == bfsEdgeQueue.size());
//
//
//                    if (nodeData.equals(sink)) {
//                        lastEdge = nowIsTransposedFlow;
//                        break;
//                    }
//
//                    final int nodeFlow = nodeFlowArray[iNodeData];
//                    final int nodeCap = capacity[iNodeData];
//                    final int nodeRemain = nodeCap - nodeFlow;
//
//                    assert (nodeFlow >= 0);
//                    assert (nodeRemain >= 0);
//
//                    if (nodeRemain > 0 || nowIsTransposedFlow == null || nowIsTransposedFlow.isTransposedEdge()) {
//                        for (final AdjacencyListGraphEdge<TaskData> nextEdge : node.getEdges().values()) {
//                            final AdjacencyListGraphNode<TaskData> nextNode = nextEdge.getNode2();
//                            final TaskData nextData = nextNode.getData();
//                            final int iNextData = dataIndex.get(nextData);
//
//                            assert (!nextData.equals(nodeData));
//
//                            final int edgeFlow = flow[iNodeData][iNextData];
//                            final int edgeCap = capacity[iNextData];
//                            final int edgeRemain = edgeCap - edgeFlow;
//
//                            assert (edgeFlow >= 0);
//                            assert (edgeRemain >= 0);
//
//                            if (edgeRemain <= 0) {
//                                continue;
//                            }
//
//                            if (isDiscovered[iNextData]) {
//                                continue;
//                            }
//                            isDiscovered[iNextData] = true;
//
//                            bfsQueue.addLast(nextData);
//
//                            final IsTransposedEdge<TaskData> nextIsTransposedFlow = new IsTransposedEdge<>(false, nextEdge);
//                            bfsEdgeQueue.addLast(nextIsTransposedFlow);
//                            prePathMap.put(nextIsTransposedFlow, nowIsTransposedFlow);
//                        }
//                    }
//
////                    final int transposedNodeFlow = nodeBackFlowArray[iNodeData];
////                    final int transposedNodeRemain = BACK_FLOW_CAPACITY - transposedNodeFlow;
////
////                    assert (transposedNodeFlow <= 0);
////                    assert (transposedNodeRemain >= 0);
////
////                    if (transposedNodeRemain > 0 || nowIsTransposedFlow == null || !nowIsTransposedFlow.isTransposedEdge()) {
//                        for (final AdjacencyListGraphEdge<TaskData> nextTransposedEdge : transposedNode.getEdges().values()) {
//                            final AdjacencyListGraphNode<TaskData> nextTransposedNode = nextTransposedEdge.getNode2();
//                            final TaskData nextTransposedData = nextTransposedNode.getData();
//                            final int iNextTransposedData = dataIndex.get(nextTransposedData);
//
//                            assert (!nextTransposedData.equals(nodeData));
//
//                            final int edgeTransposedFlow = flow[iNodeData][iNextTransposedData];
//                            final int edgeTransposedRemain = BACK_FLOW_CAPACITY - edgeTransposedFlow;
//
//                            assert (edgeTransposedFlow <= 0);
//                            assert (edgeTransposedRemain >= 0);
//
//                            if (edgeTransposedRemain <= 0) {
//                                continue;
//                            }
//
//                            if (isDiscovered[iNextTransposedData]) {
//                                continue;
//                            }
//                            isDiscovered[iNextTransposedData] = true;
//
//                            bfsQueue.addLast(nextTransposedData);
//
//                            final IsTransposedEdge<TaskData> nextIsTransposedFlow = new IsTransposedEdge<>(true, nextTransposedEdge);
//                            bfsEdgeQueue.addLast(nextIsTransposedFlow);
//                            prePathMap.put(nextIsTransposedFlow, nowIsTransposedFlow);
//                        }
////                    }
//                } // end bfs
//
//                if (lastEdge == null) {
//                    break;
//                }
//
//                assert (path.isEmpty());
//                int minRemainCapacity = Integer.MAX_VALUE;
//
//                while (lastEdge != null) {
//                    final AdjacencyListGraphEdge<TaskData> edge = lastEdge.getEdge();
//
//                    final AdjacencyListGraphNode<TaskData> from = edge.getNode1();
//                    final TaskData fromData = from.getData();
//                    final int iFromData = dataIndex.get(fromData);
//
//                    final AdjacencyListGraphNode<TaskData> to = edge.getNode2();
//                    final TaskData toData = to.getData();
//                    final int iToData = dataIndex.get(toData);
//
//                    if (lastEdge.isTransposedEdge()) {
//                        final int edgeTransposedFlow = flow[iFromData][iToData];
//                        assert (edgeTransposedFlow < 0);
//
//                        final int edgeTransposedRemain = BACK_FLOW_CAPACITY - edgeTransposedFlow;
//                        assert (edgeTransposedRemain > 0);
//
//                        minRemainCapacity = Math.min(minRemainCapacity, edgeTransposedRemain);
//                    } else {
//                        final int edgeCapacity = capacity[iToData];
//
//                        final int edgeFlow = flow[iFromData][iToData];
//                        assert (edgeFlow >= 0);
//
//                        final int edgeRemain = edgeCapacity - edgeFlow;
//                        assert (edgeRemain > 0);
//
//                        minRemainCapacity = Math.min(minRemainCapacity, edgeRemain);
//                    }
//                    path.addFirst(lastEdge);
//
//                    lastEdge = prePathMap.get(lastEdge);
//                }
//
//                while (!path.isEmpty()) {
//                    final IsTransposedEdge<TaskData> isTransposedFlow = path.poll();
//                    final AdjacencyListGraphEdge<TaskData> edge = isTransposedFlow.getEdge();
//
//                    final AdjacencyListGraphNode<TaskData> from = edge.getNode1();
//                    final TaskData fromData = from.getData();
//                    final int iFromData = dataIndex.get(fromData);
//
//                    final AdjacencyListGraphNode<TaskData> to = edge.getNode2();
//                    final TaskData toData = to.getData();
//                    final int iToData = dataIndex.get(toData);
//
//                    flow[iFromData][iToData] += minRemainCapacity;
//                    flow[iToData][iFromData] -= minRemainCapacity;
//
//                    if (isTransposedFlow.isTransposedEdge()) {
//                        nodeFlowArray[iFromData] -= minRemainCapacity;
//                        nodeBackFlowArray[iFromData] += minRemainCapacity;
//                    } else {
//                        nodeFlowArray[iFromData] += minRemainCapacity;
//                        nodeBackFlowArray[iFromData] -= minRemainCapacity;
//                    }
//                }
//
//                final int iSinkData = dataIndex.get(sink);
//                nodeFlowArray[iSinkData] += minRemainCapacity;
//                nodeBackFlowArray[iSinkData] -= minRemainCapacity;
//
//                outTotalFlow += minRemainCapacity;
//            }
//        }
//
//        return outTotalFlow;
//    }
}