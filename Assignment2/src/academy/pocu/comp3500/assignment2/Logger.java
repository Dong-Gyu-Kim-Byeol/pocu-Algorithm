package academy.pocu.comp3500.assignment2;

import academy.pocu.comp3500.assignment2.datastructure.Stack;

import java.io.BufferedWriter;
import java.io.IOException;

public final class Logger {
    private static final char[] BASE_INDENT_CHAR = {' ', ' '};

    private static Indent rootIndent;

    private static Stack<Indent> indentStack;
    private static char[] indentChar;
    private static int indentCharNextIndex;

    public static void log(final String text) {
        if (rootIndent == null) {
            clear();
        }

        indentStack.peek().addLog(new Log(ELogType.TEXT, text));
    }

    public static void printTo(final BufferedWriter writer) throws IOException {
        printTo(writer, "");
    }

    public static void printTo(final BufferedWriter writer, final String filter) throws IOException {
        rootIndent.printTo(writer, filter);
        writer.flush();
    }

    public static void clear() {
        indentChar = new char[4];
        indentCharNextIndex = 0;

        rootIndent = new Indent(indentChar, indentCharNextIndex);

        indentStack = new Stack<Indent>();
        indentStack.push(rootIndent);
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
        indentStack.peek().addLog(new Log(ELogType.INDENT, indent));
        indentStack.push(indent);
        return indent;
    }

    public static void unindent() {
        if (indentStack.getSize() == 1) {
            assert (indentCharNextIndex == 0);
            return;
        }

        assert (indentCharNextIndex >= BASE_INDENT_CHAR.length);

        for (int i = 0; i < BASE_INDENT_CHAR.length; i++) {
            indentChar[indentCharNextIndex - i] = 0;
        }
        indentCharNextIndex -= BASE_INDENT_CHAR.length;

        indentStack.pop();
    }

    // private
}