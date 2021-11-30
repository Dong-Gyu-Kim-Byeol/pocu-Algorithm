package academy.pocu.comp3500.assignment4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Function;

public final class DirectedGraph<D> {
    private final Function<D, Integer> getDataWeight;

    private final HashMap<D, Integer> dataIndex;
    private final HashMap<D, Boolean> dataScc;

    private final HashMap<D, DirectedGraphNode<D>> graph;
    private final HashMap<D, DirectedGraphNode<D>> transposedGraph;

    // ---

    public DirectedGraph(final ArrayList<D> dataArray, final HashMap<D, ArrayList<D>> dataArrayNodes, final Function<D, Integer> getDataWeight) {
        this.getDataWeight = getDataWeight;
        this.graph = createGraph(dataArray, dataArrayNodes);
        this.transposedGraph = createTransposedGraph(dataArray, dataArrayNodes);

        assert (this.graph.size() == dataArray.size());
        assert (this.transposedGraph.size() == dataArray.size());

        {
            this.dataIndex = new HashMap<>(dataArray.size());
            int i = 0;
            for (final D data : dataArray) {
                this.dataIndex.put(data, i++);
            }
        }

        this.dataScc = new HashMap<>();
        setScc();
    }

    // ---

    public final int nodeCount() {
        assert (this.dataIndex.size() == this.graph.size() && this.dataIndex.size() == this.transposedGraph.size());
        return this.dataIndex.size();
    }

    public final HashMap<D, Boolean> getDataScc() {
        return dataScc;
    }

    public final HashMap<D, DirectedGraphNode<D>> getGraph() {
        return graph;
    }

    public final HashMap<D, DirectedGraphNode<D>> getTransposedGraph() {
        return transposedGraph;
    }

    // setter
    public final void setScc() {
        this.dataScc.clear();
        kosarajuScc(this.dataScc);
    }

    public final void addNode(final D data, ArrayList<D> dataNodes) {
        addNodeInGraph(data, dataNodes);
        addNodeInTransposedGraph(data, dataNodes);

        assert (!this.dataIndex.containsKey(data));
        this.dataIndex.put(data, this.dataIndex.size());
    }

    public final void removeNode(final D data, ArrayList<D> dataNodes) {
        removeNodeInGraph(data, dataNodes);
        removeNodeInTransposedGraph(data, dataNodes);

        assert (this.dataIndex.containsKey(data));
        this.dataIndex.remove(data);
    }

    public final void addTransposedNode(final D transposedData, ArrayList<D> transposedDataNodes) {
        addTransposedNodeInGraph(transposedData, transposedDataNodes);
        addTransposedNodeInTransposedGraph(transposedData, transposedDataNodes);

        assert (!this.dataIndex.containsKey(transposedData));
        this.dataIndex.put(transposedData, this.dataIndex.size());
    }

    public final void removeTransposedNode(final D transposedData, ArrayList<D> transposedDataNodes) {
        removeTransposedNodeInGraph(transposedData, transposedDataNodes);
        removeTransposedNodeInTransposedGraph(transposedData, transposedDataNodes);

        assert (this.dataIndex.containsKey(transposedData));
        this.dataIndex.remove(transposedData);
    }

    // bfs
    public final void bfs(final boolean isSkipScc,
                          final D startData,
                          final boolean isTransposedGraph,
                          final LinkedList<DirectedGraphNode<D>> outNodeList) {

        final HashMap<D, DirectedGraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;
        final DirectedGraphNode<D> startNode = graph.get(startData);

        final boolean[] isDiscovered = new boolean[this.dataIndex.size()];

        final LinkedList<DirectedGraphNode<D>> bfsQueue = new LinkedList<>();

        {
            if (isSkipScc) {
                if (this.dataScc.containsKey(startNode.getData())) {
                    return;
                }
            }

            if (isDiscovered[this.dataIndex.get(startNode.getData())]) {
                return;
            }

            isDiscovered[this.dataIndex.get(startNode.getData())] = true;
            bfsQueue.addLast(startNode);
        }

        while (!bfsQueue.isEmpty()) {
            final DirectedGraphNode<D> node = bfsQueue.poll();

            outNodeList.addFirst(node);

            for (final DirectedGraphNode<D> nextNode : node.getNodes()) {
                if (isSkipScc) {
                    if (this.dataScc.containsKey(startNode.getData())) {
                        continue;
                    }
                }

                if (isDiscovered[this.dataIndex.get(nextNode.getData())]) {
                    continue;
                }

                isDiscovered[this.dataIndex.get(nextNode.getData())] = true;
                bfsQueue.addLast(nextNode);
            }
        }
    }

