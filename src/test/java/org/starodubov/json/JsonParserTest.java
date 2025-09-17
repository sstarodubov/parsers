package org.starodubov.json;

import org.junit.jupiter.api.Test;

class JsonParserTest {

    String json = """
            {
                "hello" : "world",
                "f1" : null,
                "f2" : true,
                "f3" : 1.00,
                "a" : {
                 "hoho" : "test",
                 "d" : "2"
                }
            }""";

    JsonParser parser = new JsonParser();

    @Test
    public void test() {
        JsonValue parse = parser.parse(json);

        System.out.println(parse);
    }
}