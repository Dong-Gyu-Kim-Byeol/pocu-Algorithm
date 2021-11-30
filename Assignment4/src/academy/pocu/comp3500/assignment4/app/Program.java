package academy.pocu.comp3500.assignment4.app;

import academy.pocu.comp3500.assignment4.Project;
import academy.pocu.comp3500.assignment4.project.Task;

public class Program {

    public static void main(String[] args) {
//        {
//            Task[] tasks = createTasks();
//
//            Project project = new Project(tasks);
//
//            int manMonths1 = project.findTotalManMonths("ms1");
//            assert (manMonths1 == 17);
//
//            int manMonths2 = project.findTotalManMonths("ms2");
//            assert (manMonths2 == 46);
//
//            int minDuration1 = project.findMinDuration("ms1");
//            assert (minDuration1 == 14);
//
//            int minDuration2 = project.findMinDuration("ms2");
//            assert (minDuration2 == 32);
//
//            int bonusCount1 = project.findMaxBonusCount("ms1");
//            assert (bonusCount1 == 6);
//
//            int bonusCount2 = project.findMaxBonusCount("ms2");
//            assert (bonusCount2 == 6);
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

    }

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