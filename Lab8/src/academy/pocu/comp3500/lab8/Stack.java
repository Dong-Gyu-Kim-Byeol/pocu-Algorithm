package academy.pocu.comp3500.lab8;

public final class Stack<T> {
    private static final int CAPACITY_INCREASE_RATE = 2;

    private int size;
    private Object[] array;

    public Stack(final int capacity) {
        this.array = new Object[capacity];
    }

    public int size() {
        return this.size;
    }

    public int capacity() {
        return this.array.length;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        assert (this.capacity() >= this.size);
        assert (this.size > 0);

        return (T) this.array[this.size - 1];
    }

    public void push(final T data) {
        assert (this.capacity() >= this.size);

        if (this.capacity() <= this.size) {
            final Object[] newArray = new Object[this.capacity() * CAPACITY_INCREASE_RATE];

            for (int i = 0; i < this.array.length; ++i) {
                newArray[i] = this.array[i];
            }

            this.array = newArray;
        }

        assert (this.capacity() > this.size);

        this.array[this.size] = data;
        ++this.size;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        assert (this.capacity() >= this.size);
        assert (this.size > 0);

        final T data = (T) this.array[this.size - 1];
        this.array[this.size - 1] = null;
        --this.size;

        return data;
    }

    public boolean isEmpty() {
        assert (this.capacity() >= this.size);

        return this.size == 0;
    }

    public void clear() {
        for (int i = 0; i < this.array.length; ++i) {
            this.array[i] = null;
        }

        this.size = 0;
    }
}
