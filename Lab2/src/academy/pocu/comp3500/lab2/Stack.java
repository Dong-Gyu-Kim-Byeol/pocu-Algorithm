package academy.pocu.comp3500.lab2;

import academy.pocu.comp3500.lab2.datastructure.Node;

public final class Stack {
    private Node root;
    private int size;

    public Stack() {
    }

    public void push(final int data) {
        root = LinkedList.prepend(root, data);
        assert (root != null);
        size++;
    }

    public int peek() {
        Node lastNode = LinkedList.getOrNull(root, 0);
        return lastNode.getData();
    }

    public int pop() {
        Node popNode = LinkedList.getOrNull(root, 0);
        assert (popNode != null);

        root = LinkedList.removeAt(root, 0);
        size--;

        return popNode.getData();
    }

    public int getSize() {
        return size;
    }
}