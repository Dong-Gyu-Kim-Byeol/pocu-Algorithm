package academy.pocu.comp3500.assignment4;

import academy.pocu.comp3500.assignment4.project.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public final class Project {
    private final HashMap<String, TaskData> taskDataMap;
    private final DirectedGraph<TaskData> graph;

    // ---

    public Project(final Task[] tasks) {
        this.taskDataMap = new HashMap<>(tasks.length);

        // create graph
        // create taskDataArray
        {
            final ArrayList<TaskData> taskDataArray = new ArrayList<>(tasks.length);
            final HashMap<Task, TaskData> tempTaskDataMap = new HashMap<>(tasks.length);

            for (final Task task : tasks) {
                final TaskData taskData = new TaskData(task.getTitle(), task.getEstimate());

                this.taskDataMap.put(taskData.getTitle(), taskData);

                taskDataArray.add(taskData);
                tempTaskDataMap.put(task, taskData);
            }

            final HashMap<TaskData, ArrayList<TaskData>> taskDataArrayNodes = new HashMap<>(tasks.length);

            for (final Task task : tasks) {
                assert (tempTaskDataMap.containsKey(task));

                final ArrayList<TaskData> taskDataNodes = new ArrayList<>(task.getPredecessors().size());
                taskDataArrayNodes.put(tempTaskDataMap.get(task), taskDataNodes);

                for (final Task taskNode : task.getPredecessors()) {
                    assert (tempTaskDataMap.containsKey(taskNode));

                    taskDataNodes.add(tempTaskDataMap.get(taskNode));
                }
            }

            this.graph = new DirectedGraph<>(taskDataArray, taskDataArrayNodes, TaskData::getEstimate);
        }
    }

    // ---

    public final int findTotalManMonths(final String task) {
        assert (this.taskDataMap.containsKey(task));
        final TaskData taskNode = this.taskDataMap.get(task);

        final LinkedList<DirectedGraphNode<TaskData>> searchNodes = new LinkedList<>();
        this.graph.bfs(true, taskNode, false, searchNodes);

        int manMonths = 0;

        for (final DirectedGraphNode<TaskData> node : searchNodes) {
            manMonths += node.getData().getEstimate();
        }

        return manMonths;
    }

    public final int findMinDuration(final String task) {
        assert (this.taskDataMap.containsKey(task));
        final TaskData taskNode = this.taskDataMap.get(task);

        final LinkedList<WeightNode<DirectedGraphNode<TaskData>>> sums = this.graph.bfsNodeAllPathSumWeightSkipSccAndWithoutDiscovered(taskNode, false);

        int max = 0;
        for (final WeightNode<DirectedGraphNode<TaskData>> snm : sums) {
            max = Math.max(max, snm.getWeight());
        }

        return max;
    }

    public final int findMaxBonusCount(final String task) {
        assert (this.taskDataMap.containsKey(task));
        final TaskData taskNode = this.taskDataMap.get(task);

        final ArrayList<TaskData> ghostCombineNodes;
        final TaskData ghostTask;
        {

            final LinkedList<DirectedGraphNode<TaskData>> searchNodes = new LinkedList<>();
            this.graph.bfs(true, taskNode, false, searchNodes);

            int leafCapacitySum = 0;
            ghostCombineNodes = new ArrayList<>(searchNodes.size());
            for (final DirectedGraphNode<TaskData> node : searchNodes) {
                if (node.getNodes().size() == 0) {
                    ghostCombineNodes.add(node.getData());
                    leafCapacitySum += node.getData().getEstimate();
                }
            }

            ghostTask = new TaskData("GHOST", leafCapacitySum);
            this.graph.addTransposedNode(ghostTask, ghostCombineNodes);
        }

        final int[] flow = new int[this.graph.nodeCount()];
        final int[] flowIndex = new int[2];
        this.graph.maxFlow(true, ghostTask, taskNode, true, flow, flowIndex);
        // this.graph.maxFlow(true, taskNode, ghostTask, false, flow, flowIndex);


        {
            assert (flow[flowIndex[0]] == flow[flowIndex[1]]);

            this.graph.removeTransposedNode(ghostTask, ghostCombineNodes);

            return flow[flowIndex[0]];
        }
    }

}