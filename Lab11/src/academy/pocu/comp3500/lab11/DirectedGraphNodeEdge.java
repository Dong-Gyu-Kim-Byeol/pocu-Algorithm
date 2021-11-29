package academy.pocu.comp3500.lab11;

public final class DirectedGraphNodeEdge<T> {
    private final int weight;
    private final DirectedGraphNode<T> to;

    // ---

    public DirectedGraphNodeEdge(final int weight, final DirectedGraphNode<T> node) {
        this.weight = weight;

        this.to = node;
    }

    // ---

    public final int getWeight() {
        return weight;
    }

    public final DirectedGraphNode<T> getTo() {
        return to;
    }
}