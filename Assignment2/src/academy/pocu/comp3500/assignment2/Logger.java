package academy.pocu.comp3500.assignment2;

import academy.pocu.comp3500.assignment2.datastructure.ArrayList;

import java.io.BufferedWriter;
import java.io.IOException;

public final class Logger {
    private static final ArrayList<String> texts = new ArrayList<String>();

    public static void log(final String text) {
        texts.add(text);
    }

    public static void printTo(final BufferedWriter writer) throws IOException {
        for (final String text : texts) {
            writer.write(text);
            writer.newLine();
        }

        writer.flush();
    }

    public static void printTo(final BufferedWriter writer, final String filter) {

    }

    public static void clear() {
        texts.clear();
    }

    public static Indent indent() {
        return null;
    }

    public static void unindent() {

    }
}