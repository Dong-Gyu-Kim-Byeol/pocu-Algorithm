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
            final int next = getMaxCheckCountClipIndexOrMinusOne(isChecked, checkCount, clips, pre);
            if (next == -1) {
                break;
            }

            if (clips[next].getEndTime() >= time) {
                uncheckClip(isChecked, clips[next]);

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

    private static int getMaxCheckCountClipIndexOrMinusOne(final boolean[] isChecked, final int nowCheckCount, final VideoClip[] clips, final int start) {

        int maxNewCheckCountClip = -1;
        int maxNewCheckCount = Integer.MIN_VALUE;

        for (int i = start + 1; i < clips.length; ++i) {
            if (clips[start].getStartTime() <= clips[i].getEndTime()) {
                int newCheckCount = 0;

                final int min = clips[i].getStartTime();
                final int max = Math.min(clips[i].getEndTime(), isChecked.length - 1);

                for (int k = min; k <= max; ++k) {
                    if (isChecked[k] == false) {
                        ++newCheckCount;
                    }
                }

                if (maxNewCheckCount < newCheckCount) {
                    maxNewCheckCount = newCheckCount;
                    maxNewCheckCountClip = i;

                    if (nowCheckCount + maxNewCheckCount == isChecked.length) {
                        return maxNewCheckCountClip;
                    }
                }
            } else {
                break;
            }
        }

        return maxNewCheckCountClip;
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

    private static int getInterval(final VideoClip videoClip) {
        return videoClip.getEndTime() - videoClip.getStartTime();
    }

}