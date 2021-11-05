package academy.pocu.comp3500.lab7;

import java.nio.charset.StandardCharsets;

public final class FixedStack<T> extends java.util.ArrayList<T> {
    private static final long serialVersionUID = Hash.fnv1("FixedStack".getBytes(StandardCharsets.UTF_8));

    private final int capacity;

    public FixedStack(final int capacity) {
        super(capacity);
        this.capacity = capacity;
    }

    public int capacity() {
        assert (this.capacity >= this.size());
        return this.capacity;
    }

    public T peek() {
        assert (this.capacity >= this.size());
        return this.get(this.size() - 1);
    }

    public void push(final T data) {
        assert (this.capacity > this.size());
        this.add(data);
    }

    public T pop() {
        assert (this.capacity >= this.size());
        final T data = this.get(this.size() - 1);
        this.remove(this.size() - 1);
        return data;
    }

    public boolean empty() {
        assert (this.capacity >= this.size());
        return this.size() == 0;
    }
}
