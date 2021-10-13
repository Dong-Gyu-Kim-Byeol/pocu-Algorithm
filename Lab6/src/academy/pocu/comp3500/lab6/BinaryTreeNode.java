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

    public BinaryTreeNode<T> getParent() {
        return parent;
    }

    public BinaryTreeNode<T> getLeft() {
        return left;
    }

    public BinaryTreeNode<T> getRight() {
        return right;
    }

    public T getData() {
        return data;
    }

    public void setData(final T data) {
        this.data = data;
    }

    public void setLeft(final BinaryTreeNode<T> left) {
        this.left = left;
        if (left != null) {
            left.setParent(this);
        }
    }

    public void setRight(final BinaryTreeNode<T> right) {
        this.right = right;
        if (right != null) {
            right.setParent(this);
        }
    }

    public void setNullAll() {
        this.setData(null);
        this.setParent(null);
        this.setLeft(null);
        this.setRight(null);
    }

    // private
    private void setParent(BinaryTreeNode<T> parent) {
        this.parent = parent;
    }

}

