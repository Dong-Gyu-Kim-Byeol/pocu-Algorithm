package academy.pocu.comp3500.assignment4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Graph<D> {
    private final HashMap<D, Integer> indexMap;

    private final HashMap<D, GraphNode<D>> graph;
    private int[][] graphEdgeWeight;

    private HashMap<D, GraphNode<D>> transposedGraph;
    private int[][] transposedGraphEdgeWeight;

    private HashMap<D, Boolean> dataScc;

    // ---

    public Graph(final boolean useTransposedGraph,
                 final ArrayList<D> nodeDataArray,
                 final HashMap<D, ArrayList<D>> edgeDataArrayMap,
                 final HashMap<D, ArrayList<Integer>> edgeWeightArrayMap) {
        {
            this.indexMap = new HashMap<>(nodeDataArray.size());
            int i = 0;
            for (final D data : nodeDataArray) {
                this.indexMap.put(data, i++);
            }
        }

        this.graphEdgeWeight = new int[nodeDataArray.size()][nodeDataArray.size()];
        this.graph = createGraph(nodeDataArray, edgeDataArrayMap, edgeWeightArrayMap);

        if (useTransposedGraph) {
            this.transposedGraphEdgeWeight = new int[nodeDataArray.size()][nodeDataArray.size()];
            this.transposedGraph = createTransposedGraph(nodeDataArray, edgeDataArrayMap);
        }

        assert (this.graph.size() == nodeDataArray.size());
        assert !useTransposedGraph || (this.transposedGraph.size() == nodeDataArray.size());

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

    public final void addNode(final D newData,
                              final ArrayList<D> edgeDataArray,
                              final ArrayList<Integer> edgeWeightArray,
                              final ArrayList<Integer> transposedEdgeWeightArray) {

        assert (edgeDataArray.size() == transposedEdgeWeightArray.size());
        assert (edgeDataArray.size() == edgeWeightArray.size());

        assert (!this.indexMap.containsKey(newData));
        this.indexMap.put(newData, this.indexMap.size());

        // increase edge size
        {
            {
                final int[][] edgeWeight = new int[this.indexMap.size()][this.indexMap.size()];
                copyEdgeWeight(this.graphEdgeWeight, edgeWeight);
                this.graphEdgeWeight = edgeWeight;
            }

            {
                final int[][] edgeWeight = new int[this.indexMap.size()][this.indexMap.size()];
                copyEdgeWeight(this.transposedGraphEdgeWeight, edgeWeight);
                this.transposedGraphEdgeWeight = edgeWeight;
            }
        }

        // addNodeInGraph
        {
            final GraphNode<D> newFrom = new GraphNode<>(newData);
            this.graph.put(newData, newFrom);

            final int iFrom = this.indexMap.get(newData);

            for (int i = 0; i < edgeDataArray.size(); ++i) {
                final D toData = edgeDataArray.get(i);
                assert (this.graph.containsKey(toData));

                final GraphNode<D> to = this.graph.get(toData);
                final int iTo = this.indexMap.get(toData);

                newFrom.addNode(to);
                this.graphEdgeWeight[iFrom][iTo] = edgeWeightArray.get(i);
            }
        }

        // addNodeInTransposedGraph
        if (this.transposedGraph != null) {
            final GraphNode<D> newToTransposed = new GraphNode<>(newData);
            this.transposedGraph.put(newData, newToTransposed);

            final int iTo = this.indexMap.get(newData);

            for (final D fromData : edgeDataArray) {
                assert (this.transposedGraph.containsKey(fromData));

                final GraphNode<D> fromTransposed = this.transposedGraph.get(fromData);
                final int iFrom = this.indexMap.get(fromData);

                fromTransposed.addNode(newToTransposed);
                this.transposedGraphEdgeWeight[iFrom][iTo] = this.graphEdgeWeight[iTo][iFrom];
            }
        }
    }

    public final void removeNode(final D removeData) {
        // removeNodeInTransposedGraph
        if (this.transposedGraph != null) {
            assert (this.transposedGraph.containsKey(removeData));
            final GraphNode<D> removeTransposed = this.transposedGraph.get(removeData);
            final int iTo = this.indexMap.get(removeData);

            final GraphNode<D> removeOrigin = this.graph.get(removeData);

            for (final GraphNode<D> fromOriginNode : removeOrigin.getNodes()) {
                final D fromData = fromOriginNode.getData();
                assert (this.transposedGraph.containsKey(fromData));

                final GraphNode<D> fromTransposedNode = this.transposedGraph.get(fromData);
                final int iFrom = this.indexMap.get(fromData);

                fromTransposedNode.removeNode(removeTransposed);
                this.transposedGraphEdgeWeight[iFrom][iTo] = 0;
            }
        }

        // removeNodeInGraph
        {
            assert (this.graph.containsKey(removeData));
            final GraphNode<D> removeFromNode = this.graph.get(removeData);
            final int iFrom = this.indexMap.get(removeData);

            for (final GraphNode<D> to : removeFromNode.getNodes()) {
                final D toData = to.getData();
                final int iTo = this.indexMap.get(toData);

                this.graphEdgeWeight[iFrom][iTo] = 0;
            }
        }


        assert (this.indexMap.containsKey(removeData));
        this.indexMap.remove(removeData);

        this.graph.remove(removeData);
        if (this.transposedGraph != null) {
            this.transposedGraph.remove(removeData);
        }
    }

    public final void addTransposedNode(final D newTransposedData,
                                        final ArrayList<D> transposedEdgeDataArray,
                                        final ArrayList<Integer> transposedEdgeWeightArray,
                                        final ArrayList<Integer> edgeWeightArray) {

        assert (this.transposedGraph != null);

        assert (transposedEdgeDataArray.size() == transposedEdgeWeightArray.size());
        assert (transposedEdgeDataArray.size() == edgeWeightArray.size());

        assert (!this.indexMap.containsKey(newTransposedData));
        this.indexMap.put(newTransposedData, this.indexMap.size());

        // increase edge size
        {
            {
                final int[][] edgeWeight = new int[this.indexMap.size()][this.indexMap.size()];
                copyEdgeWeight(this.graphEdgeWeight, edgeWeight);
                this.graphEdgeWeight = edgeWeight;
            }

            {
                final int[][] edgeWeight = new int[this.indexMap.size()][this.indexMap.size()];
                copyEdgeWeight(this.transposedGraphEdgeWeight, edgeWeight);
                this.transposedGraphEdgeWeight = edgeWeight;
            }
        }

        // addTransposedNodeInTransposedGraph
        {
            final GraphNode<D> newFromTransposed = new GraphNode<>(newTransposedData);
            this.transposedGraph.put(newTransposedData, newFromTransposed);

            final int iFrom = this.indexMap.get(newTransposedData);

            for (int i = 0; i < transposedEdgeDataArray.size(); ++i) {
                final D toData = transposedEdgeDataArray.get(i);
                assert (this.transposedGraph.containsKey(toData));

                final GraphNode<D> toTransposed = this.transposedGraph.get(toData);
                final int iTo = this.indexMap.get(toData);

                newFromTransposed.addNode(toTransposed);
                this.transposedGraphEdgeWeight[iFrom][iTo] = transposedEdgeWeightArray.get(i);
            }
        }

        // addTransposedNodeInGraph
        {
            final GraphNode<D> newTo = new GraphNode<>(newTransposedData);
            this.graph.put(newTransposedData, newTo);

            final int iTo = this.indexMap.get(newTransposedData);

            for (final D fromData : transposedEdgeDataArray) {
                assert (this.graph.containsKey(fromData));

                final GraphNode<D> from = this.graph.get(fromData);
                final int iFrom = this.indexMap.get(fromData);

                from.addNode(newTo);
                this.graphEdgeWeight[iFrom][iTo] = this.transposedGraphEdgeWeight[iTo][iFrom];
            }
        }
    }

    public final void removeTransposedNode(final D removeTransposedData) {

        assert (this.transposedGraph != null);

        // removeTransposedNodeInGraph
        {
            assert (this.graph.containsKey(removeTransposedData));
            final GraphNode<D> removeTo = this.graph.get(removeTransposedData);
            final int iTo = this.indexMap.get(removeTransposedData);

            final GraphNode<D> removeTransposed = this.transposedGraph.get(removeTransposedData);

            for (final GraphNode<D> fromTransposed : removeTransposed.getNodes()) {
                final D fromData = fromTransposed.getData();
                assert (this.graph.containsKey(fromData));

                final GraphNode<D> from = this.graph.get(fromData);
                final int iFrom = this.indexMap.get(fromData);

                from.removeNode(removeTo);
                this.graphEdgeWeight[iFrom][iTo] = 0;
            }
        }

        // removeTransposedNodeInTransposedGraph
        {
            assert (this.transposedGraph.containsKey(removeTransposedData));
            final GraphNode<D> removeFromTransposed = this.transposedGraph.get(removeTransposedData);
            final int iFrom = this.indexMap.get(removeTransposedData);

            for (final GraphNode<D> toTransposed : removeFromTransposed.getNodes()) {
                final D toData = toTransposed.getData();
                final int iTo = this.indexMap.get(toData);

                this.transposedGraphEdgeWeight[iFrom][iTo] = 0;
            }
        }


        this.graph.remove(removeTransposedData);
        this.transposedGraph.remove(removeTransposedData);

        assert (this.indexMap.containsKey(removeTransposedData));
        this.indexMap.remove(removeTransposedData);
    }

    // bfs
    public final void bfs(final boolean isSkipScc,
                          final D startData,
                          final boolean isTransposedGraph,
                          final LinkedList<GraphNode<D>> outBfsList) {

        final HashMap<D, GraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;
        final GraphNode<D> startNode = graph.get(startData);

        final boolean[] isDiscovered = new boolean[this.indexMap.size()];

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

            for (final GraphNode<D> next : node.getNodes()) {
                if (isSkipScc) {
                    if (this.dataScc.containsKey(next.getData())) {
                        continue;
                    }
                }

                if (isDiscovered[this.indexMap.get(next.getData())]) {
                    continue;
                }

                isDiscovered[this.indexMap.get(next.getData())] = true;
                bfsQueue.addLast(next);
            }
        }
    }

    // dfs
    public final void dfsPostOrderReverse(final boolean isSkipScc,
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

    public final void dfsPostOrderReverse(final boolean isSkipScc,
                                          final LinkedList<GraphNode<D>> orderedNodes,
                                          final boolean isTransposedGraph,
                                          final LinkedList<GraphNode<D>> outPostOrderNodeReverseList) {
        // O(n + e)

        final HashMap<D, GraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;

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

        for (final GraphNode<D> next : startNode.getNodes()) {
            final D nextData = next.getData();

            if (isSkipScc) {
                if (this.dataScc.containsKey(nextData)) {
                    continue;
                }
            }

            if (isDiscovered[this.indexMap.get(nextData)]) {
                continue;
            }

            dfsPostOrderReverseRecursive(isSkipScc, nextData, isTransposedGraph, isDiscovered, outPostOrderNodeReverseList);
        }

        outPostOrderNodeReverseList.addFirst(startNode);
    }

    public final void dfs(final boolean isSkipScc,
                          final D startData,
                          final boolean isTransposedGraph,
                          final LinkedList<GraphNode<D>> outDfsList) {

        final HashMap<D, GraphNode<D>> graph = isTransposedGraph ? this.transposedGraph : this.graph;
        final GraphNode<D> startNode = graph.get(startData);

        final boolean[] isDiscovered = new boolean[this.indexMap.size()];

        final LinkedList<GraphNode<D>> bfsStack = new LinkedList<>();

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
            bfsStack.addLast(startNode);
        }

        while (!bfsStack.isEmpty()) {
            final GraphNode<D> node = bfsStack.getLast();
            bfsStack.removeLast();

            outDfsList.addLast(node);

            for (final GraphNode<D> next : node.getNodes()) {
                final D nextData = next.getData();

                if (isSkipScc) {
                    if (this.dataScc.containsKey(nextData)) {
                        continue;
                    }
                }

                if (isDiscovered[this.indexMap.get(nextData)]) {
                    continue;
                }

                isDiscovered[this.indexMap.get(nextData)] = true;
                bfsStack.addLast(next);
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
            final ArrayList<GraphEdge<D>> mst = this.kruskalMst();
            if (mst.isEmpty()) {
                final ArrayList<GraphNode<D>> outTspList = new ArrayList<>(1);
                final GraphNode<D> startNode = this.graph.get(startData);
                outTspList.add(startNode);
                return outTspList;
            }

            final HashMap<D, ArrayList<D>> dataEdgeArrayMap = new HashMap<>(this.indexMap.size());
            final HashMap<D, ArrayList<Integer>> weightEdgeArrayMap = new HashMap<>(this.indexMap.size());

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
                dataArray = new ArrayList<>(this.indexMap.size());

                for (final D data : this.indexMap.keySet()) {
                    dataArray.add(data);
                }
            }

            mstGraph = new Graph<>(false, dataArray, dataEdgeArrayMap, weightEdgeArrayMap);
        }// end create mst graph

        final ArrayList<GraphNode<D>> outMstDfsPreOrderAndAddReturnList;
        {
            final boolean[] isDiscovered = new boolean[this.indexMap.size()];
            outMstDfsPreOrderAndAddReturnList = new ArrayList<>(mstGraph.nodeCount() * 2);
            mstGraph.dfsPreOrderAndAddReturnRecursive(isSkipScc, startData, false, isDiscovered, outMstDfsPreOrderAndAddReturnList);
        }

        final ArrayList<GraphNode<D>> outTspList;
        {
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

        for (final GraphNode<D> next : startNode.getNodes()) {
            final D nextData = next.getData();

            if (isSkipScc) {
                if (this.dataScc.containsKey(nextData)) {
                    continue;
                }
            }

            if (isDiscovered[this.indexMap.get(nextData)]) {
                continue;
            }

            dfsPreOrderAndAddReturnRecursive(isSkipScc, nextData, isTransposedGraph, isDiscovered, outDfsPreOrderAndAddReturnList);
            outDfsPreOrderAndAddReturnList.add(startNode);
        }
    }

    public ArrayList<GraphEdge<D>> kruskalMst() {
        ArrayList<GraphEdge<D>> mst = new ArrayList<>(this.graph.size());

        ArrayList<GraphNode<D>> nodes = new ArrayList<>(this.graph.size());
        ArrayList<GraphEdge<D>> edges = new ArrayList<>(this.graph.size() * this.graph.size());

        for (final GraphNode<D> from : this.graph.values()) {
            final D fromData = from.getData();
            final int iFrom = this.indexMap.get(fromData);

            nodes.add(from);
            for (final GraphNode<D> to : from.getNodes()) {
                final D toData = to.getData();
                final int iTo = this.indexMap.get(toData);

                edges.add(new GraphEdge<>(this.graphEdgeWeight[iFrom][iTo], from, to));
            }
        }

        DisjointSet<GraphNode<D>> set = new DisjointSet<>(nodes);
        Sort.radixSort(edges, GraphEdge::getWeight);

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
            final int iFrom = this.indexMap.get(fromData);

            final ArrayList<D> toDataEdgeArray = dataEdgeArrayMap.get(fromData);
            final ArrayList<Integer> toWeightEdgeArray = weightEdgeArrayMap.get(fromData);

            assert (toDataEdgeArray.size() == toWeightEdgeArray.size());

            for (int i = 0; i < toDataEdgeArray.size(); ++i) {
                final D toData = toDataEdgeArray.get(i);
                assert (outGraph.containsKey(toData));

                final int iTo = this.indexMap.get(toData);
                final GraphNode<D> to = outGraph.get(toData);

                from.addNode(to);
                this.graphEdgeWeight[iFrom][iTo] = toWeightEdgeArray.get(i);
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
            final GraphNode<D> to = outTransposedGraph.get(toData);
            final int iTo = this.indexMap.get(toData);

            final ArrayList<D> fromDataEdgeArray = dataEdgeArrayMap.get(toData);

            for (final D fromData : fromDataEdgeArray) {
                assert (outTransposedGraph.containsKey(fromData));

                final GraphNode<D> from = outTransposedGraph.get(fromData);
                final int iFrom = this.indexMap.get(fromData);

                from.addNode(to);
                this.transposedGraphEdgeWeight[iFrom][iTo] = this.graphEdgeWeight[iTo][iFrom];
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
        final HashMap<D, GraphNode<D>> mainGraph = mainIsTransposedGraph ? this.transposedGraph : this.graph;
        final int[][] capacity = mainIsTransposedGraph ? this.transposedGraphEdgeWeight : this.graphEdgeWeight;

        final int[][] flow = new int[this.indexMap.size()][this.indexMap.size()];
        final LinkedList<Integer> bfsQueue = new LinkedList<>();
        final int iSink = this.indexMap.get(sink);

        while (true) {
            {
                final int[] parent = new int[this.indexMap.size()];
                for (int i = 0; i < parent.length; ++i) {
                    parent[i] = -1;
                }
                bfsQueue.clear();

                final int iSource = this.indexMap.get(source);
                bfsQueue.addLast(iSource);

                // bfs
                while (!bfsQueue.isEmpty() && parent[iSink] == -1) {
                    final int here = bfsQueue.poll();
                    for (int there = 0; there < this.indexMap.size(); ++there)
                        if (capacity[here][there] - flow[here][there] > 0 && parent[there] == -1) {
                            bfsQueue.addLast(there);
                            parent[there] = here;
                        }
                } // end bfs

                if (parent[iSink] == -1) {
                    // 증가경로가 없으면 종료
                    break;
                }

                int amount = Integer.MAX_VALUE;
                for (int p = iSink; p != iSource; p = parent[p]) {
                    // 병목간선을 찾음
                    amount = Math.min(capacity[parent[p]][p] - flow[parent[p]][p], amount);
                }

                for (int p = iSink; p != iSource; p = parent[p]) {
                    flow[parent[p]][p] += amount;
                    flow[p][parent[p]] -= amount;
                }

                outTotalFlow += amount;
            }
        }

        return outTotalFlow;
    }

    private void copyEdgeWeight(final int[][] source, final int[][] dest) {
        assert (source.length <= dest.length);
        assert (source[0].length <= dest[0].length);

        for (int iFrom = 0; iFrom < source.length; ++iFrom) {
            for (int iTo = 0; iTo < source[0].length; ++iTo) {
                dest[iFrom][iTo] = source[iFrom][iTo];
            }
        }
    }
}
