package academy.pocu.comp3500.assignment4;

public class HashSetNode<T> {
    private final T data;
    private HashSetNode<T> nextOrNull;

    // ---

    public HashSetNode(final T data) {
        this.data = data;
    }

    // ---

    public final T getData() {
        return data;
    }

    public final HashSetNode<T> getNextOrNull() {
        return nextOrNull;
    }

    public final void setNext(final HashSetNode<T> next) {
        this.nextOrNull = next;
    }
}
