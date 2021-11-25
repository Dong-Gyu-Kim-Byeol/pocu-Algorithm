package academy.pocu.comp3500.assignment2;

public class Log {
    private final ELogType logType;
    private final Indent indentOrNull;
    private final String textOrNull;

    public Log(final String text) {
        this.logType = ELogType.TEXT;
        this.textOrNull = text;
        this.indentOrNull = null;
    }

    public Log(final Indent indent) {
        this.logType = ELogType.INDENT;
        this.textOrNull = null;
        this.indentOrNull = indent;
    }

    public ELogType getLogType() {
        return logType;
    }

    public Indent getIndentOrNull() {
        return indentOrNull;
    }

    public String getTextOrNull() {
        return textOrNull;
    }
}
