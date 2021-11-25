package academy.pocu.comp3500.assignment2;

import academy.pocu.comp3500.assignment2.datastructure.Stack;

import java.io.BufferedWriter;
import java.io.IOException;

public final class Logger {
    private static Indent root;
    private static Stack<Indent> indentStack;

    // ---

    public static void clear() {
        root = new Indent();
        indentStack = new Stack<>();
        indentStack.push(root);
    }

    private Logger() {
    }

    // ---

    public static void log(final String text) {
        if (root == null || indentStack == null || indentStack.getSize() == 0) {
            clear();
        }

        final Indent top = indentStack.peek();
        top.addLog(new Text(text));
    }

    public static void printTo(final BufferedWriter writer) {
        printTo(writer, "");
    }

    public static void printTo(final BufferedWriter writer, final String filter) {
        root.printTo(writer, filter);

        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Indent indent() {
        final Indent indent = new Indent();

        final Indent top = indentStack.peek();
        top.addLog(indent);

        indentStack.push(indent);

        return indent;
    }

    public static void unindent() {
        indentStack.pop();
    }
}