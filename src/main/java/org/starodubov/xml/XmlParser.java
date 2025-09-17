package org.starodubov.xml;

import org.starodubov.json.JsonObject;
import org.starodubov.json.JsonTokenizer;
import org.starodubov.json.JsonValue;

import java.util.ArrayList;

import static org.starodubov.xml.XmlTokenizer.TokenType;

public class XmlParser {
    private XmlTokenizer tokenizer;
    private XmlTokenizer.Token currentToken;

    public XmlValue parse(final String xml) {
        this.tokenizer = new XmlTokenizer(xml);
        this.currentToken = tokenizer.nextToken();
        final XmlValue value = parseValue();
        if (currentToken.type() != XmlTokenizer.TokenType.EOF) {
            throw new RuntimeException("Unexpected token after root value: " + currentToken);
        }

        return value;
    }

    private boolean consume(XmlTokenizer.TokenType expectedType) {
        if (currentToken.type() != expectedType) {
            throw new RuntimeException("Expected " + expectedType + ", got " + currentToken.type());
        }
        currentToken = tokenizer.nextToken();
        return true;
    }

    private XmlValue parseValue() {
        return switch (currentToken.type()) {
            case OPEN_TAG -> parseObject();
            case STRING -> {
                final var s = currentToken.lexeme();
                consume(TokenType.STRING);
                yield new XmlString(s);
            }
            case NUMBER -> {
                final var s = currentToken.lexeme();
                consume(TokenType.NUMBER);
                yield XmlNumber.fromString(s);
            }
            case BOOLEAN -> {
                final var s = currentToken.lexeme();
                consume(TokenType.BOOLEAN);
                yield XmlBoolean.fromString(s);
            }
            case NULL -> {
                consume(TokenType.NULL);
                yield XmlNull.INSTANCE;
            }
            default -> throw new RuntimeException("Unexpected token: " + currentToken);
        };
    }

    private XmlObject parseObject() {
        final String openTagName = currentToken.lexeme();
        consume(TokenType.OPEN_TAG);
        final var obj = new XmlObject(openTagName);
        while (!(currentToken.type() == TokenType.CLOSE_TAG && currentToken.lexeme().equals(openTagName))) {
            obj.add(parseValue());
        }

        consume(TokenType.CLOSE_TAG);
        return obj;
    }
}
