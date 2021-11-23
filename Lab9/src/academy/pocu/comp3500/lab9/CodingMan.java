package academy.pocu.comp3500.lab9;

import academy.pocu.comp3500.lab9.data.VideoClip;

public final class CodingMan {
    private CodingMan() {
    }

    // ---

    public static int findMinClipsCount(final VideoClip[] clips, final int time) {
        assert (time > 0);

        if (clips.length == 0) {
            return -1;
        }

        Sort.radixSort(clips, VideoClip::getEndTime);
        Sort.reverse(clips);

        int useCount = 1;
        int pre = 0;

        if (clips[pre].getStartTime() == 0) {
            return useCount;
        }

        while (true) {
            final int next = getNextMinStartTimeClipIndexOrMinusOne(clips, pre);
            if (next == -1) {
                break;
            }

            if (clips[next].getEndTime() >= time) {
                useCount = 0;
            }

            ++useCount;

            if (clips[next].getStartTime() == 0) {
                return useCount;
            }

            pre = next;
        }


        return -1;
    }

    // ---

    private static int getNextMinStartTimeClipIndexOrMinusOne(final VideoClip[] clips, final int nowClipIndex) {
        assert (nowClipIndex < clips.length);

        int minStartTimeClip = -1;
        int minStartTime = Integer.MAX_VALUE;

        for (int i = nowClipIndex + 1; i < clips.length; ++i) {
            if (clips[nowClipIndex].getStartTime() <= clips[i].getEndTime()) {
                if (minStartTime > clips[i].getStartTime()) {
                    minStartTime = clips[i].getStartTime();
                    minStartTimeClip = i;

                    if (minStartTime == 0) {
                        return minStartTimeClip;
                    }
                }
            } else {
                break;
            }
        }

        return minStartTimeClip;
    }
}