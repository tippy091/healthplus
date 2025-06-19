package vn.com.nested.backend.common.operation.slog.logging;

/**
 * @author tippy091
 * @created 02/06/2025
 * @project server
 **/
public enum SingletonLogWriter implements LogWriter {
    INSTANCE;

    private LogWriter writer;

    private SingletonLogWriter() {
    }

    public void write(LogMessage message) {
        this.writer.write(message);
    }

    public void register(LogWriter logWriter) {
        this.writer = logWriter;
    }
}
