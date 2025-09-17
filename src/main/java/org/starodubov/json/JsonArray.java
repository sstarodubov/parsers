package org.starodubov.json;

import java.util.ArrayList;
import java.util.List;

class JsonArray implements JsonValue {
    private final List<JsonValue> elements = new ArrayList<>();

    public void add(JsonValue value) {
        elements.add(value);
    }

    public JsonValue get(int index) {
        return elements.get(index);
    }

    public int size() {
        return elements.size();
    }

    @Override
    public String toString() {
        return elements.toString();
    }
}