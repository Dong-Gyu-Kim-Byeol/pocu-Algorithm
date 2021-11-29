package academy.pocu.comp3500.lab11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public final class DirectedGraph<D> {
    private final HashMap<D, Integer> dataIndex;
    private final HashMap<D, Boolean> dataScc;

    private final HashMap<D, DirectedGraphNode<D>> graph;
    private final HashMap<D, DirectedGraphNode<D>> transposedGraph;

    // ---

    public DirectedGraph(final ArrayList<D> dataNodeArray, final HashMap<D, ArrayList<D>> dataEdgeArrayMap, final HashMap<D, ArrayList<Integer>> weightEdgeArrayMap) {
        this.graph = createGraph(dataNodeArray, dataEdgeArrayMap, weightEdgeArrayMap);
        this.transposedGraph = createTransposedGraph(dataNodeArray, dataEdgeArrayMap);

        assert (this.graph.size() == dataNodeArray.size());
        assert (this.transposedGraph.size() == dataNodeArray.size());

        {
            this.dataIndex = new HashMap<>(dataNodeArray.size());
            int i = 0;
            for (final D data : dataNodeArray) {
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

                newNode.addNode(new DirectedGraphNodeEdge<>(weightEdgeArray.get(i), this.graph.get(dataEdgeArray.get(i))));
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

                transposedNode.addNode(new DirectedGraphNodeEdge<>(transposedWeightEdgeArray.get(i), this.transposedGraph.get(data)));
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

                newTransposedNode.addNode(new DirectedGraphNodeEdge<>(transposedWeightEdgeArray.get(i), this.transposedGraph.get(transposedDataEdgeArray.get(i))));
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

                node.addNode(new DirectedGraphNodeEdge<>(weightEdgeArray.get(i), this.graph.get(transposedData)));
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

            for (final DirectedGraphNodeEdge<D> nextEdge : node.getEdges().values()) {
                if (isSkipScc) {
                    if (this.dataScc.containsKey(startNode.getData())) {
                        continue;
                    }
                }

                if (isDiscovered[this.dataIndex.get(nextEdge.getTo().getData())]) {
                    continue;
                }

                isDiscovered[this.dataIndex.get(nextEdge.getTo().getData())] = true;
                bfsQueue.addLast(nextEdge.getTo());
            }
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

        for (final DirectedGraphNodeEdge<D> edge : startNode.getEdges().values()) {
            if (isSkipScc) {
                if (this.dataScc.containsKey(startNode.getData())) {
                    continue;
                }
            }

            if (isDiscovered[this.dataIndex.get(edge.getTo().getData())]) {
                continue;
            }

            dfsPostOrderReverseRecursive(isSkipScc, edge.getTo().getData(), isTransposedGraph, isDiscovered, outPostOrderNodeReverseList);
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
    private HashMap<D, DirectedGraphNode<D>> createGraph(final ArrayList<D> dataNodeArray,
                                                         final HashMap<D, ArrayList<D>> dataEdgeArrayMap,
                                                         final HashMap<D, ArrayList<Integer>> weightEdgeArrayMap) {
        // O(n) + O(ne)

        final HashMap<D, DirectedGraphNode<D>> outGraph = new HashMap<>(dataNodeArray.size());

        for (final D data : dataNodeArray) {
            final DirectedGraphNode<D> dataNode = new DirectedGraphNode<>(data);
            outGraph.put(dataNode.getData(), dataNode);
        }

        for (final D data : dataNodeArray) {
            final DirectedGraphNode<D> dataNode = outGraph.get(data);
            final ArrayList<D> dataEdgeArray = dataEdgeArrayMap.get(dataNode.getData());
            final ArrayList<Integer> weightEdgeArray = weightEdgeArrayMap.get(dataNode.getData());

            assert (dataEdgeArray.size() == weightEdgeArray.size());

            for (int i = 0; i < dataEdgeArray.size(); ++i) {
                assert (outGraph.containsKey(dataEdgeArray.get(i)));

                dataNode.addNode(new DirectedGraphNodeEdge<>(weightEdgeArray.get(i), outGraph.get(dataEdgeArray.get(i))));
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

        for (final D data : dataNodeArray) {
            final ArrayList<D> dataEdgeArray = dataEdgeArrayMap.get(data);

            for (final D dataEdge : dataEdgeArray) {
                assert (outTransposedGraph.containsKey(dataEdge));

                final DirectedGraphNode<D> transposedNode = outTransposedGraph.get(dataEdge);

                final int edgeWeight = this.graph.get(data).getEdges().get(transposedNode.getData()).getWeight();
                transposedNode.addNode(new DirectedGraphNodeEdge<>(edgeWeight, outTransposedGraph.get(data)));
            }
        }

        return outTransposedGraph;
    }

}