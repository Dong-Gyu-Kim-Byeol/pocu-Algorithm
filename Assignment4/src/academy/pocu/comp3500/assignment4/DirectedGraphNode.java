package academy.pocu.comp3500.assignment4;

import java.util.LinkedList;

public final class DirectedGraphNode<T> {
    private final T data;
    private final LinkedList<DirectedGraphNode<T>> nextNodes;

    // ---

    public DirectedGraphNode(final T data) {
        this.data = data;
        this.nextNodes = new LinkedList<>();
    }

    // ---

    public final T getData() {
        return data;
    }

    public final LinkedList<DirectedGraphNode<T>> getNextNodes() {
        return nextNodes;
    }

    public final void addNext(final DirectedGraphNode<T> next) {
        this.nextNodes.add(next);
    }

    public final void removeNext(final DirectedGraphNode<T> next) {
        this.nextNodes.remove(next);
    }
}
