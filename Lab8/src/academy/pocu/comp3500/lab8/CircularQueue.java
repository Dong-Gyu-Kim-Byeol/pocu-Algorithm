package academy.pocu.comp3500.lab8;

public class CircularQueue<T> {
    private static final int CAPACITY_INCREASE_RATE = 2;

    private int size;
    private int front;
    private int rear;

    private Object[] array;

    public CircularQueue(final int capacity) {
        assert (capacity > 0);
        this.array = new Object[capacity + 1];
    }

    public int size() {
        assert (this.capacity() >= this.size);

        return this.size;
    }

    public int capacity() {
        return this.array.length - 1;
    }

    public void enqueue(final T data) {
        assert (this.capacity() >= this.size);
        assert (this.array[this.front] == null);

        if ((this.rear + 1) % this.array.length == this.front) {
            final int newArrayCapacity = this.capacity() * CAPACITY_INCREASE_RATE + 1;
            final Object[] newArray = new Object[newArrayCapacity];

            int nowArrayIndex = (this.front + 1) % this.array.length;
            for (int i = 0; i < newArray.length; ++i) {
                if (this.array[nowArrayIndex] == null) {
                    assert (i != 0);
                    this.rear = i - 1;
                    break;
                }

                newArray[i] = this.array[nowArrayIndex];
                nowArrayIndex = (nowArrayIndex + 1) % this.array.length;
            }

            this.array = newArray;
            this.front = newArray.length - 1;
        }

        this.rear = (this.rear + 1) % this.array.length;
        assert (this.array[this.rear] == null);
        this.array[this.rear] = data;

        ++this.size;
        assert (this.size <= this.capacity());
    }

    @SuppressWarnings("unchecked")
    public T dequeue() {
        assert (this.capacity() >= this.size);
        assert (this.array[this.front] == null);

        assert (this.size != 0);
        assert (this.rear != this.front);

        assert (this.size > 0);

        this.front = (this.front + 1) % this.array.length;
        final T data = (T) this.array[this.front];
        this.array[this.front] = null;

        --this.size;

        return data;
    }

    public void clear() {
        int nowArrayIndex = (this.front + 1) % this.array.length;
        for (int i = 0; i < this.array.length; ++i) {
            if (this.array[nowArrayIndex] == null) {
                break;
            }

            this.array[nowArrayIndex] = null;
            nowArrayIndex = (nowArrayIndex + 1) % this.array.length;
        }

        this.size = 0;
        this.front = 0;
        this.rear = 0;
    }
}
