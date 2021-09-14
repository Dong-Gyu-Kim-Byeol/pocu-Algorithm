package academy.pocu.comp3500.lab2;

import academy.pocu.comp3500.lab2.datastructure.Node;

public final class Stack {
    private Node root;
    private int size;

    public Stack() {
    }

    public void push(final int data) {
        root = LinkedList.prepend(root, data);
        size++;
    }

    public int peek() {
        Node lastNode = LinkedList.getOrNull(root, 0);
        return lastNode.getData();
    }

    public int pop() {
        Node popNode = LinkedList.getOrNull(root, 0);
        LinkedList.removeAt(root, 0);
        size--;

        return popNode.getData();
    }

    public int getSize() {
        return size;
    }
}