import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String source;
    private final String fileName;
    private char current;
    private int line, column;

    public Lexer(String source, String fileName) {
        this.fileName = fileName;
        this.source = source;
        this.current = source.charAt(0);
        this.line = 1;
        this.column = 1;
    }

    public List<Token> scanTokens() {
        List<Token> tokens = new ArrayList<>();
        while (!isAtEnd()) {
            Token token = processCurrentChar();
            if (token != null) {
                tokens.add(token);
            }
            advance();
        }
        return tokens;
    }

    private Token processCurrentChar() {
        char c = this.current;
        switch (c) {
            case '(' -> {
                return new Token(TokenType.LEFT_PAREN, fileName, String.valueOf(line), String.valueOf(column));
            }
            case ')' -> {
                return new Token(TokenType.RIGHT_PAREN, fileName, String.valueOf(line), String.valueOf(column));
            }
            case '{' -> {
                return new Token(TokenType.LEFT_BRACE, fileName, String.valueOf(line), String.valueOf(column));
            }
            case '}' -> {
                return new Token(TokenType.RIGHT_BRACE, fileName, String.valueOf(line), String.valueOf(column));
            }
            case ',' -> {
                return new Token(TokenType.COMMA, fileName, String.valueOf(line), String.valueOf(column));
            }
            case '.' -> {
                return new Token(TokenType.DOT, fileName, String.valueOf(line), String.valueOf(column));
            }
            case '-' -> {
                return new Token(TokenType.MINUS, fileName, String.valueOf(line), String.valueOf(column));
            }
            case '+' -> {
                return new Token(TokenType.PLUS, fileName, String.valueOf(line), String.valueOf(column));
            }
            case ';' -> {
                return new Token(TokenType.SEMICOLON, fileName, String.valueOf(line), String.valueOf(column));
            }
            case '*' -> {
                return new Token(TokenType.STAR, fileName, String.valueOf(line), String.valueOf(column));
            }
            case '!' -> {
                return new Token(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG, fileName, String.valueOf(line), String.valueOf(column));
            }
            case '=' -> {
                return new Token(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL, fileName, String.valueOf(line), String.valueOf(column));
            }
            case '<' -> {
                return new Token(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS, fileName, String.valueOf(line), String.valueOf(column));
            }
            case '>' -> {
                return new Token(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER, fileName, String.valueOf(line), String.valueOf(column));
            }
            case '/' -> {
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                    return null;
                } else if (match('*')) {
                    while (peek() != '*' && peek() != '/' && !isAtEnd()) {
                        if (peek() == '\n') {
                            line++;
                            column = 1;
                        }
                        advance();
                    }
                    if (isAtEnd()) {
                        throw new RuntimeException("Unterminated comment");
                    }
                    advance();
                    advance();
                    return null;
                } 
                else {
                    return new Token(TokenType.SLASH, fileName, String.valueOf(line), String.valueOf(column));
                }
            }
            case ' ', '\r', '\t' -> {
                return null;
            }
            case '\n' -> {
                line++;
                column = 1;
                return null;
            }
            default -> {
                return new Token(TokenType.EOF, fileName, String.valueOf(line), String.valueOf(column));
            }
        }
    }

    private boolean isAtEnd() {
        return this.column >= this.source.length();
    }

    private char advance() {
        this.column++;
        return this.current = this.source.charAt(this.column);
    }

    private boolean match(char expected) {
        return this.current == expected;
    }

    private char peek() {
        return this.current;
    }
}
