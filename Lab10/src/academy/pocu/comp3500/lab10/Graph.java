package academy.pocu.comp3500.lab10;

import java.util.HashSet;
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
    public static <T> HashMap<T, GraphNode<T>> getTransposedGraph(final T[] graph, final Function<T, List<T>> getNeighbors) {
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

    // ---

    // search
    // dfs
    public static <T> void dfsGraph(final ArrayList<T> graph, final Function<T, List<T>> getNeighbors, final LinkedList<T> outNodeList, final boolean isOutReverse) {
        // O(n + e)

        final HashSet<T> isDiscovered = new HashSet<>();

        for (final T node : graph) {
            dfsNode(node, getNeighbors, isDiscovered, outNodeList, isOutReverse);
        }
    }

    public static <T> void dfsNode(final T startNode, final Function<T, List<T>> getNeighbors, final HashSet<T> isDiscovered, final LinkedList<T> outNodeList, final boolean isOutReverse) {
        final LinkedList<T> dfsStack = new LinkedList<>();

        {
            if (isDiscovered.contains(startNode)) {
                return;
            }

            isDiscovered.add(startNode);
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
                if (isDiscovered.contains(neighbor)) {
                    continue;
                }

                isDiscovered.add(neighbor);
                dfsStack.addLast(neighbor);
            }
        }
    }

    public static <T> void dfsNodeUntilFirstLeafNode(final T startNode, final Function<T, List<T>> getNeighbors, final HashSet<T> isDiscovered, final LinkedList<T> outNodeList, final boolean isOutReverse) {
        final LinkedList<T> dfsStack = new LinkedList<>();

        {
            if (isDiscovered.contains(startNode)) {
                return;
            }

            isDiscovered.add(startNode);
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
                if (isDiscovered.contains(neighbor)) {
                    continue;
                }

                isDiscovered.add(neighbor);
                dfsStack.addLast(neighbor);
                break;
            }
        }
    }

    public static <T> void dfsAllPathsNodeToLeafNode(final T startNode, final Function<T, List<T>> getNeighbors, final HashMap<T, GraphNode<T>> transposedGraph, final LinkedList<LinkedList<T>> outNodeLists) {
        final HashSet<T> isDiscovered = new HashSet<>();
        LinkedList<T> searchNodes = new LinkedList<>();

        while (true) {
            for (final T node : searchNodes) {
                if (getNeighbors.apply(node).size() > 1) {
                    int discoveredPredecessorCount = 0;
                    for (final T predecessor : getNeighbors.apply(node)) {
                        if (isDiscovered.contains(predecessor)) {
                            ++discoveredPredecessorCount;
                        }
                    }

                    if (discoveredPredecessorCount < getNeighbors.apply(node).size()) {
                        isDiscovered.remove(node);
                        for (final GraphNode<T> graphNode : transposedGraph.get(node).getNeighbors()) {
                            isDiscovered.remove(graphNode.getData());
                        }
                    }
                }
            }
            searchNodes = new LinkedList<>();
            Graph.dfsNodeUntilFirstLeafNode(startNode, getNeighbors, isDiscovered, searchNodes, true);

            if (searchNodes.isEmpty()) {
                break;
            }

            outNodeLists.add(searchNodes);
        }
    }

    public static <T> void dfsPostOrder(final ArrayList<GraphNode<T>> graph, final LinkedList<GraphNode<T>> outNodeList) {
        // O(n + e)

        final HashSet<GraphNode<T>> isDiscovered = new HashSet<>(graph.size());

        for (final GraphNode<T> node : graph) {
            if (isDiscovered.contains(node)) {
                continue;
            }

            dfsPostOrderRecursive(node, isDiscovered, outNodeList);
        }
    }

    public static <T> void dfsPostOrderRecursive(final GraphNode<T> startNode, final HashSet<GraphNode<T>> isDiscovered, final LinkedList<GraphNode<T>> outNodeList) {
        isDiscovered.add(startNode);

        for (final GraphNode<T> node : startNode.getNeighbors()) {
            if (isDiscovered.contains(node)) {
                continue;
            }

            dfsPostOrderRecursive(node, isDiscovered, outNodeList);
        }

        outNodeList.add(startNode);
    }

    public static <T> void dfsPostOrder(final ArrayList<T> graph, final Function<T, List<T>> getNeighbors, final LinkedList<T> outNodeList) {
        // O(n + e)

        final HashSet<T> isDiscovered = new HashSet<>(graph.size());

        for (final T node : graph) {
            if (isDiscovered.contains(node)) {
                continue;
            }

            dfsPostOrderRecursive(node, getNeighbors, isDiscovered, outNodeList);
        }
    }

    public static <T> void dfsPostOrderRecursive(final T startNode, final Function<T, List<T>> getNeighbors, final HashSet<T> isDiscovered, final LinkedList<T> outNodeList) {
        isDiscovered.add(startNode);

        for (final T node : getNeighbors.apply(startNode)) {
            if (isDiscovered.contains(node)) {
                continue;
            }

            dfsPostOrderRecursive(node, getNeighbors, isDiscovered, outNodeList);
        }

        outNodeList.add(startNode);
    }


    // ---

    // topological sort
    public static <T> LinkedList<GraphNode<T>> topologicalSort(final HashMap<T, GraphNode<T>> graph, final Function<T, List<T>> getTransposedNeighbors, final boolean includeCycle) {
        // O(n + e) + O(n + e) + O(n + e)

        final LinkedList<GraphNode<T>> dfsPostOrderNodeReverseList = new LinkedList<>();
        topologicalSortDfsPostOrder(graph, null, dfsPostOrderNodeReverseList);

        // get scc groups with size > 1
        final HashSet<GraphNode<T>> skipNodes = new HashSet<>(dfsPostOrderNodeReverseList.size());
        {
            final HashSet<T> tIsDiscovered = new HashSet<>(graph.size());
            final LinkedList<T> scc = new LinkedList<>();

            for (final GraphNode<T> node : dfsPostOrderNodeReverseList) {
                if (tIsDiscovered.contains(node.getData())) {
                    continue;
                }

                topologicalSortDfsPostOrderRecursive(node.getData(), getTransposedNeighbors, tIsDiscovered, null, scc);

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
        final LinkedList<GraphNode<T>> outSortedList = new LinkedList<>();
        if (includeCycle) {
            topologicalSortDfsPostOrder(dfsPostOrderNodeReverseList, null, outSortedList);
        } else {
            topologicalSortDfsPostOrder(dfsPostOrderNodeReverseList, skipNodes, outSortedList);
        }


        return outSortedList;
    }

    public static <T> LinkedList<T> topologicalSort(final T[] graph, final Function<T, List<T>> getNeighbors, final HashMap<T, GraphNode<T>> transposedGraph, final boolean includeCycle) {
        // O(n + e) + O(n + e) + O(n + e)

        final LinkedList<T> dfsPostOrderNodeReverseList = new LinkedList<>();
        topologicalSortDfsPostOrder(graph, getNeighbors, null, dfsPostOrderNodeReverseList);

        // get scc groups with size > 1
        final HashSet<T> skipNodes = new HashSet<>(dfsPostOrderNodeReverseList.size());
        {
            final HashSet<GraphNode<T>> tIsDiscovered = new HashSet<>(graph.length);
            final LinkedList<GraphNode<T>> scc = new LinkedList<>();

            for (final T node : dfsPostOrderNodeReverseList) {
                if (tIsDiscovered.contains(transposedGraph.get(node))) {
                    continue;
                }

                topologicalSortDfsPostOrderRecursive(transposedGraph.get(node), tIsDiscovered, null, scc);

                assert (scc.size() >= 1);

                if (scc.size() > 1) {
                    for (final GraphNode<T> skip : scc) {
                        skipNodes.add(skip.getData());
                    }
                }

                scc.clear();
            }
        }

        // get sortedList
        final LinkedList<T> outSortedList = new LinkedList<>();
        if (includeCycle) {
            topologicalSortDfsPostOrder(dfsPostOrderNodeReverseList, getNeighbors, null, outSortedList);
        } else {
            topologicalSortDfsPostOrder(dfsPostOrderNodeReverseList, getNeighbors, skipNodes, outSortedList);
        }

        return outSortedList;
    }

    // ---

    // topological sort
    private static <T> void topologicalSortDfsPostOrder(final T[] graph, final Function<T, List<T>> getNeighbors, final HashSet<T> skipNodesOrNull, final LinkedList<T> outPostOrderNodeReverseList) {
        // O(n + e)

        final HashSet<T> isDiscovered = new HashSet<>(graph.length);

        for (T node : graph) {
            if (skipNodesOrNull != null && skipNodesOrNull.contains(node)) {
                continue;
            }

            if (isDiscovered.contains(node)) {
                continue;
            }

            topologicalSortDfsPostOrderRecursive(node, getNeighbors, isDiscovered, skipNodesOrNull, outPostOrderNodeReverseList);
        }
    }

    private static <T> void topologicalSortDfsPostOrder(final List<T> graph, final Function<T, List<T>> getNeighbors, final HashSet<T> skipNodesOrNull, final LinkedList<T> outPostOrderNodeReverseList) {
        // O(n + e)

        final HashSet<T> isDiscovered = new HashSet<>(graph.size());

        for (T node : graph) {
            if (skipNodesOrNull != null && skipNodesOrNull.contains(node)) {
                continue;
            }

            if (isDiscovered.contains(node)) {
                continue;
            }

            topologicalSortDfsPostOrderRecursive(node, getNeighbors, isDiscovered, skipNodesOrNull, outPostOrderNodeReverseList);
        }
    }

    private static <T> void topologicalSortDfsPostOrderRecursive(final T startNode, final Function<T, List<T>> getNeighbors, final HashSet<T> isDiscovered, final HashSet<T> skipNodesOrNull, final LinkedList<T> outPostOrderNodeReverseList) {
        isDiscovered.add(startNode);

        for (final T node : getNeighbors.apply(startNode)) {
            if (skipNodesOrNull != null && skipNodesOrNull.contains(node)) {
                continue;
            }

            if (isDiscovered.contains(node)) {
                continue;
            }

            topologicalSortDfsPostOrderRecursive(node, getNeighbors, isDiscovered, skipNodesOrNull, outPostOrderNodeReverseList);
        }

        outPostOrderNodeReverseList.addFirst(startNode);
    }

    private static <T> void topologicalSortDfsPostOrder(final HashMap<T, GraphNode<T>> graph, final HashSet<GraphNode<T>> skipNodesOrNull, final LinkedList<GraphNode<T>> outPostOrderNodeReverseList) {
        // O(n + e)

        final HashSet<GraphNode<T>> isDiscovered = new HashSet<>(graph.size());

        for (GraphNode<T> node : graph.values()) {
            if (skipNodesOrNull != null && skipNodesOrNull.contains(node)) {
                continue;
            }

            if (isDiscovered.contains(node)) {
                continue;
            }

            topologicalSortDfsPostOrderRecursive(node, isDiscovered, skipNodesOrNull, outPostOrderNodeReverseList);
        }
    }

    private static <T> void topologicalSortDfsPostOrder(final List<GraphNode<T>> graph, final HashSet<GraphNode<T>> skipNodesOrNull, final LinkedList<GraphNode<T>> outPostOrderNodeReverseList) {
        // O(n + e)

        final HashSet<GraphNode<T>> isDiscovered = new HashSet<>(graph.size());

        for (GraphNode<T> node : graph) {
            if (skipNodesOrNull != null && skipNodesOrNull.contains(node)) {
                continue;
            }

            if (isDiscovered.contains(node)) {
                continue;
            }

            topologicalSortDfsPostOrderRecursive(node, isDiscovered, skipNodesOrNull, outPostOrderNodeReverseList);
        }
    }

    private static <T> void topologicalSortDfsPostOrderRecursive(final GraphNode<T> startNode, final HashSet<GraphNode<T>> isDiscovered, final HashSet<GraphNode<T>> skipNodesOrNull, final LinkedList<GraphNode<T>> outPostOrderNodeReverseList) {
        isDiscovered.add(startNode);

        for (GraphNode<T> node : startNode.getNeighbors()) {
            if (skipNodesOrNull != null && skipNodesOrNull.contains(node)) {
                continue;
            }

            if (isDiscovered.contains(node)) {
                continue;
            }

            topologicalSortDfsPostOrderRecursive(node, isDiscovered, skipNodesOrNull, outPostOrderNodeReverseList);
        }

        outPostOrderNodeReverseList.addFirst(startNode);
    }
}
