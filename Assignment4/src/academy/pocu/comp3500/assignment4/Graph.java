package academy.pocu.comp3500.assignment4;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;

public final class Graph<D> {
    private final HashMap<D, Integer> dataIndex;
    private HashMap<D, Boolean> dataScc;

    private final HashMap<D, GraphNode<D>> graph;
    private HashMap<D, GraphNode<D>> transposedGraph;

    // ---

    public Graph(final boolean useTransposedGraph, final ArrayList<D> dataNodeArray, final HashMap<D, ArrayList<D>> dataEdgeArrayMap, final HashMap<D, ArrayList<Integer>> weightEdgeArrayMap) {
        this.graph = createGraph(dataNodeArray, dataEdgeArrayMap, weightEdgeArrayMap);
        if (useTransposedGraph) {
            this.transposedGraph = createTransposedGraph(dataNodeArray, dataEdgeArrayMap);
        }

        assert (this.graph.size() == dataNodeArray.size());
        assert !useTransposedGraph || (this.transposedGraph.size() == dataNodeArray.size());

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

    public final int nodeCount() {
        assert (this.dataIndex.size() == this.graph.size());
        assert (this.transposedGraph == null || this.dataIndex.size() == this.transposedGraph.size());

        return this.dataIndex.size();
    }

    public final HashMap<D, Boolean> getDataScc() {
        return dataScc;
    }

    public final HashMap<D, GraphNode<D>> getGraph() {
        return graph;
    }

    public final HashMap<D, GraphNode<D>> getTransposedGraph() {
        return transposedGraph;
    }

    // setter
    public final void setScc() {
        this.dataScc.clear();
        final LinkedList<GraphNode<D>> scc = new LinkedList<>();
        kosarajuScc(scc);

        for (final GraphNode<D> sccNode : scc) {
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
            final GraphNode<D> newNode = new GraphNode<>(data);
            this.graph.put(data, newNode);

            for (int i = 0; i < dataEdgeArray.size(); ++i) {
                assert (this.graph.containsKey(dataEdgeArray.get(i)));

                newNode.addNode(new GraphEdge<>(weightEdgeArray.get(i), newNode, this.graph.get(dataEdgeArray.get(i))));
            }
        }

        // addNodeInTransposedGraph
        if (this.transposedGraph != null) {
            {
                final GraphNode<D> newTransposedNode = new GraphNode<>(data);
                this.transposedGraph.put(data, newTransposedNode);
            }

            for (int i = 0; i < dataEdgeArray.size(); ++i) {
                assert (this.transposedGraph.containsKey(dataEdgeArray.get(i)));

                final GraphNode<D> transposedNode = this.transposedGraph.get(dataEdgeArray.get(i));

                transposedNode.addNode(new GraphEdge<>(transposedWeightEdgeArray.get(i), transposedNode, this.transposedGraph.get(data)));
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
            final GraphNode<D> removeNode = this.graph.get(data);

            for (final D dataEdge : dataEdgeArray) {
                assert (this.graph.containsKey(dataEdge));

                removeNode.removeEdge(dataEdge);
            }
        }

        // removeNodeInTransposedGraph
        if (this.transposedGraph != null) {
            assert (this.transposedGraph.containsKey(data));

            for (final D dataEdge : dataEdgeArray) {
                assert (this.transposedGraph.containsKey(dataEdge));

                final GraphNode<D> transposedNode = this.transposedGraph.get(dataEdge);
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
                                        final ArrayList<D> transposedDataEdgeArray,
                                        final ArrayList<Integer> transposedWeightEdgeArray,
                                        final ArrayList<Integer> weightEdgeArray) {

        assert (this.transposedGraph != null);

        assert (transposedDataEdgeArray.size() == transposedWeightEdgeArray.size());
        assert (transposedDataEdgeArray.size() == weightEdgeArray.size());

        // addTransposedNodeInTransposedGraph
        {
            final GraphNode<D> newTransposedNode = new GraphNode<>(transposedData);
            this.transposedGraph.put(transposedData, newTransposedNode);

            for (int i = 0; i < transposedDataEdgeArray.size(); ++i) {
                assert (this.transposedGraph.containsKey(transposedDataEdgeArray.get(i)));

                newTransposedNode.addNode(new GraphEdge<>(transposedWeightEdgeArray.get(i), newTransposedNode, this.transposedGraph.get(transposedDataEdgeArray.get(i))));
            }
        }

        // addTransposedNodeInGraph
        {
            {
                final GraphNode<D> newNode = new GraphNode<>(transposedData);
                this.graph.put(transposedData, newNode);
            }

            for (int i = 0; i < transposedDataEdgeArray.size(); ++i) {
                assert (this.graph.containsKey(transposedDataEdgeArray.get(i)));

                final GraphNode<D> node = this.graph.get(transposedDataEdgeArray.get(i));

                node.addNode(new GraphEdge<>(weightEdgeArray.get(i), node, this.graph.get(transposedData)));
            }
        }

        assert (!this.dataIndex.containsKey(transposedData));
        this.dataIndex.put(transposedData, this.dataIndex.size());
    }

    public final void removeTransposedNode(final D transposedData,
                                           final ArrayList<D> transposedDataEdgeArray) {

        assert (this.transposedGraph != null);

        // removeTransposedNodeInGraph
        {
            assert (this.graph.containsKey(transposedData));

            for (final D transposedDataEdge : transposedDataEdgeArray) {
                assert (this.graph.containsKey(transposedDataEdge));

                final GraphNode<D> node = this.graph.get(transposedDataEdge);
                node.removeEdge(transposedData);
            }
        }

        // removeTransposedNodeInTransposedGraph
        {
            assert (this.transposedGraph.containsKey(transposedData));
            final GraphNode<D> removeTransposedNode = this.transposedGraph.get(transposedData);

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
                          final LinkedList<GraphNode<D>> outBfsList) {

        final HashMap<D, GraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;
        final GraphNode<D> startNode = graph.get(startData);

        final boolean[] isDiscovered = new boolean[this.dataIndex.size()];

        final LinkedList<GraphNode<D>> bfsQueue = new LinkedList<>();

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
            final GraphNode<D> node = bfsQueue.poll();

            outBfsList.addFirst(node);

            for (final GraphEdge<D> nextEdge : node.getEdges().values()) {
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
                                          final LinkedList<GraphNode<D>> outPostOrderNodeReverseList) {
        // O(n + e)

        final HashMap<D, GraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;

        final boolean[] isDiscovered = new boolean[this.dataIndex.size()];

        for (final GraphNode<D> node : graph.values()) {
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
                                          final LinkedList<GraphNode<D>> orderedNodes,
                                          final boolean isTransposedGraph,
                                          final LinkedList<GraphNode<D>> outPostOrderNodeReverseList) {
        // O(n + e)

        final HashMap<D, GraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;

        final boolean[] isDiscovered = new boolean[this.dataIndex.size()];

        for (final GraphNode<D> node : orderedNodes) {
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
                                                   final LinkedList<GraphNode<D>> outPostOrderNodeReverseList) {

        final HashMap<D, GraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;
        final GraphNode<D> startNode = graph.get(startData);

        isDiscovered[this.dataIndex.get(startNode.getData())] = true;

        for (final GraphEdge<D> edge : startNode.getEdges().values()) {
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
                          final LinkedList<GraphNode<D>> outDfsList) {

        final HashMap<D, GraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;
        final GraphNode<D> startNode = graph.get(startData);

        final boolean[] isDiscovered = new boolean[this.dataIndex.size()];

        final LinkedList<GraphNode<D>> bfsStack = new LinkedList<>();

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
            final GraphNode<D> node = bfsStack.getLast();
            bfsStack.removeLast();

            outDfsList.addLast(node);

            for (final GraphEdge<D> nextEdge : node.getEdges().values()) {
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
    public final ArrayList<GraphNode<D>> tsp2Approximation(final boolean isSkipScc,
                                                           final D startData) {
        assert (this.dataIndex.containsKey(startData));

        // create mst graph
        final Graph<D> mstGraph;
        {
            final ArrayList<GraphEdge<D>> mst = this.kruskalMst();
            if (mst.isEmpty()) {
                final ArrayList<GraphNode<D>> outTspList = new ArrayList<>(1);
                final GraphNode<D> startNode = this.graph.get(startData);
                outTspList.add(startNode);
                return outTspList;
            }

            final HashMap<D, ArrayList<D>> dataEdgeArrayMap = new HashMap<>(this.dataIndex.size());
            final HashMap<D, ArrayList<Integer>> weightEdgeArrayMap = new HashMap<>(this.dataIndex.size());

            for (final GraphEdge<D> edge : mst) {
                final GraphNode<D> from = edge.getNode1();
                final D fromPoint = from.getData();
                final GraphNode<D> to = edge.getNode2();
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

            mstGraph = new Graph<>(false, dataArray, dataEdgeArrayMap, weightEdgeArrayMap);
        }// end create mst graph

        final ArrayList<GraphNode<D>> outMstDfsPreOrderAndAddReturnList;
        {
            final boolean[] isDiscovered = new boolean[this.dataIndex.size()];
            outMstDfsPreOrderAndAddReturnList = new ArrayList<>(mstGraph.nodeCount() * 2);
            mstGraph.dfsPreOrderAndAddReturnRecursive(isSkipScc, startData, false, isDiscovered, outMstDfsPreOrderAndAddReturnList);
        }

        final ArrayList<GraphNode<D>> outTspList;
        {
            outTspList = new ArrayList<>(mstGraph.nodeCount());

            final boolean[] isDiscovered = new boolean[this.dataIndex.size()];

            for (final GraphNode<D> mstNode : outMstDfsPreOrderAndAddReturnList) {
                final D data = mstNode.getData();
                assert (this.graph.containsKey(data));

                final int iData = this.dataIndex.get(data);
                if (isDiscovered[iData]) {
                    continue;
                }

                isDiscovered[iData] = true;

                final GraphNode<D> node = this.graph.get(data);
                outTspList.add(node);
            }

            final GraphNode<D> startNode = this.graph.get(startData);
            outTspList.add(startNode);
        }

        return outTspList;
    }

    public final void dfsPreOrderAndAddReturnRecursive(final boolean isSkipScc,
                                                       final D startData,
                                                       final boolean isTransposedGraph,
                                                       final boolean[] isDiscovered,
                                                       final ArrayList<GraphNode<D>> outDfsPreOrderAndAddReturnList) {

        final HashMap<D, GraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;
        final GraphNode<D> startNode = graph.get(startData);

        isDiscovered[this.dataIndex.get(startNode.getData())] = true;

        outDfsPreOrderAndAddReturnList.add(startNode);

        for (final GraphEdge<D> edge : startNode.getEdges().values()) {
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

    public ArrayList<GraphEdge<D>> kruskalMst() {
        ArrayList<GraphEdge<D>> mst = new ArrayList<>(this.graph.size());

        ArrayList<GraphNode<D>> nodes = new ArrayList<>(this.graph.size());
        ArrayList<GraphEdge<D>> edges = new ArrayList<>(this.graph.size() * this.graph.size());

        for (final GraphNode<D> node : this.graph.values()) {
            nodes.add(node);
            for (final GraphEdge<D> edge : node.getEdges().values()) {
                edges.add(edge);
            }
        }

        DisjointSet<GraphNode<D>> set = new DisjointSet<>(nodes);
        Sort.radixSort(edges, GraphEdge<D>::getWeight);

        for (final GraphEdge<D> edge : edges) {
            final GraphNode<D> n1 = edge.getNode1();
            final GraphNode<D> n2 = edge.getNode2();

            final GraphNode<D> root1 = set.find(n1);
            final GraphNode<D> root2 = set.find(n2);

            if (!root1.equals(root2)) {
                mst.add(edge);
                set.union(n1, n2);
            }
        }

        return mst;
    }

    // topological sort
    public final LinkedList<GraphNode<D>> topologicalSort(final boolean includeScc) {
        // O(n + e) + O(n + e) + O(n + e)

        final LinkedList<GraphNode<D>> dfsPostOrderNodeReverseList = new LinkedList<>();
        dfsPostOrderReverse(false, false, dfsPostOrderNodeReverseList);

        // get sortedList
        final LinkedList<GraphNode<D>> outSortedList = new LinkedList<>();
        dfsPostOrderReverse(!includeScc, dfsPostOrderNodeReverseList, false, outSortedList);

        return outSortedList;
    }

    // scc Kosaraju
    public void kosarajuScc(final LinkedList<GraphNode<D>> outScc) {
        // O(n + e) + O(n + e)

        final LinkedList<GraphNode<D>> dfsPostOrderNodeReverseList = new LinkedList<>();
        dfsPostOrderReverse(false, false, dfsPostOrderNodeReverseList);

        // get scc groups with size > 1
        {
            final boolean[] transposedIsDiscovered = new boolean[this.dataIndex.size()];
            final LinkedList<GraphNode<D>> scc = new LinkedList<>();

            for (final GraphNode<D> node : dfsPostOrderNodeReverseList) {
                final D data = node.getData();
                final GraphNode<D> transposedNode = this.transposedGraph.get(data);

                if (transposedIsDiscovered[this.dataIndex.get(transposedNode.getData())]) {
                    continue;
                }

                dfsPostOrderReverseRecursive(false, transposedNode.getData(), true, transposedIsDiscovered, scc);

                assert (scc.size() >= 1);

                if (scc.size() > 1) {
                    for (final GraphNode<D> skip : scc) {
                        outScc.add(skip);
                    }
                }

                scc.clear();
            }
        }
    }

    // ---

    // create
    private HashMap<D, GraphNode<D>> createGraph(final ArrayList<D> dataNodeArray,
                                                 final HashMap<D, ArrayList<D>> dataEdgeArrayMap,
                                                 final HashMap<D, ArrayList<Integer>> weightEdgeArrayMap) {
        // O(n) + O(ne)

        final HashMap<D, GraphNode<D>> outGraph = new HashMap<>(dataNodeArray.size());

        for (final D data : dataNodeArray) {
            final GraphNode<D> dataNode = new GraphNode<>(data);
            outGraph.put(dataNode.getData(), dataNode);
        }

        for (final D fromData : dataNodeArray) {
            final GraphNode<D> from = outGraph.get(fromData);
            final ArrayList<D> toDataEdgeArray = dataEdgeArrayMap.get(fromData);
            final ArrayList<Integer> toWeightEdgeArray = weightEdgeArrayMap.get(fromData);

            assert (toDataEdgeArray.size() == toWeightEdgeArray.size());

            for (int i = 0; i < toDataEdgeArray.size(); ++i) {
                final D toData = toDataEdgeArray.get(i);
                assert (outGraph.containsKey(toData));

                final GraphNode<D> to = outGraph.get(toData);

                from.addNode(new GraphEdge<>(toWeightEdgeArray.get(i), from, to));
            }
        }

        return outGraph;
    }

    private HashMap<D, GraphNode<D>> createTransposedGraph(final ArrayList<D> dataNodeArray,
                                                           final HashMap<D, ArrayList<D>> dataEdgeArrayMap) {
        // O(n) + O(ne)

        final HashMap<D, GraphNode<D>> outTransposedGraph = new HashMap<>(dataNodeArray.size());

        for (final D data : dataNodeArray) {
            final GraphNode<D> transposedNode = new GraphNode<>(data);
            outTransposedGraph.put(transposedNode.getData(), transposedNode);
        }

        for (final D toData : dataNodeArray) {
            final ArrayList<D> fromDataEdgeArray = dataEdgeArrayMap.get(toData);

            for (final D fromDataEdge : fromDataEdgeArray) {
                assert (outTransposedGraph.containsKey(fromDataEdge));

                final GraphNode<D> from = outTransposedGraph.get(fromDataEdge);

                final int edgeWeight = this.graph.get(toData).getEdges().get(from.getData()).getWeight();
                from.addNode(new GraphEdge<>(edgeWeight, from, outTransposedGraph.get(toData)));
            }
        }

        return outTransposedGraph;
    }


    //
    //
    //
    //
    //



    // mac flow
//    public final void maxFlow(final boolean isSkipScc,
//                              final D source,
//                              final D sink,
//                              final boolean mainIsTransposedGraph,
//                              final int[] outFlow,
//                              final int[] outFlowIndex) {
//
//        if (isSkipScc) {
//            assert (!this.dataScc.containsKey(source));
//            assert (!this.dataScc.containsKey(sink));
//        }
//
//        final int BACK_FLOW_CAPACITY = 0;
//
//        assert (outFlow.length == this.dataIndex.size());
//        assert (outFlowIndex.length == 2);
//
//        final HashMap<D, GraphNode<D>> mainGraph = mainIsTransposedGraph ? this.transposedGraph : this.graph;
//        final HashMap<D, GraphNode<D>> transposedGraph = !mainIsTransposedGraph ? this.transposedGraph : this.graph;
//
//        final int[] transposedFlow = new int[this.dataIndex.size()];
//        final LinkedList<IsTransposedFlow<D>> path = new LinkedList<>();
//        final int[] minRemainCapacity = new int[1];
//
//        final LinkedList<IsTransposedFlow<D>> bfsQueue = new LinkedList<>();
//        final HashMap<IsTransposedFlow<D>, IsTransposedFlow<D>> prePathMap = new HashMap<>();
//
//        while (true) {
//            path.clear();
//            minRemainCapacity[0] = 0;
//
//            // bfs
//            {
//                final boolean[] isDiscovered = new boolean[this.dataIndex.size()];
//                bfsQueue.clear();
//                prePathMap.clear();
//
//                {
//                    isDiscovered[this.dataIndex.get(source)] = true;
//                    final IsTransposedFlow<D> sourceIsTransposedFlow = new IsTransposedFlow<>(false, null, source);
//                    bfsQueue.addLast(sourceIsTransposedFlow);
//                    prePathMap.put(sourceIsTransposedFlow, null);
//                }
//
//                IsTransposedFlow<D> isTransposedFlow = null;
//                while (!bfsQueue.isEmpty()) {
//                    isTransposedFlow = bfsQueue.poll();
//
//                    final DirectedGraphNode<D> node = mainGraph.get(isTransposedFlow.getTo());
//                    final DirectedGraphNode<D> transposedNode = transposedGraph.get(isTransposedFlow.getTo());
//                    final D nodeData = node.getData();
//                    final int iNodeData = this.dataIndex.get(nodeData);
//
//                    if (isTransposedFlow.getTo().equals(sink)) {
//                        break;
//                    }
//
//                    for (final DirectedGraphNode<D> nextNode : node.getNodes()) {
//                        if (isSkipScc) {
//                            if (this.dataScc.containsKey(nextNode.getData())) {
//                                continue;
//                            }
//                        }
//
//                        final D nextData = nextNode.getData();
//                        final int iNextData = this.dataIndex.get(nextData);
//
//                        final int edgeFlow = outFlow[iNextData];
//                        final int edgeCap = this.getDataWeight.apply(nextData);
//                        final int edgeRemain = edgeCap - edgeFlow;
//
//                        assert (edgeFlow >= 0);
//                        assert (edgeRemain >= 0);
//
//                        if (edgeRemain <= 0) {
//                            continue;
//                        }
//
//                        if (isDiscovered[iNextData]) {
//                            continue;
//                        }
//
//                        isDiscovered[iNextData] = true;
//                        final IsTransposedFlow<D> nextIsTransposedFlow = new IsTransposedFlow<>(false, nodeData, nextData);
//                        bfsQueue.addLast(nextIsTransposedFlow);
//                        prePathMap.put(nextIsTransposedFlow, isTransposedFlow);
//                    }
//
//                    for (final DirectedGraphNode<D> nextTransposedNode : transposedNode.getNodes()) {
//                        if (isSkipScc) {
//                            if (this.dataScc.containsKey(nextTransposedNode.getData())) {
//                                continue;
//                            }
//                        }
//
//                        final D nextTransposedData = nextTransposedNode.getData();
//                        final int iNextTransposedData = this.dataIndex.get(nextTransposedData);
//
//                        final int edgeTransposedFlow = transposedFlow[iNodeData];
//                        final int edgeTransposedRemain = BACK_FLOW_CAPACITY - edgeTransposedFlow;
//
//                        assert (edgeTransposedFlow <= 0);
//                        assert (edgeTransposedRemain >= 0);
//
//                        if (edgeTransposedRemain <= 0) {
//                            continue;
//                        }
//
//                        if (isDiscovered[iNextTransposedData]) {
//                            continue;
//                        }
//
//                        isDiscovered[this.dataIndex.get(nodeData)] = true;
//                        final IsTransposedFlow<D> nextIsTransposedFlow = new IsTransposedFlow<>(true, nodeData, nextTransposedData);
//                        bfsQueue.addLast(nextIsTransposedFlow);
//                        prePathMap.put(nextIsTransposedFlow, isTransposedFlow);
//                    }
//                }
//
//                assert (isTransposedFlow != null);
//
//                if (!sink.equals(isTransposedFlow.getTo())) {
//                    break;
//                }
//
//                minRemainCapacity[0] = Integer.MAX_VALUE;
//                while (isTransposedFlow != null) {
//                    if (isTransposedFlow.isTransposedFlow()) {
//                        final D fromData = isTransposedFlow.getFromOrNull();
//                        final int iFromData = this.dataIndex.get(fromData);
//
//                        final int edgeTransposedFlow = transposedFlow[iFromData];
//                        final int edgeTransposedRemain = BACK_FLOW_CAPACITY - edgeTransposedFlow;
//                        minRemainCapacity[0] = Math.min(minRemainCapacity[0], edgeTransposedRemain);
//                    } else {
//                        final D toData = isTransposedFlow.getTo();
//                        final int iToData = this.dataIndex.get(toData);
//
//                        final int edgeCapacity = this.getDataWeight.apply(toData);
//                        final int edgeFlow = outFlow[iToData];
//                        final int edgeRemain = edgeCapacity - edgeFlow;
//                        minRemainCapacity[0] = Math.min(minRemainCapacity[0], edgeRemain);
//                    }
//                    path.addFirst(isTransposedFlow);
//
//                    isTransposedFlow = prePathMap.get(isTransposedFlow);
//                }
//            } // end bfs
//
//            if (path.isEmpty()) {
//                break;
//            }
//
//            IsTransposedFlow<D> isTransposedFlow = path.getFirst();
//            while (!path.isEmpty()) {
//                isTransposedFlow = path.poll();
//
//                if (isTransposedFlow.isTransposedFlow()) {
//                    final D fromData = isTransposedFlow.getFromOrNull();
//                    final int iFromData = this.dataIndex.get(fromData);
//
//                    transposedFlow[iFromData] += minRemainCapacity[0];
//                    outFlow[iFromData] -= minRemainCapacity[0];
//                } else {
//                    final D toData = isTransposedFlow.getTo();
//                    final int iToData = this.dataIndex.get(toData);
//
//                    outFlow[iToData] += minRemainCapacity[0];
//                    transposedFlow[iToData] -= minRemainCapacity[0];
//                }
//            }
//        }
//
//        assert (outFlow[this.dataIndex.get(source)] == outFlow[this.dataIndex.get(sink)]);
//
//        outFlowIndex[0] = this.dataIndex.get(source);
//        outFlowIndex[1] = this.dataIndex.get(sink);
//    }

}