package academy.pocu.comp3500.assignment3;

import java.util.ArrayList;

public class ManualMemoryPool<T> {
    protected int nextIndex;
    protected final ArrayList<T> pool;

    public ManualMemoryPool() {
        this.pool = new ArrayList<T>();
    }

    public int poolSize() {
        return this.pool.size();
    }

    private T getNextOrNull() {
        T next = null;
        if (nextIndex < this.pool.size()) {
            next = this.pool.get(nextIndex);
            nextIndex++;
        }

        return next;
    }

    private void addPool(final T object) {
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

    // init
    public static void init(final ManualMemoryPool<char[][]> manualMemoryPool, final int arrayRowSize, final int arrayColumnSize, final int size) {
        for (int i = 0; i < size; ++i) {
            manualMemoryPool.addPool(new char[arrayRowSize][arrayColumnSize]);
        }
    }

    // getNext
    public static char[][] getNext(final ManualMemoryPool<char[][]> manualMemoryPool, final int arrayRowSize, final int arrayColumnSize) {
        char[][] temp = manualMemoryPool.getNextOrNull();
        if (temp == null) {
            manualMemoryPool.addPool(new char[arrayRowSize][arrayColumnSize]);
            temp = manualMemoryPool.getNextOrNull();
        }

        for (int y = 0; y < arrayRowSize; ++y) {
            for (int x = 0; x < arrayColumnSize; ++x) {
                temp[y][x] = 0;
            }
        }

        return temp;
    }
}
