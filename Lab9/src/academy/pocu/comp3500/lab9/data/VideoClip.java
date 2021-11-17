package academy.pocu.comp3500.lab9.data;

public final class VideoClip {
    private final int startTime;
    private final int endTime;

    public VideoClip(int start, int end) {
        startTime = start;
        endTime = end;
    }

    public final int getStartTime() {
        return startTime;
    }

    public final int getEndTime() {
        return endTime;
    }
}
