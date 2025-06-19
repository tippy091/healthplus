package vn.com.nested.backend.common.operation.audit;

import vn.com.nested.backend.common.operation.slog.OPLogWriter;
import vn.com.nested.backend.common.operation.slog.logging.LogMessage;
import vn.com.nested.backend.common.operation.slog.logging.LogWriter;

/**
 * @author tippy091
 * @created 17/06/2025
 * @project healthcare_backend
 **/
public enum PortalLogWriter implements LogWriter {
    INSTANCE;

    private OPLogWriter writer;

    private PortalLogWriter() {
    }

    public void write(LogMessage message) {
        this.writer.write(message);
    }

    public void register(OPLogWriter logWriter) {
        this.writer = logWriter;
    }
}
