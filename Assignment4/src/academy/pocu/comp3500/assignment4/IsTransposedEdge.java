package academy.pocu.comp3500.assignment4;

public final class IsTransposedEdge<T> {
    private final boolean isTransposedEdge;
    private final AdjacencyListGraphEdge<T> edge;

    // ---

    public IsTransposedEdge(final boolean isTransposedEdge, final AdjacencyListGraphEdge<T> edge) {
        this.isTransposedEdge = isTransposedEdge;
        this.edge = edge;
    }

    // ---

    public final boolean isTransposedEdge() {
        return isTransposedEdge;
    }

    public final AdjacencyListGraphEdge<T> getEdge() {
        return edge;
    }

    @Override
    public final String toString() {
        return String.format("isTransposedEdge: %s  /  edge : %s", this.isTransposedEdge ? "Transposed" : "origin", this.edge.toString());
    }
}
