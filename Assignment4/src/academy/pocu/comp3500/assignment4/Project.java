package academy.pocu.comp3500.assignment4;

import academy.pocu.comp3500.assignment4.project.Task;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public final class Project {
    private final Task[] tasks;
    private final HashMap<Task, Integer> taskEstimateBackup;

    private final HashMap<Task, GraphNode<Task>> graph;

    final LinkedList<Task> tasksWithoutCycle;
    final LinkedList<GraphNode<Task>> sortedGraphNodeWithoutCycle;
    private final HashMap<String, Task> taskMapWithOutCycle;

    private final HashMap<Task, Integer> taskIndex;
    private final HashMap<GraphNode<Task>, Integer> graphNodeIndex;

    private final HashMap<Task, Boolean> taskScc;
    private final HashMap<GraphNode<Task>, Boolean> graphNodeScc;


    // ---

    public Project(final Task[] tasks) {
        this.tasks = tasks;
        this.graph = getTransposedGraph();

        {
            this.taskIndex = new HashMap<>(tasks.length);
            int i = 0;
            for (final Task task : tasks) {
                this.taskIndex.put(task, i++);
            }
        }

        {
            this.graphNodeIndex = new HashMap<>(this.graph.size());
            int i = 0;
            for (final GraphNode<Task> graphNode : this.graph.values()) {
                this.graphNodeIndex.put(graphNode, i++);
            }
        }

        this.taskScc = new HashMap<>(tasks.length);
        this.graphNodeScc = new HashMap<>(tasks.length);

        this.sortedGraphNodeWithoutCycle = topologicalSort(this.graphNodeScc);
        this.tasksWithoutCycle = new LinkedList<>();
        this.taskMapWithOutCycle = new HashMap<>(tasks.length);
        for (final GraphNode<Task> node : sortedGraphNodeWithoutCycle) {
            this.tasksWithoutCycle.addLast(node.getData());
            this.taskMapWithOutCycle.put(node.getData().getTitle(), node.getData());
        }

        for (final GraphNode<Task> node : this.graphNodeScc.keySet()) {
            this.taskScc.put(node.getData(), true);
        }

        this.taskEstimateBackup = new HashMap<>(this.tasks.length);
        for (final Task task : this.tasks) {
            this.taskEstimateBackup.put(task, task.getEstimate());
        }
    }

    // ---

    public final int findTotalManMonths(final String task) {
        assert (this.taskMapWithOutCycle.containsKey(task));
        final Task taskNode = this.taskMapWithOutCycle.get(task);

        final boolean[] isDiscovered = new boolean[this.graph.size()];
        final LinkedList<Task> searchNodes = new LinkedList<>();
        bfsNode(taskNode, isDiscovered, searchNodes);

        int manMonths = 0;

        for (final Task node : searchNodes) {
            manMonths += node.getEstimate();
        }

        return manMonths;
    }

    public final int findMinDuration(final String task) {
        assert (this.taskMapWithOutCycle.containsKey(task));
        final Task taskNode = this.taskMapWithOutCycle.get(task);

        final LinkedList<WeightNode<Task>> sums = bfsNodeAllPathSumEstimate(taskNode);

        int max = 0;
        for (final WeightNode<Task> snm : sums) {
            max = Math.max(max, snm.getWeight());
        }

        return max;
    }

    public final int findMaxBonusCount(final String task) {
        assert (this.taskMapWithOutCycle.containsKey(task));
        final Task taskNode = this.taskMapWithOutCycle.get(task);

        final boolean[] isDiscovered = new boolean[this.graph.size()];
        final Stack<GraphNode<Task>> bfsPostOrderStack = getBfsPostOrderStack(taskNode, isDiscovered);

        while (!bfsPostOrderStack.isEmpty()) {
            final GraphNode<Task> nowNode = bfsPostOrderStack.pop();

            int sccCount = 0;

            int preWeightSum = 0;
            for (final Task pre : nowNode.getData().getPredecessors()) {
                if (this.taskScc.containsKey(pre)) {
                    ++sccCount;
                    continue;
                }

                final GraphNode<Task> preNode = this.graph.get(pre);
                preWeightSum += preNode.getDataWeight();
            }

            if (nowNode.getData().getPredecessors().size() == sccCount) {
                preWeightSum = nowNode.getDataWeight();
            }

            int cnaUseWeight = Math.min(nowNode.getDataWeight(), preWeightSum);
            nowNode.setDataWeight(cnaUseWeight);
            assert (nowNode.getDataWeight() >= 0);

            for (final Task pre : nowNode.getData().getPredecessors()) {
                if (this.taskScc.containsKey(pre)) {
                    continue;
                }

                final GraphNode<Task> preNode = this.graph.get(pre);
                preNode.setDataWeight(Math.max(0, preNode.getDataWeight() - cnaUseWeight));
                assert (preNode.getDataWeight() >= 0);
            }
        }

        final int maxBonusCount = this.graph.get(taskNode).getDataWeight();

        graphNodeDataWeightRestore();

        return maxBonusCount;
    }

    // ---

    private void graphNodeDataWeightRestore() {
        for (final Task task : this.tasks) {
            final GraphNode<Task> node = this.graph.get(task);
            node.setDataWeight(this.taskEstimateBackup.get(task));
        }
    }

    // Transposed
    private HashMap<Task, GraphNode<Task>> getTransposedGraph() {
        // O(n) + O(ne)

        final HashMap<Task, GraphNode<Task>> outTransposedGraph = new HashMap<>(this.tasks.length);

        for (final Task data : this.tasks) {
            final GraphNode<Task> transposedNode = new GraphNode<>(data);
            transposedNode.setDataWeight(data.getEstimate());
            outTransposedGraph.put(data, transposedNode);
        }

        for (final Task data : this.tasks) {
            for (final Task neighbor : data.getPredecessors()) {
                assert (outTransposedGraph.containsKey(neighbor));

                final GraphNode<Task> transposedNode = outTransposedGraph.get(neighbor);
                transposedNode.addNeighbor(outTransposedGraph.get(data));
            }
        }

        return outTransposedGraph;
    }

    // bfs
    private void bfsNode(final Task startNode,
                         final boolean[] isDiscovered,
                         final LinkedList<Task> outNodeList) {
        final LinkedList<Task> bfsQueue = new LinkedList<>();

        {
            if (this.taskScc.containsKey(startNode)) {
                return;
            }

            if (isDiscovered[this.taskIndex.get(startNode)]) {
                return;
            }

            isDiscovered[this.taskIndex.get(startNode)] = true;
            bfsQueue.addLast(startNode);
        }

        while (!bfsQueue.isEmpty()) {
            final Task node = bfsQueue.poll();

            outNodeList.addFirst(node);

            for (final Task predecessor : node.getPredecessors()) {
                if (this.taskScc.containsKey(predecessor)) {
                    continue;
                }

                if (isDiscovered[this.taskIndex.get(predecessor)]) {
                    continue;
                }

                isDiscovered[this.taskIndex.get(predecessor)] = true;
                bfsQueue.addLast(predecessor);
            }
        }
    }

    private LinkedList<WeightNode<Task>> bfsNodeAllPathSumEstimate(final Task startNode) {
        final LinkedList<WeightNode<Task>> bfsQueue = new LinkedList<>();
        final LinkedList<WeightNode<Task>> outEndSumEstimate = new LinkedList<>();

        {
            assert (!this.taskScc.containsKey(startNode));

            bfsQueue.addLast(new WeightNode<>(startNode.getEstimate(), startNode));
        }

        while (!bfsQueue.isEmpty()) {
            final WeightNode<Task> node = bfsQueue.poll();

            if (node.getData().getPredecessors().size() == 0) {
                outEndSumEstimate.add(node);
            }

            for (final Task predecessor : node.getData().getPredecessors()) {
                if (this.taskScc.containsKey(predecessor)) {
                    continue;
                }

                bfsQueue.addLast(new WeightNode<>(node.getWeight() + predecessor.getEstimate(), predecessor));
            }
        }

        return outEndSumEstimate;
    }

    private Stack<GraphNode<Task>> getBfsPostOrderStack(final Task startNode,
                                                        final boolean[] isDiscovered) {
        final LinkedList<Task> bfsFirstQueue = new LinkedList<>();
        final Stack<GraphNode<Task>> bfsSecondStack = new Stack<>();

        {
            assert (!this.taskScc.containsKey(startNode));

            bfsFirstQueue.addLast(startNode);
        }

        while (!bfsFirstQueue.isEmpty()) {
            final Task now = bfsFirstQueue.poll();

            final GraphNode<Task> graphNode = this.graph.get(now);
            bfsSecondStack.push(graphNode);

            for (final Task predecessor : now.getPredecessors()) {
                if (this.taskScc.containsKey(predecessor)) {
                    continue;
                }

                if (isDiscovered[this.taskIndex.get(predecessor)]) {
                    continue;
                }

                isDiscovered[this.taskIndex.get(predecessor)] = true;
                bfsFirstQueue.addLast(predecessor);
            }
        }

        return bfsSecondStack;
    }

    // topological sort
    private LinkedList<GraphNode<Task>> topologicalSort(final HashMap<GraphNode<Task>, Boolean> outScc) {
        // O(n + e) + O(n + e) + O(n + e)

        final LinkedList<GraphNode<Task>> dfsPostOrderNodeReverseList = new LinkedList<>();
        topologicalSortDfsPostOrderGraph(dfsPostOrderNodeReverseList);

        // get scc groups with size > 1
        {
            final boolean[] tIsDiscovered = new boolean[this.graph.size()];
            final LinkedList<Task> scc = new LinkedList<>();

            for (final GraphNode<Task> node : dfsPostOrderNodeReverseList) {
                if (tIsDiscovered[this.taskIndex.get(node.getData())]) {
                    continue;
                }

                topologicalSortDfsPostOrderNodeRecursive(node.getData(), tIsDiscovered, scc);

                assert (scc.size() >= 1);

                if (scc.size() > 1) {
                    for (final Task skip : scc) {
                        outScc.put(this.graph.get(skip), true);
                    }
                }

                scc.clear();
            }
        }

        // get sortedList
        final LinkedList<GraphNode<Task>> outSortedList = new LinkedList<>();
        topologicalSortDfsPostOrderGraph(dfsPostOrderNodeReverseList, outSortedList);


        return outSortedList;
    }

    // ---

    // topological sort
    private void topologicalSortDfsPostOrderNodeRecursive(final Task startNode,
                                                          final boolean[] isDiscovered,
                                                          final LinkedList<Task> outPostOrderNodeReverseList) {
        isDiscovered[this.taskIndex.get(startNode)] = true;

        for (final Task node : startNode.getPredecessors()) {
            if (this.taskScc.containsKey(node)) {
                continue;
            }

            if (isDiscovered[this.taskIndex.get(node)]) {
                continue;
            }

            topologicalSortDfsPostOrderNodeRecursive(node, isDiscovered, outPostOrderNodeReverseList);
        }

        outPostOrderNodeReverseList.addFirst(startNode);
    }

    private void topologicalSortDfsPostOrderGraph(final LinkedList<GraphNode<Task>> outPostOrderNodeReverseList) {
        // O(n + e)

        final boolean[] isDiscovered = new boolean[this.graph.size()];

        for (GraphNode<Task> node : this.graph.values()) {
            if (this.graphNodeScc.containsKey(node)) {
                continue;
            }

            if (isDiscovered[this.graphNodeIndex.get(node)]) {
                continue;
            }

            topologicalSortDfsPostOrderNodeRecursive(node, isDiscovered, outPostOrderNodeReverseList);
        }
    }

    private void topologicalSortDfsPostOrderGraph(final LinkedList<GraphNode<Task>> taskList, final LinkedList<GraphNode<Task>> outPostOrderNodeReverseList) {
        // O(n + e)
        final boolean[] isDiscovered = new boolean[taskList.size()];

        for (final GraphNode<Task> node : taskList) {
            if (this.graphNodeScc.containsKey(node)) {
                continue;
            }

            if (isDiscovered[this.graphNodeIndex.get(node)]) {
                continue;
            }

            topologicalSortDfsPostOrderNodeRecursive(node, isDiscovered, outPostOrderNodeReverseList);
        }
    }

    private void topologicalSortDfsPostOrderNodeRecursive(final GraphNode<Task> startNode,
                                                          final boolean[] isDiscovered,
                                                          final LinkedList<GraphNode<Task>> outPostOrderNodeReverseList) {
        isDiscovered[this.graphNodeIndex.get(startNode)] = true;

        for (final GraphNode<Task> node : startNode.getNeighbors()) {
            if (this.graphNodeScc.containsKey(node)) {
                continue;
            }

            if (isDiscovered[this.graphNodeIndex.get(node)]) {
                continue;
            }

            topologicalSortDfsPostOrderNodeRecursive(node, isDiscovered, outPostOrderNodeReverseList);
        }

        outPostOrderNodeReverseList.addFirst(startNode);
    }
}