package academy.pocu.comp3500.lab10;

import academy.pocu.comp3500.lab10.project.Task;

import java.util.*;

public class Project {
    public static List<String> findSchedule(final Task[] tasks, final boolean includeMaintenance) {
        final LinkedList<GraphNode<Task>> sortedList = new LinkedList<>();
        final HashSet<GraphNode<Task>> isDiscovered = new HashSet<>();

        final ArrayList<GraphNode<Task>> graph = createTransposedGraph(tasks);

        // get sortedList
        dfsPostOrder(graph, isDiscovered, null, sortedList);

        if (!includeMaintenance) {
            // get scc with size > 1
            final HashSet<Task> skipData = new HashSet<>(sortedList.size());
            {
                final LinkedList<Task> scc = new LinkedList<>();
                final HashSet<Task> sccIsDiscovered = new HashSet<>();

                for (final GraphNode<Task> node : sortedList) {
                    if (sccIsDiscovered.contains(node.getData())) {
                        continue;
                    }

                    dfsPostOrderRecursive(node.getData(), sccIsDiscovered, scc);

                    assert (scc.size() >= 1);
                    if (scc.size() > 1) {
                        for (final Task skipTask : scc) {
                            skipData.add(skipTask);
                        }
                    }

                    scc.clear();
                }
            }

            // get sortedList without cycle
            isDiscovered.clear();
            sortedList.clear();
            dfsPostOrder(graph, isDiscovered, skipData, sortedList);
        }

        final ArrayList<String> outSortedList = new ArrayList<>(sortedList.size());
        for (final GraphNode<Task> node : sortedList) {
            final Task task = node.getData();
            outSortedList.add(task.getTitle());
        }

        return outSortedList;
    }

    public static <T> void dfsPostOrder(final ArrayList<GraphNode<T>> graph, final HashSet<GraphNode<T>> isDiscovered, final HashSet<T> skipDataOrNull, final LinkedList<GraphNode<T>> outSortedList) {
        for (GraphNode<T> node : graph) {
            if (skipDataOrNull != null && skipDataOrNull.contains(node.getData())) {
                continue;
            }

            if (isDiscovered.contains(node)) {
                continue;
            }

            dfsPostOrderRecursive(node, isDiscovered, skipDataOrNull, outSortedList);
        }
    }

    public static <T> void dfsPostOrderRecursive(final GraphNode<T> startNode, final HashSet<GraphNode<T>> isDiscovered, final HashSet<T> skipDataOrNull, final LinkedList<GraphNode<T>> outSortedList) {
        isDiscovered.add(startNode);

        for (GraphNode<T> node : startNode.getNeighbors()) {
            if (skipDataOrNull != null && skipDataOrNull.contains(node.getData())) {
                continue;
            }

            if (isDiscovered.contains(node)) {
                continue;
            }

            dfsPostOrderRecursive(node, isDiscovered, skipDataOrNull, outSortedList);
        }

        outSortedList.addFirst(startNode);
    }

    public static ArrayList<GraphNode<Task>> createTransposedGraph(final Task[] tasks) {
        final ArrayList<GraphNode<Task>> transposedGraph = new ArrayList<>(tasks.length);
        final HashMap<Task, GraphNode<Task>> transposedNodes = new HashMap<>(tasks.length);

        for (final Task task : tasks) {
            final GraphNode<Task> transposedNode = new GraphNode<>(task);
            transposedGraph.add(transposedNode);

            transposedNodes.put(task, transposedNode);
        }

        for (final Task task : tasks) {
            for (final Task preTask : task.getPredecessors()) {
                assert (transposedNodes.containsKey(preTask));

                final GraphNode<Task> transposedNode = transposedNodes.get(preTask);
                transposedNode.addNeighbor(transposedNodes.get(task));
            }
        }

        return transposedGraph;
    }

    public static void dfsPostOrderRecursive(final Task startTask, final HashSet<Task> isDiscovered, final LinkedList<Task> outSortedList) {
        isDiscovered.add(startTask);

        for (final Task task : startTask.getPredecessors()) {
            if (isDiscovered.contains(task)) {
                continue;
            }

            dfsPostOrderRecursive(task, isDiscovered, outSortedList);
        }

        outSortedList.addFirst(startTask);
    }
}