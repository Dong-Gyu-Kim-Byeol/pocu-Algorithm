package academy.pocu.comp3500.assignment1;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Supplier;

public class Sort {
    private Sort() {
    }

    public static <T> void bubbleSort(final T[] objects, final Comparator<T> getter) {
        for (int i = 0; i < objects.length - 1; ++i) {
            for (int j = 0; j < objects.length - i - 1; ++j) {
                final int compare = getter.compare(objects[j], objects[j + 1]);
                if (compare > 0) {
                    swap(objects, j, j + 1);
                }
            }
        }
    }

    public static <T> void quickSort(final T[] objects, final Comparator<T> comparator) {
        quickSortRecursive(objects, comparator, 0, objects.length - 1);
    }

    public static <T> void swap(final T[] objects, final int o1, final int o2) {
        final T temp = objects[o1];
        objects[o1] = objects[o2];
        objects[o2] = temp;
    }

    public static <T> void quickSortRecursive(final T[] objects, final Comparator<T> comparator, final int left, final int right) {
        if (left >= right) {
            return;
        }

        final int pivotPos = partition(objects, comparator, left, right);

        quickSortRecursive(objects, comparator, left, pivotPos - 1);
        quickSortRecursive(objects, comparator, pivotPos + 1, right);
    }

    private static <T> int partition(final T[] objects, final Comparator<T> comparator, final int left, final int right) {
        assert (left < right);

        int pivot = right;

        int pointer = left - 1;
        for (int i = left; i < right; ++i) {
            final int compare = comparator.compare(objects[i], objects[pivot]);
            if (compare < 0) {
                ++pointer;
                swap(objects, pointer, i);
            }
        }

        pivot = pointer + 1;
        swap(objects, pivot, right);

        return pivot;
    }
}
