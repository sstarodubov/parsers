package org.starodubov.xml;

import java.util.*;

public record XmlObject(String name, List<XmlValue> children) implements XmlValue {

    public XmlObject(String name) {
        this(name, new ArrayList<>());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        XmlObject xmlObject = (XmlObject) o;
        return Objects.equals(name, xmlObject.name());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    public XmlObject getObject(final String name) {
       for (var child : children) {
           if (child instanceof XmlObject (var objName, var _)) {
              if (name.equals(objName)) {
                  return child.asObject();
              }
           }
       }

       return null;
    }

    public XmlNumber getNumber() {
        for (var child : children) {
            if (child instanceof XmlNumber number) {
                return number;
            }
        }

        return null;
    }

    public XmlString getString() {
        for (var child : children) {
            if (child instanceof XmlString s) {
                return s;
            }
        }

        return null;
    }

    public void add(XmlValue newVal) {
        final var idx = children.indexOf(newVal);
        if (idx == -1) {
            children.add(newVal);
        } else {
            final XmlValue existed = children.get(idx);
            if (existed instanceof XmlObject(var _, var existedChildren) && newVal instanceof XmlObject newObj) {
                existedChildren.addAll(newObj.children());
            } else {
                children.add(existed);
            }
        }
    }

    @Override
    public String toString() {
        return "XmlObject{name=%s, children=%s}".formatted(name, children);
    }
}
