package academy.pocu.comp3500.lab11;

import java.util.HashMap;

public final class DirectedGraphNode<T> {
    private final T data;
    private final HashMap<T, DirectedGraphNodeEdge<T>> edges;

    // ---

    public DirectedGraphNode(final T data) {
        this.data = data;
        this.edges = new HashMap<>();
    }

    // ---

    public final T getData() {
        return data;
    }

    public final HashMap<T, DirectedGraphNodeEdge<T>> getEdges() {
        return edges;
    }

    public final void addNode(final DirectedGraphNodeEdge<T> edge) {
        assert (!this.edges.containsKey(edge.getNode2().getData()));
        this.edges.put(edge.getNode2().getData(), edge);
    }

    public final void removeEdge(final DirectedGraphNodeEdge<T> edge) {
        assert (this.edges.containsKey(edge.getNode2().getData()));
        this.edges.remove(edge.getNode2().getData());
    }

    public final void removeEdge(final T to) {
        assert (this.edges.containsKey(to));
        this.edges.remove(to);
    }

    @Override
    public final String toString() {
        return data.toString();
    }

}
