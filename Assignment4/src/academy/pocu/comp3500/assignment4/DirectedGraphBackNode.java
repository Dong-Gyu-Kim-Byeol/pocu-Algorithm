package academy.pocu.comp3500.assignment4;

import java.util.LinkedList;

public class DirectedGraphBackNode<T> {
    private final T data;
    private final LinkedList<DirectedGraphBackNode<T>> backNodes;

    // ---

    public DirectedGraphBackNode(final T data) {
        this.data = data;
        this.backNodes = new LinkedList<>();
    }

    // ---

    public final T getData() {
        return data;
    }

    public final LinkedList<DirectedGraphBackNode<T>> getBackNodes() {
        return backNodes;
    }

    public final void addBack(final DirectedGraphBackNode<T> back) {
        this.backNodes.add(back);
    }


    public final void removeBack(final DirectedGraphBackNode<T> back) {
        this.backNodes.remove(back);
    }
}
