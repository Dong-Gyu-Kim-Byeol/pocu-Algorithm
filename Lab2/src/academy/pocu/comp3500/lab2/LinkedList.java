package academy.pocu.comp3500.lab2;

import academy.pocu.comp3500.lab2.datastructure.Node;

public final class LinkedList {
    private LinkedList() {
    }

    public static Node append(final Node rootOrNull, final int data) {
        if (rootOrNull == null) {
            return new Node(data);
        }

        Node node = rootOrNull;
        while (node.getNextOrNull() != null) {
            node = node.getNextOrNull();
        }

        node.setNext(new Node(data));
        return rootOrNull;
    }

    public static Node prepend(final Node rootOrNull, final int data) {
        Node preNode = new Node(data);
        preNode.setNext(rootOrNull);

        return preNode;
    }

    public static Node insertAt(final Node rootOrNull, final int index, final int data) {
        if (index < 0) {
            return rootOrNull;
        }

        if (index == 0) {
            return prepend(rootOrNull, data);
        }

        Node node = rootOrNull;
        int nodeIndex = 0;
        while (node != null) {
            if (index == nodeIndex + 1) {
                Node nextNode = node.getNextOrNull();
                Node insertNode = new Node(data);

                node.setNext(insertNode);
                insertNode.setNext(nextNode);
                return rootOrNull;
            }

            ++nodeIndex;
            node = node.getNextOrNull();
        }

        return rootOrNull;
    }

    public static Node removeAt(final Node rootOrNull, final int index) {
        if (rootOrNull == null) {
            return null;
        }

        if (index == 0) {
            Node nextRootNode = rootOrNull.getNextOrNull();
            rootOrNull.setNext(null);

            return nextRootNode;
        }

        Node node = rootOrNull;
        int nodeIndex = 0;
        while (node != null) {
            if (index == nodeIndex + 1) {
                Node removeNode = node.getNextOrNull();
                if (removeNode == null) {
                    return rootOrNull;
                }

                Node nextNode = removeNode.getNextOrNull();
                removeNode.setNext(null);

                node.setNext(nextNode);
                return rootOrNull;
            }

            ++nodeIndex;
            node = node.getNextOrNull();
        }

        return rootOrNull;
    }

    public static int getIndexOf(final Node rootOrNull, final int data) {
        Node node = rootOrNull;
        int nodeIndex = 0;
        while (node != null) {
            if (node.getData() == data) {
                return nodeIndex;
            }

            ++nodeIndex;
            node = node.getNextOrNull();
        }

        return -1;
    }

    public static Node getOrNull(final Node rootOrNull, final int index) {
        Node node = rootOrNull;
        int nodeIndex = 0;
        while (node != null) {
            if (nodeIndex == index) {
                return node;
            }

            ++nodeIndex;
            node = node.getNextOrNull();
        }

        return null;
    }

    public static Node reverse(final Node rootOrNull) {
        if (rootOrNull == null) {
            return null;
        }

        Node newRoot = rootOrNull;
        Node oldNode = rootOrNull.getNextOrNull();

        newRoot.setNext(null);

        while (oldNode != null) {
            Node newNode = oldNode;
            oldNode = oldNode.getNextOrNull();

            newNode.setNext(newRoot);
            newRoot = newNode;
        }

        return newRoot;
    }

    public static Node interleaveOrNull(final Node root0OrNull, final Node root1OrNull) {
        if (root0OrNull == null && root1OrNull == null) {
            return null;
        }

        Node newRoot;
        Node node0;
        Node node1;

        if (root0OrNull == null) {
            newRoot = root1OrNull;

            node0 = root0OrNull;
            node1 = root1OrNull.getNextOrNull();
        } else {
            newRoot = root0OrNull;

            node0 = root0OrNull.getNextOrNull();
            node1 = root1OrNull;
        }

        Node newLastNode = newRoot;

        while (node0 != null || node1 != null) {

            if (node1 != null) {
                newLastNode.setNext(node1);

                node1 = node1.getNextOrNull();
                newLastNode = newLastNode.getNextOrNull();
                newLastNode.setNext(null);
            }

            if (node0 != null) {
                newLastNode.setNext(node0);

                node0 = node0.getNextOrNull();
                newLastNode = newLastNode.getNextOrNull();
                newLastNode.setNext(null);
            }
        }

        return newRoot;
    }
}