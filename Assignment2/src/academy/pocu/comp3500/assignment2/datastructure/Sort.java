package academy.pocu.comp3500.assignment2.datastructure;

import academy.pocu.comp3500.assignment2.Indent;
import academy.pocu.comp3500.assignment2.Logger;

import static academy.pocu.comp3500.assignment2.Logger.log;

public final class Sort {
    private Sort() {
    }

    public static void quickSort(final int[] nums) {
        log(String.format("%s", getString(nums, 0, nums.length - 1)));
        quickSortRecursive(nums, 0, nums.length - 1, 'L');
    }

    private static void quickSortRecursive(int[] nums, int left, int right, char side) {
        if (left >= right) {
            return;
        }

        int pivotPos = partition(nums, left, right);

        Logger.indent();
        {
            log(String.format("L: %s", getString(nums, left, pivotPos)));

            quickSortRecursive(nums, left, pivotPos - 1, 'L');

            log(String.format("R: %s", getString(nums, pivotPos + 1, right)));

            quickSortRecursive(nums, pivotPos + 1, right, 'R');

            log(String.format("X: %s", getString(nums, left, right)));
        }
        Logger.unindent();
    }

    private static int partition(int[] array, int left, int right) {
        final int pivot = array[right];

        int i = left;
        for (int j = left; j < right; ++j) {
            if (array[j] < pivot) {
                swap(array, i, j);
                ++i;
            }
        }

        int pivotPos = i;
        swap(array, pivotPos, right);

        return pivotPos;
    }

    private static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private static String getString(int[] array, int start, int end) {
        StringBuilder sb = new StringBuilder(50);

        for (int j = start; j <= end; ++j) {
            sb.append(array[j]);
            sb.append(" ");
        }

        String s = sb.toString();
        s = s.trim();

        return s;
    }
}