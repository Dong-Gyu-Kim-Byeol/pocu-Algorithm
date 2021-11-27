package academy.pocu.comp3500.assignment4;


public final class HashSet<T> {
    private static final int CAPACITY_INCREASE_RATE = 3;

    // ---

    private HashSetNode<T>[] array;
    private int size;

    private final byte[] bytes;

    // ---

    @SuppressWarnings("unchecked")
    public HashSet(final int capacity) {
        this.array = (HashSetNode<T>[]) new HashSetNode[capacity * CAPACITY_INCREASE_RATE];
        this.bytes = new byte[4];
    }

    // ---

    public final boolean isEmpty() {
        assert (this.size >= 0);
        assert (this.size <= this.capacity());

        return this.size == 0;
    }

    public final int size() {
        assert (this.size >= 0);
        assert (this.size <= this.capacity());

        return this.size;
    }

    public final int capacity() {
        assert (this.size >= 0);
        assert (this.size <= this.array.length / CAPACITY_INCREASE_RATE);

        return this.array.length / CAPACITY_INCREASE_RATE;
    }

    public final void add(final T data) {
        assert (this.size >= 0);
        assert (this.size <= this.capacity());

        if (this.contains(data)) {
            return;
        }

        if (this.size >= this.capacity()) {
            this.rehashingAndIncreaseCapacity();
        }

        final int i = this.calculateIndex(data);
        final HashSetNode<T> dataNode = new HashSetNode<>(data);
        ++this.size;
        assert (this.size >= 0);
        assert (this.size <= this.capacity());

        if (this.array[i] == null) {
            this.array[i] = dataNode;
            return;
        }

        HashSetNode<T> pre = null;
        HashSetNode<T> node = this.array[i];
        while (node != null) {
            if (node.getData().equals(data)) {
                return;
            }

            pre = node;
            node = node.getNextOrNull();
        }

        assert (pre != null);
        pre.setNext(dataNode);
    }

    public final void remove(final T data) {
        assert (this.size >= 0);
        assert (this.size <= this.capacity());

        final int i = this.calculateIndex(data);

        if (this.array[i] == null) {
            return;
        }

        HashSetNode<T> pre = null;
        HashSetNode<T> node = this.array[i];
        while (node != null) {
            if (!node.getData().equals(data)) {
                pre = node;
                node = node.getNextOrNull();
                continue;
            }

            --this.size;
            assert (this.size >= 0);
            assert (this.size <= this.capacity());

            if (pre == null) {
                this.array[i] = node.getNextOrNull();
                node.setNext(null);
                return;
            }

            pre.setNext(node.getNextOrNull());
            node.setNext(null);
            return;
        }
    }

    public final boolean contains(final T data) {
        assert (this.size >= 0);
        assert (this.size <= this.capacity());

        final int i = this.calculateIndex(data);

        if (this.array[i] == null) {
            return false;
        }

        HashSetNode<T> node = this.array[i];
        while (node != null) {
            if (node.getData().equals(data)) {
                return true;
            }

            node = node.getNextOrNull();
        }

        return false;
    }

    // ---

    private void setBytes(final T data) {
        int h = data.hashCode();
        for (int i = 3; i >= 0; --i) {
            this.bytes[i] = (byte) h;
            h = h >>> 8;
        }
    }

    private int calculateIndex(final T data) {
        setBytes(data);
        final long h = Hash.fnv1a(this.bytes);
        final int ih = Math.abs((int) h);
        final int i = ih % this.array.length;
        assert (i >= 0);
        return i;
    }

    private void rehashingAndIncreaseCapacity() {
        final HashSet<T> newSet = new HashSet<>(this.capacity() * CAPACITY_INCREASE_RATE);
        for (HashSetNode<T> node : this.array) {
            if (node == null) {
                continue;
            }

            while (node != null) {
                newSet.add(node.getData());
                node = node.getNextOrNull();
            }
        }

        this.array = newSet.array;
    }
}
