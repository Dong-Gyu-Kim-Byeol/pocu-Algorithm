package academy.pocu.comp3500.assignment4.app;

import academy.pocu.comp3500.assignment4.Graph;
import academy.pocu.comp3500.assignment4.GraphNode;
import academy.pocu.comp3500.assignment4.Project;
import academy.pocu.comp3500.assignment4.project.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Program {

    public static void main(String[] args) {
        {
            checkNormalMaxPath1();
            checkNormalMaxPath2();
        }

        //
        //
        //

        {
            Task[] tasks = createTasksDefault();

            Project project = new Project(tasks);

            int manMonths1 = project.findTotalManMonths("ms1");
            assert (manMonths1 == 17);

            int manMonths2 = project.findTotalManMonths("ms2");
            assert (manMonths2 == 46);

            int minDuration1 = project.findMinDuration("ms1");
            assert (minDuration1 == 14);

            int minDuration2 = project.findMinDuration("ms2");
            assert (minDuration2 == 32);

            int bonusCount1 = project.findMaxBonusCount("ms1");
            assert (bonusCount1 == 6);

            int bonusCount2 = project.findMaxBonusCount("ms2");
            assert (bonusCount2 == 6);
        }

        {
            Task[] tasks = createTasks3();

            Project project = new Project(tasks);

            int bonusCount2 = project.findMaxBonusCount("ms");
            assert (bonusCount2 == 3);
        }

        {
            checkNormalMaxFlow();
        }

        {
            Task[] tasks = createTasksTestBackEdge1();

            Project project = new Project(tasks);

            int bonusCount2 = project.findMaxBonusCount("ms1");
            assert (bonusCount2 == 2);
        }

        {
            Task[] tasks = createTasksTestBackEdge2();

            Project project = new Project(tasks);

            int bonusCount2 = project.findMaxBonusCount("ms1");
            assert (bonusCount2 == 2);
        }

        {
            Task[] tasks = createTasks1();

            Project project = new Project(tasks);

            int bonusCount2 = project.findMaxBonusCount("ms2");
            assert (bonusCount2 == 7);
        }

        {
            Task[] tasks = createTasks2();

            Project project = new Project(tasks);

            int bonusCount2 = project.findMaxBonusCount("ms2");
            assert (bonusCount2 == 7);
        }

        {
            Task[] tasks = createTasks3();

            Project project = new Project(tasks);

            int bonusCount2 = project.findMaxBonusCount("ms");
            assert (bonusCount2 == 3);
        }

        {
            Task[] tasks = createTasks4();

            Project project = new Project(tasks);

            int bonusCount2 = project.findMaxBonusCount("ms2");
            assert (bonusCount2 == 6);
        }
    }

    private static void checkNormalMaxFlow() {
        Task a = new Task("A", -1);
        Task b = new Task("B", -1);
        Task c = new Task("C", -1);
        Task d = new Task("D", -1);
        Task ms = new Task("ms", -1);
        Task start = new Task("start", -1);

        ms.addPredecessor(b, a);
        final int[] msW = new int[]{2, 7};
        final ArrayList<Integer> msEdgeWeightArray = new ArrayList<>(ms.getPredecessors().size());
        for (int i = 0; i < ms.getPredecessors().size(); ++i) {
            msEdgeWeightArray.add(msW[i]);
        }

        a.addPredecessor(c);
        final int[] aW = new int[]{5};
        final ArrayList<Integer> aEdgeWeightArray = new ArrayList<>(a.getPredecessors().size());
        for (int i = 0; i < a.getPredecessors().size(); ++i) {
            aEdgeWeightArray.add(aW[i]);
        }

        b.addPredecessor(c, d);
        final int[] bW = new int[]{2, 4};
        final ArrayList<Integer> bEdgeWeightArray = new ArrayList<>(b.getPredecessors().size());
        for (int i = 0; i < b.getPredecessors().size(); ++i) {
            bEdgeWeightArray.add(bW[i]);
        }

        c.addPredecessor(d, start);
        final int[] cW = new int[]{1, 2};
        final ArrayList<Integer> cEdgeWeightArray = new ArrayList<>(c.getPredecessors().size());
        for (int i = 0; i < c.getPredecessors().size(); ++i) {
            cEdgeWeightArray.add(cW[i]);
        }

        d.addPredecessor(start);
        final int[] dW = new int[]{9};
        final ArrayList<Integer> dEdgeWeightArray = new ArrayList<>(d.getPredecessors().size());
        for (int i = 0; i < d.getPredecessors().size(); ++i) {
            dEdgeWeightArray.add(dW[i]);
        }

        Task[] tasks = new Task[]{
                a, b, c, d, ms, start,
        };

        // create graph

        final ArrayList<Task> taskDataArray = new ArrayList<>(tasks.length);

        // create taskDataArray
        for (final Task task : tasks) {
            taskDataArray.add(task);
        }

        final HashMap<Task, ArrayList<Task>> edgeArrayMap = new HashMap<>(tasks.length);
        final HashMap<Task, ArrayList<Integer>> edgeWeightArrayMap = new HashMap<>(tasks.length);

        for (final Task task : tasks) {
            final ArrayList<Task> edgeArray = new ArrayList<>(task.getPredecessors().size());
            edgeArrayMap.put(task, edgeArray);

            if (task.equals(ms)) {
                edgeWeightArrayMap.put(task, msEdgeWeightArray);
            } else if (task.equals(a)) {
                edgeWeightArrayMap.put(task, aEdgeWeightArray);
            } else if (task.equals(b)) {
                edgeWeightArrayMap.put(task, bEdgeWeightArray);
            } else if (task.equals(c)) {
                edgeWeightArrayMap.put(task, cEdgeWeightArray);
            } else if (task.equals(d)) {
                edgeWeightArrayMap.put(task, dEdgeWeightArray);
            } else {
                edgeWeightArrayMap.put(task, new ArrayList<>());
            }

            for (final Task predecessor : task.getPredecessors()) {
                edgeArray.add(predecessor);
            }
        }

        final Graph<Task> graph = new Graph<>(true, taskDataArray, edgeArrayMap, edgeWeightArrayMap);

        int bonusCount2 = graph.maxFlow(false, ms, start, false);
        assert (bonusCount2 == 5);
    }

    private static Task[] createTasksTestBackEdge1() {
        Task a = new Task("A", 2);
        Task b = new Task("B", 1);

        Task d = new Task("D", 2);
        Task e = new Task("E", 2);
        Task c = new Task("C", 1);
        Task ms1 = new Task("ms1", 6);

        Task ca = new Task("CA", 3);
        Task cb = new Task("CB", 5);
        Task cc = new Task("CC", 3);

        ms1.addPredecessor(e, c);

        c.addPredecessor(b, d);
        b.addPredecessor(a);

        d.addPredecessor(a);

        e.addPredecessor(b);

        ca.addPredecessor(e, cc);
        cc.addPredecessor(cb);
        cb.addPredecessor(ca);

        return new Task[]{
                a, b, c, d, e, ms1, cc, ca, cb,
        };
    }

    private static Task[] createTasksTestBackEdge2() {
        Task a = new Task("A", 2);
        Task b = new Task("B", 1);
        Task c = new Task("C", 1);
        Task d = new Task("D", 2);
        Task e = new Task("E", 2);
        Task f = new Task("F", 2);
        Task g = new Task("G", 2);
        Task ms1 = new Task("ms1", 6);

        Task ca = new Task("CA", 3);
        Task cb = new Task("CB", 5);
        Task cc = new Task("CC", 3);

        ms1.addPredecessor(c, g);

        c.addPredecessor(b, e);
        b.addPredecessor(a);

        e.addPredecessor(d);
        d.addPredecessor(a);

        g.addPredecessor(f);
        f.addPredecessor(b);

        ca.addPredecessor(f, cc);
        cc.addPredecessor(cb);
        cb.addPredecessor(ca);

        return new Task[]{
                a, b, c, d, e, f, g, ms1, cc, ca, cb,
        };
    }

    private static Task[] createTasksDefault() {
        Task a = new Task("A", 3);
        Task b = new Task("B", 5);
        Task c = new Task("C", 3);
        Task d = new Task("D", 2);
        Task e = new Task("E", 1);
        Task f = new Task("F", 2);
        Task g = new Task("G", 6);
        Task h = new Task("H", 8);
        Task i = new Task("I", 2);
        Task j = new Task("J", 4);
        Task k = new Task("K", 2);
        Task l = new Task("L", 8);
        Task m = new Task("M", 7);
        Task n = new Task("N", 1);
        Task o = new Task("O", 1);
        Task p = new Task("P", 6);
        Task ms1 = new Task("ms1", 6);
        Task ms2 = new Task("ms2", 8);

        Task ca = new Task("CA", 3);
        Task cb = new Task("CB", 5);
        Task cc = new Task("CC", 3);

        ca.addPredecessor(ms1, cc);
        cc.addPredecessor(cb);
        cb.addPredecessor(ca);

        c.addPredecessor(b);
        d.addPredecessor(a);

        ms1.addPredecessor(a, c);

        e.addPredecessor(c);
        f.addPredecessor(g);
        g.addPredecessor(e);

        i.addPredecessor(h);
        j.addPredecessor(ms1);

        k.addPredecessor(j);
        n.addPredecessor(k);
        m.addPredecessor(n);
        l.addPredecessor(m);

        p.addPredecessor(i, j);
        o.addPredecessor(j);

        ms2.addPredecessor(o, p);

        return new Task[]{
                a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, ms1, ms2, ca, cb, cc
        };
    }

    private static Task[] createTasks1() {
        Task a = new Task("A", 3);
        Task b = new Task("B", 5);
        Task c = new Task("C", 3);
        Task d = new Task("D", 2);
        Task e = new Task("E", 1);
        Task f = new Task("F", 2);
        Task g = new Task("G", 6);
        Task h = new Task("H", 8);
        Task i = new Task("I", 2);
        Task j = new Task("J", 4);
        Task k = new Task("K", 2);
        Task l = new Task("L", 8);
        Task m = new Task("M", 7);
        Task n = new Task("N", 1);
        Task o = new Task("O", 1);
        Task p = new Task("P", 6);
        Task ms1 = new Task("ms1", 6);
        Task ms2 = new Task("ms2", 8);

        Task ca = new Task("CA", 3);
        Task cb = new Task("CB", 5);
        Task cc = new Task("CC", 3);

        ca.addPredecessor(ms1, cc);
        cc.addPredecessor(cb);
        cb.addPredecessor(ca);

        c.addPredecessor(b);
        d.addPredecessor(a);

        ms1.addPredecessor(a, c);

        e.addPredecessor(c);
        f.addPredecessor(g);
        g.addPredecessor(e);

        i.addPredecessor(h);
        j.addPredecessor(ms1);

        k.addPredecessor(j);
        n.addPredecessor(k);
        m.addPredecessor(n);
        l.addPredecessor(m);

        p.addPredecessor(i, j);
        o.addPredecessor(j, b);  // add b

        ms2.addPredecessor(o, p);

        return new Task[]{
                a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, ms1, ms2, ca, cb, cc
        };
    }

    private static Task[] createTasks2() {
        Task a = new Task("A", 3);
        Task b = new Task("B", 5);
        Task c = new Task("C", 3);
        Task d = new Task("D", 2);
        Task e = new Task("E", 1);
        Task f = new Task("F", 2);
        Task g = new Task("G", 6);
        Task h = new Task("H", 8);
        Task i = new Task("I", 2);
        Task j = new Task("J", 4);
        Task k = new Task("K", 2);
        Task l = new Task("L", 8);
        Task m = new Task("M", 7);
        Task n = new Task("N", 1);
        Task o = new Task("O", 1);
        Task p = new Task("P", 6);
        Task ms1 = new Task("ms1", 6);
        Task ms2 = new Task("ms2", 8);

        Task ca = new Task("CA", 3);
        Task cb = new Task("CB", 5);
        Task cc = new Task("CC", 3);

        ca.addPredecessor(ms1, cc);
        cc.addPredecessor(cb);
        cb.addPredecessor(ca);

        c.addPredecessor(b);
        d.addPredecessor(a);

        ms1.addPredecessor(a, c);

        e.addPredecessor(c);
        f.addPredecessor(g);
        g.addPredecessor(e);

        i.addPredecessor(h);
        j.addPredecessor(ms1);

        k.addPredecessor(j);
        n.addPredecessor(k);
        m.addPredecessor(n);
        l.addPredecessor(m);

        p.addPredecessor(i, j);
        o.addPredecessor(j, c);  // add c

        ms2.addPredecessor(o, p);

        return new Task[]{
                a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, ms1, ms2, ca, cb, cc
        };
    }

    private static Task[] createTasks3() {
        Task a = new Task("A", 3);
        Task b = new Task("B", 2);
        Task ms = new Task("ms", 3);

        ms.addPredecessor(a, b);

        return new Task[]{
                a, b, ms,
        };
    }

    private static Task[] createTasks4() {
        Task a = new Task("A", 3);
        Task b = new Task("B", 5);
        Task c = new Task("C", 3);
        Task d = new Task("D", 2);
        Task e = new Task("E", 1);
        Task f = new Task("F", 2);
        Task g = new Task("G", 6);
        Task h = new Task("H", 8);
        Task i = new Task("I", 2);
        Task j = new Task("J", 4);
        Task k = new Task("K", 2);
        Task l = new Task("L", 8);
        Task m = new Task("M", 7);
        Task n = new Task("N", 1);
        Task o = new Task("O", 1);
        Task p = new Task("P", 6);
        Task ms1 = new Task("ms1", 6);
        Task ms2 = new Task("ms2", 8);

        Task ca = new Task("CA", 3);
        Task cb = new Task("CB", 5);
        Task cc = new Task("CC", 3);

        ca.addPredecessor(b, cc);
        cc.addPredecessor(cb);
        cb.addPredecessor(ca);

        c.addPredecessor(b);
        d.addPredecessor(a);

        ms1.addPredecessor(a, c);

        e.addPredecessor(c);
        f.addPredecessor(g);
        g.addPredecessor(e);

        i.addPredecessor(h);
        j.addPredecessor(ms1);

        k.addPredecessor(j);
        n.addPredecessor(k);
        m.addPredecessor(n);
        l.addPredecessor(m);

        p.addPredecessor(i, j);
        o.addPredecessor(j);

        ms2.addPredecessor(o, p);

        return new Task[]{
                a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, ms1, ms2, ca, cb, cc
        };
    }

    private static void checkNormalMaxPath1() {
        Task a = new Task("A", -1);
        Task b = new Task("B", -1);
        Task c = new Task("C", -1);
        Task d = new Task("D", -1);

        a.addPredecessor(b, d);
        final int[] aW = new int[]{2, 1};
        final ArrayList<Integer> aEdgeWeightArray = new ArrayList<>(a.getPredecessors().size());
        for (int i = 0; i < a.getPredecessors().size(); ++i) {
            aEdgeWeightArray.add(aW[i]);
        }

        b.addPredecessor(c);
        final int[] bW = new int[]{2};
        final ArrayList<Integer> bEdgeWeightArray = new ArrayList<>(b.getPredecessors().size());
        for (int i = 0; i < b.getPredecessors().size(); ++i) {
            bEdgeWeightArray.add(bW[i]);
        }

        c.addPredecessor();
        final int[] cW = new int[]{};
        final ArrayList<Integer> cEdgeWeightArray = new ArrayList<>(c.getPredecessors().size());
        for (int i = 0; i < c.getPredecessors().size(); ++i) {
            cEdgeWeightArray.add(cW[i]);
        }

        d.addPredecessor(c);
        final int[] dW = new int[]{4};
        final ArrayList<Integer> dEdgeWeightArray = new ArrayList<>(d.getPredecessors().size());
        for (int i = 0; i < d.getPredecessors().size(); ++i) {
            dEdgeWeightArray.add(dW[i]);
        }

        Task[] tasks = new Task[]{
                a, b, c, d,
        };

        // create graph

        final ArrayList<Task> taskDataArray = new ArrayList<>(tasks.length);

        // create taskDataArray
        for (final Task task : tasks) {
            taskDataArray.add(task);
        }

        final HashMap<Task, ArrayList<Task>> edgeArrayMap = new HashMap<>(tasks.length);
        final HashMap<Task, ArrayList<Integer>> edgeWeightArrayMap = new HashMap<>(tasks.length);

        for (final Task task : tasks) {
            final ArrayList<Task> edgeArray = new ArrayList<>(task.getPredecessors().size());
            edgeArrayMap.put(task, edgeArray);

            if (task.equals(a)) {
                edgeWeightArrayMap.put(task, aEdgeWeightArray);
            } else if (task.equals(b)) {
                edgeWeightArrayMap.put(task, bEdgeWeightArray);
            } else if (task.equals(c)) {
                edgeWeightArrayMap.put(task, cEdgeWeightArray);
            } else if (task.equals(d)) {
                edgeWeightArrayMap.put(task, dEdgeWeightArray);
            } else {
                edgeWeightArrayMap.put(task, new ArrayList<>());
            }

            for (final Task predecessor : task.getPredecessors()) {
                edgeArray.add(predecessor);
            }
        }

        final Graph<Task> graph = new Graph<>(true, taskDataArray, edgeArrayMap, edgeWeightArrayMap);

        final HashMap<Task, Task> prevMap = new HashMap<>(graph.nodeCount());
        final HashMap<Task, Integer> maxDistMap = graph.dijkstraMaxPath(false, a, false, prevMap);

        int max = 0;
        for (final int dist : maxDistMap.values()) {
            max = Math.max(dist, max);
        }

        assert (max == 5);
    }

    private static void checkNormalMaxPath2() {
        Task a = new Task("A", -1);
        Task b = new Task("B", -1);
        Task c = new Task("C", -1);

        a.addPredecessor(b, c);
        final int[] aW = new int[]{1, 1};
        final ArrayList<Integer> aEdgeWeightArray = new ArrayList<>(a.getPredecessors().size());
        for (int i = 0; i < a.getPredecessors().size(); ++i) {
            aEdgeWeightArray.add(aW[i]);
        }

        b.addPredecessor();
        final int[] bW = new int[]{};
        final ArrayList<Integer> bEdgeWeightArray = new ArrayList<>(b.getPredecessors().size());
        for (int i = 0; i < b.getPredecessors().size(); ++i) {
            bEdgeWeightArray.add(bW[i]);
        }

        c.addPredecessor(b);
        final int[] cW = new int[]{1};
        final ArrayList<Integer> cEdgeWeightArray = new ArrayList<>(c.getPredecessors().size());
        for (int i = 0; i < c.getPredecessors().size(); ++i) {
            cEdgeWeightArray.add(cW[i]);
        }

        Task[] tasks = new Task[]{
                a, b, c,
        };

        // create graph

        final ArrayList<Task> taskDataArray = new ArrayList<>(tasks.length);

        // create taskDataArray
        for (final Task task : tasks) {
            taskDataArray.add(task);
        }

        final HashMap<Task, ArrayList<Task>> edgeArrayMap = new HashMap<>(tasks.length);
        final HashMap<Task, ArrayList<Integer>> edgeWeightArrayMap = new HashMap<>(tasks.length);

        for (final Task task : tasks) {
            final ArrayList<Task> edgeArray = new ArrayList<>(task.getPredecessors().size());
            edgeArrayMap.put(task, edgeArray);

            if (task.equals(a)) {
                edgeWeightArrayMap.put(task, aEdgeWeightArray);
            } else if (task.equals(b)) {
                edgeWeightArrayMap.put(task, bEdgeWeightArray);
            } else if (task.equals(c)) {
                edgeWeightArrayMap.put(task, cEdgeWeightArray);
            } else {
                edgeWeightArrayMap.put(task, new ArrayList<>());
            }

            for (final Task predecessor : task.getPredecessors()) {
                edgeArray.add(predecessor);
            }
        }

        final Graph<Task> graph = new Graph<>(true, taskDataArray, edgeArrayMap, edgeWeightArrayMap);

        final HashMap<Task, Task> prevMap = new HashMap<>(graph.nodeCount());
        final HashMap<Task, Integer> maxDistMap = graph.dijkstraMaxPath(false, a, false, prevMap);

        int max = 0;
        for (final int dist : maxDistMap.values()) {
            max = Math.max(dist, max);
        }

        assert (max == 2);
    }
}