package academy.pocu.comp3500.lab9;

public final class PyramidBuilder {
    public static int findMaxHeight(final int[] widths, final int statue) {
        Sort.radixSort(widths);

        int level = 0;
        int usedCount = 0;

        int preUsedCount = 1;
        int preLevelWidth = statue;

        int nowUsedCount = 0;
        int nowLevelWidth = 0;

        force_break:
        while (true) {
            while (preLevelWidth >= nowLevelWidth || preUsedCount >= nowUsedCount) {
                if (usedCount >= widths.length) {
                    break force_break;
                }

                nowLevelWidth += widths[usedCount++];
                ++nowUsedCount;
            }

            ++level;

            preUsedCount = nowUsedCount;
            preLevelWidth = nowLevelWidth;

            nowUsedCount = 0;
            nowLevelWidth = 0;
        }

        return level;
    }
}