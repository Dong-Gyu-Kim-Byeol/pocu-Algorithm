package academy.pocu.comp3500.lab11;

import academy.pocu.comp3500.lab11.data.Point;

import java.util.*;

public final class DirectedGraph<D> {
    private final HashMap<D, Integer> dataIndex;
    private HashMap<D, Boolean> dataScc;

    private final HashMap<D, DirectedGraphNode<D>> graph;
    private HashMap<D, DirectedGraphNode<D>> transposedGraph;

    // ---

    public DirectedGraph(final boolean useTransposedGraph, final ArrayList<D> dataNodeArray, final HashMap<D, ArrayList<D>> dataEdgeArrayMap, final HashMap<D, ArrayList<Integer>> weightEdgeArrayMap) {
        this.graph = createGraph(dataNodeArray, dataEdgeArrayMap, weightEdgeArrayMap);
        if (useTransposedGraph) {
            this.transposedGraph = createTransposedGraph(dataNodeArray, dataEdgeArrayMap);
        }

        assert (this.graph.size() == dataNodeArray.size());
        if (useTransposedGraph) {
            assert (this.transposedGraph.size() == dataNodeArray.size());
        }

        {
            this.dataIndex = new HashMap<>(dataNodeArray.size());
            int i = 0;
            for (final D data : dataNodeArray) {
                this.dataIndex.put(data, i++);
            }
        }

        if (useTransposedGraph) {
            this.dataScc = new HashMap<>();
            setScc();
        }
    }

    // ---

    public ArrayList<DirectedGraphNodeEdge<D>> kruskalMst() {
        ArrayList<DirectedGraphNodeEdge<D>> mst = new ArrayList<>(this.graph.size());

        ArrayList<DirectedGraphNode<D>> nodes = new ArrayList<>(this.graph.size());
        ArrayList<DirectedGraphNodeEdge<D>> edges = new ArrayList<>(this.graph.size() * this.graph.size());

        for (final DirectedGraphNode<D> node : this.graph.values()) {
            nodes.add(node);
            for (final DirectedGraphNodeEdge<D> edge : node.getEdges().values()) {
                edges.add(edge);
            }
        }

        DisjointSet<DirectedGraphNode<D>> set = new DisjointSet<>(nodes);
        Sort.quickSort(edges, Comparator.comparing(DirectedGraphNodeEdge<D>::getWeight));

        for (final DirectedGraphNodeEdge<D> edge : edges) {
            final DirectedGraphNode<D> n1 = edge.getNode1();
            final DirectedGraphNode<D> n2 = edge.getNode2();

            final DirectedGraphNode<D> root1 = set.find(n1);
            final DirectedGraphNode<D> root2 = set.find(n2);

            if (!root1.equals(root2)) {
                mst.add(edge);
                set.union(n1, n2);
            }
        }

        return mst;
    }

    //

    public final int nodeCount() {
        assert (this.dataIndex.size() == this.graph.size());
        assert (this.transposedGraph == null || this.dataIndex.size() == this.transposedGraph.size());

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
        final LinkedList<DirectedGraphNode<D>> scc = new LinkedList<>();
        kosarajuScc(scc);

        for (final DirectedGraphNode<D> sccNode : scc) {
            this.dataScc.put(sccNode.getData(), true);
        }
    }

    public final void addNode(final D data,
                              final ArrayList<D> dataEdgeArray,
                              final ArrayList<Integer> weightEdgeArray,
                              final ArrayList<Integer> transposedWeightEdgeArray) {

        assert (dataEdgeArray.size() == transposedWeightEdgeArray.size());
        assert (dataEdgeArray.size() == weightEdgeArray.size());

        // addNodeInGraph
        {
            final DirectedGraphNode<D> newNode = new DirectedGraphNode<>(data);
            this.graph.put(data, newNode);

            for (int i = 0; i < dataEdgeArray.size(); ++i) {
                assert (this.graph.containsKey(dataEdgeArray.get(i)));

                newNode.addNode(new DirectedGraphNodeEdge<>(weightEdgeArray.get(i), newNode, this.graph.get(dataEdgeArray.get(i))));
            }
        }

        // addNodeInTransposedGraph
        {
            {
                final DirectedGraphNode<D> newTransposedNode = new DirectedGraphNode<>(data);
                this.transposedGraph.put(data, newTransposedNode);
            }

            for (int i = 0; i < dataEdgeArray.size(); ++i) {
                assert (this.transposedGraph.containsKey(dataEdgeArray.get(i)));

                final DirectedGraphNode<D> transposedNode = this.transposedGraph.get(dataEdgeArray.get(i));

                transposedNode.addNode(new DirectedGraphNodeEdge<>(transposedWeightEdgeArray.get(i), transposedNode, this.transposedGraph.get(data)));
            }
        }

        assert (!this.dataIndex.containsKey(data));
        this.dataIndex.put(data, this.dataIndex.size());
    }

