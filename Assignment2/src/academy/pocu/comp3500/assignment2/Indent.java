package academy.pocu.comp3500.assignment2;

import academy.pocu.comp3500.assignment2.datastructure.ArrayList;

import java.io.BufferedWriter;
import java.io.IOException;

public class Indent {
    private static final String INDENT_STRING = "  ";

    private final ArrayList<String> texts;
    private final String indentChar;
    private boolean isPrint;

    public Indent(final char[] indentChar, final int indentCharCount) {
        this.texts = new ArrayList<String>();
        this.indentChar = String.valueOf(indentChar, 0, indentCharCount);
        this.isPrint = true;
    }

    public void printTo(final BufferedWriter writer, final String filter) throws IOException {
        if (this.isPrint == false) {
            return;
        }

        for (final String text : this.texts) {
            if (text.contains(filter) == false) {
                continue;
            }

            writer.write(this.indentChar);
            writer.write(text);
            writer.write(System.lineSeparator());
        }
    }

    public void add(final String text) {
        this.texts.add(text);
    }

    public void discard() {
        this.isPrint = false;
    }
}
