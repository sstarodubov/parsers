package org.starodubov.xml;

public record XmlNumber(long value) implements XmlValue {

    public static XmlNumber fromString(final String s) {
        return new XmlNumber(Long.parseLong(s));
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }
}
