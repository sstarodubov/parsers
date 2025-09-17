package org.starodubov.xml;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.starodubov.xml.XmlTokenizer.TokenType;

class XmlParserTest {

    String xml = """
            <root>
                <hello>world</hello>
                <f1>null<f2>
                <f2 >true</f2>
                <f3>1.0</f3>
                <a>
                    <hoho>test</hoho>
                </a>
                <tags>
                    <tag>apple</tag>
                    <tag>banana</tag>
                    <tag>cherry</tag>
                </tags>
            </root> 
            """;

    XmlParser parser = new XmlParser();

    @Test
    void parse_nested_object() {
        XmlValue val = parser.parse("""
                <root>
                    <a>1</a>
                    <a>2</a>
                    <b>3</b>
                    <c><a>4</a></c>
                </root>
                """);
       var obj = val.asObject();
       assertEquals("root", obj.name());
       var list = obj.getObject("a").children();
       assertEquals(2, list.size());
       assertEquals(1, list.get(0).asNumber().value());
       assertEquals(2, list.get(1).asNumber().value());
       assertEquals(4, obj.getObject("c").getObject("a").getNumber().value());
    }

    @Test
    void parse_null() {
        XmlValue value = parser.parse("""
                    NULL 
                """);
        assertInstanceOf(XmlNull.class, value);
    }

    @Test
    void parse_bool() {
        XmlValue value = parser.parse("""
                    true 
                """);

        assertTrue(((XmlBoolean) value).value());
    }

    @Test
    void parse_num() {
        XmlValue value = parser.parse("""
                    +123 
                """);

        assertEquals(123L, ((XmlNumber) value).value());
    }

    @Test
    void parse_string() {
        XmlValue value = parser.parse("""
                    hoho 
                """);

        assertEquals("hoho", ((XmlString) value).value());
    }

    @Test
    void test_object2() {
        var t = new XmlTokenizer("""
                    <root>
                        <hello>hoho</hello>
                    </root>
                """);

        var t1 = t.nextToken();
        assertEquals(TokenType.OPEN_TAG, t1.type());
        assertEquals("root", t1.lexeme());

        var t3 = t.nextToken();
        assertEquals(TokenType.OPEN_TAG, t3.type());
        assertEquals("hello", t3.lexeme());

        var t7 = t.nextToken();
        assertEquals(TokenType.STRING, t7.type());
        assertEquals("hoho", t7.lexeme());

        var t8 = t.nextToken();
        assertEquals(TokenType.CLOSE_TAG, t8.type());
        assertEquals("hello", t8.lexeme());

        var t9 = t.nextToken();
        assertEquals(TokenType.CLOSE_TAG, t9.type());
        assertEquals("root", t9.lexeme());
    }

    @Test
    void test_object() {
        var t = new XmlTokenizer("""
                    <root>
                        100
                    </root>
                """);

        var t1 = t.nextToken();
        assertEquals(TokenType.OPEN_TAG, t1.type());
        assertEquals("root", t1.lexeme());

        var t3 = t.nextToken();
        assertEquals(TokenType.NUMBER, t3.type());
        assertEquals("100", t3.lexeme());

        var t7 = t.nextToken();
        assertEquals(TokenType.CLOSE_TAG, t7.type());
        assertEquals("root", t7.lexeme());
    }

    @Test
    void test_string() {
        var t = new XmlTokenizer("""
                    hoho
                """);
        var value = t.nextToken();

        assertEquals("hoho", value.lexeme());
        assertEquals(TokenType.STRING, value.type());
        assertEquals(TokenType.EOF, t.nextToken().type());
    }

    @Test
    void test_null() {
        var t = new XmlTokenizer("""
                    NULL
                """);
        var value = t.nextToken();

        assertEquals("null", value.lexeme());
        assertEquals(TokenType.NULL, value.type());
        assertEquals(TokenType.EOF, t.nextToken().type());
    }

    @Test
    void test_null_1() {
        var t = new XmlTokenizer("""
                    NULL\n
                """);
        var value = t.nextToken();

        assertEquals("null", value.lexeme());
        assertEquals(TokenType.NULL, value.type());
        assertEquals(TokenType.EOF, t.nextToken().type());
    }

    @Test
    void test_null_2() {
        var t = new XmlTokenizer("""
                    NULLable
                """);
        var value = t.nextToken();

        assertEquals("NULLable", value.lexeme());
        assertEquals(TokenType.STRING, value.type());
        assertEquals(TokenType.EOF, t.nextToken().type());
    }

    @Test
    void test_num() {
        var t = new XmlTokenizer("""
                    127
                """);
        var value = t.nextToken();

        assertEquals("127", value.lexeme());
        assertEquals(TokenType.NUMBER, value.type());
        assertEquals(TokenType.EOF, t.nextToken().type());
    }

    @Test
    void test_boolean() {
        var t = new XmlTokenizer("""
                    true
                """);
        var value = t.nextToken();

        assertEquals("true", value.lexeme());
        assertEquals(TokenType.BOOLEAN, value.type());
        assertEquals(TokenType.EOF, t.nextToken().type());
    }


    @Test
    void test_false_boolean() {
        var t = new XmlTokenizer("""
                    false
                """);
        var value = t.nextToken();

        assertEquals("false", value.lexeme());
        assertEquals(TokenType.BOOLEAN, value.type());
        assertEquals(TokenType.EOF, t.nextToken().type());
    }


    @Test
    void test_false_string() {
        var t = new XmlTokenizer("""
                    false1
                """);
        var value = t.nextToken();

        assertEquals("false1", value.lexeme());
        assertEquals(TokenType.STRING, value.type());
        assertEquals(TokenType.EOF, t.nextToken().type());
    }
}




















