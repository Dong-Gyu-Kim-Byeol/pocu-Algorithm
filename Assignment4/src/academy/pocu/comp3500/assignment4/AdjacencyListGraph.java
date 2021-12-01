package academy.pocu.comp3500.assignment4;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;

public final class AdjacencyListGraph<D> {
    private final HashMap<D, Integer> dataIndex;
    private HashMap<D, Boolean> dataScc;

    private final HashMap<D, AdjacencyListGraphNode<D>> graph;
    private HashMap<D, AdjacencyListGraphNode<D>> transposedGraph;

    // ---

    public AdjacencyListGraph(final boolean useTransposedGraph,
                              final ArrayList<D> nodeDataArray,
                              final HashMap<D, ArrayList<D>> edgeDataArrayMap,
                              final HashMap<D, ArrayList<Integer>> edgeWeightArrayMap) {
        this.graph = createGraph(nodeDataArray, edgeDataArrayMap, edgeWeightArrayMap);
        if (useTransposedGraph) {
            this.transposedGraph = createTransposedGraph(nodeDataArray, edgeDataArrayMap);
        }

        assert (this.graph.size() == nodeDataArray.size());
        assert !useTransposedGraph || (this.transposedGraph.size() == nodeDataArray.size());

        {
            this.dataIndex = new HashMap<>(nodeDataArray.size());
            int i = 0;
            for (final D data : nodeDataArray) {
                this.dataIndex.put(data, i++);
            }
        }

        if (useTransposedGraph) {
            this.dataScc = new HashMap<>();
            setScc();
        }
    }

    // ---

    public final int nodeCount() {
        assert (this.dataIndex.size() == this.graph.size());
        assert (this.transposedGraph == null || this.dataIndex.size() == this.transposedGraph.size());

        return this.dataIndex.size();
    }

    public final HashMap<D, Integer> getDataIndex() {
        return dataIndex;
    }

    public final HashMap<D, Boolean> getDataScc() {
        return dataScc;
    }

    public final HashMap<D, AdjacencyListGraphNode<D>> getGraph() {
        return graph;
    }

    public final HashMap<D, AdjacencyListGraphNode<D>> getTransposedGraph() {
        return transposedGraph;
    }

    // setter
    public final void setScc() {
        this.dataScc.clear();
        final LinkedList<AdjacencyListGraphNode<D>> scc = new LinkedList<>();
        kosarajuScc(scc);

        for (final AdjacencyListGraphNode<D> sccNode : scc) {
            this.dataScc.put(sccNode.getData(), true);
        }
    }

    public final void addNode(final D data,
                              final ArrayList<D> edgeDataArray,
                              final ArrayList<Integer> edgeWeightArray,
                              final ArrayList<Integer> transposedEdgeWeightArray) {

        assert (edgeDataArray.size() == transposedEdgeWeightArray.size());
        assert (edgeDataArray.size() == edgeWeightArray.size());

        // addNodeInGraph
        {
            final AdjacencyListGraphNode<D> newNode = new AdjacencyListGraphNode<>(data);
            this.graph.put(data, newNode);

            for (int i = 0; i < edgeDataArray.size(); ++i) {
                assert (this.graph.containsKey(edgeDataArray.get(i)));

                newNode.addNode(new AdjacencyListGraphEdge<>(edgeWeightArray.get(i), newNode, this.graph.get(edgeDataArray.get(i))));
            }
        }

        // addNodeInTransposedGraph
        if (this.transposedGraph != null) {
            {
                final AdjacencyListGraphNode<D> newTransposedNode = new AdjacencyListGraphNode<>(data);
                this.transposedGraph.put(data, newTransposedNode);
            }

            for (int i = 0; i < edgeDataArray.size(); ++i) {
                assert (this.transposedGraph.containsKey(edgeDataArray.get(i)));

                final AdjacencyListGraphNode<D> transposedNode = this.transposedGraph.get(edgeDataArray.get(i));

                transposedNode.addNode(new AdjacencyListGraphEdge<>(transposedEdgeWeightArray.get(i), transposedNode, this.transposedGraph.get(data)));
            }
        }

        assert (!this.dataIndex.containsKey(data));
        this.dataIndex.put(data, this.dataIndex.size());
    }

