package academy.pocu.comp3500.assignment4.app;

//import academy.pocu.comp3500.assignment4.Graph;
//import academy.pocu.comp3500.assignment4.GraphEdge;
import academy.pocu.comp3500.assignment4.Project;
//import academy.pocu.comp3500.assignment4.TaskData;
import academy.pocu.comp3500.assignment4.project.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Program {

    public static void main(String[] args) {
        {
            Task[] tasks = createTasks();

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
            Task[] tasks = createTasksTestBackEdge1();

            Project project = new Project(tasks);

            int bonusCount2 = project.findMaxBonusCount("ms1");
            assert (bonusCount2 == 2);
        }

        {
            Task[] tasks = createTasksTestBackEdge();

            Project project = new Project(tasks);

            int bonusCount2 = project.findMaxBonusCount("ms1");
            assert (bonusCount2 == 2);
        }

//        {
//            Graph<Task> graph = createTasksTestBackEdge2();
//        }

        {
            Task[] tasks = createTasks2();

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

            int bonusCount2 = project.findMaxBonusCount("ms2");
            assert (bonusCount2 == 7);
        }

        // ---

        {
            Task[] tasks = createTasks();

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
            Task[] tasks = createTasks();

            Project project = new Project(tasks);

            int bonusCount2 = project.findMaxBonusCount("ms2");
            assert (bonusCount2 == 6);
        }
    }

    private static Task[] createTasksTestBackEdge() {
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

        Task[] tasks = new Task[]{
                a, b, c, d, e, ms1, cc, ca, cb,
        };

        return tasks;
    }

    private static Task[] createTasksTestBackEdge1() {
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

        Task[] tasks = new Task[]{
                a, b, c, d, e, f, g, ms1, cc, ca, cb,
        };

        return tasks;
    }

//    private static Graph<Task> createTasksTestBackEdge2() {
//        Task a = new Task("A", -1);
//        Task b = new Task("B", -1);
//        Task c = new Task("C", -1);
//        Task d = new Task("D", -1);
//        Task ms = new Task("ms", -1);
//        Task start = new Task("start", -1);
//
//        ms.addPredecessor(b, a);
//        final int[] msW = new int[]{2, 7};
//        final ArrayList<Integer> msEdgeWeightArray = new ArrayList<>(ms.getPredecessors().size());
//        {
//            int i = 0;
//            for (final Task predecessor : ms.getPredecessors()) {
//                msEdgeWeightArray.add(msW[i]);
//                ++i;
//            }
//        }
//
//        a.addPredecessor(c);
//        final int[] aW = new int[]{5};
//        final ArrayList<Integer> aEdgeWeightArray = new ArrayList<>(a.getPredecessors().size());
//        {
//            int i = 0;
//            for (final Task predecessor : a.getPredecessors()) {
//                aEdgeWeightArray.add(aW[i]);
//                ++i;
//            }
//        }
//
//        b.addPredecessor(c, d);
//        final int[] bW = new int[]{2, 4};
//        final ArrayList<Integer> bEdgeWeightArray = new ArrayList<>(b.getPredecessors().size());
//        {
//            int i = 0;
//            for (final Task predecessor : b.getPredecessors()) {
//                bEdgeWeightArray.add(bW[i]);
//                ++i;
//            }
//        }
//
//        c.addPredecessor(d, start);
//        final int[] cW = new int[]{1, 2};
//        final ArrayList<Integer> cEdgeWeightArray = new ArrayList<>(c.getPredecessors().size());
//        {
//            int i = 0;
//            for (final Task predecessor : c.getPredecessors()) {
//                cEdgeWeightArray.add(cW[i]);
//                ++i;
//            }
//        }
//
//        d.addPredecessor(start);
//        final int[] dW = new int[]{9};
//        final ArrayList<Integer> dEdgeWeightArray = new ArrayList<>(d.getPredecessors().size());
//        {
//            int i = 0;
//            for (final Task predecessor : d.getPredecessors()) {
//                dEdgeWeightArray.add(dW[i]);
//                ++i;
//            }
//        }
//
//        Task[] tasks = new Task[]{
//                a, b, c, d, ms, start,
//        };
//
//        // create graph
//
//        final ArrayList<Task> taskDataArray = new ArrayList<>(tasks.length);
//
//        // create taskDataArray
//        for (final Task task : tasks) {
//            taskDataArray.add(task);
//        }
//
//        final HashMap<Task, ArrayList<Task>> edgeArrayMap = new HashMap<>(tasks.length);
//        final HashMap<Task, ArrayList<Integer>> edgeWeightArrayMap = new HashMap<>(tasks.length);
//
//        for (final Task task : tasks) {
//            final ArrayList<Task> edgeArray = new ArrayList<>(task.getPredecessors().size());
//            edgeArrayMap.put(task, edgeArray);
//
//            if (task.equals(ms)) {
//                edgeWeightArrayMap.put(task, msEdgeWeightArray);
//            } else if (task.equals(a)) {
//                edgeWeightArrayMap.put(task, aEdgeWeightArray);
//            } else if (task.equals(b)) {
//                edgeWeightArrayMap.put(task, bEdgeWeightArray);
//            } else if (task.equals(c)) {
//                edgeWeightArrayMap.put(task, cEdgeWeightArray);
//            } else if (task.equals(d)) {
//                edgeWeightArrayMap.put(task, dEdgeWeightArray);
//            } else {
//                edgeWeightArrayMap.put(task, new ArrayList<>());
//            }
//
//            for (final Task predecessor : task.getPredecessors()) {
//                edgeArray.add(predecessor);
//            }
//        }
//
//        Graph<Task> graph = new Graph<>(true, taskDataArray, edgeArrayMap, edgeWeightArrayMap);
//
//        int bonusCount2 = graph.maxFlow(false, ms, start, false);
//        assert (bonusCount2 == 5);
//
//        return graph;
//    }

    private static Task[] createTasks() {
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

        Task[] tasks = new Task[]{
                a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, ms1, ms2, ca, cb, cc
        };

        return tasks;
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
        o.addPredecessor(j, b);  // add b

        ms2.addPredecessor(o, p);

        Task[] tasks = new Task[]{
                a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, ms1, ms2, ca, cb, cc
        };

        return tasks;
    }

    private static Task[] createTasks3() {
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

        Task[] tasks = new Task[]{
                a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, ms1, ms2, ca, cb, cc
        };

        return tasks;
    }
}