package academy.pocu.comp3500.assignment1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Function;

public class Search {
    private Search() {
    }

    // ---

    public static void binarySearch(final int targetNum, final int[] nums, final ArrayList<Integer> outIndexes) {
        binarySearchRecursive(targetNum, nums, false, 0, nums.length - 1, outIndexes);
    }

    public static void binarySearchReverse(final int targetNum, final int[] nums, final ArrayList<Integer> outIndexes) {
        binarySearchRecursive(targetNum, nums, true, 0, nums.length - 1, outIndexes);
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

    public static <T> void binarySearch(final T target, final T[] objects, final Comparator<T> comparator, final ArrayList<Integer> outIndexes) {
        binarySearchRecursive(target, objects, comparator, false, 0, objects.length - 1, outIndexes);
    }

    public static <T> void binarySearchReverse(final T target, final T[] objects, final Comparator<T> comparator, final ArrayList<Integer> outIndexes) {
        binarySearchRecursive(target, objects, comparator, false, 0, objects.length - 1, outIndexes);
    }

    public static <T> void binarySearchRecursive(final T target, final T[] objects, final Comparator<T> comparator, final boolean isReverse, final int left, final int right, final ArrayList<Integer> outIndexes) {
        if (left > right) {
            return;
        }

        final int mid = (left + right) / 2;

        final int compare = comparator.compare(target, objects[mid]);
        if (compare == 0) {
            outIndexes.add(mid);
            return;
        }

        if (compare < 0 ^ isReverse) {
            binarySearchRecursive(target, objects, comparator, isReverse, left, mid - 1, outIndexes);
        } else { // objects[mid] < target ^ isReverse
            binarySearchRecursive(target, objects, comparator, isReverse, mid + 1, right, outIndexes);
        }
    }

    public static <T> T binarySearchFindAndNear(final int target, final T[] objects, final Function<T, Integer> function, final boolean isSmall) {
        int left = 0;
        int right = objects.length - 1;

        int targetIndex = -1;
        int minDifference = Integer.MAX_VALUE;

        while (left <= right) {
            final int mid = (left + right) / 2;

            final int difference = Math.abs(function.apply(objects[mid]) - target);

            if (difference == 0) {
                minDifference = difference;
                targetIndex = mid;

                return objects[targetIndex];
            }

            if (minDifference == difference) {
                if (function.apply(objects[targetIndex]) < function.apply(objects[mid]) ^ isSmall) {
                    targetIndex = mid;
                }
            }

            if (minDifference > difference) {
                minDifference = difference;
                targetIndex = mid;
            }

            if (target < function.apply(objects[mid])) {
                right = mid - 1;
            } else { // function.apply(objects[mid]) < target
                left = mid + 1;
            }
        }

        return objects[targetIndex];
    }
}
