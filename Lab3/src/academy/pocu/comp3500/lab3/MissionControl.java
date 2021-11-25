package academy.pocu.comp3500.lab3;

import java.util.ArrayList;

public final class MissionControl {
    private MissionControl() {
    }

    public static int findMaxAltitudeTime(final int[] altitudes) {

        return findMaxAltitudeTimeRecursive(altitudes, 0, altitudes.length - 1);
    }

    private static int findMaxAltitudeTimeRecursive(final int[] altitudes, final int left, final int right) {
        final int mid = (left + right) / 2;

        if (altitudes.length == 1) {
            return 0;
        }

        if (left > right) {
            return -1;
        }


        if (mid == 0 && altitudes[mid] < altitudes[mid + 1]) {
            return findMaxAltitudeTimeRecursive(altitudes, mid + 1, right);
        } else if (mid == 0 && altitudes[mid] > altitudes[mid + 1]) {
            return mid;
        }

        if (mid == altitudes.length - 1 && altitudes[mid - 1] < altitudes[mid]) {
            return mid;
        } else if (mid == altitudes.length - 1 && altitudes[mid - 1] > altitudes[mid]) {
            return findMaxAltitudeTimeRecursive(altitudes, left, mid - 1);
        }


        if (altitudes[mid - 1] < altitudes[mid] && altitudes[mid] > altitudes[mid + 1]) {
            return mid;
        }

        if (altitudes[mid - 1] < altitudes[mid] && altitudes[mid] < altitudes[mid + 1]) {
            return findMaxAltitudeTimeRecursive(altitudes, mid + 1, right);
        }

        if (altitudes[mid - 1] > altitudes[mid] && altitudes[mid] > altitudes[mid + 1]) {
            return findMaxAltitudeTimeRecursive(altitudes, left, mid - 1);
        }

        assert (false);
        return -1;
    }

    public static ArrayList<Integer> findAltitudeTimes(final int[] altitudes, final int targetAltitude) {
        ArrayList<Integer> outTimes = new ArrayList<Integer>(altitudes.length / 2);

        final int maxAltitudeTime = findMaxAltitudeTime(altitudes);

        Search.binarySearchRecursive(targetAltitude, altitudes, false, 0, maxAltitudeTime, outTimes);
        Search.binarySearchRecursive(targetAltitude, altitudes, true, maxAltitudeTime + 1, altitudes.length - 1, outTimes);
        return outTimes;
    }


}