    public final void removeNode(final D data,
                                 final ArrayList<D> dataEdgeArray) {

        // removeNodeInGraph
        {
            assert (this.graph.containsKey(data));
            final DirectedGraphNode<D> removeNode = this.graph.get(data);

            for (final D dataEdge : dataEdgeArray) {
                assert (this.graph.containsKey(dataEdge));

                removeNode.removeEdge(dataEdge);
            }
        }

        // removeNodeInTransposedGraph
        {
            assert (this.transposedGraph.containsKey(data));

            for (final D dataEdge : dataEdgeArray) {
                assert (this.transposedGraph.containsKey(dataEdge));

                final DirectedGraphNode<D> transposedNode = this.transposedGraph.get(dataEdge);
                transposedNode.removeEdge(data);
            }
        }


        assert (this.dataIndex.containsKey(data));
        this.dataIndex.remove(data);

        this.graph.remove(data);
        this.transposedGraph.remove(data);
    }

    public final void addTransposedNode(final D transposedData,
                                        final ArrayList<D> transposedDataEdgeArray,
                                        final ArrayList<Integer> transposedWeightEdgeArray,
                                        final ArrayList<Integer> weightEdgeArray) {

        assert (transposedDataEdgeArray.size() == transposedWeightEdgeArray.size());
        assert (transposedDataEdgeArray.size() == weightEdgeArray.size());

        // addTransposedNodeInTransposedGraph
        {
            final DirectedGraphNode<D> newTransposedNode = new DirectedGraphNode<>(transposedData);
            this.transposedGraph.put(transposedData, newTransposedNode);

            for (int i = 0; i < transposedDataEdgeArray.size(); ++i) {
                assert (this.transposedGraph.containsKey(transposedDataEdgeArray.get(i)));

                newTransposedNode.addNode(new DirectedGraphNodeEdge<>(transposedWeightEdgeArray.get(i), newTransposedNode, this.transposedGraph.get(transposedDataEdgeArray.get(i))));
            }
        }

        // addTransposedNodeInGraph
        {
            {
                final DirectedGraphNode<D> newNode = new DirectedGraphNode<>(transposedData);
                this.graph.put(transposedData, newNode);
            }

            for (int i = 0; i < transposedDataEdgeArray.size(); ++i) {
                assert (this.graph.containsKey(transposedDataEdgeArray.get(i)));

                final DirectedGraphNode<D> node = this.graph.get(transposedDataEdgeArray.get(i));

                node.addNode(new DirectedGraphNodeEdge<>(weightEdgeArray.get(i), node, this.graph.get(transposedData)));
            }
        }

        assert (!this.dataIndex.containsKey(transposedData));
        this.dataIndex.put(transposedData, this.dataIndex.size());
    }

