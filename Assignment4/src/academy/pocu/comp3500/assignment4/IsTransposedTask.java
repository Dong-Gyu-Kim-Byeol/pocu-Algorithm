package academy.pocu.comp3500.assignment4;

public class IsTransposedTask {
    private final boolean isTransposedEdge;
    private final int from;
    private final int to;

    // ---

    public IsTransposedTask(final boolean isTransposedEdge, final int from, final int to) {
        this.isTransposedEdge = isTransposedEdge;
        this.from = from;
        this.to = to;
    }

    // ---

    public final boolean isTransposedEdge() {
        return isTransposedEdge;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    @Override
    public final String toString() {
        return String.format("isTransposedEdge: %s  /  edge : from( %s ), to( %s )", this.isTransposedEdge ? "Transposed" : "origin", this.from, this.to);
    }
}

