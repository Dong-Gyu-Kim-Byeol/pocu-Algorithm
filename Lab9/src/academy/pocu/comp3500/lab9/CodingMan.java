package academy.pocu.comp3500.lab9;

import academy.pocu.comp3500.lab9.data.VideoClip;

public final class CodingMan {
    public static int findMinClipsCount(final VideoClip[] clips, final int time) {
        Sort.radixSort(clips, CodingMan::getInterval);
        final int mid = clips.length / 2;
        for (int i = 0; i < mid; ++i) {
            Sort.swap(clips, i, clips.length - 1 - i);
        }

        final boolean[] isChecked = new boolean[time];
        int checkCount = 0;

        int useCount = 0;

        for (final VideoClip clip : clips) {
            final int min = clip.getStartTime();
            final int max = Math.min(clip.getEndTime(), isChecked.length - 1);

            boolean isUsed = false;

            for (int i = min; i <= max; ++i) {
                if (isChecked[i] == false) {
                    isChecked[i] = true;

                    ++checkCount;
                    isUsed = true;
                }
            }

            if (isUsed) {
                ++useCount;
            }

            if (checkCount == time) {
                return useCount;
            }
        }

        return -1;
    }

    // ---

    private static int getInterval(final VideoClip videoClip) {
        return videoClip.getEndTime() - videoClip.getStartTime();
    }

}