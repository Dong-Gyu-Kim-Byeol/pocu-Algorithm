package academy.pocu.comp3500.lab11;

public final class DirectedGraphNodeEdge<T> {
    private final int weight;
    private final DirectedGraphNode<T> node1;
    private final DirectedGraphNode<T> node2;

    // ---

    public DirectedGraphNodeEdge(final int weight, final DirectedGraphNode<T> from, final DirectedGraphNode<T> to) {
        this.weight = weight;
        this.node1 = from;
        this.node2 = to;
    }

    // ---

    public final int getWeight() {
        return weight;
    }

    public final DirectedGraphNode<T> getNode1() {
        return node1;
    }

    public final DirectedGraphNode<T> getNode2() {
        return node2;
    }

    @Override
    public final String toString() {
        return String.format("from : [ %s ], to : [ %s ], weight : %d", node1.toString(), node2.toString(), weight);
    }
}