    public final void removeNode(final D data,
                                 final ArrayList<D> edgeDataArray) {

        // removeNodeInGraph
        {
            assert (this.graph.containsKey(data));
            final AdjacencyListGraphNode<D> removeNode = this.graph.get(data);

            for (final D dataEdge : edgeDataArray) {
                assert (this.graph.containsKey(dataEdge));

                removeNode.removeEdge(dataEdge);
            }
        }

        // removeNodeInTransposedGraph
        if (this.transposedGraph != null) {
            assert (this.transposedGraph.containsKey(data));

            for (final D dataEdge : edgeDataArray) {
                assert (this.transposedGraph.containsKey(dataEdge));

                final AdjacencyListGraphNode<D> transposedNode = this.transposedGraph.get(dataEdge);
                transposedNode.removeEdge(data);
            }
        }


        assert (this.dataIndex.containsKey(data));
        this.dataIndex.remove(data);

        this.graph.remove(data);
        if (this.transposedGraph != null) {
            this.transposedGraph.remove(data);
        }
    }

    public final void addTransposedNode(final D transposedData,
                                        final ArrayList<D> transposedEdgeDataArray,
                                        final ArrayList<Integer> transposedEdgeWeightArray,
                                        final ArrayList<Integer> edgeWeightArray) {

        assert (this.transposedGraph != null);

        assert (transposedEdgeDataArray.size() == transposedEdgeWeightArray.size());
        assert (transposedEdgeDataArray.size() == edgeWeightArray.size());

        // addTransposedNodeInTransposedGraph
        {
            final AdjacencyListGraphNode<D> newTransposedNode = new AdjacencyListGraphNode<>(transposedData);
            this.transposedGraph.put(transposedData, newTransposedNode);

            for (int i = 0; i < transposedEdgeDataArray.size(); ++i) {
                assert (this.transposedGraph.containsKey(transposedEdgeDataArray.get(i)));

                newTransposedNode.addNode(new AdjacencyListGraphEdge<>(transposedEdgeWeightArray.get(i), newTransposedNode, this.transposedGraph.get(transposedEdgeDataArray.get(i))));
            }
        }

        // addTransposedNodeInGraph
        {
            {
                final AdjacencyListGraphNode<D> newNode = new AdjacencyListGraphNode<>(transposedData);
                this.graph.put(transposedData, newNode);
            }

            for (int i = 0; i < transposedEdgeDataArray.size(); ++i) {
                assert (this.graph.containsKey(transposedEdgeDataArray.get(i)));

                final AdjacencyListGraphNode<D> node = this.graph.get(transposedEdgeDataArray.get(i));

                node.addNode(new AdjacencyListGraphEdge<>(edgeWeightArray.get(i), node, this.graph.get(transposedData)));
            }
        }

        assert (!this.dataIndex.containsKey(transposedData));
        this.dataIndex.put(transposedData, this.dataIndex.size());
    }

    public final void removeTransposedNode(final D transposedData,
                                           final ArrayList<D> transposedEdgeDataArray) {

        assert (this.transposedGraph != null);

        // removeTransposedNodeInGraph
        {
            assert (this.graph.containsKey(transposedData));

            for (final D transposedDataEdge : transposedEdgeDataArray) {
                assert (this.graph.containsKey(transposedDataEdge));

                final AdjacencyListGraphNode<D> node = this.graph.get(transposedDataEdge);
                node.removeEdge(transposedData);
            }
        }

        // removeTransposedNodeInTransposedGraph
        {
            assert (this.transposedGraph.containsKey(transposedData));
            final AdjacencyListGraphNode<D> removeTransposedNode = this.transposedGraph.get(transposedData);

            for (final D transposedDataEdge : transposedEdgeDataArray) {
                assert (this.transposedGraph.containsKey(transposedDataEdge));

                removeTransposedNode.removeEdge(transposedDataEdge);
            }
        }


        this.graph.remove(transposedData);
        this.transposedGraph.remove(transposedData);

        assert (this.dataIndex.containsKey(transposedData));
        this.dataIndex.remove(transposedData);
    }

