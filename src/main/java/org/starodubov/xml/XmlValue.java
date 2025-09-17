package org.starodubov.xml;

public interface XmlValue {

    default XmlObject asObject() {
       return (XmlObject) this;
    }
    default XmlNumber asNumber() {
        return (XmlNumber) this;
    }

    default XmlString asString() {
        return (XmlString) this;
    }

    default XmlBoolean asBoolen() {
        return (XmlBoolean) this;
    }

    default boolean isNull() {
        return this instanceof XmlNull;
    }
}
