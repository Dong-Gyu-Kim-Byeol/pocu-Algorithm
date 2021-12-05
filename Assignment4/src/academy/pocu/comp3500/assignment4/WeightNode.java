package academy.pocu.comp3500.assignment4;

public final class WeightNode<T> implements Comparable<WeightNode<T>> {
    private final int weight;
    private final T node;

    // ---

    public WeightNode(final int weight, final T node) {
        this.weight = weight;
        this.node = node;
    }

    // ---

    public final int getWeight() {
        return weight;
    }

    public final T getNode() {
        return node;
    }

    @Override
    public final int compareTo(final WeightNode<T> o) {
        return this.weight - o.weight;
    }
}
