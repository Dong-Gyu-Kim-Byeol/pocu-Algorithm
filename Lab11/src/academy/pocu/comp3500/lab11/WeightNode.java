package academy.pocu.comp3500.lab11;

public final class WeightNode<T> {
    private final int weight;
    private final T data;

    // ---

    public WeightNode(final int weight, final T data) {
        this.weight = weight;
        this.data = data;
    }

    // ---

    public final int getWeight() {
        return weight;
    }

    public final T getData() {
        return data;
    }
}
