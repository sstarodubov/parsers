package org.starodubov.json;

public class JsonBoolean implements JsonValue {
    private final boolean value;

    public JsonBoolean(boolean value) {
        this.value = value;
    }

    public boolean getValue() { return value; }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}