package academy.pocu.comp3500.assignment2;

import academy.pocu.comp3500.assignment2.datastructure.ArrayList;

import java.io.BufferedWriter;
import java.io.IOException;

public final class Logger {
    private static final char[] BASE_INDENT_CHAR = {' ', ' '};

    private static ArrayList<Indent> indents;
    private static char[] indentChar;
    private static int indentCharNextIndex;

    public static void log(final String text) {
        if (indents == null) {
            clear();
        }

        assert (indents.getSize() > 0);
        indents.get(indents.getSize() - 1).add(text);
    }

    public static void printTo(final BufferedWriter writer) throws IOException {
        printTo(writer, "");
    }

    public static void printTo(final BufferedWriter writer, final String filter) throws IOException {
        for (final Indent indent : indents) {
            indent.printTo(writer, filter);
        }

        writer.flush();
    }

    public static void clear() {
        indentCharNextIndex = 0;

        if (indents == null) {
            indents = new ArrayList<Indent>();
        } else {
            indents.clear();
        }

        indentChar = new char[4];
        indentCharNextIndex = 0;

        Indent indent = new Indent(indentChar, indentCharNextIndex);
        indents.add(indent);
    }

    public static Indent indent() {
        if (indentCharNextIndex + BASE_INDENT_CHAR.length >= indentChar.length - 1) {
            char[] temp = indentChar;
            indentChar = new char[temp.length * 2];
            for (int i = 0; i < temp.length; i++) {
                indentChar[i] = temp[i];
            }
        }

        for (int i = 0; i < BASE_INDENT_CHAR.length; i++) {
            indentChar[indentCharNextIndex + i] = BASE_INDENT_CHAR[i];
        }
        indentCharNextIndex += BASE_INDENT_CHAR.length;

        Indent indent = new Indent(indentChar, indentCharNextIndex);
        indents.add(indent);
        return indent;
    }

    public static void unindent() {
        if (indentCharNextIndex <= 0) {
            return;
        }

        for (int i = 0; i < BASE_INDENT_CHAR.length; i++) {
            indentChar[indentCharNextIndex - i] = 0;
        }
        indentCharNextIndex -= BASE_INDENT_CHAR.length;

        Indent indent = new Indent(indentChar, indentCharNextIndex);
        indents.add(indent);
    }

    // private

}