package org.starodubov.pratt;
import java.util.*;

public class PrattParser {

    private final List<Token> tokens;
    private int pos = 0;

    // Таблица приоритетов (левая привязка = left binding power)
    private static final Map<String, Integer> INFIX_PREC = Map.of(
            "+", 10,
            "-", 10,
            "*", 20,
            "/", 20,
            "^", 30  // правоассоциативный
    );

    private static final Set<String> PREFIX_OPS = Set.of("+", "-");

    // Токен: тип + значение
    static class Token {
        final String type;  // "number", "operator", "lparen", "rparen"
        final String value;

        Token(String type, String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public PrattParser(String input) {
        this.tokens = tokenize(input);
    }

    // === Лексер: разбиваем строку на токены ===
    private List<Token> tokenize(String s) {
        List<Token> tokens = new ArrayList<>();
        int i = 0;
        while (i < s.length()) {
            char ch = s.charAt(i);

            if (Character.isWhitespace(ch)) {
                i++;
                continue;
            }

            if (Character.isDigit(ch) || ch == '.') {
                int start = i;
                boolean hasDot = ch == '.';
                while (i < s.length() && (Character.isDigit(s.charAt(i)) || s.charAt(i) == '.')) {
                    if (s.charAt(i) == '.') {
                        if (hasDot) break; // только одна точка
                        hasDot = true;
                    }
                    i++;
                }
                tokens.add(new Token("number", s.substring(start, i)));
                continue;
            }

            if (ch == '(') {
                tokens.add(new Token("lparen", "("));
                i++;
                continue;
            }
            if (ch == ')') {
                tokens.add(new Token("rparen", ")"));
                i++;
                continue;
            }

            if ("+-*/^".indexOf(ch) != -1) {
                tokens.add(new Token("operator", String.valueOf(ch)));
                i++;
                continue;
            }

            throw new RuntimeException("Unknown character: " + ch);
        }
        return tokens;
    }

    private Token current() {
        if (pos >= tokens.size()) return null;
        return tokens.get(pos);
    }

    private Token consume() {
        if (pos >= tokens.size()) return null;
        return tokens.get(pos++);
    }

    // === Pratt Parser: parseExpression с минимальным приоритетом ===
    private Object parseExpression(int minPrec) {
        if (current() == null) {
            throw new RuntimeException("Unexpected end of input");
        }

        Token token = consume();
        Object left;

        // NUD: значение токена само по себе
        switch (token.type) {
            case "number" -> left = Double.parseDouble(token.value);
            case "operator" -> {
                if (!PREFIX_OPS.contains(token.value)) {
                    throw new RuntimeException("Unexpected prefix operator: " + token.value);
                }
                Object operand = parseExpression(100); // высокий приоритет для унарных
                left = switch (token.value) {
                    case "-" -> -(Double) operand;
                    case "+" -> operand;
                    default -> throw new RuntimeException("Unknown prefix op: " + token.value);
                };
            }
            case "lparen" -> {
                left = parseExpression(0); // разбираем выражение в скобках
                if (current() == null || !"rparen".equals(current().type)) {
                    throw new RuntimeException("Expected ')'");
                }
                consume(); // съесть ')'
            }
            default -> throw new RuntimeException("Unexpected token: " + token);
        }

        // LED: пока операторы имеют приоритет >= minPrec — продолжаем
        while (current() != null && "operator".equals(current().type)) {
            String op = current().value;
            Integer prec = INFIX_PREC.get(op);
            if (prec == null || prec < minPrec) {
                break; // оператор слишком слабый — возвращаем, что уже есть
            }

            consume(); // съели оператор

            // Для правоассоциативных операторов (например, ^) — уменьшаем приоритет на 1
            int nextMinPrec = op.equals("^") ? prec : prec + 1;

            Object right = parseExpression(nextMinPrec);

            // Применяем оператор
            left = switch (op) {
                case "+" -> (Double) left + (Double) right;
                case "-" -> (Double) left - (Double) right;
                case "*" -> (Double) left * (Double) right;
                case "/" -> (Double) left / (Double) right;
                case "^" -> Math.pow((Double) left, (Double) right);
                default -> throw new RuntimeException("Unknown operator: " + op);
            };
        }

        return left;
    }

    public Object parse() {
        Object result = parseExpression(0);
        if (current() != null) {
            throw new RuntimeException("Unexpected token after expression: " + current());
        }
        return result;
    }

    // === Тестирование ===

    public static void main(String[] args) {
        String[] tests = {
                "1 + 2 * 3",
                "2 * 3 + 1",
                "(1 + 2) * 3",
                "10 - 5 - 2",     // левоассоциативно: (10-5)-2 = 3
                "2 ^ 3 ^ 2",      // правоассоциативно: 2^(3^2) = 512
                "-5 + 3",
                "+7 * 2",
                "1 + 2 * -3",     // унарный минус имеет высокий приоритет
                "((1 + 2) * (3 - 4)) / 5"
        };

        for (String test : tests) {
            try {
                PrattParser parser = new PrattParser(test);
                Object result = parser.parse();
                System.out.printf("✅ %-25s → %s%n", test, result);
            } catch (Exception e) {
                System.out.printf("❌ %-25s → Error: %s%n", test, e.getMessage());
            }
        }
    }
}
