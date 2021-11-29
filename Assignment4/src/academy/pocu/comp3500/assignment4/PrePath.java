package academy.pocu.comp3500.assignment4;

public final class PrePath<T> {
    private final T data;
    private final PrePath<T> pre;

    // ---

    public PrePath(final T data, final PrePath<T> pre) {
        this.data = data;
        this.pre = pre;
    }

    // ---

    public final T getData() {
        return data;
    }

    public final PrePath<T> getPre() {
        return pre;
    }
}
