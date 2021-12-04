package academy.pocu.comp3500.assignment4;

public final class GraphEdge<T> {
    private final int weight;
    private final boolean isTransposedEdge;
    private final GraphNode<T> node1;
    private final GraphNode<T> node2;

    // ---

    public GraphEdge(final boolean isTransposedEdge, final int weight, final GraphNode<T> from, final GraphNode<T> to) {
        this.isTransposedEdge = isTransposedEdge;
        this.weight = weight;
        this.node1 = from;
        this.node2 = to;
    }

    // ---

    public final boolean isTransposedEdge() {
        return isTransposedEdge;
    }

    public final int getWeight() {
        return weight;
    }

    public final GraphNode<T> getNode1() {
        return node1;
    }

    public final GraphNode<T> getNode2() {
        return node2;
    }

    @Override
    public final String toString() {
        if (this.node1 == null) {
            return String.format("isTransposedEdge: %s / from : null, to : [ %s ], weight : %d", this.isTransposedEdge ? "Transposed" : "origin", node2, weight);
        }

        return String.format("isTransposedEdge: %s / from : [ %s ], to : [ %s ], weight : %d", this.isTransposedEdge ? "Transposed" : "origin", node1, node2, weight);
    }
}