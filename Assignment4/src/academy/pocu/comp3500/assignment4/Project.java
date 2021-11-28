package academy.pocu.comp3500.assignment4;

import academy.pocu.comp3500.assignment4.project.Task;

import java.util.HashMap;
import java.util.LinkedList;

public final class Project {
    private final HashMap<String, Task> transposedGraphWithOutCycle;
    private final HashMap<Task, Integer> taskIndex;

    private final HashMap<Task, Boolean> taskScc;

    private final HashMap<Task, GraphNode<Task>> graph;
    private final HashMap<GraphNode<Task>, Integer> graphNodeIndex;

    private final HashMap<GraphNode<Task>, Boolean> graphNodeScc;


    // ---

    public Project(final Task[] tasks) {
        this.graphNodeScc = new HashMap<>(tasks.length);

        this.graph = Graph.getTransposedGraph(tasks, Task::getPredecessors);

        {
            this.graphNodeIndex = new HashMap<>(this.graph.size());
            int i = 0;
            for (final GraphNode<Task> graphNode : this.graph.values()) {
                this.graphNodeIndex.put(graphNode, i++);
            }
        }

        {
            this.taskIndex = new HashMap<>(tasks.length);
            int i = 0;
            for (final Task task : tasks) {
                this.taskIndex.put(task, i++);
            }
        }

        this.transposedGraphWithOutCycle = new HashMap<>();
        final LinkedList<GraphNode<Task>> sortedWithoutCycle = Graph.topologicalSort(this.graph, this.graphNodeIndex, Task::getPredecessors, this.taskIndex, false, this.graphNodeScc);
        for (final GraphNode<Task> node : sortedWithoutCycle) {
            this.transposedGraphWithOutCycle.put(node.getData().getTitle(), node.getData());
        }

        this.taskScc = new HashMap<>(tasks.length);
        for (final GraphNode<Task> node : this.graphNodeScc.keySet()) {
            this.taskScc.put(node.getData(), true);
        }
    }

    // ---

    public final int findTotalManMonths(final String task) {
        assert (this.transposedGraphWithOutCycle.containsKey(task));
        final Task taskNode = this.transposedGraphWithOutCycle.get(task);

        final boolean[] isDiscovered = new boolean[this.graph.size()];
        final LinkedList<Task> searchNodes = new LinkedList<>();
        Graph.dfsNode(taskNode, Task::getPredecessors, this.taskIndex, isDiscovered, this.taskScc, searchNodes, true);

        int manMonths = 0;

        for (final Task node : searchNodes) {
            manMonths += node.getEstimate();
        }

        return manMonths;
    }

    public final int findMinDuration(final String task) {
        assert (this.transposedGraphWithOutCycle.containsKey(task));
        final Task taskNode = this.transposedGraphWithOutCycle.get(task);
        final HashMap<Task, Integer> isDiscoveredAndEstimate = new HashMap<>();
        final LinkedList<LinkedList<Task>> searchNodeLists = new LinkedList<>();

        int maxManMonths = 0;

        Graph.dfsAllPathsNodeToLeafNode(taskNode, Task::getPredecessors, this.graph, isDiscoveredAndEstimate, Task::getEstimate, this.taskScc, searchNodeLists, true);

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
        assert (this.transposedGraphWithOutCycle.containsKey(task));
        final Task taskNode = this.transposedGraphWithOutCycle.get(task);

        final HashMap<Task, Integer> isDiscoveredAndEstimate = new HashMap<>();
        final LinkedList<LinkedList<Task>> searchNodeLists = new LinkedList<>();

        Graph.dfsAllPathsNodeToLeafNode(taskNode, Task::getPredecessors, this.graph, isDiscoveredAndEstimate, Task::getEstimate, this.taskScc, searchNodeLists, true);

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


}