    public final LinkedList<WeightNode<DirectedGraphNode<D>>> bfsNodeAllPathSumWeightSkipSccAndWithoutDiscovered(final D startData,
                                                                                                                 final boolean isTransposedGraph) {

        final HashMap<D, DirectedGraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;
        final DirectedGraphNode<D> startNode = graph.get(startData);

        final LinkedList<WeightNode<DirectedGraphNode<D>>> bfsQueue = new LinkedList<>();
        final LinkedList<WeightNode<DirectedGraphNode<D>>> outEndSumEstimate = new LinkedList<>();

        {
            assert (!this.dataScc.containsKey(startNode.getData()));

            bfsQueue.addLast(new WeightNode<>(this.getDataWeight.apply(startNode.getData()), startNode));
        }

        while (!bfsQueue.isEmpty()) {
            final WeightNode<DirectedGraphNode<D>> weightNode = bfsQueue.poll();

            if (weightNode.getData().getNodes().size() == 0) {
                outEndSumEstimate.add(weightNode);
            }

            for (final DirectedGraphNode<D> nextNode : weightNode.getData().getNodes()) {
                if (this.dataScc.containsKey(nextNode.getData())) {
                    continue;
                }

                bfsQueue.addLast(new WeightNode<>(weightNode.getWeight() + this.getDataWeight.apply(nextNode.getData()), nextNode));
            }
        }

        return outEndSumEstimate;
    }

    // mac flow
    public final void maxFlow(final boolean isSkipScc,
                              final D source,
                              final D sink,
                              final boolean mainIsTransposedGraph,
                              final int[] outFlow,
                              final int[] outFlowIndex) {

        assert (outFlow.length == this.dataIndex.size());
        assert (outFlowIndex.length == 2);

        final int[] transposedFlow = new int[this.dataIndex.size()];
        final LinkedList<IsTransposedFlow<D>> path = new LinkedList<>();
        final int[] minRemainCapacity = new int[1];

        while (true) {
            path.clear();
            minRemainCapacity[0] = 0;

            bfsShortestPathSourceToSinkSkipZeroFlow(isSkipScc, source, sink, outFlow, transposedFlow, mainIsTransposedGraph, path, minRemainCapacity);

            if (path.isEmpty()) {
                break;
            }

            for (final IsTransposedFlow<D> isTransposedFlow : path) {
                final int index = this.dataIndex.get(isTransposedFlow.getData());
                if (isTransposedFlow.isTransposedFlow()) {
                    transposedFlow[index] += minRemainCapacity[0];
                    outFlow[index] -= minRemainCapacity[0];
                } else {
                    outFlow[index] += minRemainCapacity[0];
                    transposedFlow[index] -= minRemainCapacity[0];
                }
            }
        }

        assert (outFlow[this.dataIndex.get(source)] == outFlow[this.dataIndex.get(sink)]);

        outFlowIndex[0] = this.dataIndex.get(source);
        outFlowIndex[1] = this.dataIndex.get(sink);
    }

