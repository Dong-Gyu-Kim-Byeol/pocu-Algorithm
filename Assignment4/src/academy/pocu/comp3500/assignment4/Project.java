package academy.pocu.comp3500.assignment4;

import academy.pocu.comp3500.assignment4.project.Task;

import java.util.HashMap;
import java.util.LinkedList;

public final class Project {
    private final Task[] tasks;
    private final HashMap<String, Task> tasksWithOutCycle;
    private final HashMap<Task, GraphNode<Task>> graph;

    private final HashMap<Task, Integer> taskIndex;
    private final HashMap<GraphNode<Task>, Integer> graphNodeIndex;

    private final HashMap<Task, Boolean> taskScc;
    private final HashMap<GraphNode<Task>, Boolean> graphNodeScc;


    // ---

    public Project(final Task[] tasks) {
        this.tasks = tasks;
        this.tasksWithOutCycle = new HashMap<>(tasks.length);
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

        final LinkedList<GraphNode<Task>> sortedWithoutCycle = topologicalSort(false, this.graphNodeScc);
        for (final GraphNode<Task> node : sortedWithoutCycle) {
            this.tasksWithOutCycle.put(node.getData().getTitle(), node.getData());
        }

        for (final GraphNode<Task> node : this.graphNodeScc.keySet()) {
            this.taskScc.put(node.getData(), true);
        }
    }

    // ---

    public final int findTotalManMonths(final String task) {
        assert (this.tasksWithOutCycle.containsKey(task));
        final Task taskNode = this.tasksWithOutCycle.get(task);

        final boolean[] isDiscovered = new boolean[this.graph.size()];
        final LinkedList<Task> searchNodes = new LinkedList<>();
        dfsNode(taskNode, isDiscovered, searchNodes);

        int manMonths = 0;

        for (final Task node : searchNodes) {
            manMonths += node.getEstimate();
        }

        return manMonths;
    }

    public final int findMinDuration(final String task) {
        assert (this.tasksWithOutCycle.containsKey(task));
        final Task taskNode = this.tasksWithOutCycle.get(task);
        final HashMap<Task, Integer> isDiscoveredAndEstimate = new HashMap<>();
        final LinkedList<LinkedList<Task>> searchNodeLists = new LinkedList<>();

        int maxManMonths = 0;

        dfsAllPathsNodeToLeafNode(taskNode, isDiscoveredAndEstimate, searchNodeLists);

        for (final LinkedList<Task> searchNodeList : searchNodeLists) {
            int manMonths = 0;
            for (final Task node : searchNodeList) {
                manMonths += node.getEstimate();
            }

            if (maxManMonths < manMonths) {
                maxManMonths = manMonths;
            }
        }

        return maxManMonths;
    }

    public final int findMaxBonusCount(final String task) {
        assert (this.tasksWithOutCycle.containsKey(task));
        final Task taskNode = this.tasksWithOutCycle.get(task);

        final HashMap<Task, Integer> isDiscoveredAndEstimate = new HashMap<>();
        final LinkedList<LinkedList<Task>> searchNodeLists = new LinkedList<>();

        dfsAllPathsNodeToLeafNode(taskNode, isDiscoveredAndEstimate, searchNodeLists);

        int sumPathMinManMonths = 0;

        for (final LinkedList<Task> searchNodeList : searchNodeLists) {
            int pathMinManMonths = searchNodeList.getFirst().getEstimate();
            for (final Task node : searchNodeList) {
                if (pathMinManMonths > node.getEstimate()) {
                    pathMinManMonths = node.getEstimate();
                }
            }
            for (final Task node : searchNodeList) {
                final int estimate = isDiscoveredAndEstimate.get(node);
                isDiscoveredAndEstimate.put(node, Math.max(0, estimate - pathMinManMonths));
            }

            sumPathMinManMonths += pathMinManMonths;
        }

        return sumPathMinManMonths;
    }

    // ---

