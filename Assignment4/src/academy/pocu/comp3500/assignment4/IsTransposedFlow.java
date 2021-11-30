package academy.pocu.comp3500.assignment4;

public final class IsTransposedFlow<T> {
    private final boolean isTransposedFlow;
    private final T fromOrNull;
    private final T to;

    // ---

    public IsTransposedFlow(final boolean isTransposedFlow, final T formOrNull, final T to) {
        this.isTransposedFlow = isTransposedFlow;
        this.fromOrNull = formOrNull;
        this.to = to;
    }

    // ---

    public final boolean isTransposedFlow() {
        return isTransposedFlow;
    }

    public T getFromOrNull() {
        return fromOrNull;
    }

    public final T getTo() {
        return to;
    }

    @Override
    public final String toString() {
        if (fromOrNull != null) {
            return String.format("from : %s  / to : %s", this.fromOrNull.toString(), this.to.toString());
        }

        return String.format("from : null  / to : %s", this.to.toString());
    }
}