    public final void bfsShortestPathSourceToSinkSkipZeroFlow(final boolean isSkipScc,
                                                              final D source,
                                                              final D sink,
                                                              final int[] flow,
                                                              final int[] transposedFlow,
                                                              final boolean mainIsTransposedGraph,
                                                              final LinkedList<IsTransposedFlow<D>> outPath,
                                                              final int[] outMinRemainCapacity) {

        final HashMap<D, DirectedGraphNode<D>> mainGraph = mainIsTransposedGraph ? this.transposedGraph : this.graph;
        final HashMap<D, DirectedGraphNode<D>> transposedGraph = !mainIsTransposedGraph ? this.transposedGraph : this.graph;

        final int BACK_FLOW_CAPACITY = 0;

        assert (outPath.isEmpty());
        assert (outMinRemainCapacity.length == 1);

        final boolean[] isDiscovered = new boolean[this.dataIndex.size()];

        final LinkedList<IsTransposedFlow<D>> bfsQueue = new LinkedList<>();

        final HashMap<IsTransposedFlow<D>, IsTransposedFlow<D>> prePath = new HashMap<>();

        {
            if (isSkipScc) {
                if (this.dataScc.containsKey(source)) {
                    return;
                }
            }

            isDiscovered[this.dataIndex.get(source)] = true;
            final IsTransposedFlow<D> sourceIsTransposedFlow = new IsTransposedFlow<>(false, source);
            bfsQueue.addLast(sourceIsTransposedFlow);
            prePath.put(sourceIsTransposedFlow, null);
        }

        IsTransposedFlow<D> isTransposedFlow = null;
        while (!bfsQueue.isEmpty()) {
            isTransposedFlow = bfsQueue.poll();

            final DirectedGraphNode<D> node = mainGraph.get(isTransposedFlow.getData());
            final DirectedGraphNode<D> transposedNode = transposedGraph.get(isTransposedFlow.getData());
            final D nodeData = node.getData();

            if (isTransposedFlow.getData().equals(sink)) {
                break;
            }

            for (final DirectedGraphNode<D> nextNode : node.getNodes()) {
                final D nextData = nextNode.getData();

                if (isSkipScc) {
                    if (this.dataScc.containsKey(nextNode.getData())) {
                        continue;
                    }
                }

                if (isDiscovered[this.dataIndex.get(nextData)]) {
                    continue;
                }

                assert (flow[this.dataIndex.get(nodeData)] >= 0);
                if (this.getDataWeight.apply(nodeData) - flow[this.dataIndex.get(nodeData)] == 0) {
                    continue;
                }

                isDiscovered[this.dataIndex.get(nextData)] = true;
                final IsTransposedFlow<D> nextIsTransposedFlow = new IsTransposedFlow<>(false, nextData);
                bfsQueue.addLast(nextIsTransposedFlow);
                prePath.put(nextIsTransposedFlow, isTransposedFlow);
            }

            for (final DirectedGraphNode<D> nextTransposedNode : transposedNode.getNodes()) {
                final D nextTransposedData = nextTransposedNode.getData();

                if (isSkipScc) {
                    if (this.dataScc.containsKey(nextTransposedNode.getData())) {
                        continue;
                    }
                }

                if (isDiscovered[this.dataIndex.get(nextTransposedData)]) {
                    continue;
                }


                assert (transposedFlow[this.dataIndex.get(nodeData)] <= 0);
                if (BACK_FLOW_CAPACITY - transposedFlow[this.dataIndex.get(nodeData)] == 0) {
                    continue;
                }

                isDiscovered[this.dataIndex.get(nodeData)] = true;
                final IsTransposedFlow<D> nextIsTransposedFlow = new IsTransposedFlow<>(true, nextTransposedData);
                bfsQueue.addLast(nextIsTransposedFlow);
                prePath.put(nextIsTransposedFlow, isTransposedFlow);
            }
        }

        assert (isTransposedFlow != null);

        if (!sink.equals(isTransposedFlow.getData())) {
            return;
        }

        outMinRemainCapacity[0] = Integer.MAX_VALUE;
        while (isTransposedFlow != null) {
            final int iData = this.dataIndex.get(isTransposedFlow.getData());

            if (isTransposedFlow.isTransposedFlow()) {
                final int flowCount = transposedFlow[iData];
                outMinRemainCapacity[0] = Math.min(outMinRemainCapacity[0], BACK_FLOW_CAPACITY - flowCount);
            } else {
                final int capacity = this.getDataWeight.apply(isTransposedFlow.getData());
                final int flowCount = flow[iData];
                outMinRemainCapacity[0] = Math.min(outMinRemainCapacity[0], capacity - flowCount);
            }
            outPath.addFirst(isTransposedFlow);

            isTransposedFlow = prePath.get(isTransposedFlow);
        }
    }

    // dfs
    public final void dfsPostOrderReverse(final boolean isSkipScc,
                                          final boolean isTransposedGraph,
                                          final LinkedList<D> outPostOrderNodeReverseList) {
        // O(n + e)

        final HashMap<D, DirectedGraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;

        final boolean[] isDiscovered = new boolean[this.dataIndex.size()];

        for (final DirectedGraphNode<D> node : graph.values()) {
            if (isSkipScc) {
                if (this.dataScc.containsKey(node.getData())) {
                    continue;
                }
            }

            if (isDiscovered[this.dataIndex.get(node.getData())]) {
                continue;
            }

            dfsPostOrderReverseRecursive(isSkipScc, node.getData(), isTransposedGraph, isDiscovered, outPostOrderNodeReverseList);
        }
    }

