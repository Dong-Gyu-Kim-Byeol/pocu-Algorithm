package academy.pocu.comp3500.assignment4;

import java.util.ArrayList;
import java.util.HashMap;

public final class DisjointSet<D> {
    private class SetNode {
        private D parent;
        private int size;

        public SetNode(final D parent, final int size) {
            this.parent = parent;
            this.size = size;
        }
    }

    // ---

    private final HashMap<D, SetNode> sets = new HashMap<>(64);

    // ---

    public DisjointSet(final ArrayList<D> nodes) {
        for (D d : nodes) {
            SetNode setNode = new SetNode(d, 1);
            this.sets.put(d, setNode);
        }
    }

    // ---

    public final D find(final D node) {
        assert (this.sets.containsKey(node));

        SetNode n = this.sets.get(node);
        D parent = n.parent;
        if (parent.equals(node)) {
            return node;
        }

        n.parent = find(n.parent);

        return n.parent;
    }

    public final void union(final D node1, final D node2) {
        assert (this.sets.containsKey(node1));
        assert (this.sets.containsKey(node2));

        D root1 = find(node1);
        D root2 = find(node2);

        if (root1.equals(root2)) {
            return;
        }

        SetNode parent = this.sets.get(root1);
        SetNode child = this.sets.get(root2);

        if (parent.size < child.size) {
            SetNode temp = parent;

            parent = child;
            child = temp;
        }

        child.parent = parent.parent;
        parent.size = child.size + parent.size;
    }
}
