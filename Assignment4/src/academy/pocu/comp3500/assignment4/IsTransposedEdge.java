package academy.pocu.comp3500.assignment4;

public final class IsTransposedEdge<T> {
    private final boolean isTransposedEdge;
    private final GraphEdge<T> edge;

    // ---

    public IsTransposedEdge(final boolean isTransposedEdge, final GraphEdge<T> edge) {
        this.isTransposedEdge = isTransposedEdge;
        this.edge = edge;
    }

    // ---

    public final boolean isTransposedEdge() {
        return isTransposedEdge;
    }

    public final GraphEdge<T> getEdge() {
        return edge;
    }

    @Override
    public final String toString() {
        return String.format("isTransposedEdge: %s  /  edge : %s", this.isTransposedEdge ? "Transposed" : "origin", this.edge.toString());
    }
}
