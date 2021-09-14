package academy.pocu.comp3500.lab2;

import academy.pocu.comp3500.lab2.datastructure.Node;

public final class Stack {
    private Node root;

    public Stack() {
    }

    public void push(final int data) {
        root = LinkedList.prepend(root, data);
    }

    public int peek() {
        Node lastNode = LinkedList.getOrNull(root, 0);
        return lastNode.getData();
    }

    public int pop() {
        Node popNode = LinkedList.getOrNull(root, 0);
        LinkedList.removeAt(root, 0);

        return popNode.getData();
    }

    public int getSize() {
        Node node = root;
        int size = 0;
        while (node != null) {
            size++;
            node = node.getNextOrNull();
        }

        return size;
    }
}