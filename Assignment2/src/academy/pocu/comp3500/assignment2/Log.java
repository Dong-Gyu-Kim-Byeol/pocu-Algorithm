package academy.pocu.comp3500.assignment2;

public class Log {
    private final ELogType logType;
    private final Indent indentOrNull;
    private final String textOrNull;

    public Log(final ELogType logType, final Object textOrIndent) {
        this.logType = logType;
        switch (this.logType) {
            case TEXT:
                assert (textOrIndent instanceof String);
                this.textOrNull = (String) textOrIndent;
                this.indentOrNull = null;
                break;
            case INDENT:
                assert (textOrIndent instanceof Indent);
                this.textOrNull = null;
                this.indentOrNull = (Indent) textOrIndent;
                break;
            default:
                throw new IllegalArgumentException("unknown type");
        }
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
