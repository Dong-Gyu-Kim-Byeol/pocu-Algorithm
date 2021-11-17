package academy.pocu.comp3500.lab9.data;

public final class Task {
    private final int difficulty;
    private final int profit;

    public Task(int difficulty, int profit) {
        this.difficulty = difficulty;
        this.profit = profit;
    }

    public final int getDifficulty() {
        return difficulty;
    }

    public final int getProfit() {
        return profit;
    }
}
