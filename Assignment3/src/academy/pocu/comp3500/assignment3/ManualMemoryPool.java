package academy.pocu.comp3500.assignment3;

import java.util.ArrayList;

public class ManualMemoryPool<T> {
    protected int nextIndex;
    protected final ArrayList<T> pool;

    public ManualMemoryPool() {
        this.pool = new ArrayList<T>();
    }

    public final void addPool(final T object) {
        this.pool.add(object);
    }

    private T getNextOrNull() {
        T next = null;
        if (nextIndex < this.pool.size()) {
            next = this.pool.get(nextIndex);
            nextIndex++;
        }

        return next;
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


    public static void init(final ManualMemoryPool<char[][]> manualMemoryPool, final int arrayRowSize, final int arrayColumnSize, final int size) {
        for (int i = 0; i < size; ++i) {
            manualMemoryPool.addPool(new char[arrayRowSize][arrayColumnSize]);
        }
    }


    public static void initCompactMoveList(final ManualMemoryPool<ArrayList<CompactMove>> manualMemoryPool, final int arrayListCapacity, final int size) {
        for (int i = 0; i < size; ++i) {
            manualMemoryPool.addPool(new ArrayList<CompactMove>(arrayListCapacity));
        }
    }

    public static void initScoreMoveList(final ManualMemoryPool<ArrayList<ScoreMove>> manualMemoryPool, final int arrayListCapacity, final int size) {
        for (int i = 0; i < size; ++i) {
            manualMemoryPool.addPool(new ArrayList<ScoreMove>(arrayListCapacity));
        }
    }


    public static char[][] getNext(final ManualMemoryPool<char[][]> manualMemoryPool, final int arrayRowSize, final int arrayColumnSize) {
        char[][] temp = manualMemoryPool.getNextOrNull();
        if (temp == null) {
            manualMemoryPool.addPool(new char[arrayRowSize][arrayColumnSize]);
            temp = manualMemoryPool.getNextOrNull();
        }

        return temp;
    }

    public static ArrayList<CompactMove> getNextCompactMoveList(final ManualMemoryPool<ArrayList<CompactMove>> manualMemoryPool, final int arrayListCapacity) {
        ArrayList<CompactMove> temp = manualMemoryPool.getNextOrNull();
        if (temp == null) {
            manualMemoryPool.addPool(new ArrayList<CompactMove>(arrayListCapacity));
            temp = manualMemoryPool.getNextOrNull();
        }
        temp.clear();
        temp.ensureCapacity(arrayListCapacity);

        return temp;
    }

    public static ArrayList<ScoreMove> getNextScoreMoveList(final ManualMemoryPool<ArrayList<ScoreMove>> manualMemoryPool, final int arrayListCapacity) {
        ArrayList<ScoreMove> temp = manualMemoryPool.getNextOrNull();
        if (temp == null) {
            manualMemoryPool.addPool(new ArrayList<ScoreMove>(arrayListCapacity));
            temp = manualMemoryPool.getNextOrNull();
        }
        temp.clear();
        temp.ensureCapacity(arrayListCapacity);

        return temp;
    }
}
