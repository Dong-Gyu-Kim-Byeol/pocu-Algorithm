//package academy.pocu.comp3500.assignment4;
//
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.Stack;
//
//public final class Graph <H, D> {
//    private final D[] dataArray;
//    private final HashMap<D, Integer> dataIndex;
//    private final HashMap<D, Boolean> dataScc;
//
//    private final HashMap<D, DirectedGraphNode<D>> graph;
//    private final HashMap<D, DirectedGraphNode<D>> backGraph;
//
//    // ---
//
//    public Graph(final D[] dataArray) {
//        this.dataArray = dataArray;
//        this.graph = getGraph();
//        this.backGraph = getBackGraph();
//
//        assert (this.graph.size() == this.dataArray.length);
//        assert (this.backGraph.size() == this.dataArray.length);
//
//        {
//            this.dataIndex = new HashMap<>(this.dataArray.length);
//            int i = 0;
//            for (final D task : this.dataArray) {
//                this.dataIndex.put(task, i++);
//            }
//        }
//
//        this.dataScc = new HashMap<>(tasks.length);
//        final LinkedList<D> sortedWithoutCycle = topologicalSort(false, this.dataScc);
//    }
//
//    // ---
//
//    // create
//    private HashMap<D, DirectedGraphNode<D>> getBackGraph() {
//        // O(n) + O(ne)
//
//        final HashMap<D, DirectedGraphNode<D>> outGraph = new HashMap<>(this.dataArray.length);
//
//        for (final D task : this.dataArray) {
//            final DirectedGraphNode<D> outNode = new DirectedGraphNode<>(task);
//            outGraph.put(task, outNode);
//        }
//
//        for (final D task : this.dataArray) {
//            for (final D preTask : task.getPredecessors()) {
//                assert (outGraph.containsKey(preTask));
//
//                final DirectedGraphNode<D> outPreNode = outGraph.get(preTask);
//                outPreNode.addNext(outGraph.get(task));
//            }
//        }
//
//        return outGraph;
//    }
//
//    private HashMap<D, DirectedGraphNode<D>> getGraph() {
//        // O(n) + O(ne)
//
//        final HashMap<D, DirectedGraphNode<D>> outBackGraph = new HashMap<>(this.dataArray.length);
//
//        for (final D task : this.dataArray) {
//            final DirectedGraphNode<D> outBackNode = new DirectedGraphNode<>(task);
//            outBackGraph.put(task, outBackNode);
//        }
//
//        for (final D task : this.dataArray) {
//            final DirectedGraphNode<D> outBackNode = outBackGraph.get(task);
//
//            for (final D preTask : task.getPredecessors()) {
//                assert (outBackGraph.containsKey(preTask));
//
//                outBackNode.addBack(outBackGraph.get(preTask));
//            }
//        }
//
//        return outBackGraph;
//    }
//
//    private LinkedList<WeightNode<DirectedGraphNode<D>>> bfsBackNodeAllPathSumEstimate(final DirectedGraphNode<D> startNode) {
//        final LinkedList<WeightNode<DirectedGraphNode<D>>> bfsQueue = new LinkedList<>();
//        final LinkedList<WeightNode<DirectedGraphNode<D>>> outEndSumEstimate = new LinkedList<>();
//
//        {
//            assert (!this.dataScc.containsKey(startNode.getData()));
//
//            bfsQueue.addLast(new WeightNode<>(startNode.getData().getEstimate(), startNode));
//        }
//
//        while (!bfsQueue.isEmpty()) {
//            final WeightNode<DirectedGraphNode<D>> weightNode = bfsQueue.poll();
//
//            if (weightNode.getData().getBackNodes().size() == 0) {
//                outEndSumEstimate.add(weightNode);
//            }
//
//            for (final DirectedGraphNode<D> preNode : weightNode.getData().getBackNodes()) {
//                if (this.dataScc.containsKey(preNode.getData())) {
//                    continue;
//                }
//
//                bfsQueue.addLast(new WeightNode<>(weightNode.getWeight() + preNode.getData().getEstimate(), preNode));
//            }
//        }
//
//        return outEndSumEstimate;
//    }
//
//    private Stack<DirectedGraphNode<D>> getBfsPostOrderStack(final DirectedGraphNode<D> startNode,
//                                                                final boolean[] isDiscovered) {
//        final LinkedList<DirectedGraphNode<D>> bfsFirstQueue = new LinkedList<>();
//        final Stack<DirectedGraphNode<D>> bfsSecondStack = new Stack<>();
//
//        {
//            assert (!this.dataScc.containsKey(startNode.getData()));
//
//            bfsFirstQueue.addLast(startNode);
//        }
//
//        while (!bfsFirstQueue.isEmpty()) {
//            final DirectedGraphNode<D> now = bfsFirstQueue.poll();
//
//            final DirectedGraphNode<D> graphNode = this.graph.get(now.getData());
//            bfsSecondStack.push(graphNode);
//
//            for (final DirectedGraphNode<D> preNode : now.getBackNodes()) {
//                if (this.dataScc.containsKey(preNode.getData())) {
//                    continue;
//                }
//
//                if (isDiscovered[this.dataIndex.get(preNode.getData())]) {
//                    continue;
//                }
//
//                isDiscovered[this.dataIndex.get(preNode.getData())] = true;
//                bfsFirstQueue.addLast(preNode);
//            }
//        }
//
//        return bfsSecondStack;
//    }
//
//    // library
//    // ghost
//    private void addGhostNode(final D ghostTask,
//                              final LinkedList<D> predecessors) {
//        final DirectedGraphNode<D> ghostNode = new DirectedGraphNode<>(ghostTask);
//        final DirectedGraphNode<D> ghostBackNode = new DirectedGraphNode<>(ghostTask);
//
//        for (final D preTask : predecessors) {
//            this.backGraph.get(preTask).addBack(ghostBackNode);
//
//            ghostNode.addNext(this.graph.get(preTask));
//        }
//
//        this.graph.put(ghostTask, ghostNode);
//        this.backGraph.put(ghostTask, ghostBackNode);
//
//        this.dataIndex.put(ghostTask, this.dataIndex.size());
//    }
//
//    private void removeGhostNode(final D ghostTask,
//                                 final LinkedList<D> predecessors) {
//        final DirectedGraphNode<D> ghostNode = this.graph.get(ghostTask);
//        final DirectedGraphNode<D> ghostBackNode = this.backGraph.get(ghostTask);
//
//        for (final D preTask : predecessors) {
//            this.backGraph.get(preTask).removeBack(ghostBackNode);
//
////            ghostNode.removeNext(this.graph.get(preTask));
//        }
//
//        this.graph.remove(ghostTask);
//        this.backGraph.remove(ghostTask);
//
//        this.dataIndex.remove(ghostTask);
//    }
//
//    // bfs
//    private void bfs(final HashMap<D, Boolean> skipOrNull,
//                     final DirectedGraphNode<D> startNode,
//                     final LinkedList<D> outNodeList) {
//
//        final boolean[] isDiscovered = new boolean[this.dataIndex.size()];
//
//        final LinkedList<DirectedGraphNode<D>> bfsQueue = new LinkedList<>();
//
//        {
//            if (skipOrNull != null) {
//                if (skipOrNull.containsKey(startNode.getData())) {
//                    return;
//                }
//            }
//
//            if (isDiscovered[this.dataIndex.get(startNode.getData())]) {
//                return;
//            }
//
//            isDiscovered[this.dataIndex.get(startNode.getData())] = true;
//            bfsQueue.addLast(startNode);
//        }
//
//        while (!bfsQueue.isEmpty()) {
//            final DirectedGraphNode<D> backNode = bfsQueue.poll();
//
//            outNodeList.addFirst(backNode.getData());
//
//            for (final DirectedGraphNode<D> preNode : backNode.getBackNodes()) {
//                if (skipOrNull != null) {
//                    if (skipOrNull.containsKey(preNode.getData())) {
//                        continue;
//                    }
//                }
//
//                if (isDiscovered[this.dataIndex.get(preNode.getData())]) {
//                    continue;
//                }
//
//                isDiscovered[this.dataIndex.get(preNode.getData())] = true;
//                bfsQueue.addLast(preNode);
//            }
//        }
//    }
//
//    private void bfsAllPathsStartToAllLeafNodeCanOverlap(final HashMap<D, Boolean> skipOrNull,
//                                                         final DirectedGraphNode<D> startNode,
//                                                         final LinkedList<LinkedList<D>> outPaths) {
//
//        assert (outPaths.isEmpty());
//
//        final LinkedList<PrePath<DirectedGraphNode<D>>> ends = new LinkedList<>();
//        final LinkedList<PrePath<DirectedGraphNode<D>>> bfsQueue = new LinkedList<>();
//
//        {
//            assert (skipOrNull == null || (!skipOrNull.containsKey(startNode.getData())));
//
//            bfsQueue.addLast(new PrePath<>(startNode, null));
//        }
//
//        while (!bfsQueue.isEmpty()) {
//            final PrePath<DirectedGraphNode<D>> now = bfsQueue.poll();
//
//            if (now.getData().getBackNodes().isEmpty()) {
//                ends.add(now);
//            }
//
//            for (final DirectedGraphNode<D> nextBackNode : now.getData().getBackNodes()) {
//                if (skipOrNull != null) {
//                    if (skipOrNull.containsKey(nextBackNode.getData())) {
//                        continue;
//                    }
//                }
//
//                bfsQueue.addLast(new PrePath<>(nextBackNode, now));
//            }
//        }
//
//        for (PrePath<DirectedGraphNode<D>> prePath : ends) {
//            final LinkedList<D> path = new LinkedList<>();
//            outPaths.add(path);
//            while (prePath != null) {
//                path.addFirst(prePath.getData().getData());
//
//                prePath = prePath.getPre();
//            }
//        }
//    }
//
//    private void maxFlow(final HashMap<D, Boolean> skipOrNull,
//                         final D source,
//                         final D sink,
//                         final int[] outFlow,
//                         final HashMap<D, Integer> indexes) {
//
//        assert (outFlow.length == indexes.size());
//
//        final int[] backFlow = new int[indexes.size()];
//        final LinkedList<IsBackFlow<D>> path = new LinkedList<>();
//        final int[] minRemainCapacity = new int[1];
//
//        while (true) {
//            path.clear();
//            minRemainCapacity[0] = 0;
//
//            bfsShortestPathSourceToSinkSkipZeroFlow(skipOrNull, source, sink, outFlow, backFlow, this.dataIndex, path, minRemainCapacity);
//
//            if (path.isEmpty()) {
//                break;
//            }
//
//            for (final IsBackFlow<D> isBackFlow : path) {
//                final int index = indexes.get(isBackFlow.getData());
//                if (isBackFlow.isBackFlow()) {
//                    backFlow[index] -= minRemainCapacity[0];
//                } else {
//                    outFlow[index] += minRemainCapacity[0];
//                }
//            }
//        }
//    }
//
//    private void bfsShortestPathSourceToSinkSkipZeroFlow(final HashMap<D, Boolean> skipOrNull,
//                                                         final D source,
//                                                         final D sink,
//                                                         final int[] flow,
//                                                         final int[] backFlow,
//                                                         final HashMap<D, Integer> indexes,
//                                                         final LinkedList<IsBackFlow<D>> outPath,
//                                                         final int[] outMinRemainCapacity) {
//
//        assert (outPath.isEmpty());
//        assert (outMinRemainCapacity.length == 1);
//
//        final boolean[] isDiscovered = new boolean[indexes.size()];
//
//        final LinkedList<IsBackFlow<D>> bfsQueue = new LinkedList<>();
//
//        final HashMap<IsBackFlow<D>, IsBackFlow<D>> prePath = new HashMap<>();
//
//        {
//            assert (skipOrNull == null || (!skipOrNull.containsKey(source)));
//
//            if (isDiscovered[indexes.get(source)]) {
//                return;
//            }
//
//            if (source.getEstimate() - flow[indexes.get(source)] == 0) {
//                return;
//            }
//
//            isDiscovered[indexes.get(source)] = true;
//            bfsQueue.addLast(new IsBackFlow<>(false, source));
//        }
//
//        IsBackFlow<D> isBackFlow = null;
//        while (!bfsQueue.isEmpty()) {
//            isBackFlow = bfsQueue.poll();
//            final DirectedGraphNode<D> node = this.graph.get(isBackFlow.getData());
//            final DirectedGraphNode<D> backNode = this.backGraph.get(isBackFlow.getData());
//
//            if (isBackFlow.getData().equals(sink)) {
//                break;
//            }
//
//            for (final DirectedGraphNode<D> nextNode : node.getNextNodes()) {
//                final D nextTask = nextNode.getData();
//
//                if (skipOrNull != null) {
//                    if (skipOrNull.containsKey(nextTask)) {
//                        continue;
//                    }
//                }
//
//                if (isDiscovered[indexes.get(nextTask)]) {
//                    continue;
//                }
//
//                assert (flow[indexes.get(nextTask)] >= 0);
//                if (nextTask.getEstimate() - flow[indexes.get(nextTask)] == 0) {
//                    continue;
//                }
//
//                isDiscovered[indexes.get(nextTask)] = true;
//                final IsBackFlow<D> nextIsBackFlow = new IsBackFlow<>(false, nextTask);
//                bfsQueue.addLast(nextIsBackFlow);
//                prePath.put(nextIsBackFlow, isBackFlow);
//            }
//
//            for (final DirectedGraphNode<D> nextBackNode : backNode.getBackNodes()) {
//                final D nextBackTask = nextBackNode.getData();
//
//                if (skipOrNull != null) {
//                    if (skipOrNull.containsKey(nextBackNode.getData())) {
//                        continue;
//                    }
//                }
//
//                if (isDiscovered[indexes.get(nextBackTask)]) {
//                    continue;
//                }
//
//                final int BACK_FLOW_CAPACITY = 0;
//                assert (backFlow[indexes.get(nextBackTask)] <= 0);
//                if (BACK_FLOW_CAPACITY - backFlow[indexes.get(nextBackTask)] == 0) {
//                    continue;
//                }
//
//                isDiscovered[indexes.get(nextBackTask)] = true;
//                final IsBackFlow<D> nextIsBackFlow = new IsBackFlow<>(true, nextBackTask);
//                bfsQueue.addLast(nextIsBackFlow);
//                prePath.put(nextIsBackFlow, isBackFlow);
//            }
//        }
//
//        assert (isBackFlow != null);
//
//        if (!sink.equals(isBackFlow.getData())) {
//            return;
//        }
//
//        outMinRemainCapacity[0] = Integer.MAX_VALUE;
//        while (isBackFlow != null) {
//            outMinRemainCapacity[0] = Math.min(outMinRemainCapacity[0], isBackFlow.getData().getEstimate() - flow[indexes.get(isBackFlow.getData())]);
//            outPath.addFirst(isBackFlow);
//
//            isBackFlow = prePath.get(isBackFlow);
//        }
//    }
//
//    // dfs
//    private void dfsPostOrderReverse(final HashMap<D, Boolean> skipOrNull,
//                                     final HashMap<D, DirectedGraphNode<D>> graph,
//                                     final LinkedList<D> outPostOrderNodeReverseList) {
//        // O(n + e)
//        final boolean[] isDiscovered = new boolean[graph.size()];
//
//        for (final DirectedGraphNode<D> node : graph.values()) {
//            if (skipOrNull != null) {
//                if (skipOrNull.containsKey(node.getData())) {
//                    continue;
//                }
//            }
//
//            if (isDiscovered[this.dataIndex.get(node.getData())]) {
//                continue;
//            }
//
//            dfsPostOrderReverseRecursive(skipOrNull, node, isDiscovered, outPostOrderNodeReverseList);
//        }
//    }
//
//    private void dfsPostOrderReverse(final HashMap<D, Boolean> skipOrNull,
//                                     final LinkedList<D> orderedNodes,
//                                     final HashMap<D, DirectedGraphNode<D>> graph,
//                                     final LinkedList<D> outPostOrderNodeReverseList) {
//        // O(n + e)
//        final boolean[] isDiscovered = new boolean[orderedNodes.size()];
//
//        for (final D task : orderedNodes) {
//            final DirectedGraphNode<D> node = graph.get(task);
//
//            if (skipOrNull != null) {
//                if (skipOrNull.containsKey(node.getData())) {
//                    continue;
//                }
//            }
//
//            if (isDiscovered[this.dataIndex.get(node.getData())]) {
//                continue;
//            }
//
//            dfsPostOrderReverseRecursive(skipOrNull, node, isDiscovered, outPostOrderNodeReverseList);
//        }
//    }
//
//    private void dfsPostOrderReverseRecursive(final HashMap<D, Boolean> skipOrNull,
//                                              final DirectedGraphNode<D> startNode,
//                                              final boolean[] isDiscovered,
//                                              final LinkedList<D> outPostOrderNodeReverseList) {
//        isDiscovered[this.dataIndex.get(startNode.getData())] = true;
//
//        for (final DirectedGraphNode<D> node : startNode.getNextNodes()) {
//            if (skipOrNull != null) {
//                if (skipOrNull.containsKey(node.getData())) {
//                    continue;
//                }
//            }
//
//            if (isDiscovered[this.dataIndex.get(node.getData())]) {
//                continue;
//            }
//
//            dfsPostOrderReverseRecursive(skipOrNull, node, isDiscovered, outPostOrderNodeReverseList);
//        }
//
//        outPostOrderNodeReverseList.addFirst(startNode.getData());
//    }
//
//    private void dfsPostOrderReverseRecursive(final HashMap<D, Boolean> skipOrNull,
//                                              final DirectedGraphNode<D> startNode,
//                                              final boolean[] isDiscovered,
//                                              final LinkedList<D> outPostOrderNodeReverseList) {
//        isDiscovered[this.dataIndex.get(startNode.getData())] = true;
//
//        for (final DirectedGraphNode<D> node : startNode.getBackNodes()) {
//            if (skipOrNull != null) {
//                if (skipOrNull.containsKey(node.getData())) {
//                    continue;
//                }
//            }
//
//            if (isDiscovered[this.dataIndex.get(node.getData())]) {
//                continue;
//            }
//
//            dfsPostOrderReverseRecursive(skipOrNull, node, isDiscovered, outPostOrderNodeReverseList);
//        }
//
//        outPostOrderNodeReverseList.addFirst(startNode.getData());
//    }
//
//    // topological sort
//    private LinkedList<D> topologicalSort(final boolean includeScc,
//                                             final HashMap<D, Boolean> outScc) {
//        // O(n + e) + O(n + e) + O(n + e)
//
//        final LinkedList<D> dfsPostOrderNodeReverseList = new LinkedList<>();
//        dfsPostOrderReverse(null, this.graph, dfsPostOrderNodeReverseList);
//
//        // get scc groups with size > 1
//        {
//            final boolean[] backIsDiscovered = new boolean[this.backGraph.size()];
//            final LinkedList<D> scc = new LinkedList<>();
//
//            for (final D task : dfsPostOrderNodeReverseList) {
//                final DirectedGraphNode<D> backNode = this.backGraph.get(task);
//
//                if (backIsDiscovered[this.dataIndex.get(backNode.getData())]) {
//                    continue;
//                }
//
//                dfsPostOrderReverseRecursive(null, backNode, backIsDiscovered, scc);
//
//                assert (scc.size() >= 1);
//
//                if (scc.size() > 1) {
//                    for (final D skip : scc) {
//                        outScc.put(skip, true);
//                    }
//                }
//
//                scc.clear();
//            }
//        }
//
//
//        // get sortedList
//        final LinkedList<D> outSortedList = new LinkedList<>();
//        if (includeScc) {
//            dfsPostOrderReverse(outScc, dfsPostOrderNodeReverseList, this.graph, outSortedList);
//        } else {
//            dfsPostOrderReverse(null, dfsPostOrderNodeReverseList, this.graph, outSortedList);
//        }
//
//        return outSortedList;
//    }
//}