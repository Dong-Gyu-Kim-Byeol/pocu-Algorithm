package academy.pocu.comp3500.lab7;

public final class FixedStack<T> {
    private int size;
    private final T[] array;

    @SuppressWarnings("unchecked")
    public FixedStack(final int capacity) {
        this.array = (T[]) new Object[capacity];
    }

    public int size() {
        return this.size;
    }

    public int capacity() {
        return this.array.length;
    }

    public T peek() {
        assert (this.capacity() >= this.size);
        assert (this.size > 0);

        return this.array[this.size - 1];
    }

    public void push(final T data) {
        assert (this.capacity() > this.size);

        this.array[this.size] = data;
        ++this.size;
    }

    public T pop() {
        assert (this.capacity() >= this.size);
        assert (this.size > 0);

        final T data = this.array[this.size - 1];
        this.array[this.size - 1] = null;
        --this.size;

        return data;
    }

    public boolean isEmpty() {
        assert (this.capacity() >= this.size);

        return this.size == 0;
    }
}
