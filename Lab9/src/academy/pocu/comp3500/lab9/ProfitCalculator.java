package academy.pocu.comp3500.lab9;

import academy.pocu.comp3500.lab9.data.Task;

public final class ProfitCalculator {
    private ProfitCalculator() {
    }

    // ---

    public static int findMaxProfit(final Task[] tasks, final int[] skillLevels) {
        Sort.radixSort(tasks, Task::getProfit);

        int sum = 0;

        for (final int skillLevel : skillLevels) {
            for (int i = tasks.length - 1; i >= 0; --i) {
                if (skillLevel >= tasks[i].getDifficulty()) {
                    sum += tasks[i].getProfit();
                    break;
                }
            }
        }

        return sum;
    }
}