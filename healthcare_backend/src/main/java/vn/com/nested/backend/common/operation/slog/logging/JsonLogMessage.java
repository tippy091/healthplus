package vn.com.nested.backend.common.operation.slog.logging;

import vn.com.nested.backend.common.operation.json.JsonObject;

/**
 * @author tippy091
 * @created 02/06/2025
 * @project server
 **/
public class JsonLogMessage implements LogMessage {
    private final JsonObject jo;

    public JsonLogMessage(JsonObject jo) {
        this.jo = jo;
    }

    public String getRequestId() {
        return this.jo.getString("requestId");
    }

    public String toString() {
        return this.jo.set("endTime", System.currentTimeMillis()).toString();
    }
}
