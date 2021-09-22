package academy.pocu.comp3500.lab2;

import academy.pocu.comp3500.lab2.datastructure.Node;

public final class Stack {
    private Node top;
    private int size;

    public Stack() {
    }

    public void push(final int data) {
        top = LinkedList.prepend(top, data);
        size++;
    }

    public int peek() {
        assert (top != null);
        assert (getSize() > 0);

        return top.getData();
    }

    public int pop() {
        assert (top != null);
        assert (getSize() > 0);

        final int data = top.getData();
        top = top.getNextOrNull();
        size--;
        return data;
    }

    public int getSize() {
        return size;
    }
}