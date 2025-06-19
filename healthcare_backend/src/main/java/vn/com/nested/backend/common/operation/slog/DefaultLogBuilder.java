package vn.com.nested.backend.common.operation.slog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Date;

/**
 * @author tippy091
 * @created 02/06/2025
 * @project server
 **/

public class DefaultLogBuilder implements LogBuilder {
    private final JsonObject builder = new JsonObject();
    private final Gson builderPrettyFormat = (new GsonBuilder()).setPrettyPrinting().create();
    private final Date startTime = new Date();

    public DefaultLogBuilder() {
    }

    public void append(String key, String value) {
        try {
            this.builder.addProperty(key, value);
        } catch (RuntimeException var4) {
        }

    }

    public void append(String key, Number value) {
        try {
            this.builder.addProperty(key, value);
        } catch (RuntimeException var4) {
        }

    }

    public void append(String key, Boolean value) {
        try {
            this.builder.addProperty(key, value);
        } catch (RuntimeException var4) {
        }

    }

    public void append(String key, JsonObject value) {
        try {
            this.builder.add(key, value);
        } catch (RuntimeException var4) {
        }

    }

    public void append(String key, JsonArray value) {
        try {
            this.builder.add(key, value);
        } catch (Exception var4) {
        }

    }

    public Date getInitTime() {
        return this.startTime;
    }

    public String build() {
        return this.builder.toString();
    }

    public String buildPrettyFormat() {
        return this.builderPrettyFormat.toJson(this.builder);
    }

    public JsonObject buildObject() {
        return this.builder;
    }
}