    // Transposed
    private HashMap<Task, GraphNode<Task>> getTransposedGraph() {
        // O(n) + O(ne)

        final HashMap<Task, GraphNode<Task>> outTransposedGraph = new HashMap<>(this.tasks.length);

        for (final Task data : this.tasks) {
            final GraphNode<Task> transposedNode = new GraphNode<>(data);
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


    private void dfsNode(final Task startNode,
                         final boolean[] isDiscovered,
                         final LinkedList<Task> outNodeList) {
        final LinkedList<Task> dfsStack = new LinkedList<>();

        {
            if (this.taskScc.containsKey(startNode)) {
                return;
            }

            if (isDiscovered[this.taskIndex.get(startNode)]) {
                return;
            }

            isDiscovered[this.taskIndex.get(startNode)] = true;
            dfsStack.addLast(startNode);
        }

        while (!dfsStack.isEmpty()) {
            final Task node = dfsStack.getLast();
            dfsStack.removeLast();

            outNodeList.addFirst(node);

            for (final Task predecessor : node.getPredecessors()) {
                if (this.taskScc.containsKey(predecessor)) {
                    continue;
                }

                if (isDiscovered[this.taskIndex.get(predecessor)]) {
                    continue;
                }

                isDiscovered[this.taskIndex.get(predecessor)] = true;
                dfsStack.addLast(predecessor);
            }
        }
    }

    private void dfsNodeUntilFirstLeafNode(final Task startNode,
                                           final HashMap<Task, Integer> isDiscoveredAndCapacity,
                                           final LinkedList<Task> outNodeList) {
        final LinkedList<Task> dfsStack = new LinkedList<>();

        {
            if (this.taskScc.containsKey(startNode)) {
                return;
            }

            if (isDiscoveredAndCapacity.containsKey(startNode)) {
                return;
            }

            isDiscoveredAndCapacity.put(startNode, startNode.getEstimate());
            dfsStack.addLast(startNode);
        }

        while (!dfsStack.isEmpty()) {
            final Task node = dfsStack.getLast();
            dfsStack.removeLast();

            outNodeList.addFirst(node);

            if (node.getPredecessors().isEmpty()) {
                break;
            }

            for (final Task predecessor : node.getPredecessors()) {
                if (this.taskScc.containsKey(predecessor)) {
                    continue;
                }

                if (isDiscoveredAndCapacity.containsKey(predecessor)) {
                    continue;
                }

                isDiscoveredAndCapacity.put(predecessor, predecessor.getEstimate());
                dfsStack.addLast(predecessor);
                break;
            }
        }
    }

    private void dfsAllPathsNodeToLeafNode(final Task startNode,
                                           final HashMap<Task, Integer> isDiscoveredAndCapacity,
                                           final LinkedList<LinkedList<Task>> outNodeLists) {
        LinkedList<Task> searchNodes = new LinkedList<>();

        while (true) {
            for (final Task node : searchNodes) {
                if (node.getPredecessors().size() > 1) {
                    int discoveredPredecessorCount = 0;
                    for (final Task predecessor : node.getPredecessors()) {
                        if (isDiscoveredAndCapacity.containsKey(predecessor)) {
                            ++discoveredPredecessorCount;
                            continue;
                        }

                        if (this.taskScc.containsKey(predecessor)) {
                            ++discoveredPredecessorCount;
                        }
                    }

                    if (discoveredPredecessorCount < node.getPredecessors().size()) {
                        isDiscoveredAndCapacity.remove(node);
                        for (final GraphNode<Task> graphNode : this.graph.get(node).getNeighbors()) {
                            isDiscoveredAndCapacity.remove(graphNode.getData());
                        }
                    }
                }
            }
            searchNodes = new LinkedList<>();
            dfsNodeUntilFirstLeafNode(startNode, isDiscoveredAndCapacity, searchNodes);

            if (searchNodes.isEmpty()) {
                break;
            }

            outNodeLists.add(searchNodes);
        }
    }

    // topological sort
    private LinkedList<GraphNode<Task>> topologicalSort(final boolean includeCycle,
                                                        final HashMap<GraphNode<Task>, Boolean> outScc) {
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
        if (includeCycle) {
            topologicalSortDfsPostOrderGraph(dfsPostOrderNodeReverseList, outSortedList);
        } else {
            topologicalSortDfsPostOrderGraph(dfsPostOrderNodeReverseList, outSortedList);
        }


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