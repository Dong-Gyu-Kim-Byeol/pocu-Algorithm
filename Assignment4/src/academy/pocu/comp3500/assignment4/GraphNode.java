package academy.pocu.comp3500.assignment4;

import java.util.HashMap;
import java.util.LinkedList;

public class GraphNode<T> {
    private final T data;
    private final LinkedList<GraphNode<T>> nodes;

    // ---

    public GraphNode(final T data) {
        this.data = data;
        this.nodes = new LinkedList<>();
    }

    // ---

    public final T getData() {
        return data;
    }

    public final LinkedList<GraphNode<T>> getNodes() {
        return nodes;
    }

    public final void addNode(final GraphNode<T> node) {
        this.nodes.add(node);
    }

    public final void removeNode(final GraphNode<T> node) {
        this.nodes.remove(node);
    }

    @Override
    public final String toString() {
        return data.toString();
    }
}
