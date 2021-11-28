package academy.pocu.comp3500.assignment4;


import academy.pocu.comp3500.assignment4.project.Task;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.Function;

public class Graph {
    private Graph() {
    }

    // ---

    // Transposed
    public static <T> HashMap<T, GraphNode<T>> getTransposedGraph(final T[] graph,
                                                                  final Function<T, List<T>> getNeighbors) {
        // O(n) + O(ne)

        final HashMap<T, GraphNode<T>> outTransposedGraph = new HashMap<>(graph.length);

        for (final T data : graph) {
            final GraphNode<T> transposedNode = new GraphNode<>(data);
            outTransposedGraph.put(data, transposedNode);
        }

        for (final T data : graph) {
            for (final T neighbor : getNeighbors.apply(data)) {
                assert (outTransposedGraph.containsKey(neighbor));

                final GraphNode<T> transposedNode = outTransposedGraph.get(neighbor);
                transposedNode.addNeighbor(outTransposedGraph.get(data));
            }
        }

        return outTransposedGraph;
    }


    public static <T> void dfsNode(final T startNode,
                                   final Function<T, List<T>> getNeighbors,
                                   final HashMap<T, Integer> graphIndex,
                                   final boolean[] isDiscovered,
                                   final HashMap<T, Boolean> skipScc,
                                   final LinkedList<T> outNodeList,
                                   final boolean isOutReverse) {
        final LinkedList<T> dfsStack = new LinkedList<>();

        {
            if (skipScc.containsKey(startNode)) {
                return;
            }

            if (isDiscovered[graphIndex.get(startNode)]) {
                return;
            }

            isDiscovered[graphIndex.get(startNode)] = true;
            dfsStack.addLast(startNode);
        }

        while (!dfsStack.isEmpty()) {
            final T node = dfsStack.getLast();
            dfsStack.removeLast();

            if (isOutReverse) {
                outNodeList.addFirst(node);
            } else {
                outNodeList.add(node);
            }

            for (final T neighbor : getNeighbors.apply(node)) {
                if (skipScc.containsKey(neighbor)) {
                    continue;
                }

                if (isDiscovered[graphIndex.get(neighbor)]) {
                    continue;
                }

                isDiscovered[graphIndex.get(neighbor)] = true;
                dfsStack.addLast(neighbor);
            }
        }
    }

    public static <T> void dfsNodeUntilFirstLeafNode(final T startNode,
                                                     final Function<T, List<T>> getNeighbors,
                                                     final HashMap<T, Integer> isDiscoveredAndCapacity,
                                                     final Function<T, Integer> getCapacity,
                                                     final HashMap<T, Boolean> skipScc,
                                                     final LinkedList<T> outNodeList,
                                                     final boolean isOutReverse) {
        final LinkedList<T> dfsStack = new LinkedList<>();

        {
            if (skipScc.containsKey(startNode)) {
                return;
            }

            if (isDiscoveredAndCapacity.containsKey(startNode)) {
                return;
            }

            isDiscoveredAndCapacity.put(startNode, getCapacity.apply(startNode));
            dfsStack.addLast(startNode);
        }

        while (!dfsStack.isEmpty()) {
            final T node = dfsStack.getLast();
            dfsStack.removeLast();

            if (isOutReverse) {
                outNodeList.addFirst(node);
            } else {
                outNodeList.add(node);
            }

            if (getNeighbors.apply(node).isEmpty()) {
                break;
            }

            for (final T neighbor : getNeighbors.apply(node)) {
                if (skipScc.containsKey(neighbor)) {
                    continue;
                }

                if (isDiscoveredAndCapacity.containsKey(neighbor)) {
                    continue;
                }

                isDiscoveredAndCapacity.put(neighbor, getCapacity.apply(neighbor));
                dfsStack.addLast(neighbor);
                break;
            }
        }
    }

    public static <T> void dfsAllPathsNodeToLeafNode(final T startNode,
                                                     final Function<T, List<T>> getNeighbors,
                                                     final HashMap<T, GraphNode<T>> transposedGraph,
                                                     final HashMap<T, Integer> isDiscoveredAndCapacity,
                                                     final Function<T, Integer> getCapacity,
                                                     final HashMap<T, Boolean> skipScc,
                                                     final LinkedList<LinkedList<T>> outNodeLists,
                                                     final boolean isOutReverse) {
        LinkedList<T> searchNodes = new LinkedList<>();

        while (true) {
            for (final T node : searchNodes) {
                if (getNeighbors.apply(node).size() > 1) {
                    int discoveredPredecessorCount = 0;
                    for (final T predecessor : getNeighbors.apply(node)) {
                        if (isDiscoveredAndCapacity.containsKey(predecessor)) {
                            ++discoveredPredecessorCount;
                            continue;
                        }

                        if (skipScc.containsKey(predecessor)) {
                            ++discoveredPredecessorCount;
                        }
                    }

                    if (discoveredPredecessorCount < getNeighbors.apply(node).size()) {
                        isDiscoveredAndCapacity.remove(node);
                        for (final GraphNode<T> graphNode : transposedGraph.get(node).getNeighbors()) {
                            isDiscoveredAndCapacity.remove(graphNode.getData());
                        }
                    }
                }
            }
            searchNodes = new LinkedList<>();
            Graph.dfsNodeUntilFirstLeafNode(startNode, getNeighbors, isDiscoveredAndCapacity, getCapacity, skipScc, searchNodes, isOutReverse);

            if (searchNodes.isEmpty()) {
                break;
            }

            outNodeLists.add(searchNodes);
        }
    }

