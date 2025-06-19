package vn.com.nested.backend.logging;

import vn.com.nested.backend.common.operation.json.JsonObject;
import vn.com.nested.backend.common.operation.slog.logging.JsonLogMessage;
import vn.com.nested.backend.common.operation.slog.logging.LogMessage;
/**
 * @author tippy091
 * @created 17/06/2025
 * @project healthcare_backend
 **/

public class JsonLogBuilder {
    private final JsonObject jo = new JsonObject();

    public JsonLogBuilder() {
    }

    public JsonLogBuilder set(String name, String value) {
        if (null == value) {
            value = "null";
        }

        this.jo.set(name, value);
        return this;
    }

    public JsonLogBuilder set(String name, int value) {
        this.jo.set(name, value);
        return this;
    }

    public JsonLogBuilder set(String name, long value) {
        this.jo.set(name, value);
        return this;
    }

    public JsonLogBuilder set(String name, boolean value) {
        this.jo.set(name, value);
        return this;
    }

    public JsonLogBuilder set(String name, JsonObject value) {
        this.jo.set(name, value);
        return this;
    }

    public LogMessage build() {
        return new JsonLogMessage(this.jo);
    }
}
