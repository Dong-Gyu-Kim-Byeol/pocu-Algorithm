package academy.pocu.comp3500.lab11;

import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.function.Function;

public final class Graph<D> {
    private final HashMap<D, Integer> indexMap;
    private HashMap<D, Boolean> dataScc;

    private final HashMap<D, GraphNode<D>> graph;
    private HashMap<D, GraphNode<D>> transposedGraph;

    // ---

    public Graph(final boolean useTransposedGraph,
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
            this.indexMap = new HashMap<>(nodeDataArray.size());
            int i = 0;
            for (final D data : nodeDataArray) {
                this.indexMap.put(data, i++);
            }
        }

        if (useTransposedGraph) {
            this.dataScc = new HashMap<>();
            setScc();
        }
    }

    // ---

    public final int nodeCount() {
        assert (this.indexMap.size() == this.graph.size());
        assert (this.transposedGraph == null || this.indexMap.size() == this.transposedGraph.size());

        return this.indexMap.size();
    }

    public final HashMap<D, Integer> getIndexMap() {
        return indexMap;
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
                              final ArrayList<D> edgeDataArray,
                              final ArrayList<Integer> edgeWeightArray) {

        assert (edgeDataArray.size() == edgeWeightArray.size());

        assert (!this.indexMap.containsKey(data));
        this.indexMap.put(data, this.indexMap.size());

        // addNodeInGraph
        {
            final GraphNode<D> newNode = new GraphNode<>(data);
            this.graph.put(data, newNode);

            for (int i = 0; i < edgeDataArray.size(); ++i) {
                assert (this.graph.containsKey(edgeDataArray.get(i)));

                newNode.addNode(new GraphEdge<>(edgeWeightArray.get(i), newNode, this.graph.get(edgeDataArray.get(i))));
            }
        }

        // addNodeInTransposedGraph
        if (this.transposedGraph != null) {
            {
                final GraphNode<D> newTransposedNode = new GraphNode<>(data);
                this.transposedGraph.put(data, newTransposedNode);
            }

            for (int i = 0; i < edgeDataArray.size(); ++i) {
                assert (this.transposedGraph.containsKey(edgeDataArray.get(i)));

                final GraphNode<D> transposedNode = this.transposedGraph.get(edgeDataArray.get(i));

                transposedNode.addNode(new GraphEdge<>(edgeWeightArray.get(i), transposedNode, this.transposedGraph.get(data)));
            }
        }
    }

    public final void removeNode(final D removeData) {

        // removeNodeInTransposedGraph
        if (this.transposedGraph != null) {
            assert (this.transposedGraph.containsKey(removeData));
            final GraphNode<D> removeOriginNode = this.graph.get(removeData);

            for (final GraphEdge<D> fromOriginEdge : removeOriginNode.getEdges().values()) {
                final D fromData = fromOriginEdge.getTo().getData();
                assert (this.transposedGraph.containsKey(fromData));

                final GraphNode<D> fromTransposedNode = this.transposedGraph.get(fromData);
                fromTransposedNode.removeEdge(removeData);
            }
        }

        // removeNodeInGraph
        {
            assert (this.graph.containsKey(removeData));
            final GraphNode<D> removeNode = this.graph.get(removeData);
            removeNode.getEdges().clear();
        }


        assert (this.indexMap.containsKey(removeData));
        this.indexMap.remove(removeData);

        this.graph.remove(removeData);
        if (this.transposedGraph != null) {
            this.transposedGraph.remove(removeData);
        }
    }

    public final void addTransposedNode(final D transposedData,
                                        final ArrayList<D> transposedEdgeDataArray,
                                        final ArrayList<Integer> transposedEdgeWeightArray) {

        assert (this.transposedGraph != null);

        assert (transposedEdgeDataArray.size() == transposedEdgeWeightArray.size());

        assert (!this.indexMap.containsKey(transposedData));
        this.indexMap.put(transposedData, this.indexMap.size());

        // addTransposedNodeInTransposedGraph
        {
            final GraphNode<D> newTransposedNode = new GraphNode<>(transposedData);
            this.transposedGraph.put(transposedData, newTransposedNode);

            for (int i = 0; i < transposedEdgeDataArray.size(); ++i) {
                assert (this.transposedGraph.containsKey(transposedEdgeDataArray.get(i)));

                newTransposedNode.addNode(new GraphEdge<>(transposedEdgeWeightArray.get(i), newTransposedNode, this.transposedGraph.get(transposedEdgeDataArray.get(i))));
            }
        }

        // addTransposedNodeInGraph
        {
            {
                final GraphNode<D> newNode = new GraphNode<>(transposedData);
                this.graph.put(transposedData, newNode);
            }

            for (int i = 0; i < transposedEdgeDataArray.size(); ++i) {
                assert (this.graph.containsKey(transposedEdgeDataArray.get(i)));

                final GraphNode<D> node = this.graph.get(transposedEdgeDataArray.get(i));

                node.addNode(new GraphEdge<>(transposedEdgeWeightArray.get(i), node, this.graph.get(transposedData)));
            }
        }
    }

    public final void removeTransposedNode(final D removeTransposedData) {

        assert (this.transposedGraph != null);

        // removeTransposedNodeInGraph
        {
            assert (this.graph.containsKey(removeTransposedData));
            final GraphNode<D> removeTransposedNode = this.transposedGraph.get(removeTransposedData);

            for (final GraphEdge<D> fromTransposedDataEdge : removeTransposedNode.getEdges().values()) {
                final D fromData = fromTransposedDataEdge.getTo().getData();
                assert (this.graph.containsKey(fromData));

                final GraphNode<D> from = this.graph.get(fromData);
                from.removeEdge(removeTransposedData);
            }
        }

        // removeTransposedNodeInTransposedGraph
        {
            assert (this.transposedGraph.containsKey(removeTransposedData));
            final GraphNode<D> removeTransposedNode = this.transposedGraph.get(removeTransposedData);
            removeTransposedNode.getEdges().clear();
        }


        this.graph.remove(removeTransposedData);
        this.transposedGraph.remove(removeTransposedData);

        assert (this.indexMap.containsKey(removeTransposedData));
        this.indexMap.remove(removeTransposedData);
    }

    // bfs
    public final void bfsGraph(final boolean isSkipScc,
                               final boolean isTransposedGraph,
                               final LinkedList<GraphNode<D>> outBfsList) {

        final HashMap<D, GraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;
        final boolean[] isDiscovered = new boolean[this.indexMap.size()];

        for (final GraphNode<D> node : graph.values()) {
            this.bfs(isSkipScc, node.getData(), isTransposedGraph, isDiscovered, outBfsList);
        }
    }

    public final void bfs(final boolean isSkipScc,
                          final D startData,
                          final boolean isTransposedGraph,
                          final LinkedList<GraphNode<D>> outBfsList) {
        final boolean[] isDiscovered = new boolean[this.indexMap.size()];
        this.bfs(isSkipScc, startData, isTransposedGraph, isDiscovered, outBfsList);
    }

    public final void bfs(final boolean isSkipScc,
                          final D startData,
                          final boolean isTransposedGraph,
                          final boolean[] isDiscovered,
                          final LinkedList<GraphNode<D>> outBfsList) {

        final HashMap<D, GraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;
        final GraphNode<D> startNode = graph.get(startData);

        final LinkedList<GraphNode<D>> bfsQueue = new LinkedList<>();

        {
            if (isSkipScc) {
                if (this.dataScc.containsKey(startNode.getData())) {
                    return;
                }
            }

            if (isDiscovered[this.indexMap.get(startNode.getData())]) {
                return;
            }

            isDiscovered[this.indexMap.get(startNode.getData())] = true;
            bfsQueue.addLast(startNode);
        }

        while (!bfsQueue.isEmpty()) {
            final GraphNode<D> node = bfsQueue.poll();

            outBfsList.addFirst(node);

            for (final GraphEdge<D> nextEdge : node.getEdges().values()) {
                if (isSkipScc) {
                    if (this.dataScc.containsKey(nextEdge.getTo().getData())) {
                        continue;
                    }
                }

                if (isDiscovered[this.indexMap.get(nextEdge.getTo().getData())]) {
                    continue;
                }

                isDiscovered[this.indexMap.get(nextEdge.getTo().getData())] = true;
                bfsQueue.addLast(nextEdge.getTo());
            }
        }
    }

    // dfs
    public final void dfsGraphPostOrderReverse(final boolean isSkipScc,
                                               final boolean isTransposedGraph,
                                               final LinkedList<GraphNode<D>> outPostOrderNodeReverseList) {
        // O(n + e)

        final HashMap<D, GraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;

        final boolean[] isDiscovered = new boolean[this.indexMap.size()];

        for (final GraphNode<D> node : graph.values()) {
            if (isSkipScc) {
                if (this.dataScc.containsKey(node.getData())) {
                    continue;
                }
            }

            if (isDiscovered[this.indexMap.get(node.getData())]) {
                continue;
            }

            dfsPostOrderReverseRecursive(isSkipScc, node.getData(), isTransposedGraph, isDiscovered, outPostOrderNodeReverseList);
        }
    }

    public final void dfsListPostOrderReverse(final boolean isSkipScc,
                                              final LinkedList<GraphNode<D>> orderedNodes,
                                              final boolean isTransposedGraph,
                                              final LinkedList<GraphNode<D>> outPostOrderNodeReverseList) {
        // O(n + e)

        final boolean[] isDiscovered = new boolean[this.indexMap.size()];

        for (final GraphNode<D> node : orderedNodes) {
            if (isSkipScc) {
                if (this.dataScc.containsKey(node.getData())) {
                    continue;
                }
            }

            if (isDiscovered[this.indexMap.get(node.getData())]) {
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

        isDiscovered[this.indexMap.get(startNode.getData())] = true;

        for (final GraphEdge<D> edge : startNode.getEdges().values()) {
            if (isSkipScc) {
                if (this.dataScc.containsKey(edge.getTo().getData())) {
                    continue;
                }
            }

            if (isDiscovered[this.indexMap.get(edge.getTo().getData())]) {
                continue;
            }

            dfsPostOrderReverseRecursive(isSkipScc, edge.getTo().getData(), isTransposedGraph, isDiscovered, outPostOrderNodeReverseList);
        }

        outPostOrderNodeReverseList.addFirst(startNode);
    }

    public final void dfsGraph(final boolean isSkipScc,
                               final boolean isTransposedGraph,
                               final LinkedList<GraphNode<D>> outDfsList) {

        final HashMap<D, GraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;
        final boolean[] isDiscovered = new boolean[this.indexMap.size()];

        for (final GraphNode<D> node : graph.values()) {
            this.dfs(isSkipScc, node.getData(), isTransposedGraph, isDiscovered, outDfsList);
        }
    }

    public final void dfs(final boolean isSkipScc,
                          final D startData,
                          final boolean isTransposedGraph,
                          final LinkedList<GraphNode<D>> outDfsList) {
        final boolean[] isDiscovered = new boolean[this.indexMap.size()];
        this.dfs(isSkipScc, startData, isTransposedGraph, isDiscovered, outDfsList);
    }

    public final void dfs(final boolean isSkipScc,
                          final D startData,
                          final boolean isTransposedGraph,
                          final boolean[] isDiscovered,
                          final LinkedList<GraphNode<D>> outDfsList) {

        final HashMap<D, GraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;
        final GraphNode<D> startNode = graph.get(startData);

        final LinkedList<GraphNode<D>> dfsStack = new LinkedList<>();

        {
            if (isSkipScc) {
                if (this.dataScc.containsKey(startNode.getData())) {
                    return;
                }
            }

            if (isDiscovered[this.indexMap.get(startNode.getData())]) {
                return;
            }

            isDiscovered[this.indexMap.get(startNode.getData())] = true;
            dfsStack.addLast(startNode);
        }

        while (!dfsStack.isEmpty()) {
            final GraphNode<D> node = dfsStack.getLast();
            dfsStack.removeLast();

            outDfsList.addLast(node);

            for (final GraphEdge<D> nextEdge : node.getEdges().values()) {
                if (isSkipScc) {
                    if (this.dataScc.containsKey(nextEdge.getTo().getData())) {
                        continue;
                    }
                }

                if (isDiscovered[this.indexMap.get(nextEdge.getTo().getData())]) {
                    continue;
                }

                isDiscovered[this.indexMap.get(nextEdge.getTo().getData())] = true;
                dfsStack.addLast(nextEdge.getTo());
            }
        }
    }

    // tsp2Approximation
    public final ArrayList<GraphNode<D>> tsp2Approximation(final boolean isSkipScc,
                                                           final D startData) {
        assert (this.indexMap.containsKey(startData));

        // create mst graph
        final Graph<D> mstGraph;
        {
            // mst edge ??????
            // O(e)
            //
            // mst
            // O(e??????) + O(e * disjoint set ( = ??????????????? e ))
            // https://en.wikipedia.org/wiki/Disjoint-set_data_structure#Time_complexity
            final ArrayList<GraphEdge<D>> mst = this.kruskalMst();
            if (mst.isEmpty()) {
                final ArrayList<GraphNode<D>> outTspList = new ArrayList<>(1);
                final GraphNode<D> startNode = this.graph.get(startData);
                outTspList.add(startNode);
                return outTspList;
            }

            // mst ????????? ????????? ??????
            // O(n + n( = mst e)) (mst e == g n-1)
            final HashMap<D, ArrayList<D>> dataEdgeArrayMap = new HashMap<>(this.indexMap.size());
            final HashMap<D, ArrayList<Integer>> weightEdgeArrayMap = new HashMap<>(this.indexMap.size());

            for (final GraphEdge<D> edge : mst) {
                final GraphNode<D> from = edge.getFrom();
                final D fromPoint = from.getData();
                final GraphNode<D> to = edge.getTo();
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
                dataArray = new ArrayList<>(this.indexMap.size());

                for (final D data : this.indexMap.keySet()) {
                    dataArray.add(data);
                }
            }

            // mst ????????? ?????????
            // O(n + n( = mst e)) (mst e == g n-1)
            mstGraph = new Graph<>(false, dataArray, dataEdgeArrayMap, weightEdgeArrayMap);
        }// end create mst graph

        final ArrayList<GraphNode<D>> outMstDfsPreOrderAndAddReturnList;
        {
            // mst ????????? ????????? ????????? ?????? dfs
            // O(n + n( = mst e))
            final boolean[] isDiscovered = new boolean[this.indexMap.size()];
            outMstDfsPreOrderAndAddReturnList = new ArrayList<>(mstGraph.nodeCount() * 2);
            mstGraph.dfsPreOrderAndAddReturnRecursive(isSkipScc, startData, false, isDiscovered, outMstDfsPreOrderAndAddReturnList);
        }

        final ArrayList<GraphNode<D>> outTspList;
        {
            // ????????? ????????? ?????? ?????? ??????
            // O(n)
            outTspList = new ArrayList<>(mstGraph.nodeCount());

            final boolean[] isDiscovered = new boolean[this.indexMap.size()];

            for (final GraphNode<D> mstNode : outMstDfsPreOrderAndAddReturnList) {
                final D data = mstNode.getData();
                assert (this.graph.containsKey(data));

                final int iData = this.indexMap.get(data);
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

        isDiscovered[this.indexMap.get(startNode.getData())] = true;

        outDfsPreOrderAndAddReturnList.add(startNode);

        for (final GraphEdge<D> edge : startNode.getEdges().values()) {
            if (isSkipScc) {
                if (this.dataScc.containsKey(startNode.getData())) {
                    continue;
                }
            }

            if (isDiscovered[this.indexMap.get(edge.getTo().getData())]) {
                continue;
            }

            dfsPreOrderAndAddReturnRecursive(isSkipScc, edge.getTo().getData(), isTransposedGraph, isDiscovered, outDfsPreOrderAndAddReturnList);
            outDfsPreOrderAndAddReturnList.add(startNode);
        }
    }

    // mst kruskal
    public final ArrayList<GraphEdge<D>> kruskalMst() {
        ArrayList<GraphEdge<D>> mst = new ArrayList<>(this.graph.size());

        ArrayList<GraphNode<D>> nodes = new ArrayList<>(this.graph.size());
        ArrayList<GraphEdge<D>> edges = new ArrayList<>(this.graph.size() * this.graph.size());

        // mst edge ??????
        // O(e)
        for (final GraphNode<D> node : this.graph.values()) {
            nodes.add(node);
            for (final GraphEdge<D> edge : node.getEdges().values()) {
                edges.add(edge);
            }
        }

        // mst
        // O(e??????) + O(e * disjoint set ( = ??????????????? e ))
        // https://en.wikipedia.org/wiki/Disjoint-set_data_structure#Time_complexity
        DisjointSet<GraphNode<D>> set = new DisjointSet<>(nodes);
        Sort.radixSort(edges, GraphEdge::getWeight);

        for (final GraphEdge<D> edge : edges) {
            final GraphNode<D> n1 = edge.getFrom();
            final GraphNode<D> n2 = edge.getTo();

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
        dfsGraphPostOrderReverse(false, false, dfsPostOrderNodeReverseList);

        // get sortedList
        final LinkedList<GraphNode<D>> outSortedList = new LinkedList<>();
        dfsListPostOrderReverse(!includeScc, dfsPostOrderNodeReverseList, false, outSortedList);

        return outSortedList;
    }

    // scc Kosaraju
    public final void kosarajuScc(final LinkedList<GraphNode<D>> outScc) {
        // O(n + e) + O(n + e)

        final LinkedList<GraphNode<D>> dfsPostOrderNodeReverseList = new LinkedList<>();
        dfsGraphPostOrderReverse(false, false, dfsPostOrderNodeReverseList);

        // get scc groups with size > 1
        {
            final boolean[] transposedIsDiscovered = new boolean[this.indexMap.size()];
            final LinkedList<GraphNode<D>> scc = new LinkedList<>();

            for (final GraphNode<D> node : dfsPostOrderNodeReverseList) {
                final D data = node.getData();
                final GraphNode<D> transposedNode = this.transposedGraph.get(data);

                if (transposedIsDiscovered[this.indexMap.get(transposedNode.getData())]) {
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

        final HashMap<D, GraphNode<D>> mainGraph = mainIsTransposedGraph ? this.transposedGraph : this.graph;
        final HashMap<D, GraphNode<D>> transposedGraph = !mainIsTransposedGraph ? this.transposedGraph : this.graph;

        final HashMap<GraphEdge<D>, Integer> mainFlow = new HashMap<>(this.indexMap.size());
        {
            final boolean[] isDiscovered = new boolean[this.indexMap.size()];

            for (final GraphNode<D> startNode : mainGraph.values()) {
                final LinkedList<GraphNode<D>> bfsQueue = new LinkedList<>();

                {
                    if (isDiscovered[this.indexMap.get(startNode.getData())]) {
                        continue;
                    }

                    isDiscovered[this.indexMap.get(startNode.getData())] = true;
                    bfsQueue.addLast(startNode);
                }

                while (!bfsQueue.isEmpty()) {
                    final GraphNode<D> node = bfsQueue.poll();

                    for (final GraphEdge<D> nextEdge : node.getEdges().values()) {
                        mainFlow.put(nextEdge, 0);

                        if (isDiscovered[this.indexMap.get(nextEdge.getTo().getData())]) {
                            continue;
                        }

                        isDiscovered[this.indexMap.get(nextEdge.getTo().getData())] = true;
                        bfsQueue.addLast(nextEdge.getTo());
                    }
                }
            }
        }

        final HashMap<GraphEdge<D>, Integer> transposedFlow = new HashMap<>(this.indexMap.size());
        {
            final boolean[] isDiscovered = new boolean[this.indexMap.size()];

            for (final GraphNode<D> startNode : transposedGraph.values()) {
                final LinkedList<GraphNode<D>> bfsQueue = new LinkedList<>();

                {
                    if (isDiscovered[this.indexMap.get(startNode.getData())]) {
                        continue;
                    }

                    isDiscovered[this.indexMap.get(startNode.getData())] = true;
                    bfsQueue.addLast(startNode);
                }

                while (!bfsQueue.isEmpty()) {
                    final GraphNode<D> node = bfsQueue.poll();

                    for (final GraphEdge<D> nextEdge : node.getEdges().values()) {
                        transposedFlow.put(nextEdge, 0);

                        if (isDiscovered[this.indexMap.get(nextEdge.getTo().getData())]) {
                            continue;
                        }

                        isDiscovered[this.indexMap.get(nextEdge.getTo().getData())] = true;
                        bfsQueue.addLast(nextEdge.getTo());
                    }
                }
            }
        }

        final LinkedList<IsTransposedEdge<D>> bfsEdgeQueue = new LinkedList<>();
        final HashMap<IsTransposedEdge<D>, IsTransposedEdge<D>> preEdgeMap = new HashMap<>();

        while (true) {
            final boolean[] isDiscovered = new boolean[this.indexMap.size()];
            bfsEdgeQueue.clear();
            preEdgeMap.clear();

            {
                isDiscovered[this.indexMap.get(source)] = true;
                bfsEdgeQueue.addLast(new IsTransposedEdge<>(false, new GraphEdge<>(0, null, mainGraph.get(source))));
            }

            IsTransposedEdge<D> lastEdge = null;
            // bfs
            while (!bfsEdgeQueue.isEmpty()) {
                final IsTransposedEdge<D> nowIsTransposedFlow = bfsEdgeQueue.poll();
                final D nodeData = nowIsTransposedFlow.getEdge().getTo().getData();

                if (nodeData.equals(sink)) {
                    lastEdge = nowIsTransposedFlow;
                    break;
                }

                final GraphNode<D> transposedNode = transposedGraph.get(nodeData);
                for (final GraphEdge<D> nextTransposedEdge : transposedNode.getEdges().values()) {
                    final GraphNode<D> nextTransposedNode = nextTransposedEdge.getTo();
                    final D nextTransposedData = nextTransposedNode.getData();
                    final int iNextTransposedData = this.indexMap.get(nextTransposedData);

                    assert (!nextTransposedData.equals(nodeData));

                    if (isSkipScc) {
                        if (this.dataScc.containsKey(nextTransposedNode.getData())) {
                            continue;
                        }
                    }

                    final int edgeTransposedFlow = transposedFlow.get(nextTransposedEdge);
                    final int edgeTransposedRemain = BACK_FLOW_CAPACITY - edgeTransposedFlow;

                    assert (edgeTransposedFlow <= 0);
                    assert (edgeTransposedRemain >= 0);

                    if (edgeTransposedRemain <= 0) {
                        continue;
                    }

                    if (isDiscovered[iNextTransposedData]) {
                        continue;
                    }

                    isDiscovered[this.indexMap.get(nodeData)] = true;

                    final IsTransposedEdge<D> nextIsTransposedFlow = new IsTransposedEdge<>(true, nextTransposedEdge);
                    bfsEdgeQueue.addLast(nextIsTransposedFlow);
                    preEdgeMap.put(nextIsTransposedFlow, nowIsTransposedFlow);
                }

                final GraphNode<D> node = mainGraph.get(nodeData);
                for (final GraphEdge<D> nextEdge : node.getEdges().values()) {
                    final GraphNode<D> nextNode = nextEdge.getTo();
                    final D nextData = nextNode.getData();
                    final int iNextData = this.indexMap.get(nextData);

                    assert (!nextData.equals(nodeData));

                    if (isSkipScc) {
                        if (this.dataScc.containsKey(nextData)) {
                            continue;
                        }
                    }

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

                    final IsTransposedEdge<D> nextIsTransposedFlow = new IsTransposedEdge<>(false, nextEdge);
                    bfsEdgeQueue.addLast(nextIsTransposedFlow);
                    preEdgeMap.put(nextIsTransposedFlow, nowIsTransposedFlow);
                }
            } // end bfs

            if (lastEdge == null) {
                break;
            }

            int minRemainCapacity = Integer.MAX_VALUE;


            for (IsTransposedEdge<D> isTransposedEdge = lastEdge; isTransposedEdge.getEdge().getFrom() != null; isTransposedEdge = preEdgeMap.get(isTransposedEdge)) {
                final GraphEdge<D> edge = isTransposedEdge.getEdge();

                if (isTransposedEdge.isTransposedEdge()) {
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
                }
            }

            for (IsTransposedEdge<D> isTransposedFlow = lastEdge; isTransposedFlow.getEdge().getFrom() != null; isTransposedFlow = preEdgeMap.get(isTransposedFlow)) {
                final GraphEdge<D> edge = isTransposedFlow.getEdge();

                if (isTransposedFlow.isTransposedEdge()) {
                    transposedFlow.put(edge, transposedFlow.get(edge) + minRemainCapacity);
                    final GraphEdge<D> mainEdge = mainGraph.get(edge.getTo().getData()).getEdges().get(edge.getFrom().getData());
                    mainFlow.put(mainEdge, mainFlow.get(mainEdge) - minRemainCapacity);
                } else {
                    mainFlow.put(edge, mainFlow.get(edge) + minRemainCapacity);
                    final GraphEdge<D> transposedEdge = transposedGraph.get(edge.getTo().getData()).getEdges().get(edge.getFrom().getData());
                    transposedFlow.put(transposedEdge, transposedFlow.get(transposedEdge) - minRemainCapacity);
                }
            }

            outTotalFlow += minRemainCapacity;
        }

        return outTotalFlow;
    }

    // max flow
    public final int maxFlowAndNodeCapacity(final boolean isSkipScc,
                                            final D source,
                                            final D sink,
                                            final boolean mainIsTransposedGraph,
                                            final Function<D, Integer> getNodeCapacity) {

        if (isSkipScc) {
            assert (!this.dataScc.containsKey(source));
            assert (!this.dataScc.containsKey(sink));
        }

        int outTotalFlow = 0;

        final int BACK_FLOW_CAPACITY = 0;

        final HashMap<D, GraphNode<D>> mainGraph = mainIsTransposedGraph ? this.transposedGraph : this.graph;
        final HashMap<D, GraphNode<D>> transposedGraph = !mainIsTransposedGraph ? this.transposedGraph : this.graph;

        final HashMap<GraphEdge<D>, Integer> mainFlow = new HashMap<>(this.indexMap.size());
        {
            final boolean[] isDiscovered = new boolean[this.indexMap.size()];

            for (final GraphNode<D> startNode : mainGraph.values()) {
                final LinkedList<GraphNode<D>> bfsQueue = new LinkedList<>();

                {
                    if (isDiscovered[this.indexMap.get(startNode.getData())]) {
                        continue;
                    }

                    isDiscovered[this.indexMap.get(startNode.getData())] = true;
                    bfsQueue.addLast(startNode);
                }

                while (!bfsQueue.isEmpty()) {
                    final GraphNode<D> node = bfsQueue.poll();

                    for (final GraphEdge<D> nextEdge : node.getEdges().values()) {
                        mainFlow.put(nextEdge, 0);

                        if (isDiscovered[this.indexMap.get(nextEdge.getTo().getData())]) {
                            continue;
                        }

                        isDiscovered[this.indexMap.get(nextEdge.getTo().getData())] = true;
                        bfsQueue.addLast(nextEdge.getTo());
                    }
                }
            }
        }

        final HashMap<GraphEdge<D>, Integer> transposedFlow = new HashMap<>(this.indexMap.size());
        {
            final boolean[] isDiscovered = new boolean[this.indexMap.size()];

            for (final GraphNode<D> startNode : transposedGraph.values()) {
                final LinkedList<GraphNode<D>> bfsQueue = new LinkedList<>();

                {
                    if (isDiscovered[this.indexMap.get(startNode.getData())]) {
                        continue;
                    }

                    isDiscovered[this.indexMap.get(startNode.getData())] = true;
                    bfsQueue.addLast(startNode);
                }

                while (!bfsQueue.isEmpty()) {
                    final GraphNode<D> node = bfsQueue.poll();

                    for (final GraphEdge<D> nextEdge : node.getEdges().values()) {
                        transposedFlow.put(nextEdge, 0);

                        if (isDiscovered[this.indexMap.get(nextEdge.getTo().getData())]) {
                            continue;
                        }

                        isDiscovered[this.indexMap.get(nextEdge.getTo().getData())] = true;
                        bfsQueue.addLast(nextEdge.getTo());
                    }
                }
            }
        }

        final int[] nodeFlowArray = new int[this.indexMap.size()];
        final int[] nodeCapacityArray = new int[this.indexMap.size()];
        {
            for (final GraphNode<D> from : mainGraph.values()) {
                final D fromData = from.getData();
                final int iFrom = this.indexMap.get(fromData);
                nodeCapacityArray[iFrom] = getNodeCapacity.apply(fromData);
            }
        }

        final LinkedList<IsTransposedEdge<D>> bfsEdgeQueue = new LinkedList<>();
        final HashMap<IsTransposedEdge<D>, IsTransposedEdge<D>> preEdgeMap = new HashMap<>();

        while (true) {
            final boolean[] isDiscovered = new boolean[this.indexMap.size()];
            bfsEdgeQueue.clear();
            preEdgeMap.clear();

            {
                isDiscovered[this.indexMap.get(source)] = true;
                bfsEdgeQueue.addLast(new IsTransposedEdge<>(false, new GraphEdge<>(0, null, mainGraph.get(source))));
            }

            IsTransposedEdge<D> lastEdge = null;
            // bfs
            while (!bfsEdgeQueue.isEmpty()) {
                final IsTransposedEdge<D> nowIsTransposedFlow = bfsEdgeQueue.poll();
                final D nodeData = nowIsTransposedFlow.getEdge().getTo().getData();
                final int iNodeData = this.indexMap.get(nodeData);

                if (nodeData.equals(sink)) {
                    lastEdge = nowIsTransposedFlow;
                    break;
                }

                final GraphNode<D> transposedNode = transposedGraph.get(nodeData);
                for (final GraphEdge<D> nextTransposedEdge : transposedNode.getEdges().values()) {
                    final GraphNode<D> nextTransposedNode = nextTransposedEdge.getTo();
                    final D nextTransposedData = nextTransposedNode.getData();

                    assert (!nextTransposedData.equals(nodeData));

                    final int edgeTransposedFlow = transposedFlow.get(nextTransposedEdge);
                    final int edgeTransposedRemain = BACK_FLOW_CAPACITY - edgeTransposedFlow;

                    assert (edgeTransposedFlow <= 0);
                    assert (edgeTransposedRemain >= 0);

                    if (edgeTransposedRemain <= 0) {
                        continue;
                    }

                    final IsTransposedEdge<D> nextIsTransposedFlow = new IsTransposedEdge<>(true, nextTransposedEdge);
                    bfsEdgeQueue.addLast(nextIsTransposedFlow);
                    preEdgeMap.put(nextIsTransposedFlow, nowIsTransposedFlow);
                }

                // check nodeFlow
                final GraphNode<D> node = mainGraph.get(nodeData);
                {
                    final int nodeFlow = nodeFlowArray[iNodeData];
                    final int nodeCap = nodeCapacityArray[iNodeData];
                    final int nodeRemain = nodeCap - nodeFlow;

                    assert (nodeFlow >= 0);
                    assert (nodeRemain >= 0);

                    if (nodeRemain <= 0 && !nowIsTransposedFlow.isTransposedEdge()) {
                        continue;
                    }
                }
                for (final GraphEdge<D> nextEdge : node.getEdges().values()) {
                    final GraphNode<D> nextNode = nextEdge.getTo();
                    final D nextData = nextNode.getData();
                    final int iNextData = this.indexMap.get(nextData);

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

                    final IsTransposedEdge<D> nextIsTransposedFlow = new IsTransposedEdge<>(false, nextEdge);
                    bfsEdgeQueue.addLast(nextIsTransposedFlow);
                    preEdgeMap.put(nextIsTransposedFlow, nowIsTransposedFlow);
                }
            } // end bfs

            if (lastEdge == null) {
                break;
            }

            int minRemainCapacity;
            // check sink nodeFlow
            {
                assert (!lastEdge.isTransposedEdge());
                assert (sink.equals(lastEdge.getEdge().getTo().getData()));

                final int iSink = this.indexMap.get(sink);

                final int sinkFlow = nodeFlowArray[iSink];
                final int sinkCap = nodeCapacityArray[iSink];
                final int sinkRemain = sinkCap - sinkFlow;

                assert (sinkFlow >= 0);
                assert (sinkRemain >= 0);

                if (sinkRemain <= 0) {
                    return outTotalFlow;
                }

                minRemainCapacity = sinkRemain;
            }

            for (IsTransposedEdge<D> isTransposedEdge = lastEdge; isTransposedEdge.getEdge().getFrom() != null; isTransposedEdge = preEdgeMap.get(isTransposedEdge)) {
                final GraphEdge<D> edge = isTransposedEdge.getEdge();

                final GraphNode<D> from = edge.getFrom();
                final D fromData = from.getData();
                final int iFromData = this.indexMap.get(fromData);

                if (isTransposedEdge.isTransposedEdge()) {
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

            for (IsTransposedEdge<D> isTransposedFlow = lastEdge; isTransposedFlow.getEdge().getFrom() != null; isTransposedFlow = preEdgeMap.get(isTransposedFlow)) {
                final GraphEdge<D> edge = isTransposedFlow.getEdge();

                final GraphNode<D> from = edge.getFrom();
                final D fromData = from.getData();
                final int iFromData = this.indexMap.get(fromData);

                if (isTransposedFlow.isTransposedEdge()) {
                    transposedFlow.put(edge, transposedFlow.get(edge) + minRemainCapacity);
                    final GraphEdge<D> mainEdge = mainGraph.get(edge.getTo().getData()).getEdges().get(edge.getFrom().getData());
                    mainFlow.put(mainEdge, mainFlow.get(mainEdge) - minRemainCapacity);
                } else {
                    mainFlow.put(edge, mainFlow.get(edge) + minRemainCapacity);
                    final GraphEdge<D> transposedEdge = transposedGraph.get(edge.getTo().getData()).getEdges().get(edge.getFrom().getData());
                    transposedFlow.put(transposedEdge, transposedFlow.get(transposedEdge) - minRemainCapacity);
                }

                if (!isTransposedFlow.isTransposedEdge()) {
                    nodeFlowArray[iFromData] += minRemainCapacity;
                    nodeFlowArray[iFromData] = Math.min(nodeCapacityArray[iFromData], nodeFlowArray[iFromData]);
                }
            }

            {
                assert (sink.equals(lastEdge.getEdge().getTo().getData()));

                final int iSink = this.indexMap.get(sink);
                nodeFlowArray[iSink] += minRemainCapacity;
                nodeFlowArray[iSink] = Math.min(nodeCapacityArray[iSink], nodeFlowArray[iSink]);
            }

            outTotalFlow += minRemainCapacity;
        }

        return outTotalFlow;
    }

    // dijkstra - single source shortest path
    public HashMap<GraphNode<D>, Integer> dijkstra(final boolean isSkipScc,
                                                   final D startData,
                                                   final boolean isTransposedGraph,
                                                   final HashMap<GraphNode<D>, GraphNode<D>> outPrevMap) {
        // O((N + E) * logN)

        final HashMap<D, GraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;
        final GraphNode<D> startNode = graph.get(startData);

        final HashMap<GraphNode<D>, Integer> minDistMap = new HashMap<>(this.indexMap.size());
        final int INF = Integer.MAX_VALUE;
        for (final GraphNode<D> node : graph.values()) {
            minDistMap.put(node, INF);
        }

        final PriorityQueue<WeightNode<GraphNode<D>>> open = new PriorityQueue<>();
        {
            minDistMap.put(startNode, 0);
            outPrevMap.put(startNode, null);

            WeightNode<GraphNode<D>> weightNode = new WeightNode<>(0, startNode);
            open.add(weightNode);
        }

        while (!open.isEmpty()) {
            final WeightNode<GraphNode<D>> weightNode = open.poll();
            final GraphNode<D> node = weightNode.getNode();

            final int minDist = minDistMap.get(node);
            final int dist = weightNode.getWeight();

            if (minDist < dist) {
                continue;
            }

            for (final GraphEdge<D> edge : node.getEdges().values()) {
                final GraphNode<D> next = edge.getTo();
                final D nextData = next.getData();

                if (isSkipScc) {
                    if (this.dataScc.containsKey(nextData)) {
                        continue;
                    }
                }

                final int weight = edge.getWeight();
                final int newDist = minDist + weight;

                final int nextMinDist = minDistMap.get(next);

                if (newDist >= nextMinDist) {
                    continue;
                }

                minDistMap.put(next, newDist);
                outPrevMap.put(next, node);

                WeightNode<GraphNode<D>> newCandidate = new WeightNode<>(newDist, next);
                open.add(newCandidate);
            }
        }

        return minDistMap;
    }

    // dijkstra - single source longest path
    public HashMap<GraphNode<D>, Integer> dijkstraMaxPath(final boolean isSkipScc,
                                                          final D startData,
                                                          final boolean isTransposedGraph,
                                                          final HashMap<GraphNode<D>, GraphNode<D>> outPrevMap) {
        // O((N + E) * logN)

        final HashMap<D, GraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;
        final GraphNode<D> startNode = graph.get(startData);

        final HashMap<GraphNode<D>, Integer> maxDistMap = new HashMap<>(this.indexMap.size());
        final int MINUS_ONE = -1;
        for (final GraphNode<D> node : graph.values()) {
            maxDistMap.put(node, MINUS_ONE);
        }

        final PriorityQueue<WeightNode<GraphNode<D>>> open = new PriorityQueue<>(Comparator.reverseOrder());
        {
            maxDistMap.put(startNode, 0);
            outPrevMap.put(startNode, null);

            WeightNode<GraphNode<D>> weightNode = new WeightNode<>(0, startNode);
            open.add(weightNode);
        }

        while (!open.isEmpty()) {
            final WeightNode<GraphNode<D>> weightNode = open.poll();
            final GraphNode<D> node = weightNode.getNode();

            final int maxDist = maxDistMap.get(node);
            final int dist = weightNode.getWeight();

            if (maxDist > dist) {
                continue;
            }

            for (final GraphEdge<D> edge : node.getEdges().values()) {
                final GraphNode<D> next = edge.getTo();
                final D nextData = next.getData();

                if (isSkipScc) {
                    if (this.dataScc.containsKey(nextData)) {
                        continue;
                    }
                }

                final int weight = edge.getWeight();
                final int newDist = maxDist + weight;

                final int nextMaxDist = maxDistMap.get(next);

                if (newDist <= nextMaxDist) {
                    continue;
                }

                maxDistMap.put(next, newDist);
                outPrevMap.put(next, node);

                WeightNode<GraphNode<D>> newCandidate = new WeightNode<>(newDist, next);
                open.add(newCandidate);
            }
        }

        return maxDistMap;
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
}