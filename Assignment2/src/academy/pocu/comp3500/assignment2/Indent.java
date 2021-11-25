package academy.pocu.comp3500.assignment2;

import academy.pocu.comp3500.assignment2.datastructure.ArrayList;

import java.io.BufferedWriter;

public final class Indent extends Log {
    private static final String BASE_INDENT_SPACE = "  ";

    // ---

    private final ArrayList<Log> logs;

    // ---

    // package
    Indent() {
        this.logs = new ArrayList<>();
    }

    // ---

    public final void discard() {
        this.logs.clear();
    }

    // ---

    final void addLog(final Log log) {
        this.logs.add(log);
    }

    final void printTo(final BufferedWriter writer, final String filter) {
        for (final Log log : this.logs) {
            log.printTo(writer, filter, "");
        }
    }

    @Override
    final void printTo(final BufferedWriter writer, final String filter, String indentSpace) {
        indentSpace = indentSpace + BASE_INDENT_SPACE;

        for (final Log log : this.logs) {
            log.printTo(writer, filter, indentSpace);
        }
    }
}
