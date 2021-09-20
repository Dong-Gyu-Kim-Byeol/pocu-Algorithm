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

        if (mid == 0 || mid == altitudes.length - 1) {
            return -1;
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
        findAltitudeTimesRecursive(targetAltitude, altitudes, 0, altitudes.length - 1, outTimes, true, true);
        return outTimes;
    }

    private static void findAltitudeTimesRecursive(final int targetAltitude, final int[] altitudes, final int left, final int right, final ArrayList<Integer> outTimes, final boolean isFindLeft, final boolean isFindRight) {
        final int mid = (left + right) / 2;

        if (altitudes[mid] == targetAltitude) {
            outTimes.add(mid);
            return;
        }

        if (mid == 0 || mid == altitudes.length - 1) {
            return;
        }

        if (altitudes[mid - 1] < altitudes[mid] && altitudes[mid] > altitudes[mid + 1]) {
            if (isFindLeft) {
                findAltitudeTimesRecursive(targetAltitude, altitudes, left, mid - 1, outTimes, true, false);
            }

            if (isFindRight) {
                findAltitudeTimesRecursive(targetAltitude, altitudes, mid + 1, right, outTimes, false, true);
            }
        } else if (altitudes[mid - 1] < altitudes[mid] && altitudes[mid] < altitudes[mid + 1]) {

            if (targetAltitude < altitudes[mid]) {
                if (isFindLeft) {
                    findAltitudeTimesRecursive(targetAltitude, altitudes, left, mid - 1, outTimes, true, false);
                }
            }

            if (isFindRight) {
                findAltitudeTimesRecursive(targetAltitude, altitudes, mid + 1, right, outTimes, false, true);
            }
        } else if (altitudes[mid - 1] > altitudes[mid] && altitudes[mid] > altitudes[mid + 1]) {
            if (targetAltitude > altitudes[mid]) {
                if (isFindLeft) {
                    findAltitudeTimesRecursive(targetAltitude, altitudes, left, mid - 1, outTimes, true, false);
                }
            }

            if (isFindRight) {
                findAltitudeTimesRecursive(targetAltitude, altitudes, mid + 1, right, outTimes, false, true);
            }
        }

        assert (false);
        return;
    }
}