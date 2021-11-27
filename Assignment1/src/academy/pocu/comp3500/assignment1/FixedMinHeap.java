package academy.pocu.comp3500.assignment1;

import java.util.Comparator;

public final class FixedMinHeap<T> {
    private static final boolean IS_MIN_HEAP = true;

    // ---

    private final T[] array;
    private final Comparator<T> comparator;
    private int size;

    // ---

    @SuppressWarnings("unchecked")
    public FixedMinHeap(final int capacity, final Comparator<T> comparator) {
        this.array = (T[]) new Object[capacity];
        this.comparator = comparator;
    }

    public FixedMinHeap(final T[] array, final int size, final Comparator<T> comparator) {
        this.array = array;
        this.comparator = comparator;
        this.size = size;

        HeapOperation.buildHeap(this.array, this.size, IS_MIN_HEAP, this.comparator);
    }

    // ---

    public final int size() {
        return this.size;
    }

    public final boolean isEmpty() {
        return this.size == 0;
    }

    public final int capacity() {
        return array.length;
    }

    public final void insert(final T data) {
        assert (this.size < this.capacity());

        ++this.size;
        this.array[this.size - 1] = data;

        HeapOperation.siftUp(this.array, 0, this.size - 1, IS_MIN_HEAP, this.comparator);
    }

    public final T extract() {
        assert (this.size > 0);
        final T extract = this.array[0];

        this.array[0] = this.array[this.size - 1];
        this.array[this.size - 1] = null;
        --this.size;

        HeapOperation.siftDown(this.array, 0, this.size - 1, IS_MIN_HEAP, this.comparator);

        return extract;
    }

    public final T peek() {
        assert (this.size > 0);
        return this.array[0];
    }
}