    // bfs
    public final void bfs(final boolean isSkipScc,
                          final D startData,
                          final boolean isTransposedGraph,
                          final LinkedList<AdjacencyListGraphNode<D>> outBfsList) {

        final HashMap<D, AdjacencyListGraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;
        final AdjacencyListGraphNode<D> startNode = graph.get(startData);

        final boolean[] isDiscovered = new boolean[this.dataIndex.size()];

        final LinkedList<AdjacencyListGraphNode<D>> bfsQueue = new LinkedList<>();

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
            final AdjacencyListGraphNode<D> node = bfsQueue.poll();

            outBfsList.addFirst(node);

            for (final AdjacencyListGraphEdge<D> nextEdge : node.getEdges().values()) {
                if (isSkipScc) {
                    if (this.dataScc.containsKey(startNode.getData())) {
                        continue;
                    }
                }

                if (isDiscovered[this.dataIndex.get(nextEdge.getNode2().getData())]) {
                    continue;
                }

                isDiscovered[this.dataIndex.get(nextEdge.getNode2().getData())] = true;
                bfsQueue.addLast(nextEdge.getNode2());
            }
        }
    }

    // dfs
    public final void dfsPostOrderReverse(final boolean isSkipScc,
                                          final boolean isTransposedGraph,
                                          final LinkedList<AdjacencyListGraphNode<D>> outPostOrderNodeReverseList) {
        // O(n + e)

        final HashMap<D, AdjacencyListGraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;

        final boolean[] isDiscovered = new boolean[this.dataIndex.size()];

        for (final AdjacencyListGraphNode<D> node : graph.values()) {
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
                                          final LinkedList<AdjacencyListGraphNode<D>> orderedNodes,
                                          final boolean isTransposedGraph,
                                          final LinkedList<AdjacencyListGraphNode<D>> outPostOrderNodeReverseList) {
        // O(n + e)

        final HashMap<D, AdjacencyListGraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;

        final boolean[] isDiscovered = new boolean[this.dataIndex.size()];

        for (final AdjacencyListGraphNode<D> node : orderedNodes) {
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
                                                   final LinkedList<AdjacencyListGraphNode<D>> outPostOrderNodeReverseList) {

        final HashMap<D, AdjacencyListGraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;
        final AdjacencyListGraphNode<D> startNode = graph.get(startData);

        isDiscovered[this.dataIndex.get(startNode.getData())] = true;

        for (final AdjacencyListGraphEdge<D> edge : startNode.getEdges().values()) {
            if (isSkipScc) {
                if (this.dataScc.containsKey(startNode.getData())) {
                    continue;
                }
            }

            if (isDiscovered[this.dataIndex.get(edge.getNode2().getData())]) {
                continue;
            }

            dfsPostOrderReverseRecursive(isSkipScc, edge.getNode2().getData(), isTransposedGraph, isDiscovered, outPostOrderNodeReverseList);
        }

        outPostOrderNodeReverseList.addFirst(startNode);
    }

    public final void dfs(final boolean isSkipScc,
                          final D startData,
                          final boolean isTransposedGraph,
                          final LinkedList<AdjacencyListGraphNode<D>> outDfsList) {

        final HashMap<D, AdjacencyListGraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;
        final AdjacencyListGraphNode<D> startNode = graph.get(startData);

        final boolean[] isDiscovered = new boolean[this.dataIndex.size()];

        final LinkedList<AdjacencyListGraphNode<D>> bfsStack = new LinkedList<>();

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
            bfsStack.addLast(startNode);
        }

        while (!bfsStack.isEmpty()) {
            final AdjacencyListGraphNode<D> node = bfsStack.getLast();
            bfsStack.removeLast();

            outDfsList.addLast(node);

            for (final AdjacencyListGraphEdge<D> nextEdge : node.getEdges().values()) {
                if (isSkipScc) {
                    if (this.dataScc.containsKey(startNode.getData())) {
                        continue;
                    }
                }

                if (isDiscovered[this.dataIndex.get(nextEdge.getNode2().getData())]) {
                    continue;
                }

                isDiscovered[this.dataIndex.get(nextEdge.getNode2().getData())] = true;
                bfsStack.addLast(nextEdge.getNode2());
            }
        }
    }

    // tsp2Approximation
    public final ArrayList<AdjacencyListGraphNode<D>> tsp2Approximation(final boolean isSkipScc,
                                                                        final D startData) {
        assert (this.dataIndex.containsKey(startData));

        // create mst graph
        final AdjacencyListGraph<D> mstGraph;
        {
            final ArrayList<AdjacencyListGraphEdge<D>> mst = this.kruskalMst();
            if (mst.isEmpty()) {
                final ArrayList<AdjacencyListGraphNode<D>> outTspList = new ArrayList<>(1);
                final AdjacencyListGraphNode<D> startNode = this.graph.get(startData);
                outTspList.add(startNode);
                return outTspList;
            }

            final HashMap<D, ArrayList<D>> dataEdgeArrayMap = new HashMap<>(this.dataIndex.size());
            final HashMap<D, ArrayList<Integer>> weightEdgeArrayMap = new HashMap<>(this.dataIndex.size());

            for (final AdjacencyListGraphEdge<D> edge : mst) {
                final AdjacencyListGraphNode<D> from = edge.getNode1();
                final D fromPoint = from.getData();
                final AdjacencyListGraphNode<D> to = edge.getNode2();
                final D toPoint = to.getData();

                final int weight = edge.getWeight();

                // add from edge
                {
                    if (!dataEdgeArrayMap.containsKey(fromPoint)) {

                        dataEdgeArrayMap.put(fromPoint, new ArrayList<>(mst.size()));
                    }
                    final ArrayList<D> dataEdgeArray = dataEdgeArrayMap.get(fromPoint);

                    if (!weightEdgeArrayMap.containsKey(fromPoint)) {
                        weightEdgeArrayMap.put(fromPoint, new ArrayList<>(mst.size()));
                    }
                    final ArrayList<Integer> weightEdgeArray = weightEdgeArrayMap.get(fromPoint);

                    dataEdgeArray.add(toPoint);
                    weightEdgeArray.add(weight);
                }

                // add to edge
                {
                    if (!dataEdgeArrayMap.containsKey(toPoint)) {

                        dataEdgeArrayMap.put(toPoint, new ArrayList<>(mst.size()));
                    }
                    final ArrayList<D> dataEdgeArray = dataEdgeArrayMap.get(toPoint);

                    if (!weightEdgeArrayMap.containsKey(toPoint)) {
                        weightEdgeArrayMap.put(toPoint, new ArrayList<>(mst.size()));
                    }
                    final ArrayList<Integer> weightEdgeArray = weightEdgeArrayMap.get(toPoint);

                    dataEdgeArray.add(fromPoint);
                    weightEdgeArray.add(weight);
                }
            }

            final ArrayList<D> dataArray;
            {
                dataArray = new ArrayList<>(this.dataIndex.size());

                for (final D data : this.dataIndex.keySet()) {
                    dataArray.add(data);
                }
            }

            mstGraph = new AdjacencyListGraph<>(false, dataArray, dataEdgeArrayMap, weightEdgeArrayMap);
        }// end create mst graph

        final ArrayList<AdjacencyListGraphNode<D>> outMstDfsPreOrderAndAddReturnList;
        {
            final boolean[] isDiscovered = new boolean[this.dataIndex.size()];
            outMstDfsPreOrderAndAddReturnList = new ArrayList<>(mstGraph.nodeCount() * 2);
            mstGraph.dfsPreOrderAndAddReturnRecursive(isSkipScc, startData, false, isDiscovered, outMstDfsPreOrderAndAddReturnList);
        }

        final ArrayList<AdjacencyListGraphNode<D>> outTspList;
        {
            outTspList = new ArrayList<>(mstGraph.nodeCount());

            final boolean[] isDiscovered = new boolean[this.dataIndex.size()];

            for (final AdjacencyListGraphNode<D> mstNode : outMstDfsPreOrderAndAddReturnList) {
                final D data = mstNode.getData();
                assert (this.graph.containsKey(data));

                final int iData = this.dataIndex.get(data);
                if (isDiscovered[iData]) {
                    continue;
                }

                isDiscovered[iData] = true;

                final AdjacencyListGraphNode<D> node = this.graph.get(data);
                outTspList.add(node);
            }

            final AdjacencyListGraphNode<D> startNode = this.graph.get(startData);
            outTspList.add(startNode);
        }

        return outTspList;
    }

    public final void dfsPreOrderAndAddReturnRecursive(final boolean isSkipScc,
                                                       final D startData,
                                                       final boolean isTransposedGraph,
                                                       final boolean[] isDiscovered,
                                                       final ArrayList<AdjacencyListGraphNode<D>> outDfsPreOrderAndAddReturnList) {

        final HashMap<D, AdjacencyListGraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;
        final AdjacencyListGraphNode<D> startNode = graph.get(startData);

        isDiscovered[this.dataIndex.get(startNode.getData())] = true;

        outDfsPreOrderAndAddReturnList.add(startNode);

        for (final AdjacencyListGraphEdge<D> edge : startNode.getEdges().values()) {
            if (isSkipScc) {
                if (this.dataScc.containsKey(startNode.getData())) {
                    continue;
                }
            }

            if (isDiscovered[this.dataIndex.get(edge.getNode2().getData())]) {
                continue;
            }

            dfsPreOrderAndAddReturnRecursive(isSkipScc, edge.getNode2().getData(), isTransposedGraph, isDiscovered, outDfsPreOrderAndAddReturnList);
            outDfsPreOrderAndAddReturnList.add(startNode);
        }
    }

    public ArrayList<AdjacencyListGraphEdge<D>> kruskalMst() {
        ArrayList<AdjacencyListGraphEdge<D>> mst = new ArrayList<>(this.graph.size());

        ArrayList<AdjacencyListGraphNode<D>> nodes = new ArrayList<>(this.graph.size());
        ArrayList<AdjacencyListGraphEdge<D>> edges = new ArrayList<>(this.graph.size() * this.graph.size());

        for (final AdjacencyListGraphNode<D> node : this.graph.values()) {
            nodes.add(node);
            for (final AdjacencyListGraphEdge<D> edge : node.getEdges().values()) {
                edges.add(edge);
            }
        }

        DisjointSet<AdjacencyListGraphNode<D>> set = new DisjointSet<>(nodes);
        Sort.radixSort(edges, AdjacencyListGraphEdge<D>::getWeight);

        for (final AdjacencyListGraphEdge<D> edge : edges) {
            final AdjacencyListGraphNode<D> n1 = edge.getNode1();
            final AdjacencyListGraphNode<D> n2 = edge.getNode2();

            final AdjacencyListGraphNode<D> root1 = set.find(n1);
            final AdjacencyListGraphNode<D> root2 = set.find(n2);

            if (!root1.equals(root2)) {
                mst.add(edge);
                set.union(n1, n2);
            }
        }

        return mst;
    }

    // topological sort
    public final LinkedList<AdjacencyListGraphNode<D>> topologicalSort(final boolean includeScc) {
        // O(n + e) + O(n + e) + O(n + e)

        final LinkedList<AdjacencyListGraphNode<D>> dfsPostOrderNodeReverseList = new LinkedList<>();
        dfsPostOrderReverse(false, false, dfsPostOrderNodeReverseList);

        // get sortedList
        final LinkedList<AdjacencyListGraphNode<D>> outSortedList = new LinkedList<>();
        dfsPostOrderReverse(!includeScc, dfsPostOrderNodeReverseList, false, outSortedList);

        return outSortedList;
    }

    // scc Kosaraju
    public void kosarajuScc(final LinkedList<AdjacencyListGraphNode<D>> outScc) {
        // O(n + e) + O(n + e)

        final LinkedList<AdjacencyListGraphNode<D>> dfsPostOrderNodeReverseList = new LinkedList<>();
        dfsPostOrderReverse(false, false, dfsPostOrderNodeReverseList);

        // get scc groups with size > 1
        {
            final boolean[] transposedIsDiscovered = new boolean[this.dataIndex.size()];
            final LinkedList<AdjacencyListGraphNode<D>> scc = new LinkedList<>();

            for (final AdjacencyListGraphNode<D> node : dfsPostOrderNodeReverseList) {
                final D data = node.getData();
                final AdjacencyListGraphNode<D> transposedNode = this.transposedGraph.get(data);

                if (transposedIsDiscovered[this.dataIndex.get(transposedNode.getData())]) {
                    continue;
                }

                dfsPostOrderReverseRecursive(false, transposedNode.getData(), true, transposedIsDiscovered, scc);

                assert (scc.size() >= 1);

                if (scc.size() > 1) {
                    for (final AdjacencyListGraphNode<D> skip : scc) {
                        outScc.add(skip);
                    }
                }

                scc.clear();
            }
        }
    }

    // ---

    // create
    private HashMap<D, AdjacencyListGraphNode<D>> createGraph(final ArrayList<D> dataNodeArray,
                                                              final HashMap<D, ArrayList<D>> dataEdgeArrayMap,
                                                              final HashMap<D, ArrayList<Integer>> weightEdgeArrayMap) {
        // O(n) + O(ne)

        final HashMap<D, AdjacencyListGraphNode<D>> outGraph = new HashMap<>(dataNodeArray.size());

        for (final D data : dataNodeArray) {
            final AdjacencyListGraphNode<D> dataNode = new AdjacencyListGraphNode<>(data);
            outGraph.put(dataNode.getData(), dataNode);
        }

        for (final D fromData : dataNodeArray) {
            final AdjacencyListGraphNode<D> from = outGraph.get(fromData);
            final ArrayList<D> toDataEdgeArray = dataEdgeArrayMap.get(fromData);
            final ArrayList<Integer> toWeightEdgeArray = weightEdgeArrayMap.get(fromData);

            assert (toDataEdgeArray.size() == toWeightEdgeArray.size());

            for (int i = 0; i < toDataEdgeArray.size(); ++i) {
                final D toData = toDataEdgeArray.get(i);
                assert (outGraph.containsKey(toData));

                final AdjacencyListGraphNode<D> to = outGraph.get(toData);

                from.addNode(new AdjacencyListGraphEdge<>(toWeightEdgeArray.get(i), from, to));
            }
        }

        return outGraph;
    }

    private HashMap<D, AdjacencyListGraphNode<D>> createTransposedGraph(final ArrayList<D> dataNodeArray,
                                                                        final HashMap<D, ArrayList<D>> dataEdgeArrayMap) {
        // O(n) + O(ne)

        final HashMap<D, AdjacencyListGraphNode<D>> outTransposedGraph = new HashMap<>(dataNodeArray.size());

        for (final D data : dataNodeArray) {
            final AdjacencyListGraphNode<D> transposedNode = new AdjacencyListGraphNode<>(data);
            outTransposedGraph.put(transposedNode.getData(), transposedNode);
        }

        for (final D toData : dataNodeArray) {
            final ArrayList<D> fromDataEdgeArray = dataEdgeArrayMap.get(toData);

            for (final D fromDataEdge : fromDataEdgeArray) {
                assert (outTransposedGraph.containsKey(fromDataEdge));

                final AdjacencyListGraphNode<D> from = outTransposedGraph.get(fromDataEdge);

                final int edgeWeight = this.graph.get(toData).getEdges().get(from.getData()).getWeight();
                from.addNode(new AdjacencyListGraphEdge<>(edgeWeight, from, outTransposedGraph.get(toData)));
            }
        }

        return outTransposedGraph;
    }


    // max flow
    public final int maxFlow(final boolean isSkipScc,
                             final D source,
                             final D sink,
                             final boolean mainIsTransposedGraph) {

        if (isSkipScc) {
            assert (!this.dataScc.containsKey(source));
            assert (!this.dataScc.containsKey(sink));
        }

        int outTotalFlow = 0;

        final int BACK_FLOW_CAPACITY = 0;

        final HashMap<D, AdjacencyListGraphNode<D>> mainGraph = mainIsTransposedGraph ? this.transposedGraph : this.graph;
        final HashMap<D, AdjacencyListGraphNode<D>> transposedGraph = !mainIsTransposedGraph ? this.transposedGraph : this.graph;

        final int[][] flow = new int[this.dataIndex.size()][this.dataIndex.size()];
        final LinkedList<IsTransposedEdge<D>> path = new LinkedList<>();

        final LinkedList<D> bfsQueue = new LinkedList<>();
        final LinkedList<IsTransposedEdge<D>> bfsEdgeQueue = new LinkedList<>();
        final HashMap<IsTransposedEdge<D>, IsTransposedEdge<D>> prePathMap = new HashMap<>();

        while (true) {
            {
                final boolean[] isDiscovered = new boolean[this.dataIndex.size()];
                bfsQueue.clear();
                bfsEdgeQueue.clear();
                prePathMap.clear();

                {
                    isDiscovered[this.dataIndex.get(source)] = true;

                    bfsQueue.addLast(source);
                    bfsEdgeQueue.addLast(null);
                }

                IsTransposedEdge<D> lastEdge = null;
                // bfs
                while (!bfsQueue.isEmpty()) {
                    assert (bfsQueue.size() == bfsEdgeQueue.size());

                    final D nodeData = bfsQueue.poll();
                    final int iNodeData = this.dataIndex.get(nodeData);

                    final AdjacencyListGraphNode<D> node = mainGraph.get(nodeData);
                    final AdjacencyListGraphNode<D> transposedNode = transposedGraph.get(nodeData);

                    final IsTransposedEdge<D> nowIsTransposedFlow = bfsEdgeQueue.poll();

                    assert (bfsQueue.size() == bfsEdgeQueue.size());

                    if (nodeData.equals(sink)) {
                        lastEdge = nowIsTransposedFlow;
                        break;
                    }

                    for (final AdjacencyListGraphEdge<D> nextEdge : node.getEdges().values()) {
                        final AdjacencyListGraphNode<D> nextNode = nextEdge.getNode2();
                        final D nextData = nextNode.getData();
                        final int iNextData = this.dataIndex.get(nextData);

                        assert (!nextData.equals(nodeData));

                        if (isSkipScc) {
                            if (this.dataScc.containsKey(nextData)) {
                                continue;
                            }
                        }

                        if (isDiscovered[iNextData]) {
                            continue;
                        }

                        final int edgeFlow = flow[iNodeData][iNextData];
                        final int edgeCap = nextEdge.getWeight();
                        final int edgeRemain = edgeCap - edgeFlow;

                        assert (edgeFlow >= 0);
                        assert (edgeRemain >= 0);

                        if (edgeRemain <= 0) {
                            continue;
                        }

                        isDiscovered[iNextData] = true;
                        bfsQueue.addLast(nextData);

                        final IsTransposedEdge<D> nextIsTransposedFlow = new IsTransposedEdge<>(false, nextEdge);
                        bfsEdgeQueue.addLast(nextIsTransposedFlow);
                        prePathMap.put(nextIsTransposedFlow, nowIsTransposedFlow);
                    }

                    for (final AdjacencyListGraphEdge<D> nextTransposedEdge : transposedNode.getEdges().values()) {
                        final AdjacencyListGraphNode<D> nextTransposedNode = nextTransposedEdge.getNode2();
                        final D nextTransposedData = nextTransposedNode.getData();
                        final int iNextTransposedData = this.dataIndex.get(nextTransposedData);

                        assert (!nextTransposedData.equals(nodeData));

                        if (isSkipScc) {
                            if (this.dataScc.containsKey(nextTransposedNode.getData())) {
                                continue;
                            }
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

                        isDiscovered[this.dataIndex.get(nodeData)] = true;
                        bfsQueue.addLast(nextTransposedData);

                        final IsTransposedEdge<D> nextIsTransposedFlow = new IsTransposedEdge<>(true, nextTransposedEdge);
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
                    final AdjacencyListGraphEdge<D> edge = lastEdge.getEdge();

                    final AdjacencyListGraphNode<D> from = edge.getNode1();
                    final D fromData = from.getData();
                    final int iFromData = this.dataIndex.get(fromData);

                    final AdjacencyListGraphNode<D> to = edge.getNode2();
                    final D toData = to.getData();
                    final int iToData = this.dataIndex.get(toData);

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
                    final IsTransposedEdge<D> isTransposedFlow = path.poll();
                    final AdjacencyListGraphEdge<D> edge = isTransposedFlow.getEdge();

                    final AdjacencyListGraphNode<D> from = edge.getNode1();
                    final D fromData = from.getData();
                    final int iFromData = this.dataIndex.get(fromData);

                    final AdjacencyListGraphNode<D> to = edge.getNode2();
                    final D toData = to.getData();
                    final int iToData = this.dataIndex.get(toData);

                    flow[iFromData][iToData] += minRemainCapacity;
                    flow[iToData][iFromData] -= minRemainCapacity;
                }

                outTotalFlow += minRemainCapacity;
            }
        }

        return outTotalFlow;
    }

}