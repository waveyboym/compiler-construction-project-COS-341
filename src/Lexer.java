import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String source;
    private char current;
    private int line, column;

    public Lexer(String source) {
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
        }
        return tokens;
    }

    private Token processCurrentChar() {
        return null;
    }

    private boolean isAtEnd() {
        return false;
    }
}
