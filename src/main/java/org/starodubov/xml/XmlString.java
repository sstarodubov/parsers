package org.starodubov.xml;

public record XmlString(String value) implements XmlValue {

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }

}
