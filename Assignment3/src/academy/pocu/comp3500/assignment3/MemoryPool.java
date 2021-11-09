package academy.pocu.comp3500.assignment3;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public final class MemoryPool<T> {
    private final Constructor<T> constructor;
    private int nextIndex;
    private final ArrayList<T> pool;

    public MemoryPool(final Constructor<T> constructor) {
        this(constructor, 0);
    }

    public MemoryPool(final Constructor<T> constructor, final int startSize) {
        this.constructor = constructor;
        this.pool = new ArrayList<T>();

        for (int i = 0; i < startSize; ++i) {
            try {
                this.pool.add((T) constructor.newInstance());
            } catch (Exception e) {
                assert (false);
            }
        }
    }

    public T getNext() {
        T next = null;
        if (nextIndex < this.pool.size()) {
            next = this.pool.get(nextIndex);
        } else {
            try {
                next = (T) constructor.newInstance();
                this.pool.add(next);
            } catch (Exception e) {
                assert (false);
            }
        }

        assert (next != null);

        nextIndex++;
        return next;
    }

    public int getNextIndex() {
        return nextIndex;
    }

    public void resetNextIndex() {
        this.nextIndex = 0;
    }

    public void clear() {
        this.resetNextIndex();
        this.pool.clear();
    }
}
