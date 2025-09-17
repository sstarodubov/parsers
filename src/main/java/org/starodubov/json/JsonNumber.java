package org.starodubov.json;

public class JsonNumber implements JsonValue {
    private final double value;

    public JsonNumber(double value) {
        this.value = value;
    }

    public double getValue() { return value; }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}
