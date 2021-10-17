package academy.pocu.comp3500.lab6;

import java.util.Comparator;

public class BinaryTree<T> {
    private BinaryTreeNode<T> root;
    private final Comparator<T> keyComparator;
    private final Comparator<T> treeBuildComparator;
    private int size;

    public BinaryTree(final Comparator<T> keyComparator, final Comparator<T> treeBuildComparator) {
        this.keyComparator = keyComparator;
        this.treeBuildComparator = treeBuildComparator;
    }

    public void clear() {
        this.root = null;
        this.size = 0;
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
        return insertRecursive(this.root, data);
    }

    public void insertArray(final T[] data) {
        this.insertArrayRecursive(data, 0, data.length - 1);
    }

    public void initArray(final T[] data) {
        this.clear();
        this.initArrayRecursive(data, 0, data.length - 1, this.root);
    }

    public boolean delete(final T target) {
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

        final int keyCompare = this.keyComparator.compare(target, rootOrNull.getData());
        final int treeBuildCompare = this.treeBuildComparator.compare(target, rootOrNull.getData());
        if (keyCompare == 0) {
            assert (treeBuildCompare == 0);
            return rootOrNull;
        }

        if (treeBuildCompare < 0) {
            return searchOrNullRecursive(rootOrNull.getLeft(), target);
        } else {
            return searchOrNullRecursive(rootOrNull.getRight(), target);
        }
    }

    private boolean insertRecursive(final BinaryTreeNode<T> rootOrNull, final T data) {
        if (this.root == null) {
            assert (this.size() == 0);
            this.root = new BinaryTreeNode<T>(data, null, null);
            ++this.size;
            return true;
        }

        assert (rootOrNull != null);

        final int keyCompare = this.keyComparator.compare(data, rootOrNull.getData());
        final int treeBuildCompare = this.treeBuildComparator.compare(data, rootOrNull.getData());
        if (keyCompare == 0) {
            assert (treeBuildCompare == 0);
            return false;
        }

        if (treeBuildCompare < 0) {
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

    private void initArrayRecursive(final T[] data, final int left, final int right, final BinaryTreeNode<T> parentNode) {
        if (left > right) {
            return;
        }

        int mid = (left + right) / 2;
        while (true) {
            if (mid == left) {
                break;
            }

            assert (mid > left);
            final int compare = this.treeBuildComparator.compare(data[mid - 1], data[mid]);
            if (compare < 0) {
                break;
            }

            --mid;
        }
        final BinaryTreeNode<T> newNode = new BinaryTreeNode<>(data[mid], null, null);

        if (this.root == null) {
            assert (parentNode == null);
            assert (this.size() == 0);
            this.root = newNode;
        } else {
            final int treeBuildCompare = this.treeBuildComparator.compare(newNode.getData(), parentNode.getData());
            if (treeBuildCompare < 0) {
                assert (parentNode.getLeft() == null);
                parentNode.setLeft(newNode);
            } else {
                assert (parentNode.getRight() == null);
                parentNode.setRight(newNode);
            }
        }
        ++this.size;

        initArrayRecursive(data, left, mid - 1, newNode);
        initArrayRecursive(data, mid + 1, right, newNode);
    }

    private boolean deleteRecursive(final BinaryTreeNode<T> deleteNode, final T target) {
        assert (deleteNode != null);
        assert (this.root.getParent() == null);

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
            }

            deleteNode.clear();
            --this.size;
            assert (this.root == null || this.root.getParent() == null);
            return true;

        } else if (deleteNode.getRight() != null) {
            final BinaryTreeNode<T> rightSmall = getSmallNode(deleteNode.getRight());

            deleteNode.setData(rightSmall.getData());
            rightSmall.setData(target);

            assert (this.root.getParent() == null);
            return this.deleteRecursive(rightSmall, target);

        } else {
            assert (deleteNode.getLeft() != null);
            assert (deleteNode.getRight() == null);

            if (deleteNode.getParent() == null) {
                this.root = deleteNode.getLeft();
                this.root.setParent(null);
            } else {
                if (deleteNode.getParent().getLeft() == deleteNode) {
                    deleteNode.getParent().setLeft(deleteNode.getLeft());
                } else {
                    deleteNode.getParent().setRight(deleteNode.getLeft());
                }
            }

            deleteNode.clear();
            --this.size;
            assert (this.root.getParent() == null);
            return true;
        }
    }

    private static <T> void descendingTraversalRecursive(final BinaryTreeNode<T> rootOrNull, final T[] outData, final int[] outWriteIndex) {
        assert (outWriteIndex.length == 1);

        if (rootOrNull == null) {
            return;
        }


        if (outData.length <= outWriteIndex[0]) {
            return;
        }
        descendingTraversalRecursive(rootOrNull.getRight(), outData, outWriteIndex);

        if (outData.length <= outWriteIndex[0]) {
            return;
        }
        outData[outWriteIndex[0]] = rootOrNull.getData();
        outWriteIndex[0]++;

        if (outData.length <= outWriteIndex[0]) {
            return;
        }
        descendingTraversalRecursive(rootOrNull.getLeft(), outData, outWriteIndex);
    }

    private static <T> void inOrderTraversalRecursive(final BinaryTreeNode<T> rootOrNull, final T[] outData, final int[] outWriteIndex) {
        assert (outWriteIndex.length == 1);

        if (rootOrNull == null) {
            return;
        }


        if (outData.length <= outWriteIndex[0]) {
            return;
        }
        inOrderTraversalRecursive(rootOrNull.getLeft(), outData, outWriteIndex);

        if (outData.length <= outWriteIndex[0]) {
            return;
        }
        outData[outWriteIndex[0]] = rootOrNull.getData();
        outWriteIndex[0]++;

        if (outData.length <= outWriteIndex[0]) {
            return;
        }
        inOrderTraversalRecursive(rootOrNull.getRight(), outData, outWriteIndex);
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
