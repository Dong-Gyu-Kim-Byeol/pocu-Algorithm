package academy.pocu.comp3500.assignment4;

import java.util.LinkedList;

public final class DirectedGraphNode<T> {
    private final T data;
    private final LinkedList<DirectedGraphNode<T>> nodes;

    // ---

    public DirectedGraphNode(final T data) {
        this.data = data;
        this.nodes = new LinkedList<>();
    }

    // ---

    public final T getData() {
        return data;
    }

    public final LinkedList<DirectedGraphNode<T>> getNodes() {
        return nodes;
    }

    public final void addNode(final DirectedGraphNode<T> node) {
        this.nodes.add(node);
    }

    public final void removeNode(final DirectedGraphNode<T> node) {
        this.nodes.remove(node);
    }
}
