package academy.pocu.comp3500.assignment2;

import java.io.BufferedWriter;
import java.io.IOException;

public final class Text extends Log {
    private final String text;

    // ---

    Text(final String text) {
        this.text = text;
    }

    // ---

    @Override
    final void printTo(final BufferedWriter writer, final String filter, final String indentSpace) {
        if (!this.text.contains(filter)) {
            return;
        }

        try {
            writer.write(indentSpace);
            writer.write(this.text);
            writer.write(System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
