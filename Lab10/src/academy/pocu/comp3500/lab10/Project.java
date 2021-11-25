package academy.pocu.comp3500.lab10;

import academy.pocu.comp3500.lab10.project.Task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Project {
    public static List<String> findSchedule(final Task[] tasks, final boolean includeMaintenance) {
        final ArrayList<GraphNode<Task>> graph = new ArrayList<>(tasks.length);
        getTransposedGraph(tasks, graph);

        // get sortedList
        final ArrayList<GraphNode<Task>> sortedList = topologicalSort(graph, includeMaintenance);

        final ArrayList<String> outSortedList = new ArrayList<>(sortedList.size());
        for (final GraphNode<Task> node : sortedList) {
            final Task task = node.getData();
            outSortedList.add(task.getTitle());
        }

        return outSortedList;
    }

    public static <T> ArrayList<GraphNode<T>> topologicalSort(final ArrayList<GraphNode<T>> graph, final boolean includeCycle) {
        final HashSet<GraphNode<T>> isDiscovered = new HashSet<>(graph.size());
        final LinkedList<GraphNode<T>> dfsPostOrderList = new LinkedList<>();

        dfsPostOrder(graph, isDiscovered, dfsPostOrderList);

        // get scc with size > 1
        final HashSet<GraphNode<T>> skipNodes = new HashSet<>(dfsPostOrderList.size());
        {
            isDiscovered.clear();
            final LinkedList<GraphNode<T>> scc = new LinkedList<>();

            for (final GraphNode<T> node : dfsPostOrderList) {
                if (isDiscovered.contains(node)) {
                    continue;
                }

                dfsPostOrderRecursive(node, isDiscovered, null, scc);

                assert (scc.size() >= 1);

                if (scc.size() > 1) {
                    for (final GraphNode<T> skip : scc) {
                        skipNodes.add(skip);
                    }
                }

                scc.clear();
            }
        }

        // get sortedList
        final GraphNode<T> startNode = dfsPostOrderList.getLast();
        isDiscovered.clear();
        dfsPostOrderList.clear();
        if (includeCycle) {
            dfsPostOrderRecursive(startNode, isDiscovered, null, dfsPostOrderList);
        } else {
            dfsPostOrderRecursive(startNode, isDiscovered, skipNodes, dfsPostOrderList);
        }

        final ArrayList<GraphNode<T>> outSortedArray = new ArrayList<>(dfsPostOrderList.size());
        while (!dfsPostOrderList.isEmpty()) {
            outSortedArray.add(dfsPostOrderList.getLast());
            dfsPostOrderList.removeLast();
        }

        return outSortedArray;
    }

    public static <T> void dfsPostOrder(final ArrayList<GraphNode<T>> graph, final HashSet<GraphNode<T>> isDiscovered, final LinkedList<GraphNode<T>> outNodeList) {
        for (GraphNode<T> node : graph) {
            if (isDiscovered.contains(node)) {
                continue;
            }

            dfsPostOrderRecursive(node, isDiscovered, null, outNodeList);
        }
    }

    public static <T> void dfsPostOrderRecursive(final GraphNode<T> startNode, final HashSet<GraphNode<T>> isDiscovered, final HashSet<GraphNode<T>> skipNodesOrNull, final LinkedList<GraphNode<T>> outNodeList) {
        isDiscovered.add(startNode);

        for (GraphNode<T> node : startNode.getNeighbors()) {
            if (skipNodesOrNull != null && skipNodesOrNull.contains(node)) {
                continue;
            }

            if (isDiscovered.contains(node)) {
                continue;
            }

            dfsPostOrderRecursive(node, isDiscovered, skipNodesOrNull, outNodeList);
        }

        outNodeList.add(startNode);
    }

    public static void getTransposedGraph(final Task[] tasks, final ArrayList<GraphNode<Task>> outTransposedGraph) {
        assert (outTransposedGraph.isEmpty());

        final HashMap<Task, GraphNode<Task>> transposedNodes = new HashMap<>(tasks.length);

        for (final Task task : tasks) {
            final GraphNode<Task> transposedNode = new GraphNode<>(task);
            outTransposedGraph.add(transposedNode);

            transposedNodes.put(task, transposedNode);
        }

        for (final Task task : tasks) {
            for (final Task preTask : task.getPredecessors()) {
                assert (transposedNodes.containsKey(preTask));

                final GraphNode<Task> transposedNode = transposedNodes.get(preTask);
                transposedNode.addNeighbor(transposedNodes.get(task));
            }
        }
    }
}