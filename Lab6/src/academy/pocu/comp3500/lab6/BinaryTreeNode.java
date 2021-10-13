package academy.pocu.comp3500.lab6;

public class BinaryTreeNode<T> {
    private T data;
    private BinaryTreeNode<T> parent;
    private BinaryTreeNode<T> left;
    private BinaryTreeNode<T> right;

    public BinaryTreeNode(final T data, final BinaryTreeNode<T> left, final BinaryTreeNode<T> right) {
        this.data = data;

        this.setLeft(left);
        this.setRight(right);
    }

    public T getData() {
        return data;
    }

    public void setData(final T data) {
        this.data = data;
    }

    public BinaryTreeNode<T> getParent() {
        return parent;
    }

    public void setParent(BinaryTreeNode<T> parent) {
        this.parent = parent;
    }

    public BinaryTreeNode<T> getLeft() {
        return left;
    }

    public void setLeft(final BinaryTreeNode<T> left) {
        this.left = left;
        if (left != null) {
            left.setParent(this);
        }
    }

    public BinaryTreeNode<T> getRight() {
        return right;
    }

    public void setRight(final BinaryTreeNode<T> right) {
        this.right = right;
        if (right != null) {
            right.setParent(this);
        }
    }

    public void clear() {
        this.setData(null);
        this.setParent(null);
        this.setLeft(null);
        this.setRight(null);
    }

}

