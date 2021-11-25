package academy.pocu.comp3500.lab10.app;

import academy.pocu.comp3500.lab10.Project;
import academy.pocu.comp3500.lab10.project.Task;

import java.util.List;

public class Program {

    public static void main(String[] args) {
        {
//            index                                   title    estimate            predecessors
//            0    c1695778-b9ff-41fb-ad3d-7808f860de75           2
//            1    6db24098-c4f3-4fca-9592-c9bbf9756382           4                       0
//            2    d529c340-da51-4ceb-bf90-80f223752de1           3                       1
//            3    2c542814-d2a6-434d-9007-b1af6bde7366           5                       2
//            4    1c8ec1d0-8d5e-4ac6-aca9-947024437054           7
//            5    ab29ac2d-4240-428f-bb46-312659375adb           2                    1, 4
//            6    834e57e1-09dd-4650-b889-ac85e7eda35d           8                    3,;


            Task t0 = new Task("A0", 2);
            Task t1 = new Task("B1", 4);
            Task t2 = new Task("C2", 3);
            Task t3 = new Task("D3", 5);
            Task t4 = new Task("E4", 7);
            Task t5 = new Task("F5", 2);
            Task t6 = new Task("G6", 8);

            t1.addPredecessor(t0);
            t2.addPredecessor(t1);
            t3.addPredecessor(t2);

            t5.addPredecessor(t1,t4);
            t6.addPredecessor(t3);

            Task[] tasks = new Task[]{
                    t6, t5, t1, t0, t4, t3, t2,
            };

            List<String> schedule = Project.findSchedule(tasks, false);

            assert (schedule.size() == 7);
        }

        {
            Task a = new Task("A", 15);
            Task b = new Task("B", 12);
            Task c = new Task("C", 11);

            c.addPredecessor(b);
            b.addPredecessor(a);

            Task[] tasks = new Task[]{b, c, a};

            List<String> schedule = Project.findSchedule(tasks, false);

            assert (schedule.size() == 3);
            assert (schedule.get(0).equals("A"));
            assert (schedule.get(1).equals("B"));
            assert (schedule.get(2).equals("C"));
        }

        {
            Task[] tasks = createTasks();

            List<String> schedule = Project.findSchedule(tasks, true);

            assert (schedule.size() == 9);
            assert (schedule.get(0).equals("A"));
            assert (schedule.get(1).equals("B"));
            assert (schedule.get(2).equals("C"));
            assert (schedule.indexOf("C") < schedule.indexOf("E"));
            assert (schedule.indexOf("E") < schedule.indexOf("F"));
            assert (schedule.indexOf("F") < schedule.indexOf("I"));

            assert (schedule.indexOf("C") < schedule.indexOf("D"));
            assert (schedule.indexOf("D") < schedule.indexOf("G"));
            assert (schedule.indexOf("G") < schedule.indexOf("H"));
        }

        {
            Task[] tasks = createTasks();

            List<String> schedule = Project.findSchedule(tasks, false);

            assert (schedule.size() == 6);
            assert (schedule.get(0).equals("A"));
            assert (schedule.get(1).equals("B"));
            assert (schedule.get(2).equals("C"));
            assert (schedule.get(3).equals("E"));
            assert (schedule.get(4).equals("F"));
            assert (schedule.get(5).equals("I"));
        }

        {
            Task[] tasks = createMultiCycleTasksOne();
            List<String> schedule = Project.findSchedule(tasks, true);

            assert (schedule.size() == 14);
            assert (schedule.indexOf("I") < schedule.indexOf("K"));
            assert (schedule.indexOf("J") < schedule.indexOf("N"));
            assert (schedule.indexOf("N") < schedule.indexOf("K"));
        }

        {
            Task[] tasks = createMultiCycleTasksOne();
            List<String> schedule = Project.findSchedule(tasks, false);

            assert (schedule.size() == 7);
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