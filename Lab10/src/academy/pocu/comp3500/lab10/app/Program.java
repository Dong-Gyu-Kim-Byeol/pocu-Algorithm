package academy.pocu.comp3500.lab10.app;

import academy.pocu.comp3500.lab10.Project;
import academy.pocu.comp3500.lab10.project.Task;

import java.util.List;

public class Program {

    public static void main(String[] args) {
//        {
//            Task a = new Task("A", 15);
//            Task b = new Task("B", 12);
//            Task c = new Task("C", 11);
//
//            c.addPredecessor(b);
//            b.addPredecessor(a);
//
//            Task[] tasks = new Task[]{b, c, a};
//
//            List<String> schedule = Project.findSchedule(tasks, false);
//
//            assert (schedule.size() == 3);
//            assert (schedule.get(0).equals("A"));
//            assert (schedule.get(1).equals("B"));
//            assert (schedule.get(2).equals("C"));
//        }
//
//        {
//            Task[] tasks = createTasks();
//
//            List<String> schedule = Project.findSchedule(tasks, true);
//
//            assert (schedule.size() == 9);
//            assert (schedule.get(0).equals("A"));
//            assert (schedule.get(1).equals("B"));
//            assert (schedule.get(2).equals("C"));
//            assert (schedule.indexOf("C") < schedule.indexOf("E"));
//            assert (schedule.indexOf("E") < schedule.indexOf("F"));
//            assert (schedule.indexOf("F") < schedule.indexOf("I"));
//
//            assert (schedule.indexOf("C") < schedule.indexOf("D"));
//            assert (schedule.indexOf("D") < schedule.indexOf("G"));
//            assert (schedule.indexOf("G") < schedule.indexOf("H"));
//        }
//
//        {
//            Task[] tasks = createTasks();
//
//            List<String> schedule = Project.findSchedule(tasks, false);
//
//            assert (schedule.size() == 6);
//            assert (schedule.get(0).equals("A"));
//            assert (schedule.get(1).equals("B"));
//            assert (schedule.get(2).equals("C"));
//            assert (schedule.get(3).equals("E"));
//            assert (schedule.get(4).equals("F"));
//            assert (schedule.get(5).equals("I"));
//        }

        {
            Task[] tasks = createMultiCycleTasksOne();
            List<String> schedule = Project.findSchedule(tasks, true);

            assert (schedule.size() == 14);
            assert (schedule.indexOf("I") < schedule.indexOf("K"));
            assert (schedule.indexOf("J") < schedule.indexOf("N"));
            assert (schedule.indexOf("N") < schedule.indexOf("K"));
        }
    }

    private static Task[] createTasks() {
        Task a = new Task("A", 12);
        Task b = new Task("B", 7);
        Task c = new Task("C", 10);
        Task d = new Task("D", 9);
        Task e = new Task("E", 8);
        Task f = new Task("F", 11);
        Task g = new Task("G", 14);
        Task h = new Task("H", 13);
        Task i = new Task("I", 6);

        i.addPredecessor(f);
        f.addPredecessor(e);
        e.addPredecessor(b, c);
        d.addPredecessor(c, h);
        c.addPredecessor(b);
        b.addPredecessor(a);
        h.addPredecessor(g);
        g.addPredecessor(d);

        return new Task[]{
                a, b, c, d, e, f, g, h, i
        };
    }

    private static Task[] createMultiCycleTasksOne() {
        Task a = new Task("A", 12);
        Task b = new Task("B", 7);
        Task c = new Task("C", 10);
        Task d = new Task("D", 9);
        Task e = new Task("E", 8);
        Task f = new Task("F", 11);
        Task g = new Task("G", 14);
        Task h = new Task("H", 13);
        Task i = new Task("I", 6);
        Task j = new Task("J", 6);
        Task k = new Task("K", 6);
        Task l = new Task("L", 6);
        Task m = new Task("M", 6);
        Task n = new Task("N", 6);

        m.addPredecessor(l);
        l.addPredecessor(h);
        k.addPredecessor(j, n);
        i.addPredecessor(k, h);
        j.addPredecessor(i);
        h.addPredecessor(d);
        g.addPredecessor(f);
        f.addPredecessor(e);
        e.addPredecessor(d, g);
        d.addPredecessor(c);
        c.addPredecessor(b);
        b.addPredecessor(a);
        n.addPredecessor(j);


        return new Task[]{
                l, m, n, c, d, e, g, h, i, j, k, a, b, f
        };
    }
}