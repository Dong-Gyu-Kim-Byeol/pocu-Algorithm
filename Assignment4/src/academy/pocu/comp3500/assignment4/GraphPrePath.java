package academy.pocu.comp3500.assignment4;

public final class GraphPrePath<T> {
    private final T data;
    private final GraphPrePath<T> preOrNull;

    // ---

    public GraphPrePath(final T data, final GraphPrePath<T> pre) {
        this.data = data;
        this.preOrNull = pre;
    }

    // ---


    public T getData() {
        return data;
    }

    public GraphPrePath<T> getPreOrNull() {
        return preOrNull;
    }
}
