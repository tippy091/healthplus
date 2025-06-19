package vn.com.nested.backend.common.operation.slog;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Date;

/**
 * @author tippy091
 * @created 02/06/2025
 * @project server
 **/

public interface LogBuilder {
    Date getInitTime();

    void append(String var1, String var2);

    void append(String var1, Number var2);

    void append(String var1, Boolean var2);

    void append(String var1, JsonObject var2);

    void append(String var1, JsonArray var2);

    String build();

    String buildPrettyFormat();

    JsonObject buildObject();
}
