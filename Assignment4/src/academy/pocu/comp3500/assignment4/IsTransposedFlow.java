package academy.pocu.comp3500.assignment4;

public final class IsTransposedFlow<T> {
    private final boolean isTransposedFlow;
    private final T data;

    // ---

    public IsTransposedFlow(final boolean isTransposedFlow, final T data) {
        this.isTransposedFlow = isTransposedFlow;
        this.data = data;
    }

    // ---

    public final boolean isTransposedFlow() {
        return isTransposedFlow;
    }

    public final T getData() {
        return data;
    }
}
