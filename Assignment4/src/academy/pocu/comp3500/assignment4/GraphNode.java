package academy.pocu.comp3500.assignment4;

import java.util.LinkedList;

public final class GraphNode<T> {
    private int dataWeight;
    private final T data;
    private final LinkedList<GraphNode<T>> neighbors;

    // ---

    public GraphNode(final T data) {
        this.data = data;
        this.neighbors = new LinkedList<>();
    }

    // ---

    public final int getDataWeight() {
        return dataWeight;
    }

    public final void setDataWeight(int dataWeight) {
        this.dataWeight = dataWeight;
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
