package academy.pocu.comp3500.lab2;

import academy.pocu.comp3500.lab2.datastructure.Node;

public final class Queue {
    private Node root;
    private int backIndex;

    public Queue() {
        backIndex = -1;
    }

    public void enqueue(final int data) {
        root = LinkedList.append(root, data);
        ++backIndex;
    }

    public int peek() {
        return root.getData();
    }

    public int dequeue() {
        int data = root.getData();

        root = LinkedList.removeAt(root, 0);
        --backIndex;

        return data;
    }

    public int getSize() {
        return backIndex + 1;
    }
}