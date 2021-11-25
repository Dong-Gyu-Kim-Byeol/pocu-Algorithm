package academy.pocu.comp3500.lab10;

import academy.pocu.comp3500.lab10.project.Task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class Project {
    public static List<String> findSchedule(final Task[] tasks, final boolean includeMaintenance) {
        final HashMap<Task, GraphNode<Task>> graph = new HashMap<>(tasks.length);
        getTransposedGraph(tasks, graph);

        // get sortedList
        final LinkedList<GraphNode<Task>> sortedList = topologicalSort(graph, tasks, Task::getPredecessors, includeMaintenance);

        final ArrayList<String> outSortedList = new ArrayList<>(sortedList.size());
        for (final GraphNode<Task> node : sortedList) {
            final Task task = node.getData();
            outSortedList.add(task.getTitle());
        }

        return outSortedList;
    }

    public static <T> LinkedList<GraphNode<T>> topologicalSort(final HashMap<T, GraphNode<T>> graph, final T[] transposedGraph, final Function<T, List<T>> getTransposedGraphNeighbors, final boolean includeCycle) {
        final HashSet<GraphNode<T>> isDiscovered = new HashSet<>(graph.size());
        final LinkedList<GraphNode<T>> outNodeList = new LinkedList<>();

        topologicalSortDfsPostOrder(graph, isDiscovered, outNodeList);

        // get scc with size > 1
        final HashSet<GraphNode<T>> skipNodes = new HashSet<>(outNodeList.size());
        {
            final HashSet<T> tIsDiscovered = new HashSet<>(graph.size());

            isDiscovered.clear();
            final LinkedList<T> scc = new LinkedList<>();

            for (final GraphNode<T> node : outNodeList) {
                if (isDiscovered.contains(node)) {
                    continue;
                }

                topologicalSortDfsPostOrderRecursive(node.getData(), getTransposedGraphNeighbors, tIsDiscovered, null, scc);

                assert (scc.size() >= 1);

                if (scc.size() > 1) {
                    for (final T skip : scc) {
                        skipNodes.add(graph.get(skip));
                    }
                }

                scc.clear();
            }
        }

        // get sortedList
        {
            final GraphNode<T> startNode = outNodeList.getFirst();
            isDiscovered.clear();
            outNodeList.clear();

            if (includeCycle) {
                topologicalSortDfsPostOrderRecursive(startNode, isDiscovered, null, outNodeList);
            } else {
                topologicalSortDfsPostOrderRecursive(startNode, isDiscovered, skipNodes, outNodeList);
            }
        }

        return outNodeList;
    }

    public static <T> void topologicalSortDfsPostOrder(final T[] graph, final Function<T, List<T>> getTransposedGraphNeighbors, final HashSet<T> isDiscovered, final LinkedList<T> outNodeList) {
        for (T node : graph) {
            if (isDiscovered.contains(node)) {
                continue;
            }

            topologicalSortDfsPostOrderRecursive(node, getTransposedGraphNeighbors, isDiscovered, null, outNodeList);
        }
    }

    public static <T> void topologicalSortDfsPostOrderRecursive(final T startNode, final Function<T, List<T>> getTransposedGraphNeighbors, final HashSet<T> isDiscovered, final HashSet<T> skipNodesOrNull, final LinkedList<T> outNodeList) {
        isDiscovered.add(startNode);

        for (final T node : getTransposedGraphNeighbors.apply(startNode)) {
            if (skipNodesOrNull != null && skipNodesOrNull.contains(node)) {
                continue;
            }

            if (isDiscovered.contains(node)) {
                continue;
            }

            topologicalSortDfsPostOrderRecursive(node, getTransposedGraphNeighbors, isDiscovered, skipNodesOrNull, outNodeList);
        }

        outNodeList.addFirst(startNode);
    }

    public static <T> void topologicalSortDfsPostOrder(final HashMap<T, GraphNode<T>> graph, final HashSet<GraphNode<T>> isDiscovered, final LinkedList<GraphNode<T>> outNodeList) {
        for (GraphNode<T> node : graph.values()) {
            if (isDiscovered.contains(node)) {
                continue;
            }

            topologicalSortDfsPostOrderRecursive(node, isDiscovered, null, outNodeList);
        }
    }

    public static <T> void topologicalSortDfsPostOrderRecursive(final GraphNode<T> startNode, final HashSet<GraphNode<T>> isDiscovered, final HashSet<GraphNode<T>> skipNodesOrNull, final LinkedList<GraphNode<T>> outNodeList) {
        isDiscovered.add(startNode);

        for (GraphNode<T> node : startNode.getNeighbors()) {
            if (skipNodesOrNull != null && skipNodesOrNull.contains(node)) {
                continue;
            }

            if (isDiscovered.contains(node)) {
                continue;
            }

            topologicalSortDfsPostOrderRecursive(node, isDiscovered, skipNodesOrNull, outNodeList);
        }

        outNodeList.addFirst(startNode);
    }

    public static <T> void dfsPostOrder(final ArrayList<GraphNode<T>> graph, final LinkedList<GraphNode<T>> outNodeList) {
        final HashSet<GraphNode<T>> isDiscovered = new HashSet<>(graph.size());

        for (GraphNode<T> node : graph) {
            if (isDiscovered.contains(node)) {
                continue;
            }

            dfsPostOrderRecursive(node, isDiscovered, outNodeList);
        }
    }

    public static <T> void dfsPostOrderRecursive(final GraphNode<T> startNode, final HashSet<GraphNode<T>> isDiscovered, final LinkedList<GraphNode<T>> outNodeList) {
        isDiscovered.add(startNode);

        for (GraphNode<T> node : startNode.getNeighbors()) {
            if (isDiscovered.contains(node)) {
                continue;
            }

            dfsPostOrderRecursive(node, isDiscovered, outNodeList);
        }

        outNodeList.add(startNode);
    }

    public static void getTransposedGraph(final Task[] tasks, final HashMap<Task, GraphNode<Task>> outTransposedGraph) {
        assert (outTransposedGraph.isEmpty());

        for (final Task task : tasks) {
            final GraphNode<Task> transposedNode = new GraphNode<>(task);
            outTransposedGraph.put(task, transposedNode);
        }

        for (final Task task : tasks) {
            for (final Task preTask : task.getPredecessors()) {
                assert (outTransposedGraph.containsKey(preTask));

                final GraphNode<Task> transposedNode = outTransposedGraph.get(preTask);
                transposedNode.addNeighbor(outTransposedGraph.get(task));
            }
        }
    }
}