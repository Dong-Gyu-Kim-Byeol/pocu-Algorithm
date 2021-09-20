package academy.pocu.comp3500.lab3;

import java.util.ArrayList;
import java.util.Comparator;

public class Search {
    private Search() {
    }

    public static void binarySearchRecursive(final int targetNum, final int[] nums, final boolean isReverse, final int left, final int right, final ArrayList<Integer> outIndexes) {
        if (left > right) {
            return;
        }

        final int mid = (left + right) / 2;

        if (targetNum == nums[mid]) {
            outIndexes.add(mid);
            return;
        }

        if (targetNum < nums[mid] ^ isReverse) {
            binarySearchRecursive(targetNum, nums, isReverse, left, mid - 1, outIndexes);
        } else { // nums[mid] < targetNum ^ isReverse
            binarySearchRecursive(targetNum, nums, isReverse, mid + 1, right, outIndexes);
        }
    }

    public static <T> void binarySearchRecursive(final T target, final T[] objects, final Comparator<T> comparator, final int left, final int right, final ArrayList<Integer> outIndexes) {
        if (left > right) {
            return;
        }

        final int mid = (left + right) / 2;

        final int compare = comparator.compare(target, objects[mid]);
        if (compare == 0) {
            outIndexes.add(mid);
            return;
        }

        if (compare < 0) {
            binarySearchRecursive(target, objects, comparator, left, mid - 1, outIndexes);
        } else {
            binarySearchRecursive(target, objects, comparator, mid + 1, right, outIndexes);
        }
    }
}
