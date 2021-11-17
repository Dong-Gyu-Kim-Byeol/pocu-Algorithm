package academy.pocu.comp3500.lab9;

import academy.pocu.comp3500.lab9.data.VideoClip;

public final class CodingMan {
    public static int findMinClipsCount(final VideoClip[] clips, final int time) {
        Sort.radixSort(clips, VideoClip::getEndTime);
        Sort.radixSort(clips, VideoClip::getStartTime);

        int clipIndex = 0;
        int useCount = 0;
        int nowEndTime = 0;

        while (true) {
            if (nowEndTime >= time) {
                return useCount;
            }

            if (clipIndex >= clips.length) {
                return -1;
            }

            if (nowEndTime < clips[clipIndex].getStartTime()) {
                return -1;
            }


            final int nowStartTime = clips[clipIndex].getStartTime();
            while (nowStartTime == clips[clipIndex].getStartTime()) {
                nowEndTime = clips[clipIndex].getEndTime();

                ++clipIndex;
                if (clipIndex >= clips.length) {
                    break;
                }
            }

            ++useCount;
        }
    }
}