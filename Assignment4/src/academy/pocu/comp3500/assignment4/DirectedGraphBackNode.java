package academy.pocu.comp3500.assignment4;

import java.util.LinkedList;

public class DirectedGraphBackNode<T> {
    private final T data;
    private final LinkedList<DirectedGraphBackNode<T>> preNodes;

    // ---

    public DirectedGraphBackNode(final T data) {
        this.data = data;
        this.preNodes = new LinkedList<>();
    }

    // ---

    public final T getData() {
        return data;
    }

    public final LinkedList<DirectedGraphBackNode<T>> getPreNodes() {
        return preNodes;
    }

    public final void addPre(final DirectedGraphBackNode<T> pre) {
        this.preNodes.add(pre);
    }


    public final void removePre(final DirectedGraphBackNode<T> pre) {
        this.preNodes.remove(pre);
    }
}
