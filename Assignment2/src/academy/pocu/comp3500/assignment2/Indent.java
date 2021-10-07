package academy.pocu.comp3500.assignment2;

import academy.pocu.comp3500.assignment2.datastructure.ArrayList;

import java.io.BufferedWriter;
import java.io.IOException;

public class Indent {
    private final ArrayList<Log> logs;

    private final String indentChar;
    private boolean isPrint;

    public Indent(final char[] indentChar, final int indentCharCount) {
        this.logs = new ArrayList<Log>();

        this.indentChar = String.valueOf(indentChar, 0, indentCharCount);
        this.isPrint = true;
    }

    public void addLog(final Log log) {
        this.logs.add(log);
    }

    public void printTo(final BufferedWriter writer, final String filter) throws IOException {
        if (this.isPrint == false) {
            return;
        }

        for (final Log log : this.logs) {
            switch (log.getLogType()) {
                case TEXT: {
                    if (log.getTextOrNull().contains(filter) == false) {
                        continue;
                    }

                    writer.write(this.indentChar);
                    writer.write(log.getTextOrNull());
                    writer.write(System.lineSeparator());
                    break;
                }
                case INDENT: {
                    log.getIndentOrNull().printTo(writer, filter);
                    break;
                }
                default:
                    throw new IllegalArgumentException("unknown type");
            }
        }
    }

    public void discard() {
        this.isPrint = false;
    }
}
