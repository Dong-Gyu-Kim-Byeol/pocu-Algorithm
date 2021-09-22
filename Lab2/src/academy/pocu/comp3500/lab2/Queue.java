package academy.pocu.comp3500.lab2;

import academy.pocu.comp3500.lab2.datastructure.Node;

public final class Queue {
    private Node front;
    private Node preBack;
    private int size;

    public Queue() {
    }

    public void enqueue(final int data) {
        Node newNode = new Node(data);
        if (getSize() == 0) {
            assert (preBack == null);

            front = newNode;
        } else if (getSize() == 1) {
            assert (preBack == null);

            front.setNext(newNode);
            preBack = front;
        } else {
            preBack = preBack.getNextOrNull();
            preBack.setNext(newNode);
        }

        size++;
    }

    public int peek() {
        assert (front != null);
        assert (getSize() > 0);

        return front.getData();
    }

    public int dequeue() {
        assert (getSize() > 0);

        int data = front.getData();
        if (getSize() == 1) {
            assert (preBack == null);

            front = null;
        } else if (getSize() == 2) {
            assert (front == preBack);

            front = front.getNextOrNull();
            preBack = null;
        } else {
            front = front.getNextOrNull();
        }

        size--;
        return data;
    }

    public int getSize() {
        return size;
    }
}