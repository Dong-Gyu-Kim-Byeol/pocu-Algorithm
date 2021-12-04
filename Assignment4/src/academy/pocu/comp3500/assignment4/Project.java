package academy.pocu.comp3500.assignment4;

import academy.pocu.comp3500.assignment4.project.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public final class Project {
    private final HashMap<String, Integer> dataIndex;
    private final HashMap<Integer, String> titleMap;

    private final HashMap<String, Task> taskGraph;
    private final HashMap<String, TransposedTask> transposedTaskGraph;

    private final HashMap<String, Boolean> dataScc;

    // ---

    public Project(final Task[] tasks) {
        this.dataIndex = new HashMap<>(tasks.length + 1);
        this.titleMap = new HashMap<>(tasks.length + 1);
        this.taskGraph = new HashMap<>(tasks.length + 1);
        this.transposedTaskGraph = new HashMap<>(tasks.length + 1);
        this.dataScc = new HashMap<>(tasks.length + 1);

        // create graph
        {
            // create taskDataArray
            int i = 0;
            for (final Task task : tasks) {
                this.taskGraph.put(task.getTitle(), task);
                this.dataIndex.put(task.getTitle(), i);
                this.titleMap.put(i, task.getTitle());
                ++i;
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

            // TransposedGraph
            {
                for (final Task data : tasks) {
                    final TransposedTask transposedNode = new TransposedTask(data.getTitle(), data.getEstimate());
                    this.transposedTaskGraph.put(transposedNode.getTitle(), transposedNode);
                }

                for (final Task toData : tasks) {
                    final ArrayList<Task> fromDataEdgeArray = edgeArrayMap.get(toData);

                    for (final Task fromDataEdge : fromDataEdgeArray) {
                        assert (this.transposedTaskGraph.containsKey(fromDataEdge.getTitle()));

                        final TransposedTask from = this.transposedTaskGraph.get(fromDataEdge.getTitle());

                        from.addNext(this.transposedTaskGraph.get(toData.getTitle()));
                    }
                }
            }
        }

        kosarajuScc();
    }

    // ---

    public final int findTotalManMonths(final String task) {
        assert (this.taskGraph.containsKey(task));
        final Task startNode = this.taskGraph.get(task);

        int sum = 0;
        {
            final LinkedList<Task> bfsQueue = new LinkedList<>();
            final boolean[] isDiscovered = new boolean[this.dataIndex.size()];
            {
                assert (!this.dataScc.containsKey(startNode.getTitle()));

                isDiscovered[this.dataIndex.get(startNode.getTitle())] = true;
                bfsQueue.addLast(startNode);
            }

            while (!bfsQueue.isEmpty()) {
                final Task node = bfsQueue.poll();

                sum += node.getEstimate();

                for (final Task nextNode : node.getPredecessors()) {
                    if (this.dataScc.containsKey(nextNode.getTitle())) {
                        continue;
                    }

                    if (isDiscovered[this.dataIndex.get(nextNode.getTitle())]) {
                        continue;
                    }

                    isDiscovered[this.dataIndex.get(nextNode.getTitle())] = true;
                    bfsQueue.addLast(nextNode);
                }
            }
        }

        return sum;
    }

    public final int findMinDuration(final String task) {
        assert (this.taskGraph.containsKey(task));
        final Task startNode = this.taskGraph.get(task);

        int max = 0;
        {
            final LinkedList<WeightNode<Task>> bfsQueue = new LinkedList<>();
            {
                assert (!this.dataScc.containsKey(startNode.getTitle()));

                bfsQueue.addLast(new WeightNode<>(startNode.getEstimate(), startNode));
            }

            while (!bfsQueue.isEmpty()) {
                final WeightNode<Task> weightNode = bfsQueue.poll();

                final Task node = weightNode.getData();
                if (node.getPredecessors().size() == 0) {
                    max = Math.max(max, weightNode.getWeight());
                }

                for (final Task nextNode : node.getPredecessors()) {
                    final String nextData = nextNode.getTitle();

                    if (this.dataScc.containsKey(nextData)) {
                        continue;
                    }

                    bfsQueue.addLast(new WeightNode<>(weightNode.getWeight() + nextNode.getEstimate(), nextNode));
                }
            }
        }

        return max;
    }

    public final int findMaxBonusCount(final String task) {
        assert (this.taskGraph.containsKey(task));
        final Task skinNode = this.taskGraph.get(task);

        final ArrayList<Task> ghostEdgeDataArray = new ArrayList<>();
        final TransposedTask ghostData;
        {
            {
                int leafCapacitySum = 0;
                final LinkedList<Task> bfsQueue = new LinkedList<>();
                final boolean[] isDiscovered = new boolean[this.dataIndex.size()];

                {
                    assert (!this.dataScc.containsKey(skinNode.getTitle()));

                    isDiscovered[this.dataIndex.get(skinNode.getTitle())] = true;
                    bfsQueue.addLast(skinNode);
                }

                while (!bfsQueue.isEmpty()) {
                    final Task node = bfsQueue.poll();

                    if (node.getPredecessors().size() == 0) {
                        leafCapacitySum += node.getEstimate();
                        ghostEdgeDataArray.add(node);
                    }

                    for (final Task next : node.getPredecessors()) {
                        if (this.dataScc.containsKey(next.getTitle())) {
                            continue;
                        }

                        if (isDiscovered[this.dataIndex.get(next.getTitle())]) {
                            continue;
                        }

                        isDiscovered[this.dataIndex.get(next.getTitle())] = true;
                        bfsQueue.addLast(next);
                    }
                }

                ghostData = new TransposedTask("__GHOST__NODE__", leafCapacitySum);
            }

            // add ghost
            {
                this.dataIndex.put(ghostData.getTitle(), this.dataIndex.size());
                this.titleMap.put(this.titleMap.size(), ghostData.getTitle());

                // addTransposedNodeInTransposedGraph
                {
                    this.transposedTaskGraph.put(ghostData.getTitle(), ghostData);

                    for (final Task taskNext : ghostEdgeDataArray) {
                        assert (this.transposedTaskGraph.containsKey(taskNext.getTitle()));

                        ghostData.addNext(this.transposedTaskGraph.get(taskNext.getTitle()));
                    }
                }
            }
        }

        final int maxBonusCount = this.maxFlow(ghostData.getTitle(), skinNode.getTitle());

        // remove ghost
        {
            this.dataIndex.remove(ghostData.getTitle());
            this.titleMap.remove(this.titleMap.size() - 1);
            this.transposedTaskGraph.remove(ghostData.getTitle());
            ghostData.getNext().clear();
        }

        return maxBonusCount;
    }

    // ---

    // scc Kosaraju
    public void kosarajuScc() {
        // O(n + e) + O(n + e)

        final LinkedList<String> dfsPostOrderNodeReverseList = new LinkedList<>();
        dfsPostOrderReverseTask(false, dfsPostOrderNodeReverseList);

        // get scc groups with size > 1
        {
            final boolean[] transposedIsDiscovered = new boolean[this.dataIndex.size()];
            final LinkedList<String> scc = new LinkedList<>();

            for (final String data : dfsPostOrderNodeReverseList) {
                final TransposedTask transposedNode = this.transposedTaskGraph.get(data);

                if (transposedIsDiscovered[this.dataIndex.get(transposedNode.getTitle())]) {
                    continue;
                }

                dfsPostOrderReverseTransposedTaskRecursive(false, transposedNode.getTitle(), transposedIsDiscovered, scc);

                assert (scc.size() >= 1);

                if (scc.size() > 1) {
                    for (final String skip : scc) {
                        dataScc.put(skip, true);
                    }
                }

                scc.clear();
            }
        }
    }

    // dfs
    private void dfsPostOrderReverseTask(final boolean isSkipScc,
                                         final LinkedList<String> outPostOrderNodeReverseList) {
        // O(n + e)

        final boolean[] isDiscovered = new boolean[this.dataIndex.size()];

        for (final Task node : this.taskGraph.values()) {
            if (isSkipScc) {
                if (this.dataScc.containsKey(node.getTitle())) {
                    continue;
                }
            }

            if (isDiscovered[this.dataIndex.get(node.getTitle())]) {
                continue;
            }

            dfsPostOrderReverseTaskRecursive(isSkipScc, node.getTitle(), isDiscovered, outPostOrderNodeReverseList);
        }
    }

    private void dfsPostOrderReverseTaskRecursive(final boolean isSkipScc,
                                                  final String startData,
                                                  final boolean[] isDiscovered,
                                                  final LinkedList<String> outPostOrderNodeReverseList) {

        final Task startNode = this.taskGraph.get(startData);

        isDiscovered[this.dataIndex.get(startNode.getTitle())] = true;

        for (final Task edge : startNode.getPredecessors()) {
            if (isSkipScc) {
                if (this.dataScc.containsKey(edge.getTitle())) {
                    continue;
                }
            }

            if (isDiscovered[this.dataIndex.get(edge.getTitle())]) {
                continue;
            }

            dfsPostOrderReverseTaskRecursive(isSkipScc, edge.getTitle(), isDiscovered, outPostOrderNodeReverseList);
        }

        outPostOrderNodeReverseList.addFirst(startNode.getTitle());
    }

    private void dfsPostOrderReverseTransposedTaskRecursive(final boolean isSkipScc,
                                                            final String startData,
                                                            final boolean[] isDiscovered,
                                                            final LinkedList<String> outPostOrderNodeReverseList) {

        final TransposedTask startNode = this.transposedTaskGraph.get(startData);

        isDiscovered[this.dataIndex.get(startNode.getTitle())] = true;

        for (final TransposedTask edge : startNode.getNext()) {
            if (isSkipScc) {
                if (this.dataScc.containsKey(edge.getTitle())) {
                    continue;
                }
            }

            if (isDiscovered[this.dataIndex.get(edge.getTitle())]) {
                continue;
            }

            dfsPostOrderReverseTransposedTaskRecursive(isSkipScc, edge.getTitle(), isDiscovered, outPostOrderNodeReverseList);
        }

        outPostOrderNodeReverseList.addFirst(startNode.getTitle());
    }


    // max flow
    public final int maxFlow(final String source,
                             final String sink) {
        int outTotalFlow = 0;

        final HashMap<String, TransposedTask> mainGraph = this.transposedTaskGraph;
        final HashMap<String, Task> transposedGraph = this.taskGraph;

        final int BACK_FLOW_CAPACITY = 0;
        final int[][] flow = new int[dataIndex.size()][dataIndex.size()];

        final int[] nodeFlowArray = new int[dataIndex.size()];
        final int[] nodeCapacityArray = new int[dataIndex.size()];
        {
            for (final TransposedTask from : this.transposedTaskGraph.values()) {
                final String fromData = from.getTitle();
                final int iFrom = dataIndex.get(fromData);
                nodeCapacityArray[iFrom] = from.getEstimate();
            }
        }

        final LinkedList<IsTransposedTask> bfsEdgeQueue = new LinkedList<>();
        final HashMap<IsTransposedTask, IsTransposedTask> preEdgeMap = new HashMap<>();
        final int iSink = this.dataIndex.get(sink);

        while (true) {
            {
                final boolean[] isDiscovered = new boolean[dataIndex.size()];
                bfsEdgeQueue.clear();
                preEdgeMap.clear();

                {
                    isDiscovered[dataIndex.get(source)] = true;
                    bfsEdgeQueue.addLast(new IsTransposedTask(false, -1, this.dataIndex.get(source)));
                }

                IsTransposedTask lastEdge = null;
                // bfs
                while (!bfsEdgeQueue.isEmpty()) {
                    final IsTransposedTask nowIsTransposedFlow = bfsEdgeQueue.poll();
                    final int iNode = nowIsTransposedFlow.getTo();
                    final String nodeData = this.titleMap.get(iNode);

                    if (iNode == iSink) {
                        lastEdge = nowIsTransposedFlow;
                        break;
                    }

                    // back
                    final Task transposedNode = transposedGraph.get(nodeData);
                    if (transposedNode != null) {
                        for (final Task nextTransposedEdge : transposedNode.getPredecessors()) {
                            final int iNextTransposed = this.dataIndex.get(nextTransposedEdge.getTitle());
                            final int edgeTransposedFlow = flow[iNode][iNextTransposed];
                            final int edgeTransposedRemain = BACK_FLOW_CAPACITY - edgeTransposedFlow;

                            assert (edgeTransposedFlow <= 0);
                            assert (edgeTransposedRemain >= 0);

                            if (edgeTransposedRemain <= 0) {
                                continue;
                            }

//                            if (isDiscovered[iNext]) {
//                                continue;
//                            }
//
//                            isDiscovered[iNext] = true;

                            final IsTransposedTask nextIsTransposedFlow = new IsTransposedTask(true, iNode, iNextTransposed);
                            bfsEdgeQueue.addLast(nextIsTransposedFlow);
                            preEdgeMap.put(nextIsTransposedFlow, nowIsTransposedFlow);
                        }
                    }


                    final TransposedTask node = mainGraph.get(nodeData);
                    final int nodeFlow = nodeFlowArray[iNode];
                    final int nodeCap = nodeCapacityArray[iNode];
                    final int nodeRemain = nodeCap - nodeFlow;

                    assert (nodeFlow >= 0);
                    assert (nodeRemain >= 0);

                    if (nodeRemain <= 0 && !nowIsTransposedFlow.isTransposedEdge()) {
                        continue;
                    }

                    for (final TransposedTask nextNode : node.getNext()) {
                        final String nextData = nextNode.getTitle();
                        final int iNext = dataIndex.get(nextData);

                        assert (!nextData.equals(nodeData));

//                        if (isSkipScc) {
//                            if (this.dataScc.containsKey(nextData)) {
//                                continue;
//                            }
//                        }

                        final int edgeFlow = flow[iNode][iNext];
                        final int edgeCap = nextNode.getEstimate();
                        final int edgeRemain = edgeCap - edgeFlow;

                        assert (edgeFlow >= 0);
                        assert (edgeRemain >= 0);

                        if (edgeRemain <= 0) {
                            continue;
                        }

                        if (isDiscovered[iNext]) {
                            continue;
                        }

                        isDiscovered[iNext] = true;

                        final IsTransposedTask nextIsTransposedFlow = new IsTransposedTask(false, iNode, iNext);
                        bfsEdgeQueue.addLast(nextIsTransposedFlow);
                        preEdgeMap.put(nextIsTransposedFlow, nowIsTransposedFlow);
                    }
                } // end bfs

                if (lastEdge == null) {
                    break;
                }

                int minRemainCapacity = Integer.MAX_VALUE;

                for (IsTransposedTask isTransposedEdge = lastEdge; isTransposedEdge.getFrom() != -1; isTransposedEdge = preEdgeMap.get(isTransposedEdge)) {
                    final int iFrom = isTransposedEdge.getFrom();
                    final int iTo = isTransposedEdge.getTo();

                    if (isTransposedEdge.isTransposedEdge()) {
                        final int edgeTransposedFlow = flow[iFrom][iTo];
                        assert (edgeTransposedFlow < 0);

                        final int edgeTransposedRemain = BACK_FLOW_CAPACITY - edgeTransposedFlow;
                        assert (edgeTransposedRemain > 0);

                        minRemainCapacity = Math.min(minRemainCapacity, edgeTransposedRemain);
                    } else {
                        final int edgeCapacity = mainGraph.get(this.titleMap.get(iTo)).getEstimate();

                        final int edgeFlow = flow[iFrom][iTo];
                        assert (edgeFlow >= 0);

                        final int edgeRemain = edgeCapacity - edgeFlow;
                        assert (edgeRemain > 0);

                        minRemainCapacity = Math.min(minRemainCapacity, edgeRemain);

                        if (!preEdgeMap.get(isTransposedEdge).isTransposedEdge()) {
                            final int nodeCap = nodeCapacityArray[iFrom];

                            final int nodeFlow = nodeFlowArray[iFrom];
                            assert (nodeFlow >= 0);

                            final int nodeRemain = nodeCap - nodeFlow;
                            assert (nodeRemain > 0);

                            minRemainCapacity = Math.min(minRemainCapacity, nodeRemain);
                        }
                    }
                }

                for (IsTransposedTask isTransposedFlow = lastEdge; isTransposedFlow.getFrom() != -1; isTransposedFlow = preEdgeMap.get(isTransposedFlow)) {
                    final int iFrom = isTransposedFlow.getFrom();
                    final int iTo = isTransposedFlow.getTo();

                    flow[iFrom][iTo] += minRemainCapacity;
                    flow[iTo][iFrom] -= minRemainCapacity;

                    if (!isTransposedFlow.isTransposedEdge()) {
                        nodeFlowArray[iFrom] += minRemainCapacity;
                        nodeFlowArray[iFrom] = Math.min(nodeCapacityArray[iFrom], nodeFlowArray[iFrom]);
                    }
                }

                outTotalFlow += minRemainCapacity;
            }
        }

        return outTotalFlow;
    }
}