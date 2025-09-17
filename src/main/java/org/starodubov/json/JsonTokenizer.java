package org.starodubov.json;

public class JsonTokenizer {
    private final String input;
    private int pos = 0;

    public static class Token {
        enum Type {
            LEFT_BRACE, RIGHT_BRACE,
            LEFT_BRACKET, RIGHT_BRACKET,
            COLON, COMMA,
            STRING, NUMBER,
            TRUE, FALSE, NULL,
            EOF
        }

        final Type type;
        final String lexeme; // исходный текст

        Token(Type type, String lexeme) {
            this.type = type;
            this.lexeme = lexeme;
        }

        @Override
        public String toString() {
            return type + "(" + lexeme + ")";
        }
    }

    public JsonTokenizer(String input) {
        this.input = input;
    }

    private void skipWhitespace() {
        while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
            pos++;
        }
    }

    Token nextToken() {
        skipWhitespace();
        if (pos >= input.length()) {
            return new Token(Token.Type.EOF, "");
        }

        char c = input.charAt(pos);

        // Символы
        switch (c) {
            case '{':
                pos++;
                return new Token(Token.Type.LEFT_BRACE, "{");
            case '}':
                pos++;
                return new Token(Token.Type.RIGHT_BRACE, "}");
            case '[':
                pos++;
                return new Token(Token.Type.LEFT_BRACKET, "[");
            case ']':
                pos++;
                return new Token(Token.Type.RIGHT_BRACKET, "]");
            case ':':
                pos++;
                return new Token(Token.Type.COLON, ":");
            case ',':
                pos++;
                return new Token(Token.Type.COMMA, ",");
        }

        // Строка
        if (c == '"') {
            return readString();
        }

        // Число или ключевое слово
        if (Character.isDigit(c) || c == '-' || c == '+') {
            return readNumber();
        }

        // Ключевые слова
        if (c == 't' && startsWith(pos, "true")) {
            pos += 4;
            return new Token(Token.Type.TRUE, "true");
        }
        if (c == 'f' && startsWith(pos, "false")) {
            pos += 5;
            return new Token(Token.Type.FALSE, "false");
        }
        if (c == 'n' && startsWith(pos, "null")) {
            pos += 4;
            return new Token(Token.Type.NULL, "null");
        }

        throw new RuntimeException("Unexpected character: " + c + " at position " + pos);
    }

    private boolean startsWith(int start, String word) {
        return input.startsWith(word, start);
    }

    private Token readString() {
        StringBuilder sb = new StringBuilder();
        pos++; // пропускаем начальную "

        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (c == '"') {
                pos++; // пропускаем закрывающую "
                return new Token(Token.Type.STRING, sb.toString());
            }
            if (c == '\\') {
                pos++;
                if (pos >= input.length()) throw new RuntimeException("Unexpected end of string");
                c = input.charAt(pos);
                switch (c) {
                    case '"', '\\', '/' -> sb.append(c);
                    case 'b' -> sb.append('\b');
                    case 'f' -> sb.append('\f');
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case 'u' -> {
                        if (pos + 4 >= input.length()) {
                            throw new RuntimeException("Invalid unicode escape");
                        }
                        String hex = input.substring(pos + 1, pos + 5);
                        try {
                            char unicodeChar = (char) Integer.parseInt(hex, 16);
                            sb.append(unicodeChar);
                            pos += 4;
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("Invalid unicode escape: \\u" + hex);
                        }
                    }
                    default -> throw new RuntimeException("Invalid escape: \\" + c);
                }
            } else {
                sb.append(c);
            }
            pos++;
        }
        throw new RuntimeException("Unterminated string");
    }

    private Token readNumber() {
        int start = pos;
        if (input.charAt(pos) == '-' || input.charAt(pos) == '+') {
            pos++;
        }

        // Целая часть
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            pos++;
        }

        // Дробная часть
        if (pos < input.length() && input.charAt(pos) == '.') {
            pos++;
            while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
                pos++;
            }
        }

        // Экспонента
        if (pos < input.length() && (input.charAt(pos) == 'e' || input.charAt(pos) == 'E')) {
            pos++;
            if (pos < input.length() && (input.charAt(pos) == '+' || input.charAt(pos) == '-')) {
                pos++;
            }
            while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
                pos++;
            }
        }

        String numStr = input.substring(start, pos);
        try {
            double value = Double.parseDouble(numStr);
            return new Token(Token.Type.NUMBER, numStr);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid number: " + numStr);
        }
    }
}