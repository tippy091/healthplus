package vn.com.nested.backend.common.operation.slog.logging;

/**
 * @author tippy091
 * @created 02/06/2025
 * @project server
 **/

public interface LogMessage {
    String START_FIELD = "startTime";
    String END_FIELD = "endTime";
    String MODULE_FIELD = "module";
    String OPERATION_FIELD = "operation";
    String OPERATION_VERSION_FIELD = "opVer";
    String REQUEST_ID_FIELD = "requestId";
    String CALLER_CODE_FIELD = "requesterCode";
    String STATUS_FIELD = "respCode";
    String ERROR_FIELD = "error";
    String DURATION_FIELD = "duration";
    String RESULT_FIELD = "result";

    String toString();

    String getRequestId();
}
