package academy.pocu.comp3500.lab6;

import java.util.Comparator;

public class BinaryTree<T> {
    private BinaryTreeNode<T> root;
    private final Comparator<T> treeBuildComparator;
    private final Comparator<T> keyComparator;
    private int size;

    public BinaryTree(final Comparator<T> treeBuildComparator, final Comparator<T> keyComparator) {
        this.treeBuildComparator = treeBuildComparator;
        this.keyComparator = keyComparator;
    }

    public BinaryTreeNode<T> getRoot() {
        return root;
    }

    public int size() {
        return size;
    }

    public T searchOrNull(final T target) {
        return searchOrNullRecursive(this.root, target).getData();
    }

    public boolean insert(final T data) {
        if (this.root == null) {
            assert (this.size() == 0);
            this.root = new BinaryTreeNode<T>(data, null, null);
            ++this.size;
            return true;
        }

        return insertRecursive(this.root, data);
    }

    public void insertArray(final T[] data) {
        this.insertArrayRecursive(data, 0, data.length - 1);
    }

    public boolean delete(final T target) {
        if (this.root == null) {
            assert (this.size() == 0);
            return false;
        }

        final BinaryTreeNode<T> targetNode = searchOrNullRecursive(this.root, target);
        if (targetNode == null) {
            return false;
        }

        return deleteRecursive(targetNode, target);
    }

    public void descendingTraversal(final T[] outData) {
        assert (outData.length <= this.size());
        descendingTraversalRecursive(this.root, outData, new int[1]);
    }

    public void inOrderTraversal(final T[] outData) {
        assert (outData.length <= this.size());
        inOrderTraversalRecursive(this.root, outData, new int[1]);
    }

    // private
    private BinaryTreeNode<T> searchOrNullRecursive(final BinaryTreeNode<T> rootOrNull, final T target) {
        if (rootOrNull == null) {
            return null;
        }

        final int compare = this.treeBuildComparator.compare(target, rootOrNull.getData());

        if (compare == 0) {
            if (this.keyComparator.compare(target, rootOrNull.getData()) == 0) {
                return rootOrNull;
            }
        }

        if (compare < 0) {
            return searchOrNullRecursive(rootOrNull.getLeft(), target);
        } else {
            return searchOrNullRecursive(rootOrNull.getRight(), target);
        }
    }

    private boolean insertRecursive(final BinaryTreeNode<T> rootOrNull, final T data) {
        if (rootOrNull == null) {
            return false;
        }

        final int compare = this.treeBuildComparator.compare(data, rootOrNull.getData());

        if (compare < 0) {
            if (rootOrNull.getLeft() == null) {
                rootOrNull.setLeft(new BinaryTreeNode<T>(data, null, null));
                ++this.size;
                return true;
            } else {
                return insertRecursive(rootOrNull.getLeft(), data);
            }
        } else {
            if (rootOrNull.getRight() == null) {
                rootOrNull.setRight(new BinaryTreeNode<T>(data, null, null));
                ++this.size;
                return true;
            } else {
                return insertRecursive(rootOrNull.getRight(), data);
            }
        }
    }

    private void insertArrayRecursive(final T[] data, final int left, final int right) {
        if (left > right) {
            return;
        }

        final int mid = (left + right) / 2;
        this.insert(data[mid]);

        insertArrayRecursive(data, left, mid - 1);
        insertArrayRecursive(data, mid + 1, right);
    }

    private boolean deleteRecursive(final BinaryTreeNode<T> deleteNode, final T target) {
        assert (deleteNode != null);

        if (deleteNode.getLeft() == null && deleteNode.getRight() == null) {
            if (deleteNode.getParent() == null) {
                assert (this.size() == 1);
                this.root = null;
            } else {
                if (deleteNode.getParent().getLeft() == deleteNode) {
                    deleteNode.getParent().setLeft(null);
                } else {
                    deleteNode.getParent().setRight(null);
                }

                deleteNode.setNullAll();
                --this.size;
                return true;
            }
        }

        if (deleteNode.getRight() != null) {
            final BinaryTreeNode<T> rightSmall = getSmallNode(deleteNode.getRight());

            deleteNode.setData(rightSmall.getData());
            rightSmall.setData(target);

            return this.deleteRecursive(rightSmall, target);
        } else { // deleteNode.getRight() == null
            assert (deleteNode.getLeft() != null);
            final BinaryTreeNode<T> leftBig = getBigNode(deleteNode.getLeft());

            deleteNode.setData(leftBig.getData());
            leftBig.setData(target);

            return this.deleteRecursive(leftBig, target);
        }
    }

    private static <T> void descendingTraversalRecursive(final BinaryTreeNode<T> root, final T[] outData, final int[] writeIndex) {
        assert (writeIndex.length == 1);

        if (root == null) {
            return;
        }


        if (outData.length <= writeIndex[0]) {
            return;
        }
        descendingTraversalRecursive(root.getRight(), outData, writeIndex);

        if (outData.length <= writeIndex[0]) {
            return;
        }
        outData[writeIndex[0]] = root.getData();
        writeIndex[0]++;

        if (outData.length <= writeIndex[0]) {
            return;
        }
        descendingTraversalRecursive(root.getLeft(), outData, writeIndex);
    }

    private static <T> void inOrderTraversalRecursive(final BinaryTreeNode<T> root, final T[] outData, final int[] writeIndex) {
        assert (writeIndex.length == 1);

        if (root == null) {
            return;
        }


        if (outData.length <= writeIndex[0]) {
            return;
        }
        inOrderTraversalRecursive(root.getLeft(), outData, writeIndex);

        if (outData.length <= writeIndex[0]) {
            return;
        }
        outData[writeIndex[0]] = root.getData();
        writeIndex[0]++;

        if (outData.length <= writeIndex[0]) {
            return;
        }
        inOrderTraversalRecursive(root.getRight(), outData, writeIndex);
    }

    private static <T> BinaryTreeNode<T> getSmallNode(final BinaryTreeNode<T> root) {
        BinaryTreeNode<T> node = root;
        while (node.getLeft() != null) {
            node = node.getLeft();
        }

        return node;
    }

    private static <T> BinaryTreeNode<T> getBigNode(final BinaryTreeNode<T> root) {
        BinaryTreeNode<T> node = root;
        while (node.getRight() != null) {
            node = node.getRight();
        }

        return node;
    }

}
