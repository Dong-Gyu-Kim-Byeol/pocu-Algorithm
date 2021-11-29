package academy.pocu.comp3500.assignment4;

public final class IsBackFlow<T> {
    private final boolean isBackFlow;
    private final T data;

    // ---

    public IsBackFlow(final boolean isBackFlow, final T data) {
        this.isBackFlow = isBackFlow;
        this.data = data;
    }

    // ---

    public final boolean isBackFlow() {
        return isBackFlow;
    }

    public final T getData() {
        return data;
    }
}
