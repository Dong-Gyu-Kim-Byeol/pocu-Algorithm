package academy.pocu.comp3500.lab6;

import java.util.Comparator;
import java.util.function.Function;

public class BinaryTree<T> {
    private final Comparator<T> keyComparator;

    private final Function<T, Integer> treeBuildFunction;
    private final Comparator<T> treeBuildComparator;

    private BinaryTreeNode<T> rootOrNull;
    private int size;

    public BinaryTree(final Comparator<T> keyComparator, final Function<T, Integer> treeBuildFunction) {
        this.keyComparator = keyComparator;

        this.treeBuildFunction = treeBuildFunction;
        this.treeBuildComparator = Comparator.comparing(treeBuildFunction);
    }

    public void initByArray(final T[] sortedData) {
        this.clear();
        this.initByArrayRecursive(sortedData, 0, sortedData.length - 1, this.rootOrNull);
    }

    public void clear() {
        this.rootOrNull = null;
        this.size = 0;
    }

    public Comparator<T> getKeyComparator() {
        return keyComparator;
    }

    public Function<T, Integer> getTreeBuildFunction() {
        return treeBuildFunction;
    }

    public Comparator<T> getTreeBuildComparator() {
        return treeBuildComparator;
    }

    public int size() {
        return size;
    }

    public T searchOrNull(final T target) {
        return searchOrNullRecursive(this.rootOrNull, target).getData();
    }

    public boolean insert(final T data) {
        return insertRecursive(this.rootOrNull, data);
    }

    public void insertArray(final T[] data) {
        this.insertArrayRecursive(data, 0, data.length - 1);
    }

    public boolean delete(final T target) {
        final BinaryTreeNode<T> targetNode = searchOrNullRecursive(this.rootOrNull, target);
        if (targetNode == null) {
            return false;
        }

        return deleteRecursive(targetNode, target);
    }

    public void descendingTraversal(final T[] outData) {
        assert (outData.length <= this.size());
        descendingTraversalRecursive(this.rootOrNull, outData, new int[1]);
    }

    public void inOrderTraversal(final T[] outData) {
        assert (outData.length <= this.size());
        inOrderTraversalRecursive(this.rootOrNull, outData, new int[1]);
    }

    public T findAndNearOrNull(final T target) {
        return findAndNearOrNullRecursive(this.rootOrNull, target, false);
    }

    public T findAndNearWithoutTargetOrNull(final T target) {
        return findAndNearOrNullRecursive(this.rootOrNull, target, true);
    }


