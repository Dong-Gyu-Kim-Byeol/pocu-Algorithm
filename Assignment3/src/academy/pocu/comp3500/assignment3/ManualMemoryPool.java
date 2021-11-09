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


    public static char[][] getNext(final ManualMemoryPool<char[][]> manualMemoryPool) {
        char[][] temp = manualMemoryPool.getNextOrNull();
        if (temp == null) {
            manualMemoryPool.addPool(new char[Chess.BOARD_SIZE][Chess.BOARD_SIZE]);
            temp = manualMemoryPool.getNextOrNull();
        }

        return temp;
    }

    public static ArrayList<CompactMove> getNextCompactMoveList(final ManualMemoryPool<ArrayList<CompactMove>> manualMemoryPool, final int capacity) {
        ArrayList<CompactMove> temp = manualMemoryPool.getNextOrNull();
        if (temp == null) {
            manualMemoryPool.addPool(new ArrayList<CompactMove>(capacity));
            temp = manualMemoryPool.getNextOrNull();
        }
        temp.clear();
        temp.ensureCapacity(capacity);

        return temp;
    }

    public static ArrayList<ScoreMove> getNextScoreMoveList(final ManualMemoryPool<ArrayList<ScoreMove>> manualMemoryPool, final int capacity) {
        ArrayList<ScoreMove> temp = manualMemoryPool.getNextOrNull();
        if (temp == null) {
            manualMemoryPool.addPool(new ArrayList<ScoreMove>(capacity));
            temp = manualMemoryPool.getNextOrNull();
        }
        temp.clear();
        temp.ensureCapacity(capacity);

        return temp;
    }
}