    // topological sort
    public static <T> LinkedList<GraphNode<T>> topologicalSort(final HashMap<T, GraphNode<T>> graph,
                                                               final HashMap<GraphNode<T>, Integer> graphIndex,
                                                               final Function<T, List<T>> getTransposedNeighbors,
                                                               final HashMap<T, Integer> transposedIndex,
                                                               final boolean includeCycle,
                                                               final HashMap<GraphNode<T>, Boolean> outScc) {
        // O(n + e) + O(n + e) + O(n + e)

        final LinkedList<GraphNode<T>> dfsPostOrderNodeReverseList = new LinkedList<>();
        topologicalSortDfsPostOrderGraph(graph, graphIndex, null, dfsPostOrderNodeReverseList);

        // get scc groups with size > 1
        {
            final boolean[] tIsDiscovered = new boolean[graph.size()];
            final LinkedList<T> scc = new LinkedList<>();

            for (final GraphNode<T> node : dfsPostOrderNodeReverseList) {
                if (tIsDiscovered[transposedIndex.get(node.getData())]) {
                    continue;
                }

                topologicalSortDfsPostOrderNodeRecursive(node.getData(), getTransposedNeighbors, tIsDiscovered, transposedIndex, null, scc);

                assert (scc.size() >= 1);

                if (scc.size() > 1) {
                    for (final T skip : scc) {
                        outScc.put(graph.get(skip), true);
                    }
                }

                scc.clear();
            }
        }

        // get sortedList
        final LinkedList<GraphNode<T>> outSortedList = new LinkedList<>();
        if (includeCycle) {
            topologicalSortDfsPostOrderGraph(dfsPostOrderNodeReverseList, GraphNode<T>::getNeighbors, graphIndex, null, outSortedList);
        } else {
            topologicalSortDfsPostOrderGraph(dfsPostOrderNodeReverseList, GraphNode<T>::getNeighbors, graphIndex, outScc, outSortedList);
        }


        return outSortedList;
    }

    // ---

    // topological sort
    private static <T> void topologicalSortDfsPostOrderGraph(final List<T> graph,
                                                             final Function<T, List<T>> getNeighbors,
                                                             final HashMap<T, Integer> graphIndex,
                                                             final HashMap<T, Boolean> skipNodesOrNull,
                                                             final LinkedList<T> outPostOrderNodeReverseList) {
        // O(n + e)
        final boolean[] isDiscovered = new boolean[graph.size()];

        for (T node : graph) {
            if (skipNodesOrNull != null && skipNodesOrNull.containsKey(node)) {
                continue;
            }

            if (isDiscovered[graphIndex.get(node)]) {
                continue;
            }

            topologicalSortDfsPostOrderNodeRecursive(node, getNeighbors, isDiscovered, graphIndex, skipNodesOrNull, outPostOrderNodeReverseList);
        }
    }

    private static <T> void topologicalSortDfsPostOrderNodeRecursive(final T startNode,
                                                                     final Function<T, List<T>> getNeighbors,
                                                                     final boolean[] isDiscovered,
                                                                     final HashMap<T, Integer> graphIndex,
                                                                     final HashMap<T, Boolean> skipNodesOrNull,
                                                                     final LinkedList<T> outPostOrderNodeReverseList) {
        isDiscovered[graphIndex.get(startNode)] = true;

        for (final T node : getNeighbors.apply(startNode)) {
            if (skipNodesOrNull != null && skipNodesOrNull.containsKey(node)) {
                continue;
            }

            if (isDiscovered[graphIndex.get(node)]) {
                continue;
            }

            topologicalSortDfsPostOrderNodeRecursive(node, getNeighbors, isDiscovered, graphIndex, skipNodesOrNull, outPostOrderNodeReverseList);
        }

        outPostOrderNodeReverseList.addFirst(startNode);
    }

    private static <T> void topologicalSortDfsPostOrderGraph(final HashMap<T, GraphNode<T>> graph,
                                                             final HashMap<GraphNode<T>, Integer> graphIndex,
                                                             final HashMap<GraphNode<T>, Boolean> skipNodesOrNull,
                                                             final LinkedList<GraphNode<T>> outPostOrderNodeReverseList) {
        // O(n + e)

        final boolean[] isDiscovered = new boolean[graph.size()];

        for (GraphNode<T> node : graph.values()) {
            if (skipNodesOrNull != null && skipNodesOrNull.containsKey(node)) {
                continue;
            }

            if (isDiscovered[graphIndex.get(node)]) {
                continue;
            }

            topologicalSortDfsPostOrderNodeRecursive(node, isDiscovered, graphIndex, skipNodesOrNull, outPostOrderNodeReverseList);
        }
    }

    private static <T> void topologicalSortDfsPostOrderNodeRecursive(final GraphNode<T> startNode,
                                                                     final boolean[] isDiscovered,
                                                                     final HashMap<GraphNode<T>, Integer> graphIndex,
                                                                     final HashMap<GraphNode<T>, Boolean> skipNodesOrNull,
                                                                     final LinkedList<GraphNode<T>> outPostOrderNodeReverseList) {
        isDiscovered[graphIndex.get(startNode)] = true;

        for (final GraphNode<T> node : startNode.getNeighbors()) {
            if (skipNodesOrNull != null && skipNodesOrNull.containsKey(node)) {
                continue;
            }

            if (isDiscovered[graphIndex.get(node)]) {
                continue;
            }

            topologicalSortDfsPostOrderNodeRecursive(node, isDiscovered, graphIndex, skipNodesOrNull, outPostOrderNodeReverseList);
        }

        outPostOrderNodeReverseList.addFirst(startNode);
    }

}
