package academy.pocu.comp3500.assignment4;

import java.util.HashMap;

public final class GraphNode<T> {
    private final T data;
    private final HashMap<T, GraphEdge<T>> edges;

    // ---

    public GraphNode(final T data) {
        this.data = data;
        this.edges = new HashMap<>();
    }

    // ---

    public final T getData() {
        return data;
    }

    public final HashMap<T, GraphEdge<T>> getEdges() {
        return edges;
    }

    public final void addNode(final GraphEdge<T> edge) {
        assert (edge.getFrom().equals(this));
        assert (!this.edges.containsKey(edge.getTo().getData()));
        this.edges.put(edge.getTo().getData(), edge);
    }

    public final void removeEdge(final GraphEdge<T> edge) {
        assert (edge.getFrom().equals(this));
        assert (this.edges.containsKey(edge.getTo().getData()));
        this.edges.remove(edge.getTo().getData());
    }

    public final void removeEdge(final T to) {
        assert (this.edges.get(to).getFrom().equals(this));
        assert (this.edges.containsKey(to));
        this.edges.remove(to);
    }

    @Override
    public final String toString() {
        return data.toString();
    }
}
