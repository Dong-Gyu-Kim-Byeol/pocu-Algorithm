package academy.pocu.comp3500.lab7;

public final class FixedStack<T> extends java.util.ArrayList<T> {
    private int size;
    private final Object[] array;

    public FixedStack(final int capacity) {
        this.array = new Object[capacity];
    }

    public int size(){
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
}