    public final void dfsPostOrderReverse(final boolean isSkipScc,
                                          final LinkedList<D> orderedNodes,
                                          final boolean isTransposedGraph,
                                          final LinkedList<D> outPostOrderNodeReverseList) {
        // O(n + e)

        final HashMap<D, DirectedGraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;

        final boolean[] isDiscovered = new boolean[this.dataIndex.size()];

        for (final D data : orderedNodes) {
            final DirectedGraphNode<D> node = graph.get(data);

            if (isSkipScc) {
                if (this.dataScc.containsKey(node.getData())) {
                    continue;
                }
            }

            if (isDiscovered[this.dataIndex.get(node.getData())]) {
                continue;
            }

            dfsPostOrderReverseRecursive(isSkipScc, node.getData(), isTransposedGraph, isDiscovered, outPostOrderNodeReverseList);
        }
    }

    public final void dfsPostOrderReverseRecursive(final boolean isSkipScc,
                                                   final D startData,
                                                   final boolean isTransposedGraph,
                                                   final boolean[] isDiscovered,
                                                   final LinkedList<D> outPostOrderNodeReverseList) {

        final HashMap<D, DirectedGraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;
        final DirectedGraphNode<D> startNode = graph.get(startData);

        isDiscovered[this.dataIndex.get(startNode.getData())] = true;

        for (final DirectedGraphNode<D> node : startNode.getNodes()) {
            if (isSkipScc) {
                if (this.dataScc.containsKey(startNode.getData())) {
                    continue;
                }
            }

            if (isDiscovered[this.dataIndex.get(node.getData())]) {
                continue;
            }

            dfsPostOrderReverseRecursive(isSkipScc, node.getData(), isTransposedGraph, isDiscovered, outPostOrderNodeReverseList);
        }

        outPostOrderNodeReverseList.addFirst(startNode.getData());
    }

    // topological sort
    public final LinkedList<D> topologicalSort(final boolean includeScc) {
        // O(n + e) + O(n + e) + O(n + e)

        final LinkedList<D> dfsPostOrderNodeReverseList = new LinkedList<>();
        dfsPostOrderReverse(false, false, dfsPostOrderNodeReverseList);

        // get sortedList
        final LinkedList<D> outSortedList = new LinkedList<>();
        dfsPostOrderReverse(!includeScc, dfsPostOrderNodeReverseList, false, outSortedList);

        return outSortedList;
    }

    // ---

    // scc Kosaraju
    private void kosarajuScc(final HashMap<D, Boolean> outScc) {
        // O(n + e) + O(n + e)

        final LinkedList<D> dfsPostOrderNodeReverseList = new LinkedList<>();
        dfsPostOrderReverse(false, false, dfsPostOrderNodeReverseList);

        // get scc groups with size > 1
        {
            final boolean[] transposedIsDiscovered = new boolean[this.dataIndex.size()];
            final LinkedList<D> scc = new LinkedList<>();

            for (final D data : dfsPostOrderNodeReverseList) {
                final DirectedGraphNode<D> transposedNode = this.transposedGraph.get(data);

                if (transposedIsDiscovered[this.dataIndex.get(transposedNode.getData())]) {
                    continue;
                }

                dfsPostOrderReverseRecursive(false, transposedNode.getData(), true, transposedIsDiscovered, scc);

                assert (scc.size() >= 1);

                if (scc.size() > 1) {
                    for (final D skip : scc) {
                        outScc.put(skip, true);
                    }
                }

                scc.clear();
            }
        }
    }

    // create
    private HashMap<D, DirectedGraphNode<D>> createGraph(final ArrayList<D> dataArray, final HashMap<D, ArrayList<D>> dataArrayNodes) {
        // O(n) + O(ne)

        final HashMap<D, DirectedGraphNode<D>> outGraph = new HashMap<>(dataArray.size());

        for (final D data : dataArray) {
            final DirectedGraphNode<D> node = new DirectedGraphNode<>(data);
            outGraph.put(data, node);
        }

        for (final D data : dataArray) {
            final DirectedGraphNode<D> node = outGraph.get(data);

            for (final D dataNode : dataArrayNodes.get(data)) {
                assert (outGraph.containsKey(dataNode));

                node.addNode(outGraph.get(dataNode));
            }
        }

        return outGraph;
    }

