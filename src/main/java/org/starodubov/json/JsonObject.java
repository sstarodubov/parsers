package org.starodubov.json;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class JsonObject implements JsonValue {
    private final Map<String, JsonValue> members = new LinkedHashMap<>();

    public void add(String key, JsonValue value) {
        members.put(key, value);
    }

    public JsonValue get(String key) {
        return members.get(key);
    }

    public Set<String> keys() {
        return members.keySet();
    }

    @Override
    public String toString() {
        return members.toString();
    }
}
