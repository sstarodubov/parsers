package org.starodubov.xml;


public record XmlNull() implements XmlValue {

    public static final XmlNull INSTANCE = new XmlNull();

    @Override
    public String toString() {
        return "NULL";
    }
}
