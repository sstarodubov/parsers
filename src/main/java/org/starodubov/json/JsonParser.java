package org.starodubov.json;

public class JsonParser {
    private JsonTokenizer tokenizer;
    private JsonTokenizer.Token currentToken;

    JsonValue parse(String json) {
        this.tokenizer = new JsonTokenizer(json);
        this.currentToken = tokenizer.nextToken();
        JsonValue result = parseValue();
        if (currentToken.type != JsonTokenizer.Token.Type.EOF) {
            throw new RuntimeException("Unexpected token after root value: " + currentToken);
        }
        return result;
    }

    private boolean consume(JsonTokenizer.Token.Type expectedType) {
        if (currentToken.type != expectedType) {
            throw new RuntimeException("Expected " + expectedType + ", got " + currentToken.type);
        }
        currentToken = tokenizer.nextToken();
        return true;
    }

    private JsonValue parseValue() {
        return switch (currentToken.type) {
            case LEFT_BRACE -> parseObject();
            case LEFT_BRACKET -> parseArray();
            case STRING -> {
                String s = currentToken.lexeme;
                consume(JsonTokenizer.Token.Type.STRING);
                yield new JsonString(s);
            }
            case NUMBER -> {
                double n = Double.parseDouble(currentToken.lexeme);
                consume(JsonTokenizer.Token.Type.NUMBER);
                yield new JsonNumber(n);
            }
            case TRUE -> {
                consume(JsonTokenizer.Token.Type.TRUE);
                yield new JsonBoolean(true);
            }
            case FALSE -> {
                consume(JsonTokenizer.Token.Type.FALSE);
                yield new JsonBoolean(false);
            }
            case NULL -> {
                consume(JsonTokenizer.Token.Type.NULL);
                yield JsonNull.INSTANCE;
            }
            default -> throw new RuntimeException("Unexpected token: " + currentToken);
        };
    }

    private JsonObject parseObject() {
        consume(JsonTokenizer.Token.Type.LEFT_BRACE);
        JsonObject obj = new JsonObject();

        if (currentToken.type != JsonTokenizer.Token.Type.RIGHT_BRACE) {
            do {
                if (currentToken.type != JsonTokenizer.Token.Type.STRING) {
                    throw new RuntimeException("Expected string key, got " + currentToken);
                }
                String key = currentToken.lexeme;
                consume(JsonTokenizer.Token.Type.STRING);

                consume(JsonTokenizer.Token.Type.COLON);

                JsonValue value = parseValue();
                obj.add(key, value);
            } while (currentToken.type == JsonTokenizer.Token.Type.COMMA && consume(JsonTokenizer.Token.Type.COMMA));
        }

        consume(JsonTokenizer.Token.Type.RIGHT_BRACE);
        return obj;
    }

    private JsonArray parseArray() {
        consume(JsonTokenizer.Token.Type.LEFT_BRACKET);
        JsonArray arr = new JsonArray();

        if (currentToken.type != JsonTokenizer.Token.Type.RIGHT_BRACKET) {
            do {
                JsonValue value = parseValue();
                arr.add(value);
            } while (currentToken.type == JsonTokenizer.Token.Type.COMMA && consume(JsonTokenizer.Token.Type.COMMA));
        }

        consume(JsonTokenizer.Token.Type.RIGHT_BRACKET);
        return arr;
    }
}