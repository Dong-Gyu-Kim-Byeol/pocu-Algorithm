package academy.pocu.comp3500.lab10;

import java.util.LinkedList;

public final class GraphNode<T> {
    final private T data;
    final private LinkedList<GraphNode<T>> neighbors;

    public GraphNode(final T data) {
        this.data = data;
        this.neighbors = new LinkedList<>();
    }

    public final T getData() {
        return data;
    }

    public final LinkedList<GraphNode<T>> getNeighbors() {
        return neighbors;
    }

    public final void addNeighbor(final GraphNode<T> neighbor) {
        this.neighbors.add(neighbor);
    }
}