    private HashMap<D, DirectedGraphNode<D>> createTransposedGraph(final ArrayList<D> dataArray, final HashMap<D, ArrayList<D>> dataArrayNodes) {
        // O(n) + O(ne)

        final HashMap<D, DirectedGraphNode<D>> outTransposedGraph = new HashMap<>(dataArray.size());

        for (final D data : dataArray) {
            final DirectedGraphNode<D> transposedNode = new DirectedGraphNode<>(data);
            outTransposedGraph.put(data, transposedNode);
        }

        for (final D data : dataArray) {
            for (final D dataNode : dataArrayNodes.get(data)) {
                assert (outTransposedGraph.containsKey(dataNode));

                final DirectedGraphNode<D> transposedNode = outTransposedGraph.get(dataNode);
                transposedNode.addNode(outTransposedGraph.get(data));
            }
        }

        return outTransposedGraph;
    }

    private void addNodeInGraph(final D data, final ArrayList<D> dataNodes) {
        final DirectedGraphNode<D> newNode = new DirectedGraphNode<>(data);
        this.graph.put(data, newNode);

        for (final D dataNode : dataNodes) {
            assert (this.graph.containsKey(dataNode));

            newNode.addNode(this.graph.get(dataNode));
        }
    }

    private void addNodeInTransposedGraph(final D data, final ArrayList<D> dataNodes) {
        {
            final DirectedGraphNode<D> newTransposedNode = new DirectedGraphNode<>(data);
            this.transposedGraph.put(data, newTransposedNode);
        }

        for (final D dataNode : dataNodes) {
            assert (this.transposedGraph.containsKey(dataNode));

            final DirectedGraphNode<D> transposedNode = this.transposedGraph.get(dataNode);
            transposedNode.addNode(this.transposedGraph.get(data));
        }
    }

    private void addTransposedNodeInGraph(final D transposedData, final ArrayList<D> transposedDataNodes) {
        {
            final DirectedGraphNode<D> newNode = new DirectedGraphNode<>(transposedData);
            this.graph.put(transposedData, newNode);
        }

        for (final D transposedDataNode : transposedDataNodes) {
            assert (this.graph.containsKey(transposedDataNode));

            final DirectedGraphNode<D> node = this.graph.get(transposedDataNode);
            node.addNode(this.graph.get(transposedData));
        }
    }

    private void addTransposedNodeInTransposedGraph(final D transposedData, final ArrayList<D> transposedDataNodes) {
        final DirectedGraphNode<D> newTransposedNode = new DirectedGraphNode<>(transposedData);
        this.transposedGraph.put(transposedData, newTransposedNode);

        for (final D dataNode : transposedDataNodes) {
            assert (this.transposedGraph.containsKey(dataNode));

            newTransposedNode.addNode(this.transposedGraph.get(dataNode));
        }
    }

    private void removeNodeInGraph(final D data, final ArrayList<D> dataNodes) {
        assert (this.graph.containsKey(data));
        final DirectedGraphNode<D> removeNode = this.graph.get(data);

        for (final D dataNode : dataNodes) {
            assert (this.graph.containsKey(dataNode));

            removeNode.removeNode(this.graph.get(dataNode));
        }

        this.graph.remove(data);
    }

    private void removeNodeInTransposedGraph(final D data, final ArrayList<D> dataNodes) {
        assert (this.transposedGraph.containsKey(data));

        for (final D dataNode : dataNodes) {
            assert (this.transposedGraph.containsKey(dataNode));

            final DirectedGraphNode<D> transposedNode = this.transposedGraph.get(dataNode);
            transposedNode.removeNode(this.transposedGraph.get(data));
        }

        this.transposedGraph.remove(data);
    }

    private void removeTransposedNodeInGraph(final D transposedData, final ArrayList<D> transposedDataNodes) {
        assert (this.graph.containsKey(transposedData));

        for (final D transposedDataNode : transposedDataNodes) {
            assert (this.graph.containsKey(transposedDataNode));

            final DirectedGraphNode<D> node = this.graph.get(transposedDataNode);
            node.removeNode(this.graph.get(transposedData));
        }

        this.graph.remove(transposedData);
    }

    private void removeTransposedNodeInTransposedGraph(final D transposedData, final ArrayList<D> transposedDataNodes) {
        assert (this.transposedGraph.containsKey(transposedData));
        final DirectedGraphNode<D> removeTransposedNode = this.transposedGraph.get(transposedData);

        for (final D transposedDataNode : transposedDataNodes) {
            assert (this.transposedGraph.containsKey(transposedDataNode));

            removeTransposedNode.removeNode(this.transposedGraph.get(transposedDataNode));
        }

        this.transposedGraph.remove(transposedData);
    }
}