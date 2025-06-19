package vn.com.nested.backend.common.operation.json;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.util.function.Consumer;

/**
 * @author tippy091
 * @created 02/06/2025
 * @project server
 **/

public class JsonArray {
    private final com.google.gson.JsonArray ja;

    public JsonArray() {
        this.ja = new com.google.gson.JsonArray();
    }

    JsonArray(com.google.gson.JsonArray ja) {
        this.ja = ja;
    }

    public static JsonArray parse(String str) throws JsonParsingException {
        try {
            return new JsonArray(JsonParser.parseString(str).getAsJsonArray());
        } catch (JsonSyntaxException var2) {
            throw new JsonParsingException(var2);
        }
    }

    public int size() {
        return this.ja.size();
    }

    public String getString(int index) {
        return this.ja.get(index).getAsString();
    }

    public int getInt(int index) {
        return this.ja.get(index).getAsInt();
    }

    public long getLong(int index) {
        return this.ja.get(index).getAsLong();
    }

    public boolean getBoolean(int index) {
        return this.ja.get(index).getAsBoolean();
    }

    public JsonObject getObject(int index) {
        return new JsonObject(this.ja.get(index).getAsJsonObject());
    }

    public void foreach(Consumer<Object> consumer) {
        this.ja.forEach(consumer);
    }

    public String toString() {
        return this.ja.toString();
    }
}