package academy.pocu.comp3500.assignment2.app;

import academy.pocu.comp3500.assignment2.Indent;
import academy.pocu.comp3500.assignment2.Logger;
import academy.pocu.comp3500.assignment2.datastructure.Sort;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static academy.pocu.comp3500.assignment2.Logger.log;

public class Program {

    public static void main(String[] args) throws IOException {
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter("mylog1.log"));

            log("hello");
            log("world");
            log("this is logging at the top level");

            Logger.indent();
            {
                log("using indent, you can indent to organize your logs");
                log("call unindent() to decrease the indentation level");
            }
            Logger.unindent();

            Indent indent = Logger.indent();
            {
                log("whatever I say here");
                log("is discarded!");
                log("too bad!");

                indent.discard();
            }
            Logger.unindent();

            Logger.indent();
            {
                log("this won't be discarded");
                log("it's true!");

                doMagic();
            }
            Logger.unindent();

            log("back to the top level!");
            log("and let's print the logs");

            Logger.printTo(writer);

            Logger.clear();

            log("log was just cleared");
            log("so you start logging from the top level again");

            Logger.printTo(writer);

            writer.close();
        }

        Logger.clear();

        {
            final BufferedWriter writer1 = new BufferedWriter(new FileWriter("quicksort1.log"));
            final BufferedWriter writer2 = new BufferedWriter(new FileWriter("quicksort2.log"));

            int[] nums = new int[]{30, 10, 80, 90, 50, 70, 40};

            Sort.quickSort(nums);

            Logger.printTo(writer1);

            Logger.printTo(writer2, "90");

            writer1.close();
            writer2.close();
        }
    }

    private static void doMagic() {
        Indent indent = Logger.indent();
        {
            log("you can also nest an indent");
            log("like this!");
        }
        Logger.unindent();
    }
}