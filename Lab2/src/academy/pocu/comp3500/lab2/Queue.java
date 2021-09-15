package academy.pocu.comp3500.lab2;

import academy.pocu.comp3500.lab2.datastructure.Node;

public final class Queue {
    private Node front;
    private Node preBack;
    private Node back;
    private int size;

    public Queue() {
    }

    public void enqueue(final int data) {
        if (front == null) {
            front = new Node(data);
            back = front;
        } else if (front == back) {
            back.setNext(new Node(data));
            back = back.getNextOrNull();
        } else {
            preBack = back;
            back.setNext(new Node(data));
            back = back.getNextOrNull();
        }

        size++;
    }

    public int peek() {
        return front.getData();
    }

    public int dequeue() {
        int data;

        if (preBack == null) {
            data = front.getData();
            if (front.getNextOrNull() == back) {
                front = back;
            } else {
                assert (front == back);
                front = null;
                back = null;
            }
        } else {
            data = front.getData();
            front = front.getNextOrNull();
            assert (front != null);

            if (front == preBack) {
                preBack = null;
            }
        }

        size--;
        return data;
    }

    public int getSize() {
        return size;
    }
}