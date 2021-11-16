package academy.pocu.comp3500.assignment3;

import java.util.ArrayList;

public class ManualMemoryPool<T> {
    protected int nextIndex;
    protected final ArrayList<T> pool;

    // --

    public ManualMemoryPool() {
        this.pool = new ArrayList<T>();
    }

    // --

    public final int poolSize() {
        return this.pool.size();
    }

    public final T getNextOrNull() {
        T next = null;
        if (nextIndex < this.pool.size()) {
            next = this.pool.get(nextIndex);
            nextIndex++;
        }

        return next;
    }

    public final void addPool(final T object) {
        this.pool.add(object);
    }

    public final int getNextIndex() {
        return nextIndex;
    }

    public final void resetNextIndex() {
        this.nextIndex = 0;
    }

    public final void clear() {
        this.resetNextIndex();
        this.pool.clear();
    }
}
