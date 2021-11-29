package academy.pocu.comp3500.assignment4;

import academy.pocu.comp3500.assignment4.project.Task;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public final class Project {
    private final Task[] tasks;
    private final HashMap<Task, Integer> taskIndex;
    private final HashMap<String, Task> taskMapWithOutCycle;
    private final HashMap<Task, Boolean> taskScc;

    private final HashMap<Task, DirectedGraphNode<Task>> graph;
    private final HashMap<Task, DirectedGraphBackNode<Task>> backGraph;

    // ---

    public Project(final Task[] tasks) {
        this.tasks = tasks;
        this.graph = getGraph();
        this.backGraph = getBackGraph();

        assert (this.graph.size() == this.tasks.length);
        assert (this.backGraph.size() == this.tasks.length);

        {
            this.taskIndex = new HashMap<>(this.tasks.length);
            int i = 0;
            for (final Task task : this.tasks) {
                this.taskIndex.put(task, i++);
            }
        }

        this.taskScc = new HashMap<>(tasks.length);
        final LinkedList<Task> sortedWithoutCycle = topologicalSort(false, this.taskScc);

        this.taskMapWithOutCycle = new HashMap<>(tasks.length);
        for (final Task task : sortedWithoutCycle) {
            this.taskMapWithOutCycle.put(task.getTitle(), task);
        }
    }

    // ---

    public final int findTotalManMonths(final String task) {
        assert (this.taskMapWithOutCycle.containsKey(task));
        final Task taskNode = this.taskMapWithOutCycle.get(task);

        final LinkedList<Task> searchNodes = new LinkedList<>();
        bfs(this.taskScc, this.backGraph.get(taskNode), searchNodes);

        int manMonths = 0;

        for (final Task node : searchNodes) {
            manMonths += node.getEstimate();
        }

        return manMonths;
    }

    public final int findMinDuration(final String task) {
        assert (this.taskMapWithOutCycle.containsKey(task));
        final Task taskNode = this.taskMapWithOutCycle.get(task);

        final LinkedList<WeightNode<DirectedGraphBackNode<Task>>> sums = bfsBackNodeAllPathSumEstimate(this.backGraph.get(taskNode));

        int max = 0;
        for (final WeightNode<DirectedGraphBackNode<Task>> snm : sums) {
            max = Math.max(max, snm.getWeight());
        }

        return max;
    }

    public final int findMaxBonusCount(final String task) {
        assert (this.taskMapWithOutCycle.containsKey(task));
        final Task taskNode = this.taskMapWithOutCycle.get(task);


        final LinkedList<Task> ghostCombineNodes;
        final Task ghostTask;
        {

            final LinkedList<Task> searchNodes = new LinkedList<>();
            bfs(this.taskScc, this.backGraph.get(taskNode), searchNodes);

            int leafCapacitySum = 0;
            ghostCombineNodes = new LinkedList<>();
            for (final Task node : searchNodes) {
                if (node.getPredecessors().size() == 0) {
                    ghostCombineNodes.add(node);
                    leafCapacitySum += node.getEstimate();
                }
            }

            ghostTask = new Task("GHOST", leafCapacitySum);

            this.addGhostNode(ghostTask, ghostCombineNodes);
        }

        final int[] flow = new int[this.taskIndex.size()];
        maxFlow(this.taskScc, ghostTask, taskNode, flow, this.taskIndex);


        {
            assert (flow[this.taskIndex.get(ghostTask)] == flow[this.taskIndex.get(taskNode)]);

            this.removeGhostNode(ghostTask, ghostCombineNodes);

            return flow[this.taskIndex.get(taskNode)];
        }
    }


    // ---

//    private void graphNodeDataWeightRestore() {
//        for (final Task task : this.tasks) {
//            final GraphNode<Task> node = this.graph.get(task);
//            node.setDataWeight(this.taskEstimateBackup.get(task));
//        }
//    }

    // create
    private HashMap<Task, DirectedGraphNode<Task>> getGraph() {
        // O(n) + O(ne)

        final HashMap<Task, DirectedGraphNode<Task>> outGraph = new HashMap<>(this.tasks.length);

        for (final Task task : this.tasks) {
            final DirectedGraphNode<Task> outNode = new DirectedGraphNode<>(task);
            outGraph.put(task, outNode);
        }

        for (final Task task : this.tasks) {
            for (final Task preTask : task.getPredecessors()) {
                assert (outGraph.containsKey(preTask));

                final DirectedGraphNode<Task> outPreNode = outGraph.get(preTask);
                outPreNode.addNext(outGraph.get(task));
            }
        }

        return outGraph;
    }

    private HashMap<Task, DirectedGraphBackNode<Task>> getBackGraph() {
        // O(n) + O(ne)

        final HashMap<Task, DirectedGraphBackNode<Task>> outBackGraph = new HashMap<>(this.tasks.length);

        for (final Task task : this.tasks) {
            final DirectedGraphBackNode<Task> outBackNode = new DirectedGraphBackNode<>(task);
            outBackGraph.put(task, outBackNode);
        }

        for (final Task task : this.tasks) {
            final DirectedGraphBackNode<Task> outBackNode = outBackGraph.get(task);

            for (final Task preTask : task.getPredecessors()) {
                assert (outBackGraph.containsKey(preTask));

                outBackNode.addBack(outBackGraph.get(preTask));
            }
        }

        return outBackGraph;
    }

    private LinkedList<WeightNode<DirectedGraphBackNode<Task>>> bfsBackNodeAllPathSumEstimate(final DirectedGraphBackNode<Task> startNode) {
        final LinkedList<WeightNode<DirectedGraphBackNode<Task>>> bfsQueue = new LinkedList<>();
        final LinkedList<WeightNode<DirectedGraphBackNode<Task>>> outEndSumEstimate = new LinkedList<>();

        {
            assert (!this.taskScc.containsKey(startNode.getData()));

            bfsQueue.addLast(new WeightNode<>(startNode.getData().getEstimate(), startNode));
        }

        while (!bfsQueue.isEmpty()) {
            final WeightNode<DirectedGraphBackNode<Task>> weightNode = bfsQueue.poll();

            if (weightNode.getData().getBackNodes().size() == 0) {
                outEndSumEstimate.add(weightNode);
            }

            for (final DirectedGraphBackNode<Task> preNode : weightNode.getData().getBackNodes()) {
                if (this.taskScc.containsKey(preNode.getData())) {
                    continue;
                }

                bfsQueue.addLast(new WeightNode<>(weightNode.getWeight() + preNode.getData().getEstimate(), preNode));
            }
        }

        return outEndSumEstimate;
    }

    private Stack<DirectedGraphNode<Task>> getBfsPostOrderStack(final DirectedGraphBackNode<Task> startNode,
                                                                final boolean[] isDiscovered) {
        final LinkedList<DirectedGraphBackNode<Task>> bfsFirstQueue = new LinkedList<>();
        final Stack<DirectedGraphNode<Task>> bfsSecondStack = new Stack<>();

        {
            assert (!this.taskScc.containsKey(startNode.getData()));

            bfsFirstQueue.addLast(startNode);
        }

        while (!bfsFirstQueue.isEmpty()) {
            final DirectedGraphBackNode<Task> now = bfsFirstQueue.poll();

            final DirectedGraphNode<Task> graphNode = this.graph.get(now.getData());
            bfsSecondStack.push(graphNode);

            for (final DirectedGraphBackNode<Task> preNode : now.getBackNodes()) {
                if (this.taskScc.containsKey(preNode.getData())) {
                    continue;
                }

                if (isDiscovered[this.taskIndex.get(preNode.getData())]) {
                    continue;
                }

                isDiscovered[this.taskIndex.get(preNode.getData())] = true;
                bfsFirstQueue.addLast(preNode);
            }
        }

        return bfsSecondStack;
    }

    // library
    // ghost
    private void addGhostNode(final Task ghostTask,
                              final LinkedList<Task> predecessors) {
        final DirectedGraphNode<Task> ghostNode = new DirectedGraphNode<>(ghostTask);
        final DirectedGraphBackNode<Task> ghostBackNode = new DirectedGraphBackNode<>(ghostTask);

        for (final Task preTask : predecessors) {
            this.backGraph.get(preTask).addBack(ghostBackNode);

            ghostNode.addNext(this.graph.get(preTask));
        }

        this.graph.put(ghostTask, ghostNode);
        this.backGraph.put(ghostTask, ghostBackNode);

        this.taskIndex.put(ghostTask, this.taskIndex.size());
    }

    private void removeGhostNode(final Task ghostTask,
                                 final LinkedList<Task> predecessors) {
        final DirectedGraphNode<Task> ghostNode = this.graph.get(ghostTask);
        final DirectedGraphBackNode<Task> ghostBackNode = this.backGraph.get(ghostTask);

        for (final Task preTask : predecessors) {
            this.backGraph.get(preTask).removeBack(ghostBackNode);

//            ghostNode.removeNext(this.graph.get(preTask));
        }

        this.graph.remove(ghostTask);
        this.backGraph.remove(ghostTask);

        this.taskIndex.remove(ghostTask);
    }

    // bfs
    private void bfs(final HashMap<Task, Boolean> skipOrNull,
                     final DirectedGraphBackNode<Task> startNode,
                     final LinkedList<Task> outNodeList) {

        final boolean[] isDiscovered = new boolean[this.taskIndex.size()];

        final LinkedList<DirectedGraphBackNode<Task>> bfsQueue = new LinkedList<>();

        {
            if (skipOrNull != null) {
                if (skipOrNull.containsKey(startNode.getData())) {
                    return;
                }
            }

            if (isDiscovered[this.taskIndex.get(startNode.getData())]) {
                return;
            }

            isDiscovered[this.taskIndex.get(startNode.getData())] = true;
            bfsQueue.addLast(startNode);
        }

        while (!bfsQueue.isEmpty()) {
            final DirectedGraphBackNode<Task> backNode = bfsQueue.poll();

            outNodeList.addFirst(backNode.getData());

            for (final DirectedGraphBackNode<Task> preNode : backNode.getBackNodes()) {
                if (skipOrNull != null) {
                    if (skipOrNull.containsKey(preNode.getData())) {
                        continue;
                    }
                }

                if (isDiscovered[this.taskIndex.get(preNode.getData())]) {
                    continue;
                }

                isDiscovered[this.taskIndex.get(preNode.getData())] = true;
                bfsQueue.addLast(preNode);
            }
        }
    }

    private void bfsAllPathsStartToAllLeafNodeCanOverlap(final HashMap<Task, Boolean> skipOrNull,
                                                         final DirectedGraphBackNode<Task> startNode,
                                                         final LinkedList<LinkedList<Task>> outPaths) {

        assert (outPaths.isEmpty());

        final LinkedList<PrePath<DirectedGraphBackNode<Task>>> ends = new LinkedList<>();
        final LinkedList<PrePath<DirectedGraphBackNode<Task>>> bfsQueue = new LinkedList<>();

        {
            assert (skipOrNull == null || (!skipOrNull.containsKey(startNode.getData())));

            bfsQueue.addLast(new PrePath<>(startNode, null));
        }

        while (!bfsQueue.isEmpty()) {
            final PrePath<DirectedGraphBackNode<Task>> now = bfsQueue.poll();

            if (now.getData().getBackNodes().isEmpty()) {
                ends.add(now);
            }

            for (final DirectedGraphBackNode<Task> nextBackNode : now.getData().getBackNodes()) {
                if (skipOrNull != null) {
                    if (skipOrNull.containsKey(nextBackNode.getData())) {
                        continue;
                    }
                }

                bfsQueue.addLast(new PrePath<>(nextBackNode, now));
            }
        }

        for (PrePath<DirectedGraphBackNode<Task>> prePath : ends) {
            final LinkedList<Task> path = new LinkedList<>();
            outPaths.add(path);
            while (prePath != null) {
                path.addFirst(prePath.getData().getData());

                prePath = prePath.getPre();
            }
        }
    }

    private void maxFlow(final HashMap<Task, Boolean> skipOrNull,
                         final Task source,
                         final Task sink,
                         final int[] outFlow,
                         final HashMap<Task, Integer> indexes) {

        assert (outFlow.length == indexes.size());

        final int[] backFlow = new int[indexes.size()];
        final LinkedList<IsBackFlow<Task>> path = new LinkedList<>();
        final int[] minRemainCapacity = new int[1];

        while (true) {
            path.clear();
            minRemainCapacity[0] = 0;

            bfsShortestPathSourceToSinkSkipZeroFlow(skipOrNull, source, sink, outFlow, backFlow, this.taskIndex, path, minRemainCapacity);

            if (path.isEmpty()) {
                break;
            }

            for (final IsBackFlow<Task> isBackFlow : path) {
                final int index = indexes.get(isBackFlow.getData());
                if (isBackFlow.isBackFlow()) {
                    backFlow[index] -= minRemainCapacity[0];
                } else {
                    outFlow[index] += minRemainCapacity[0];
                }
            }
        }
    }

    private void bfsShortestPathSourceToSinkSkipZeroFlow(final HashMap<Task, Boolean> skipOrNull,
                                                         final Task source,
                                                         final Task sink,
                                                         final int[] flow,
                                                         final int[] backFlow,
                                                         final HashMap<Task, Integer> indexes,
                                                         final LinkedList<IsBackFlow<Task>> outPath,
                                                         final int[] outMinRemainCapacity) {

        final int BACK_FLOW_CAPACITY = 0;

        assert (outPath.isEmpty());
        assert (outMinRemainCapacity.length == 1);

        final boolean[] isDiscovered = new boolean[indexes.size()];

        final LinkedList<IsBackFlow<Task>> bfsQueue = new LinkedList<>();

        final HashMap<IsBackFlow<Task>, IsBackFlow<Task>> prePath = new HashMap<>();

        {
            assert (skipOrNull == null || (!skipOrNull.containsKey(source)));

            if (isDiscovered[indexes.get(source)]) {
                return;
            }

            if (source.getEstimate() - flow[indexes.get(source)] == 0) {
                return;
            }

            isDiscovered[indexes.get(source)] = true;
            bfsQueue.addLast(new IsBackFlow<>(false, source));
        }

        IsBackFlow<Task> isBackFlow = null;
        while (!bfsQueue.isEmpty()) {
            isBackFlow = bfsQueue.poll();
            final DirectedGraphNode<Task> node = this.graph.get(isBackFlow.getData());
            final DirectedGraphBackNode<Task> backNode = this.backGraph.get(isBackFlow.getData());

            if (isBackFlow.getData().equals(sink)) {
                break;
            }

            for (final DirectedGraphNode<Task> nextNode : node.getNextNodes()) {
                final Task nextTask = nextNode.getData();

                if (skipOrNull != null) {
                    if (skipOrNull.containsKey(nextTask)) {
                        continue;
                    }
                }

                if (isDiscovered[indexes.get(nextTask)]) {
                    continue;
                }

                assert (flow[indexes.get(nextTask)] >= 0);
                if (nextTask.getEstimate() - flow[indexes.get(nextTask)] == 0) {
                    continue;
                }

                isDiscovered[indexes.get(nextTask)] = true;
                final IsBackFlow<Task> nextIsBackFlow = new IsBackFlow<>(false, nextTask);
                bfsQueue.addLast(nextIsBackFlow);
                prePath.put(nextIsBackFlow, isBackFlow);
            }

            for (final DirectedGraphBackNode<Task> nextBackNode : backNode.getBackNodes()) {
                final Task nextBackTask = nextBackNode.getData();

                if (skipOrNull != null) {
                    if (skipOrNull.containsKey(nextBackNode.getData())) {
                        continue;
                    }
                }

                if (isDiscovered[indexes.get(nextBackTask)]) {
                    continue;
                }


                assert (backFlow[indexes.get(nextBackTask)] <= 0);
                if (BACK_FLOW_CAPACITY - backFlow[indexes.get(nextBackTask)] == 0) {
                    continue;
                }

                isDiscovered[indexes.get(nextBackTask)] = true;
                final IsBackFlow<Task> nextIsBackFlow = new IsBackFlow<>(true, nextBackTask);
                bfsQueue.addLast(nextIsBackFlow);
                prePath.put(nextIsBackFlow, isBackFlow);
            }
        }

        assert (isBackFlow != null);

        if (!sink.equals(isBackFlow.getData())) {
            return;
        }

        outMinRemainCapacity[0] = Integer.MAX_VALUE;
        while (isBackFlow != null) {
            if (isBackFlow.isBackFlow()) {
                outMinRemainCapacity[0] = Math.min(outMinRemainCapacity[0], Math.abs(BACK_FLOW_CAPACITY - flow[indexes.get(isBackFlow.getData())]));
            } else {
                outMinRemainCapacity[0] = Math.min(outMinRemainCapacity[0], isBackFlow.getData().getEstimate() - flow[indexes.get(isBackFlow.getData())]);
            }
            outPath.addFirst(isBackFlow);

            isBackFlow = prePath.get(isBackFlow);
        }
    }

    // dfs
    private void dfsPostOrderReverse(final HashMap<Task, Boolean> skipOrNull,
                                     final HashMap<Task, DirectedGraphNode<Task>> graph,
                                     final LinkedList<Task> outPostOrderNodeReverseList) {
        // O(n + e)
        final boolean[] isDiscovered = new boolean[graph.size()];

        for (final DirectedGraphNode<Task> node : graph.values()) {
            if (skipOrNull != null) {
                if (skipOrNull.containsKey(node.getData())) {
                    continue;
                }
            }

            if (isDiscovered[this.taskIndex.get(node.getData())]) {
                continue;
            }

            dfsPostOrderReverseRecursive(skipOrNull, node, isDiscovered, outPostOrderNodeReverseList);
        }
    }

    private void dfsPostOrderReverse(final HashMap<Task, Boolean> skipOrNull,
                                     final LinkedList<Task> orderedNodes,
                                     final HashMap<Task, DirectedGraphNode<Task>> graph,
                                     final LinkedList<Task> outPostOrderNodeReverseList) {
        // O(n + e)
        final boolean[] isDiscovered = new boolean[orderedNodes.size()];

        for (final Task task : orderedNodes) {
            final DirectedGraphNode<Task> node = graph.get(task);

            if (skipOrNull != null) {
                if (skipOrNull.containsKey(node.getData())) {
                    continue;
                }
            }

            if (isDiscovered[this.taskIndex.get(node.getData())]) {
                continue;
            }

            dfsPostOrderReverseRecursive(skipOrNull, node, isDiscovered, outPostOrderNodeReverseList);
        }
    }

    private void dfsPostOrderReverseRecursive(final HashMap<Task, Boolean> skipOrNull,
                                              final DirectedGraphNode<Task> startNode,
                                              final boolean[] isDiscovered,
                                              final LinkedList<Task> outPostOrderNodeReverseList) {
        isDiscovered[this.taskIndex.get(startNode.getData())] = true;

        for (final DirectedGraphNode<Task> node : startNode.getNextNodes()) {
            if (skipOrNull != null) {
                if (skipOrNull.containsKey(node.getData())) {
                    continue;
                }
            }

            if (isDiscovered[this.taskIndex.get(node.getData())]) {
                continue;
            }

            dfsPostOrderReverseRecursive(skipOrNull, node, isDiscovered, outPostOrderNodeReverseList);
        }

        outPostOrderNodeReverseList.addFirst(startNode.getData());
    }

    private void dfsPostOrderReverseRecursive(final HashMap<Task, Boolean> skipOrNull,
                                              final DirectedGraphBackNode<Task> startNode,
                                              final boolean[] isDiscovered,
                                              final LinkedList<Task> outPostOrderNodeReverseList) {
        isDiscovered[this.taskIndex.get(startNode.getData())] = true;

        for (final DirectedGraphBackNode<Task> node : startNode.getBackNodes()) {
            if (skipOrNull != null) {
                if (skipOrNull.containsKey(node.getData())) {
                    continue;
                }
            }

            if (isDiscovered[this.taskIndex.get(node.getData())]) {
                continue;
            }

            dfsPostOrderReverseRecursive(skipOrNull, node, isDiscovered, outPostOrderNodeReverseList);
        }

        outPostOrderNodeReverseList.addFirst(startNode.getData());
    }

    // topological sort
    private LinkedList<Task> topologicalSort(final boolean includeScc,
                                             final HashMap<Task, Boolean> outScc) {
        // O(n + e) + O(n + e) + O(n + e)

        final LinkedList<Task> dfsPostOrderNodeReverseList = new LinkedList<>();
        dfsPostOrderReverse(null, this.graph, dfsPostOrderNodeReverseList);

        // get scc groups with size > 1
        {
            final boolean[] backIsDiscovered = new boolean[this.backGraph.size()];
            final LinkedList<Task> scc = new LinkedList<>();

            for (final Task task : dfsPostOrderNodeReverseList) {
                final DirectedGraphBackNode<Task> backNode = this.backGraph.get(task);

                if (backIsDiscovered[this.taskIndex.get(backNode.getData())]) {
                    continue;
                }

                dfsPostOrderReverseRecursive(null, backNode, backIsDiscovered, scc);

                assert (scc.size() >= 1);

                if (scc.size() > 1) {
                    for (final Task skip : scc) {
                        outScc.put(skip, true);
                    }
                }

                scc.clear();
            }
        }


        // get sortedList
        final LinkedList<Task> outSortedList = new LinkedList<>();
        if (includeScc) {
            dfsPostOrderReverse(outScc, dfsPostOrderNodeReverseList, this.graph, outSortedList);
        } else {
            dfsPostOrderReverse(null, dfsPostOrderNodeReverseList, this.graph, outSortedList);
        }

        return outSortedList;
    }
}