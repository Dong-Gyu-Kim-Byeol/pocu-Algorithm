package academy.pocu.comp3500.lab7;

import java.util.ArrayList;
import java.util.Comparator;

public final class Sort {
    private Sort() {
    }

    public static <T> void bubbleSort(final T[] objects, final Comparator<T> comparator) {
        for (int i = 0; i < objects.length - 1; ++i) {
            for (int j = 0; j < objects.length - i - 1; ++j) {
                final int compare = comparator.compare(objects[j], objects[j + 1]);
                if (compare > 0) {
                    swap(objects, j, j + 1);
                }
            }
        }
    }

    public static void radixSort(final char[] array, int cycle) {
        final ArrayList<ArrayList<Character>> scratch = new ArrayList<ArrayList<Character>>(10);
        for (int i = 0; i < 10; ++i) {
            scratch.add(new ArrayList<Character>(array.length));
        }
        int div = 1;

        while (cycle > 0) {
            for (final char c : array) {
                final int scratchIndex = (c / div) % 10;
                scratch.get(scratchIndex).add(c);
            }

            int arrayIndex = 0;
            for (int i = 0; i < 10; ++i) {
                final ArrayList<Character> subScratch = scratch.get(i);
                for (final char c : subScratch) {
                    assert (arrayIndex < array.length);
                    array[arrayIndex++] = c;
                }
            }

            for (int i = 0; i < 10; ++i) {
                scratch.get(i).clear();
            }

            div *= 10;
            --cycle;
        }
    }

    public static void radixSort(final int[] array, int cycle) {
        final ArrayList<ArrayList<Integer>> scratch = new ArrayList<ArrayList<Integer>>(10);
        for (int i = 0; i < 10; ++i) {
            scratch.add(new ArrayList<Integer>(array.length));
        }
        int div = 1;

        while (cycle > 0) {
            for (final int num : array) {
                final int scratchIndex = (num / div) % 10;
                scratch.get(scratchIndex).add(num);
            }

            int arrayIndex = 0;
            for (int i = 0; i < 10; ++i) {
                final ArrayList<Integer> subScratch = scratch.get(i);
                for (final int num : subScratch) {
                    assert (arrayIndex < array.length);
                    array[arrayIndex++] = num;
                }
            }

            for (int i = 0; i < 10; ++i) {
                scratch.get(i).clear();
            }

            div *= 10;
            --cycle;
        }
    }

    public static <T> void quickSort(final T[] objects, final Comparator<T> comparator) {
        quickSortRecursive(objects, comparator, 0, objects.length - 1);
    }

    public static void quickSort(final char[] array) {
        quickSortRecursive(array, 0, array.length - 1);
    }

    public static <T> void swap(final T[] objects, final int o1, final int o2) {
        final T temp = objects[o1];
        objects[o1] = objects[o2];
        objects[o2] = temp;
    }

    public static void swap(final char[] array, final int i1, final int i2) {
        final char temp = array[i1];
        array[i1] = array[i2];
        array[i2] = temp;
    }

    public static <T> void quickSortRecursive(final T[] objects, final Comparator<T> comparator, final int left, final int right) {
        if (left >= right) {
            return;
        }

        final int pivotPos = chooseMedianPivotPos(objects, comparator, left, right);
        swap(objects, pivotPos, right);

        final int sortedPivotPos = partition(objects, comparator, left, right);

        quickSortRecursive(objects, comparator, left, sortedPivotPos - 1);
        quickSortRecursive(objects, comparator, sortedPivotPos + 1, right);
    }

    public static void quickSortRecursive(final char[] array, final int left, final int right) {
        if (left >= right) {
            return;
        }

        final int pivotPos = chooseMedianPivotPos(array, left, right);
        swap(array, pivotPos, right);

        final int sortedPivotPos = partition(array, left, right);

        quickSortRecursive(array, left, sortedPivotPos - 1);
        quickSortRecursive(array, sortedPivotPos + 1, right);
    }

    public static <T> T quickSelectRecursive(final int targetIndex, final T[] objects, final Comparator<T> comparator, final int left, final int right) {
        assert (left <= right);

        if (left == right) {
            return objects[left];
        }

        final int pivotPos = chooseMedianPivotPos(objects, comparator, left, right);
        swap(objects, pivotPos, right);

        final int sortedPivotPos = partition(objects, comparator, left, right);

        if (targetIndex == sortedPivotPos) {
            return objects[sortedPivotPos];
        } else if (targetIndex < sortedPivotPos) {
            return quickSelectRecursive(targetIndex, objects, comparator, left, sortedPivotPos - 1);
        } else {
            return quickSelectRecursive(targetIndex, objects, comparator, sortedPivotPos + 1, right);
        }
    }

    public static char quickSelectRecursive(final int targetIndex, final char[] array, final int left, final int right) {
        assert (left <= right);

        if (left == right) {
            return array[left];
        }

        final int pivotPos = chooseMedianPivotPos(array, left, right);
        swap(array, pivotPos, right);

        final int sortedPivotPos = partition(array, left, right);

        if (targetIndex == sortedPivotPos) {
            return array[sortedPivotPos];
        } else if (targetIndex < sortedPivotPos) {
            return quickSelectRecursive(targetIndex, array, left, sortedPivotPos - 1);
        } else {
            return quickSelectRecursive(targetIndex, array, sortedPivotPos + 1, right);
        }
    }

    private static <T> int chooseMedianPivotPos(final T[] objects, final Comparator<T> comparator, final int left, final int right) {
        final int mid = (left + right) / 2;

        if ((comparator.compare(objects[mid], objects[left]) < 0 && comparator.compare(objects[left], objects[right]) < 0)
                || (comparator.compare(objects[right], objects[left]) < 0 && comparator.compare(objects[left], objects[mid]) < 0)) {
            return left;
        } else if ((comparator.compare(objects[left], objects[mid]) < 0 && comparator.compare(objects[mid], objects[right]) < 0)
                || (comparator.compare(objects[right], objects[mid]) < 0 && comparator.compare(objects[mid], objects[left]) < 0)) {
            return mid;
        } else {
            return right;
        }
    }

    private static int chooseMedianPivotPos(final char[] array, final int left, final int right) {
        final int mid = (left + right) / 2;

        if ((array[mid] < array[left] && array[left] < array[right])
                || (array[right] < array[left] && array[left] < array[mid])) {
            return left;
        } else if ((array[left] < array[mid] && array[mid] < array[right])
                || (array[right] < array[mid] && array[mid] < array[left])) {
            return mid;
        } else {
            return right;
        }
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

    private static int partition(final char[] array, final int left, final int right) {
        assert (left < right);

        int pivot = right;

        int pointer = left - 1;
        for (int i = left; i < right; ++i) {
            final int compare = Character.compare(array[i], array[pivot]);
            if (compare < 0) {
                ++pointer;
                swap(array, pointer, i);
            }
        }

        pivot = pointer + 1;
        swap(array, pivot, right);

        return pivot;
    }
}
