package academy.pocu.comp3500.assignment1;

import java.util.Comparator;

public final class HeapOperation {
    private HeapOperation() {
    }

    // ---

    public static <T> T extractAndInsert(final T insert, final T[] array, final int size, final boolean isMinHeap, final Comparator<T> comparator) {
        final T extract = array[0];

        array[0] = insert;
        siftDown(array, 0, size - 1, isMinHeap, comparator);

        return extract;
    }

    public static <T> void buildHeap(final T[] array, final int size, final boolean isMinHeap, final Comparator<T> comparator) {
        final int iEnd = size - 1;

        int iStart = HeapOperation.iParent(iEnd);
        while (iStart >= 0) {
            siftDown(array, iStart, iEnd, isMinHeap, comparator);
            --iStart;
        }
    }

    public static <T> void siftDown(final T[] array, final int iStart, final int iEnd, final boolean isMinHeap, final Comparator<T> comparator) {
        int iRoot = iStart;

        while (iLeftChild(iRoot) <= iEnd) {
            final int iLeft = iLeftChild(iRoot);
            final int iRight = iRightChild(iRoot);

            int iSwap = iRoot;

            {
                final int compare = comparator.compare(array[iSwap], array[iLeft]);
                if (compare < 0 ^ isMinHeap) {
                    iSwap = compare == 0 ? iSwap : iLeft;
                }
            }

            if (iRight <= iEnd) {
                final int compare = comparator.compare(array[iSwap], array[iRight]);
                if (compare < 0 ^ isMinHeap) {
                    iSwap = compare == 0 ? iSwap : iRight;
                }
            }

            if (iRoot == iSwap) {
                return;
            } else {
                swap(array, iRoot, iSwap);
                iRoot = iSwap;
            }
        }
    }

    public static <T> void siftUp(final T[] array, final int iStart, final int iEnd, final boolean isMinHeap, final Comparator<T> comparator) {
        int iChild = iEnd;

        while (iChild > iStart) {
            final T child = array[iChild];

            final int iParent = iParent(iChild);
            assert (iParent >= 0);
            final T parent = array[iParent];

            final int compare = comparator.compare(parent, child);
            if (compare == 0) {
                return;
            }

            if (compare < 0 ^ isMinHeap) {
                assert (compare > 0 ^ !isMinHeap);
                swap(array, iParent, iChild);
                iChild = iParent;
            } else {
                return;
            }
        }
    }

    public static int iParent(final int iNode) {
        return iNode == 0 ? -1 : (iNode + 1) / 2 - 1;
    }

    public static int iLeftChild(final int iNode) {
        return (iNode + 1) * 2 - 1;
    }

    public static int iRightChild(final int iNode) {
        return (iNode + 1) * 2;
    }

    public static <T> void swap(final T[] array, final int i1, final int i2) {
        Sort.swap(array, i1, i2);
    }
}
