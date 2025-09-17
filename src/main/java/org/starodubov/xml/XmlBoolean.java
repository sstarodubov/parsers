package org.starodubov.xml;


public record XmlBoolean(boolean value) implements XmlValue {
    private static final XmlBoolean TRUE = new XmlBoolean(true);
    private static final XmlBoolean FALSE = new XmlBoolean(false);

    public static XmlBoolean fromString(final String s) {
        if (Boolean.parseBoolean(s)) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}