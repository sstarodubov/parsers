package org.starodubov.xml;

public class XmlTokenizer {
    private final String input;
    private int pos = 0;
    final static Token NULL_TOKEN = new Token(TokenType.NULL, "null");
    final static Token EOF_TOKEN = new Token(TokenType.EOF, "EOF");

    public enum TokenType {
        OPEN_TAG,
        CLOSE_TAG,
        STRING,
        NUMBER,
        BOOLEAN,
        NULL,
        EOF
    }

    public record Token(TokenType type, String lexeme) {
        @Override
        public String toString() {
            return "Token[type=%s, lexeme='%s']".formatted(type, lexeme);
        }
    }

    public XmlTokenizer(String xml) {
        this.input = xml;
    }

    public Token nextToken() {
        skipWhitespace();
        if (pos >= input.length()) {
            return EOF_TOKEN;
        }
        final char c = input.charAt(pos);

        return switch (c) {
            case '<' -> readTag();
            case char t when (t == 't' && startsWith(pos, "true")) || (t == 'f' && startsWith(pos, "false")) ->
                    readBooleanOrString();
            case char d when Character.isDigit(d) || d == '+' || d == '-' -> readDigitOrString();
            case char n when n == 'N' && startsWith(pos, "NULL") -> readNullOrString();
            case char _ -> readString();
        };
    }

    private Token readTag() {
       pos++; // skip '<'
       if (pos < input.length() && input.charAt(pos) == '/') {
          // close tag
           pos++; // skip '/'
           final var s = readRawString();
           pos++; // skip '>'
           return new Token(TokenType.CLOSE_TAG, s);
       } else {
           // open tag
           final String s = readRawString();
           pos++; // skip '>'
           return new Token(TokenType.OPEN_TAG, s);
       }
    }

    private String readRawString() {
        final var sb = new StringBuilder();
        while (pos < input.length() && input.charAt(pos) != '>' && input.charAt(pos) != '<') {
            sb.append(input.charAt(pos));
            pos++;
        }
        return sb.toString().trim();
    }

    private Token readString() {
        return new Token(TokenType.STRING, readRawString());
    }

    private Token readNullOrString() {
        final var s = readRawString();
        if (s.equals("NULL")) {
            return NULL_TOKEN;
        }
        return new Token(TokenType.STRING, s);
    }

    private Token readDigitOrString() {
        final var s = readRawString();
        if (isNum(s)) {
            return new Token(TokenType.NUMBER, s);
        }
        return new Token(TokenType.STRING, s);
    }

    private boolean isNum(final String s) {
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && (s.charAt(i) == '-' || s.charAt(i) == '+')) {
                continue;
            }
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean startsWith(int start, String word) {
        return input.startsWith(word, start);
    }

    private Token readBooleanOrString() {
        final var s = readRawString();
        return switch (s) {
            case "true" -> new Token(TokenType.BOOLEAN, "true");
            case "false" -> new Token(TokenType.BOOLEAN, "false");
            default -> new Token(TokenType.STRING, s);
        };
    }


    private void skipWhitespace() {
        while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
            pos++;
        }
    }
}
