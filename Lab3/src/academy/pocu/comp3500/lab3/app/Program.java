package academy.pocu.comp3500.lab3.app;

import academy.pocu.comp3500.lab3.MissionControl;

import java.util.ArrayList;

public class Program {

    public static void main(String[] args) {
        {
            final int[] altitudes = new int[]{3};

            final int maxAltitudeTime = MissionControl.findMaxAltitudeTime(altitudes);

            assert (maxAltitudeTime == 0);
        }

        {
            final int[] altitudes = new int[]{3, 4};

            final int maxAltitudeTime = MissionControl.findMaxAltitudeTime(altitudes);

            assert (maxAltitudeTime == 1);
        }

        {
            final int[] altitudes = new int[]{5, 4};

            final int maxAltitudeTime = MissionControl.findMaxAltitudeTime(altitudes);

            assert (maxAltitudeTime == 0);
        }

        {
            final int[] altitudes = new int[]{1, 2, 3, 4, 5};

            final int maxAltitudeTime = MissionControl.findMaxAltitudeTime(altitudes);

            assert (maxAltitudeTime == 4);
        }

        {
            final int[] altitudes = new int[]{5, 4, 3, 2, 1};

            final int maxAltitudeTime = MissionControl.findMaxAltitudeTime(altitudes);

            assert (maxAltitudeTime == 0);
        }

        {
            final int[] altitudes = new int[]{1, 2, 3, 4, 5, 6, 7, 4, 3, 2};

            final int maxAltitudeTime = MissionControl.findMaxAltitudeTime(altitudes);

            assert (maxAltitudeTime == 6);
        }

        // ---------------------------------------------------------------------------------------------------------

        {
            final int[] altitudes = new int[]{3};

            ArrayList<Integer> bounds = MissionControl.findAltitudeTimes(altitudes, 2);

            assert (bounds.size() == 0);

            bounds = MissionControl.findAltitudeTimes(altitudes, 3);

            assert (bounds.size() == 1);
            assert (bounds.get(0) == 0);
        }

        {
            final int[] altitudes = new int[]{3, 4};

            ArrayList<Integer> bounds = MissionControl.findAltitudeTimes(altitudes, 2);

            assert (bounds.size() == 0);

            bounds = MissionControl.findAltitudeTimes(altitudes, 3);

            assert (bounds.size() == 1);
            assert (bounds.get(0) == 0);
        }

        {
            final int[] altitudes = new int[]{3, 4};

            ArrayList<Integer> bounds = MissionControl.findAltitudeTimes(altitudes, 2);

            assert (bounds.size() == 0);

            bounds = MissionControl.findAltitudeTimes(altitudes, 4);

            assert (bounds.size() == 1);
            assert (bounds.get(0) == 1);
        }

        {
            final int[] altitudes = new int[]{4, 3};

            ArrayList<Integer> bounds = MissionControl.findAltitudeTimes(altitudes, 2);

            assert (bounds.size() == 0);

            bounds = MissionControl.findAltitudeTimes(altitudes, 4);

            assert (bounds.size() == 1);
            assert (bounds.get(0) == 0);
        }

        {
            final int[] altitudes = new int[]{4, 3};

            ArrayList<Integer> bounds = MissionControl.findAltitudeTimes(altitudes, 2);

            assert (bounds.size() == 0);

            bounds = MissionControl.findAltitudeTimes(altitudes, 3);

            assert (bounds.size() == 1);
            assert (bounds.get(0) == 1);
        }

        {
            final int[] altitudes = new int[]{1, 2, 3, 5, 6, 7};

            ArrayList<Integer> bounds = MissionControl.findAltitudeTimes(altitudes, 4);

            assert (bounds.size() == 0);

            bounds = MissionControl.findAltitudeTimes(altitudes, 6);

            assert (bounds.size() == 1);
            assert (bounds.get(0) == 4);

            bounds = MissionControl.findAltitudeTimes(altitudes, 7);

            assert (bounds.size() == 1);
            assert (bounds.get(0) == 5);

            bounds = MissionControl.findAltitudeTimes(altitudes, 2);

            assert (bounds.size() == 1);
            assert (bounds.get(0) == 1);

            bounds = MissionControl.findAltitudeTimes(altitudes, 7);

            assert (bounds.size() == 1);
            assert (bounds.get(0) == 5);
        }

        {
            final int[] altitudes = new int[]{1, 2, 3, 4, 5, 6, 7, 4, 3, 2};

            ArrayList<Integer> bounds = MissionControl.findAltitudeTimes(altitudes, 2);

            assert (bounds.size() == 2);

            assert (bounds.get(0) == 1);
            assert (bounds.get(1) == 9);

            bounds = MissionControl.findAltitudeTimes(altitudes, 5);

            assert (bounds.size() == 1);
            assert (bounds.get(0) == 4);
        }
    }
}