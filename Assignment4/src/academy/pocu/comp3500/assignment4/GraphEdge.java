package academy.pocu.comp3500.assignment4;

public final class GraphEdge<T> {
    private final int weight;
    private int flow;

    private final GraphNode<T> node1;
    private final GraphNode<T> node2;

    // ---

    public GraphEdge(final int weight, final GraphNode<T> from, final GraphNode<T> to) {
        this.weight = weight;
        this.node1 = from;
        this.node2 = to;
    }

    // ---

    public final int getFlow() {
        return flow;
    }

    public final void setFlow(int flow) {
        this.flow = flow;
    }

    public final int getWeight() {
        return weight;
    }

    public final GraphNode<T> getNode1() {
        return node1;
    }

    public final GraphNode<T> getNode2() {
        return node2;
    }

    @Override
    public final String toString() {
        if (this.node1 == null) {
            return String.format("from : null, to : [ %s ], weight : %d", node2.toString(), weight);
        }

        return String.format("from : [ %s ], to : [ %s ], weight : %d", node1.toString(), node2.toString(), weight);
    }
}