package vn.com.nested.backend.common.operation.exception;

/**
 * @author tippy091
 * @created 02/06/2025
 * @project server
 **/
public class ExportExcelException extends Exception {
    public ExportExcelException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExportExcelException(Throwable cause) {
        super(cause);
    }

    public ExportExcelException(String message) {
        super(message);
    }
}
