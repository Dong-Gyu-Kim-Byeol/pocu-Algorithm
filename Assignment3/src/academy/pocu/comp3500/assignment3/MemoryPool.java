package academy.pocu.comp3500.assignment3;

import java.lang.reflect.Constructor;

public final class MemoryPool<T> extends ManualMemoryPool<T> {
    private final Constructor<T> constructor;

    // --

    public MemoryPool(final Constructor<T> constructor, final int startSize) {
        this.constructor = constructor;
        for (int i = 0; i < startSize; ++i) {
            try {
                this.pool.add(this.constructor.newInstance());
            } catch (Exception e) {
                assert (false);
            }
        }
    }

    // --

    public T getNext() {
        T next = null;
        if (nextIndex < this.pool.size()) {
            next = this.pool.get(nextIndex);
        } else {
            try {
                next = constructor.newInstance();
                this.pool.add(next);
            } catch (Exception e) {
                assert (false);
            }
        }

        assert (next != null);

        nextIndex++;
        return next;
    }
}
