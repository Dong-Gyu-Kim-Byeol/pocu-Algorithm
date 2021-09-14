package academy.pocu.comp3500.lab2;

import academy.pocu.comp3500.lab2.datastructure.Node;

public final class Stack {
    private Node root;
    private int topIndex;

    public Stack() {
        topIndex = -1;
    }

    public void push(final int data) {
        root = LinkedList.append(root, data);
        ++topIndex;
    }

    public int peek() {
        Node lastNode = LinkedList.getOrNull(root, topIndex);
        return lastNode.getData();
    }

    public int pop() {
        Node popNode = LinkedList.getOrNull(root, topIndex);
        LinkedList.removeAt(root, topIndex);
        --topIndex;

        return popNode.getData();
    }

    public int getSize() {
        return topIndex + 1;
    }
}