    public final void removeTransposedNode(final D transposedData,
                                           final ArrayList<D> transposedDataEdgeArray) {
        // removeTransposedNodeInGraph
        {
            assert (this.graph.containsKey(transposedData));

            for (final D transposedDataEdge : transposedDataEdgeArray) {
                assert (this.graph.containsKey(transposedDataEdge));

                final DirectedGraphNode<D> node = this.graph.get(transposedDataEdge);
                node.removeEdge(transposedData);
            }
        }

        // removeTransposedNodeInTransposedGraph
        {
            assert (this.transposedGraph.containsKey(transposedData));
            final DirectedGraphNode<D> removeTransposedNode = this.transposedGraph.get(transposedData);

            for (final D transposedDataEdge : transposedDataEdgeArray) {
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
                          final LinkedList<DirectedGraphNode<D>> outBfsList) {

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

            outBfsList.addFirst(node);

            for (final DirectedGraphNodeEdge<D> nextEdge : node.getEdges().values()) {
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
                                          final LinkedList<DirectedGraphNode<D>> outPostOrderNodeReverseList) {
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
                                          final LinkedList<DirectedGraphNode<D>> orderedNodes,
                                          final boolean isTransposedGraph,
                                          final LinkedList<DirectedGraphNode<D>> outPostOrderNodeReverseList) {
        // O(n + e)

        final HashMap<D, DirectedGraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;

        final boolean[] isDiscovered = new boolean[this.dataIndex.size()];

        for (final DirectedGraphNode<D> node : orderedNodes) {
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
                                                   final LinkedList<DirectedGraphNode<D>> outPostOrderNodeReverseList) {

        final HashMap<D, DirectedGraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;
        final DirectedGraphNode<D> startNode = graph.get(startData);

        isDiscovered[this.dataIndex.get(startNode.getData())] = true;

        for (final DirectedGraphNodeEdge<D> edge : startNode.getEdges().values()) {
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
                          final LinkedList<DirectedGraphNode<D>> outDfsList) {

        final HashMap<D, DirectedGraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;
        final DirectedGraphNode<D> startNode = graph.get(startData);

        final boolean[] isDiscovered = new boolean[this.dataIndex.size()];

        final LinkedList<DirectedGraphNode<D>> bfsStack = new LinkedList<>();

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
            final DirectedGraphNode<D> node = bfsStack.getLast();
            bfsStack.removeLast();

            outDfsList.addLast(node);

            for (final DirectedGraphNodeEdge<D> nextEdge : node.getEdges().values()) {
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
    public final ArrayList<DirectedGraphNode<D>> tsp2Approximation(final boolean isSkipScc,
                                                                   final D startData) {
        assert (this.dataIndex.containsKey(startData));

        // create mst graph
        final DirectedGraph<D> mstGraph;
        {
            final ArrayList<DirectedGraphNodeEdge<D>> mst = this.kruskalMst();
            if (mst.isEmpty()) {
                final ArrayList<DirectedGraphNode<D>> outTspList = new ArrayList<>(1);
                final DirectedGraphNode<D> startNode = this.graph.get(startData);
                outTspList.add(startNode);
                return outTspList;
            }

            final HashMap<D, ArrayList<D>> dataEdgeArrayMap = new HashMap<>(this.dataIndex.size());
            final HashMap<D, ArrayList<Integer>> weightEdgeArrayMap = new HashMap<>(this.dataIndex.size());

            for (final DirectedGraphNodeEdge<D> edge : mst) {
                final DirectedGraphNode<D> from = edge.getNode1();
                final D fromPoint = from.getData();
                final DirectedGraphNode<D> to = edge.getNode2();
                final D toPoint = to.getData();

                final int dist = edge.getWeight();

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
                    weightEdgeArray.add(dist);
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
                    weightEdgeArray.add(dist);
                }
            }

            final ArrayList<D> dataArray;
            {
                dataArray = new ArrayList<>(this.dataIndex.size());

                for (final D data : this.dataIndex.keySet()) {
                    dataArray.add(data);
                }
            }

            mstGraph = new DirectedGraph<>(false, dataArray, dataEdgeArrayMap, weightEdgeArrayMap);
        } // end create mst graph

        final ArrayList<DirectedGraphNode<D>> outMstDfsPreOrderAndAddReturnList;
        {
            final boolean[] isDiscovered = new boolean[this.dataIndex.size()];
            outMstDfsPreOrderAndAddReturnList = new ArrayList<>(mstGraph.nodeCount() * 2);
            mstGraph.dfsPreOrderAndAddReturnRecursive(isSkipScc, startData, false, isDiscovered, outMstDfsPreOrderAndAddReturnList);
        }

        final ArrayList<DirectedGraphNode<D>> outTspList;
        {
            outTspList = new ArrayList<>(mstGraph.nodeCount());

            final boolean[] isDiscovered = new boolean[this.dataIndex.size()];

            for (final DirectedGraphNode<D> mstNode : outMstDfsPreOrderAndAddReturnList) {
                final D data = mstNode.getData();
                assert (this.graph.containsKey(data));

                final int iData = this.dataIndex.get(data);
                if (isDiscovered[iData]) {
                    continue;
                }

                isDiscovered[iData] = true;

                final DirectedGraphNode<D> node = this.graph.get(data);
                outTspList.add(node);
            }

            final DirectedGraphNode<D> startNode = this.graph.get(startData);
            outTspList.add(startNode);
        }

        return outTspList;
    }

    public final void dfsPreOrderAndAddReturnRecursive(final boolean isSkipScc,
                                                       final D startData,
                                                       final boolean isTransposedGraph,
                                                       final boolean[] isDiscovered,
                                                       final ArrayList<DirectedGraphNode<D>> outDfsPreOrderAndAddReturnList) {

        final HashMap<D, DirectedGraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;
        final DirectedGraphNode<D> startNode = graph.get(startData);

        isDiscovered[this.dataIndex.get(startNode.getData())] = true;

        outDfsPreOrderAndAddReturnList.add(startNode);

        for (final DirectedGraphNodeEdge<D> edge : startNode.getEdges().values()) {
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

    // topological sort
    public final LinkedList<DirectedGraphNode<D>> topologicalSort(final boolean includeScc) {
        // O(n + e) + O(n + e) + O(n + e)

        final LinkedList<DirectedGraphNode<D>> dfsPostOrderNodeReverseList = new LinkedList<>();
        dfsPostOrderReverse(false, false, dfsPostOrderNodeReverseList);

        // get sortedList
        final LinkedList<DirectedGraphNode<D>> outSortedList = new LinkedList<>();
        dfsPostOrderReverse(!includeScc, dfsPostOrderNodeReverseList, false, outSortedList);

        return outSortedList;
    }

    // ---

    // scc Kosaraju
    private void kosarajuScc(final LinkedList<DirectedGraphNode<D>> outScc) {
        // O(n + e) + O(n + e)

        final LinkedList<DirectedGraphNode<D>> dfsPostOrderNodeReverseList = new LinkedList<>();
        dfsPostOrderReverse(false, false, dfsPostOrderNodeReverseList);

        // get scc groups with size > 1
        {
            final boolean[] transposedIsDiscovered = new boolean[this.dataIndex.size()];
            final LinkedList<DirectedGraphNode<D>> scc = new LinkedList<>();

            for (final DirectedGraphNode<D> node : dfsPostOrderNodeReverseList) {
                final D data = node.getData();
                final DirectedGraphNode<D> transposedNode = this.transposedGraph.get(data);

                if (transposedIsDiscovered[this.dataIndex.get(transposedNode.getData())]) {
                    continue;
                }

                dfsPostOrderReverseRecursive(false, transposedNode.getData(), true, transposedIsDiscovered, scc);

                assert (scc.size() >= 1);

                if (scc.size() > 1) {
                    for (final DirectedGraphNode<D> skip : scc) {
                        outScc.add(skip);
                    }
                }

                scc.clear();
            }
        }
    }

    // create
    private HashMap<D, DirectedGraphNode<D>> createGraph(final ArrayList<D> dataNodeArray,
                                                         final HashMap<D, ArrayList<D>> dataEdgeArrayMap,
                                                         final HashMap<D, ArrayList<Integer>> weightEdgeArrayMap) {
        // O(n) + O(ne)

        final HashMap<D, DirectedGraphNode<D>> outGraph = new HashMap<>(dataNodeArray.size());

        for (final D data : dataNodeArray) {
            final DirectedGraphNode<D> dataNode = new DirectedGraphNode<>(data);
            outGraph.put(dataNode.getData(), dataNode);
        }

        for (final D fromData : dataNodeArray) {
            final DirectedGraphNode<D> from = outGraph.get(fromData);
            final ArrayList<D> toDataEdgeArray = dataEdgeArrayMap.get(fromData);
            final ArrayList<Integer> toWeightEdgeArray = weightEdgeArrayMap.get(fromData);

            assert (toDataEdgeArray.size() == toWeightEdgeArray.size());

            for (int i = 0; i < toDataEdgeArray.size(); ++i) {
                final D toData = toDataEdgeArray.get(i);
                assert (outGraph.containsKey(toData));

                final DirectedGraphNode<D> to = outGraph.get(toData);

                from.addNode(new DirectedGraphNodeEdge<>(toWeightEdgeArray.get(i), from, to));
            }
        }

        return outGraph;
    }

    private HashMap<D, DirectedGraphNode<D>> createTransposedGraph(final ArrayList<D> dataNodeArray,
                                                                   final HashMap<D, ArrayList<D>> dataEdgeArrayMap) {
        // O(n) + O(ne)

        final HashMap<D, DirectedGraphNode<D>> outTransposedGraph = new HashMap<>(dataNodeArray.size());

        for (final D data : dataNodeArray) {
            final DirectedGraphNode<D> transposedNode = new DirectedGraphNode<>(data);
            outTransposedGraph.put(transposedNode.getData(), transposedNode);
        }

        for (final D toData : dataNodeArray) {
            final ArrayList<D> fromDataEdgeArray = dataEdgeArrayMap.get(toData);

            for (final D fromDataEdge : fromDataEdgeArray) {
                assert (outTransposedGraph.containsKey(fromDataEdge));

                final DirectedGraphNode<D> from = outTransposedGraph.get(fromDataEdge);

                final int edgeWeight = this.graph.get(toData).getEdges().get(from.getData()).getWeight();
                from.addNode(new DirectedGraphNodeEdge<>(edgeWeight, from, outTransposedGraph.get(toData)));
            }
        }

        return outTransposedGraph;
    }

}