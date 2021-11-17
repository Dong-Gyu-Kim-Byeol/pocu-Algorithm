package academy.pocu.comp3500.lab9;

import academy.pocu.comp3500.lab9.data.VideoClip;

public final class CodingMan {
    public static int findMinClipsCount(final VideoClip[] clips, final int time) {
        Sort.radixSort(clips, VideoClip::getEndTime);
        Sort.radixSort(clips, VideoClip::getStartTime);

        final int clipsSize;
        {
            int write = 0;
            for (int i = 0; i < clips.length - 1; ++i) {
                if (clips[i].getStartTime() != clips[i + 1].getStartTime()) {
                    clips[write] = clips[i];
                    ++write;
                }
            }

            clips[write] = clips[clips.length - 1];
            ++write;

            clipsSize = write;
        }

        int clipIndex = 0;
        int useCount = 0;
        int nowEndTime = 0;

        while (true) {
            if (nowEndTime >= time) {
                return useCount;
            }

            if (clipIndex >= clipsSize) {
                return -1;
            }

            if (nowEndTime < clips[clipIndex].getStartTime()) {
                return -1;
            }

            while (true) {
                if (++clipIndex >= clipsSize) {
                    clipIndex = clipsSize - 1;
                    break;
                }

                if (nowEndTime < clips[clipIndex].getStartTime()) {
                    --clipIndex;
                    break;
                }
            }

            nowEndTime = clips[clipIndex++].getEndTime();
            ++useCount;
        }
    }
}