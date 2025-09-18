package org.starodubov.ll1.json;
import java.util.*;

public class JsonParserLL1 {
    private final String text;
    private int pos;

    public JsonParserLL1(String text) {
        this.text = text;
        this.pos = 0;
    }

    // === Утилиты ===

    private char peek() {
        if (pos >= text.length()) return '\0';
        return text.charAt(pos);
    }

    private char next() {
        if (pos >= text.length()) return '\0';
        return text.charAt(pos++);
    }

    private void skipWhitespace() {
        while (peek() == ' ' || peek() == '\t' || peek() == '\n' || peek() == '\r') {
            next();
        }
    }

    private void expect(char ch) {
        skipWhitespace();
        if (peek() != ch) {
            throw new RuntimeException("Expected '" + ch + "' at pos " + pos + ", got '" + peek() + "'");
        }
        next();
    }

    // === LL(1): выбор по первому символу ===

    private Object parseValue() {
        skipWhitespace();
        char lookahead = peek();

        switch (lookahead) {
            case '"':
                return parseString();
            case '{':
                return parseObject();
            case '[':
                return parseArray();
            case 't':
                return parseTrue();
            case 'f':
                return parseFalse();
            case 'n':
                return parseNull();
            case '-', '+', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9':
                return parseNumber();
            default:
                throw new RuntimeException("Unexpected token at pos " + pos + ": '" + lookahead + "'");
        }
    }

    private String parseString() {
        expect('"');
        StringBuilder sb = new StringBuilder();
        while (peek() != '"') {
            if (peek() == '\0') {
                throw new RuntimeException("Unterminated string at pos " + pos);
            }
            sb.append(next());
        }
        next(); // skip closing "
        return sb.toString();
    }

    private Number parseNumber() {
        int start = pos;

        // Optional sign
        if (peek() == '+' || peek() == '-') {
            next();
        }

        // Integer part
        if (!Character.isDigit(peek())) {
            throw new RuntimeException("Invalid number at pos " + pos);
        }
        while (Character.isDigit(peek())) {
            next();
        }

        // Fractional part
        if (peek() == '.') {
            next();
            if (!Character.isDigit(peek())) {
                throw new RuntimeException("Invalid float at pos " + pos);
            }
            while (Character.isDigit(peek())) {
                next();
            }
        }

        // Parse as int or double
        String numStr = text.substring(start, pos);
        return numStr.contains(".") ? Double.parseDouble(numStr) : Integer.parseInt(numStr);
    }

    private Boolean parseTrue() {
        if (next() != 't' || next() != 'r' || next() != 'u' || next() != 'e') {
            throw new RuntimeException("Expected 'true'");
        }
        return true;
    }

    private Boolean parseFalse() {
        if (next() != 'f' || next() != 'a' || next() != 'l' || next() != 's' || next() != 'e') {
            throw new RuntimeException("Expected 'false'");
        }
        return false;
    }

    private Object parseNull() {
        if (next() != 'n' || next() != 'u' || next() != 'l' || next() != 'l') {
            throw new RuntimeException("Expected 'null'");
        }
        return null;
    }

    private List<Object> parseArray() {
        expect('[');
        skipWhitespace();

        if (peek() == ']') {
            next();
            return new ArrayList<>();
        }

        List<Object> values = new ArrayList<>();
        while (true) {
            values.add(parseValue());
            skipWhitespace();
            if (peek() == ',') {
                next();
                skipWhitespace();
            } else if (peek() == ']') {
                break;
            } else {
                throw new RuntimeException("Expected ',' or ']' in array at pos " + pos);
            }
        }
        expect(']');
        return values;
    }

    private Map<String, Object> parseObject() {
        expect('{');
        skipWhitespace();

        if (peek() == '}') {
            next();
            return new HashMap<>();
        }

        Map<String, Object> obj = new HashMap<>();
        while (true) {
            String key = parseString();
            skipWhitespace();
            expect(':');
            Object value = parseValue();
            obj.put(key, value);

            skipWhitespace();
            if (peek() == ',') {
                next();
                skipWhitespace();
            } else if (peek() == '}') {
                break;
            } else {
                throw new RuntimeException("Expected ',' or '}' in object at pos " + pos);
            }
        }
        expect('}');
        return obj;
    }

    public Object parse() {
        skipWhitespace();
        Object result = parseValue();
        skipWhitespace();
        if (peek() != '\0') {
            throw new RuntimeException("Unexpected trailing characters at pos " + pos);
        }
        return result;
    }

    // === Тестирование ===

    public static void main(String[] args) {
            var test = """
                    {
                     "hello" : {
                        "test" :  true 
                        }
                    }
                    
                    """;
            try {
                JsonParserLL1 parser = new JsonParserLL1(test);
                Object result = parser.parse();
                System.out.println("✅ " + test + " → " + result);
            } catch (Exception e) {
                System.out.println("❌ " + test + " → Error: " + e.getMessage());
            }
    }
}
