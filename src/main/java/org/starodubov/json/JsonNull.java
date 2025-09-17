package org.starodubov.json;

public class JsonNull implements JsonValue {
    private JsonNull() {}

    public static final JsonNull INSTANCE = new JsonNull();

    @Override
    public String toString() {
        return "null";
    }
}
