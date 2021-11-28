package academy.pocu.comp3500.assignment4;

public final class WeightNode<T> {
    private int weight;
    private final T data;

    // ---

    public WeightNode(final int weight, final T data) {
        this.weight = weight;
        this.data = data;
    }

    // ---


    public int getWeight() {
        return weight;
    }

    public T getData() {
        return data;
    }
}
