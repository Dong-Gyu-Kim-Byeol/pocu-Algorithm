package academy.pocu.comp3500.assignment4;

public final class AdjacencyListGraphEdge<T> {
    private final int weight;
    private final AdjacencyListGraphNode<T> node1;
    private final AdjacencyListGraphNode<T> node2;

    // ---

    public AdjacencyListGraphEdge(final int weight, final AdjacencyListGraphNode<T> from, final AdjacencyListGraphNode<T> to) {
        this.weight = weight;
        this.node1 = from;
        this.node2 = to;
    }

    // ---

    public final int getWeight() {
        return weight;
    }

    public final AdjacencyListGraphNode<T> getNode1() {
        return node1;
    }

    public final AdjacencyListGraphNode<T> getNode2() {
        return node2;
    }

    @Override
    public final String toString() {
        return String.format("from : [ %s ], to : [ %s ], weight : %d", node1.toString(), node2.toString(), weight);
    }
}