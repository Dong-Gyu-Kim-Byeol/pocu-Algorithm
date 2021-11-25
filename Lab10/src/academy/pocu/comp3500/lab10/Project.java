package academy.pocu.comp3500.lab10;

import academy.pocu.comp3500.lab10.project.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Project {
    public static List<String> findSchedule(final Task[] tasks, final boolean includeMaintenance) {
        // O(n) + O(ne) + O(n + e) + O(n + e) + O(n + e) + O(n)

        // tasks == transposed graph
        final HashMap<Task, GraphNode<Task>> graph = Graph.getTransposedGraph(tasks, Task::getPredecessors);

        // get sortedList
        final LinkedList<GraphNode<Task>> sortedList = Graph.topologicalSort(graph, Task::getPredecessors, includeMaintenance);

        final ArrayList<String> outSortedList = new ArrayList<>(sortedList.size());
        for (final GraphNode<Task> node : sortedList) {
            final Task task = node.getData();
            outSortedList.add(task.getTitle());
        }

        return outSortedList;
    }
}