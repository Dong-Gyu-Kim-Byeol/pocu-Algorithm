package academy.pocu.comp3500.assignment4;

public final class GraphEdge<T> {
    private final int weight;
    private final GraphNode<T> from;
    private final GraphNode<T> to;

    // ---

    public GraphEdge(final int weight, final GraphNode<T> from, final GraphNode<T> to) {
        this.weight = weight;
        this.from = from;
        this.to = to;
    }

    // ---

    public final int getWeight() {
        return weight;
    }

    public final GraphNode<T> getFrom() {
        return from;
    }

    public final GraphNode<T> getTo() {
        return to;
    }

    @Override
    public final String toString() {
        if (this.from == null) {
            return String.format("from : null, to : [ %s ], weight : %d", to, weight);
        }

        return String.format("from : [ %s ], to : [ %s ], weight : %d", from, to, weight);
    }
}