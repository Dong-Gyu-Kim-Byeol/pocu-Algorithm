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
        final int mid = clips.length / 2;
        for (int i = 0; i < mid; ++i) {
            Sort.swap(clips, i, clips.length - 1 - i);
        }

        final boolean[] isChecked = new boolean[time];
        int checkCount = 0;

        int useCount = 0;
        int pre = 0;

        {
            checkCount += checkClip(isChecked, clips[pre]);
            ++useCount;

            if (checkCount == time) {
                return useCount;
            }
        }

        while (true) {
            final int next = getNextMinStartTimeClipIndexOrMinusOne(clips, pre);
            if (next == -1) {
                break;
            }

            if (clips[next].getEndTime() >= time) {
                uncheckClip(isChecked, clips[pre]);

                checkCount = 0;
                useCount = 0;
            }

            final int addCount = checkClip(isChecked, clips[next]);
            if (addCount == 0) {
                break;
            }

            checkCount += addCount;
            ++useCount;

            if (checkCount == time) {
                return useCount;
            }

            pre = next;
        }


        return -1;
    }

    // ---

    private static int getNextMinStartTimeClipIndexOrMinusOne(final VideoClip[] clips, final int nowClipIndex) {

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

    private static int checkClip(final boolean[] isChecked, final VideoClip videoClip) {
        final int min = videoClip.getStartTime();
        final int max = Math.min(videoClip.getEndTime(), isChecked.length - 1);

        int checkCount = 0;

        for (int i = min; i <= max; ++i) {
            if (isChecked[i] == false) {
                isChecked[i] = true;
                ++checkCount;
            }
        }

        return checkCount;
    }

    private static void uncheckClip(final boolean[] isChecked, final VideoClip videoClip) {
        final int min = videoClip.getStartTime();
        final int max = Math.min(videoClip.getEndTime(), isChecked.length - 1);

        for (int i = min; i <= max; ++i) {
            isChecked[i] = false;
        }
    }
}