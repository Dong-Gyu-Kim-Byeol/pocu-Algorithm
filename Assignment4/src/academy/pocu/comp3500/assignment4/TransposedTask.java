package academy.pocu.comp3500.assignment4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransposedTask {
    private final String title;
    private final ArrayList<TransposedTask> next = new ArrayList<>(64);
    private int estimate;

    public TransposedTask(final String title, final int estimate) {
        this.title = title;
        this.estimate = estimate;
    }

    public String getTitle() {
        return this.title;
    }

    public void addNext(final TransposedTask task) {
        this.next.add(task);
    }

    public void addNext(final TransposedTask... tasks) {
        for (TransposedTask task : tasks) {
            addNext(task);
        }
    }

    public List<TransposedTask> getNext() {
        return Collections.unmodifiableList(this.next);
    }

    public int getEstimate() {
        return this.estimate;
    }

    @Override
    public final String toString() {
        return String.format("TransposedTask  %s : %d", this.getTitle(), this.estimate);
    }
}