    // private
    private void initByArrayRecursive(final T[] sortedData, final int left, final int right, final BinaryTreeNode<T> parentNodeOrRootOrNull) {
        if (left > right) {
            return;
        }

        int mid = (left + right) / 2;
        while (true) {
            if (mid == left) {
                break;
            }

            assert (mid > left);
            final int compare = this.treeBuildComparator.compare(sortedData[mid - 1], sortedData[mid]);
            if (compare < 0) {
                break;
            }

            --mid;
        }
        final BinaryTreeNode<T> newNode = new BinaryTreeNode<>(sortedData[mid], null, null);

        if (parentNodeOrRootOrNull == null) {
            assert (rootOrNull == null);
            assert (this.size() == 0);
            this.rootOrNull = newNode;
        } else {
            final int treeBuildCompare = this.treeBuildComparator.compare(newNode.getData(), parentNodeOrRootOrNull.getData());
            if (treeBuildCompare < 0) {
                assert (parentNodeOrRootOrNull.getLeft() == null);
                parentNodeOrRootOrNull.setLeft(newNode);
            } else {
                assert (parentNodeOrRootOrNull.getRight() == null);
                parentNodeOrRootOrNull.setRight(newNode);
            }
        }
        ++this.size;

        initByArrayRecursive(sortedData, left, mid - 1, newNode);
        initByArrayRecursive(sortedData, mid + 1, right, newNode);
    }

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
        if (this.rootOrNull == null) {
            assert (this.size() == 0);
            this.rootOrNull = new BinaryTreeNode<T>(data, null, null);
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

    private boolean deleteRecursive(final BinaryTreeNode<T> deleteNode, final T target) {
        assert (deleteNode != null);
        assert (this.rootOrNull.getParent() == null);

        if (deleteNode.getLeft() == null && deleteNode.getRight() == null) {
            if (deleteNode.getParent() == null) {
                assert (this.size() == 1);
                this.rootOrNull = null;
            } else {
                if (deleteNode.getParent().getLeft() == deleteNode) {
                    deleteNode.getParent().setLeft(null);
                } else {
                    deleteNode.getParent().setRight(null);
                }
            }

            deleteNode.clear();
            --this.size;
            assert (this.rootOrNull == null || this.rootOrNull.getParent() == null);
            return true;

        } else if (deleteNode.getRight() != null) {
            final BinaryTreeNode<T> rightSmall = getSmallNode(deleteNode.getRight());

            deleteNode.setData(rightSmall.getData());
            rightSmall.setData(target);

            assert (this.rootOrNull.getParent() == null);
            return this.deleteRecursive(rightSmall, target);

        } else {
            assert (deleteNode.getLeft() != null);
            assert (deleteNode.getRight() == null);

            if (deleteNode.getParent() == null) {
                this.rootOrNull = deleteNode.getLeft();
                this.rootOrNull.setParent(null);
            } else {
                if (deleteNode.getParent().getLeft() == deleteNode) {
                    deleteNode.getParent().setLeft(deleteNode.getLeft());
                } else {
                    deleteNode.getParent().setRight(deleteNode.getLeft());
                }
            }

            deleteNode.clear();
            --this.size;
            assert (this.rootOrNull.getParent() == null);
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

    private T findAndNearOrNullRecursive(final BinaryTreeNode<T> rootOrNull, final T target, final boolean isWithoutTarget) {
        if (rootOrNull == null) {
            return null;
        }

        BinaryTreeNode<T> node = rootOrNull;
        T data = null;
        int loopMinDifference = Integer.MAX_VALUE;

        while (node != null) {
            final int keyCompare = this.keyComparator.compare(target, node.getData());
            if (keyCompare == 0) {
                if (isWithoutTarget) {
                    int rightDifference = Integer.MAX_VALUE;
                    final T right = findAndNearOrNullRecursive(node.getRight(), target, isWithoutTarget);
                    if (right != null) {
                        rightDifference = Math.abs(this.treeBuildFunction.apply(target) - this.treeBuildFunction.apply(right));
                    }

                    int leftDifference = Integer.MAX_VALUE;
                    final T left = findAndNearOrNullRecursive(node.getLeft(), target, isWithoutTarget);
                    if (left != null) {
                        leftDifference = Math.abs(this.treeBuildFunction.apply(target) - this.treeBuildFunction.apply(left));
                    }

                    final int minDifference = Math.min(loopMinDifference, Math.min(rightDifference, leftDifference));

                    if (minDifference == rightDifference) {
                        return right;
                    } else if (minDifference == loopMinDifference) {
                        return data;
                    } else {
                        assert (minDifference == leftDifference);
                        return left;
                    }
                } else {
                    data = node.getData();
                    return data;
                }
            } else { // keyCompare != 0
                final int targetTreeBuildCompare = this.treeBuildComparator.compare(target, node.getData());
                if (targetTreeBuildCompare == 0) {
                    return node.getData();
                }

                final int tempDifference = Math.abs(this.treeBuildFunction.apply(target) - this.treeBuildFunction.apply(node.getData()));

                if (loopMinDifference == tempDifference) {
                    assert (data != null);
                    if (this.treeBuildComparator.compare(data, node.getData()) < 0) {
                        data = node.getData();
                    }
                }

                if (loopMinDifference > tempDifference) {
                    loopMinDifference = tempDifference;
                    data = node.getData();
                }

                if (targetTreeBuildCompare < 0) {
                    node = node.getLeft();
                } else {
                    node = node.getRight();
                }
            }
        }

        return data;
    }
}
