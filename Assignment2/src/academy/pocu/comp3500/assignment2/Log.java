package academy.pocu.comp3500.assignment2;

import java.io.BufferedWriter;

public abstract class Log {
    public Log() {
    }

    // ---

    abstract void printTo(final BufferedWriter writer, final String filter, final String indentSpace);
}
