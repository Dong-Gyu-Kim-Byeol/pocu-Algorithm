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

            ghostTask = new Task("ADD", leafCapacitySum);

            this.addGhostNode(ghostTask, ghostCombineNodes);
        }

        final int BACK_FLOW_CAPACITY = 0;

        final int[] backFlow = new int[this.tasks.length];
        final int[] flow = new int[this.graph.size()];


        {
            this.removeGhostNode(ghostTask, ghostCombineNodes);

            return -1;
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

                outBackNode.addPre(outBackGraph.get(preTask));
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

            if (weightNode.getData().getPreNodes().size() == 0) {
                outEndSumEstimate.add(weightNode);
            }

            for (final DirectedGraphBackNode<Task> preNode : weightNode.getData().getPreNodes()) {
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

            for (final DirectedGraphBackNode<Task> preNode : now.getPreNodes()) {
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
    private void addGhostNode(final Task ghostTask, final LinkedList<Task> predecessors) {
        final DirectedGraphNode<Task> ghostNode = new DirectedGraphNode<>(ghostTask);
        final DirectedGraphBackNode<Task> ghostBackNode = new DirectedGraphBackNode<>(ghostTask);

        for (final Task preTask : predecessors) {
            this.backGraph.get(preTask).addPre(ghostBackNode);

            ghostNode.addNext(this.graph.get(preTask));
        }

        this.graph.put(ghostTask, ghostNode);
        this.backGraph.put(ghostTask, ghostBackNode);
    }

    private void removeGhostNode(final Task ghostTask, final LinkedList<Task> predecessors) {
        final DirectedGraphNode<Task> ghostNode = this.graph.get(ghostTask);
        final DirectedGraphBackNode<Task> ghostBackNode = this.backGraph.get(ghostTask);

        for (final Task preTask : predecessors) {
            this.backGraph.get(preTask).removePre(ghostBackNode);

//            ghostNode.removeNext(this.graph.get(preTask));
        }

        this.graph.remove(ghostTask);
        this.backGraph.remove(ghostTask);
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

            for (final DirectedGraphBackNode<Task> preNode : backNode.getPreNodes()) {
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

//    private void bfsSourceToSinkSkipZeroFlow(final HashMap<Task, Boolean> skipOrNull,
//                                             final Task source,
//                                             final Task skin,
//                                             final LinkedList<Task> outNodeList) {
//
//        final boolean[] isDiscovered = new boolean[this.taskIndex.size()];
//
//        final LinkedList<DirectedGraphBackNode<Task>> bfsQueue = new LinkedList<>();
//
//        {
//            assert (skipOrNull == null || (!skipOrNull.containsKey(source)));
//
//            isDiscovered[this.taskIndex.get(source)] = true;
//            bfsQueue.addLast(startNode);
//        }
//
//        while (!bfsQueue.isEmpty()) {
//            final DirectedGraphBackNode<Task> backNode = bfsQueue.poll();
//
//            outNodeList.addFirst(backNode.getData());
//
//            for (final DirectedGraphBackNode<Task> preNode : backNode.getPreNodes()) {
//                if (skipOrNull != null) {
//                    if (skipOrNull.containsKey(preNode.getData())) {
//                        continue;
//                    }
//                }
//
//                if (isDiscovered[this.backGraphIndex.get(preNode)]) {
//                    continue;
//                }
//
//                isDiscovered[this.backGraphIndex.get(preNode)] = true;
//                bfsQueue.addLast(preNode);
//            }
//        }
//    }

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

        for (final DirectedGraphBackNode<Task> node : startNode.getPreNodes()) {
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
    private LinkedList<Task> topologicalSort(final boolean includeScc, final HashMap<Task, Boolean> outScc) {
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