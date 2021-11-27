package academy.pocu.comp3500.assignment4;

import academy.pocu.comp3500.assignment4.project.Task;

import java.util.HashMap;
import java.util.LinkedList;

public final class Project {
    private final HashMap<String, Task> transposedGraphWithOutCycle;
    private final HashMap<Task, GraphNode<Task>> graph;

    // ---

    public Project(final Task[] tasks) {
        this.graph = Graph.getTransposedGraph(tasks, Task::getPredecessors);

        this.transposedGraphWithOutCycle = new HashMap<>();
        final LinkedList<GraphNode<Task>> sortedWithoutCycle = Graph.topologicalSort(this.graph, Task::getPredecessors, false);
        for (final GraphNode<Task> node : sortedWithoutCycle) {
            this.transposedGraphWithOutCycle.put(node.getData().getTitle(), node.getData());
        }
    }

    // ---

    public final int findTotalManMonths(final String task) {
        assert (this.transposedGraphWithOutCycle.containsKey(task));
        final Task taskNode = this.transposedGraphWithOutCycle.get(task);

        final HashSet<Task> isDiscovered = new HashSet<>(this.transposedGraphWithOutCycle.size());
        final LinkedList<Task> searchNodes = new LinkedList<>();
//        Graph.bfsNode(taskNode, Task::getPredecessors, isDiscovered, searchNodes);
        Graph.dfsNode(taskNode, Task::getPredecessors, isDiscovered, searchNodes, true);

        int manMonths = 0;

        for (final Task node : searchNodes) {
            manMonths += node.getEstimate();
        }

        return manMonths;
    }

    public final int findMinDuration(final String task) {
        assert (this.transposedGraphWithOutCycle.containsKey(task));
        final Task taskNode = this.transposedGraphWithOutCycle.get(task);

        final LinkedList<LinkedList<Task>> searchNodeLists = new LinkedList<>();

        int maxManMonths = 0;

        Graph.dfsAllPathsNodeToLeafNode(taskNode, Task::getPredecessors, this.graph, searchNodeLists, true);

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

        Graph.dfsAllPathsNodeToLeafNode(taskNode, Task::getPredecessors, this.graph, isDiscoveredAndEstimate, Task::getEstimate, searchNodeLists, true);

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