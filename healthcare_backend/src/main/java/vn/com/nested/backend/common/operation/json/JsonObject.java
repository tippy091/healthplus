package vn.com.nested.backend.common.operation.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.util.function.Consumer;

/**
 * @author tippy091
 * @created 02/06/2025
 * @project server
 **/

public class JsonObject {
    private final com.google.gson.JsonObject jo;

    public JsonObject() {
        this.jo = new com.google.gson.JsonObject();
    }

    public JsonObject(com.google.gson.JsonObject jo) {
        this.jo = jo;
    }

    public static JsonObject parse(String str) throws JsonParsingException {
        try {
            return new JsonObject(JsonParser.parseString(str).getAsJsonObject());
        } catch (JsonSyntaxException var2) {
            throw new JsonParsingException(var2);
        }
    }

    public int size() {
        return this.jo.size();
    }

    public JsonObject set(String name, String value) {
        this.jo.addProperty(name, value);
        return this;
    }

    public JsonObject set(String name, int value) {
        this.jo.addProperty(name, value);
        return this;
    }

    public JsonObject set(String name, long value) {
        this.jo.addProperty(name, value);
        return this;
    }

    public JsonObject set(String name, boolean value) {
        this.jo.addProperty(name, value);
        return this;
    }

    public JsonObject set(String name, JsonObject value) {
        this.jo.add(name, value.jo);
        return this;
    }

    public String getString(String name) {
        JsonElement je = this.jo.get(name);
        return null != je && !je.isJsonNull() ? je.getAsString() : null;
    }

    public int getInt(String name) throws NoSuchFieldException {
        JsonElement je = this.jo.get(name);
        if (null == je) {
            throw new NoSuchFieldException(name);
        } else {
            return je.getAsInt();
        }
    }

    public long getLong(String name) throws NoSuchFieldException {
        JsonElement je = this.jo.get(name);
        if (null == je) {
            throw new NoSuchFieldException(name);
        } else {
            return je.getAsLong();
        }
    }

    public boolean getBoolean(String name) throws NoSuchFieldException {
        JsonElement je = this.jo.get(name);
        if (null == je) {
            throw new NoSuchFieldException(name);
        } else {
            return je.getAsBoolean();
        }
    }

    public JsonObject getObject(String name) {
        JsonElement je = this.jo.get(name);
        return null == je ? null : new JsonObject(je.getAsJsonObject());
    }

    public JsonArray getArray(String name) {
        JsonElement je = this.jo.get(name);
        return null == je ? null : new JsonArray(je.getAsJsonArray());
    }

    public void foreach(Consumer<String> consumer) {
        this.jo.keySet().iterator().forEachRemaining(consumer);
    }

    public String toString() {
        return this.jo.toString();
    }
}
