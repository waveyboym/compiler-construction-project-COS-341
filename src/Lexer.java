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
            if (token == null){
                System.out.println("Error: Invalid token at line " + line + " column " + column);
                return null;
            }
            if (token.type != TokenType.NULLTYPE) {
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
                return new Token(TokenType.LEFT_PAREN, fileName, String.valueOf(line), String.valueOf(column), String.valueOf(c));
            }
            case ')' -> {
                return new Token(TokenType.RIGHT_PAREN, fileName, String.valueOf(line), String.valueOf(column), String.valueOf(c));
            }
            case '{' -> {
                return new Token(TokenType.LEFT_BRACE, fileName, String.valueOf(line), String.valueOf(column), String.valueOf(c));
            }
            case '}' -> {
                return new Token(TokenType.RIGHT_BRACE, fileName, String.valueOf(line), String.valueOf(column), String.valueOf(c));
            }
            case ',' -> {
                return new Token(TokenType.COMMA, fileName, String.valueOf(line), String.valueOf(column), String.valueOf(c));
            }
            case ';' -> {
                return new Token(TokenType.SEMICOLON, fileName, String.valueOf(line), String.valueOf(column), String.valueOf(c));
            }
            case 'n' -> {
                if (peekAndMatch('o', 1) && peekAndMatch('t', 2)) {
                    advance();
                    advance();
                    return new Token(TokenType.NOT, fileName, String.valueOf(line), String.valueOf(column), "not");
                }
                else if (peekAndMatch('u', 1) && peekAndMatch('m', 2)) {
                    advance();
                    advance();
                    return new Token(TokenType.NUM, fileName, String.valueOf(line), String.valueOf(column), "num");
                }
                else {
                    return null;
                }
            }
            case 's' -> {
                if (peekAndMatch('q', 1) && peekAndMatch('r', 2) && peekAndMatch('t', 3)) {
                    advance();
                    advance();
                    advance();
                    return new Token(TokenType.SQRT, fileName, String.valueOf(line), String.valueOf(column), "sqrt");
                }
                else if (peekAndMatch('k', 1) && peekAndMatch('i', 2) && peekAndMatch('p', 3)) {
                    advance();
                    advance();
                    advance();
                    return new Token(TokenType.SKIP, fileName, String.valueOf(line), String.valueOf(column), "skip");
                }
                else if (peekAndMatch('u', 1) && peekAndMatch('b', 2)) {
                    advance();
                    advance();
                    return new Token(TokenType.SUB, fileName, String.valueOf(line), String.valueOf(column), "sub");
                }
                else {
                    return null;
                }
            }
            case 'o' -> {
                if (peekAndMatch('r', 1)) {
                    advance();
                    return new Token(TokenType.OR, fileName, String.valueOf(line), String.valueOf(column), "or");
                }
                else {
                    return null;
                }
            }
            case 'a' -> {
                if (peekAndMatch('n', 1) && peekAndMatch('d', 2)) {
                    advance();
                    advance();
                    return new Token(TokenType.AND, fileName, String.valueOf(line), String.valueOf(column), "and");
                }
                else if (peekAndMatch('d', 1) && peekAndMatch('d', 2)) {
                    advance();
                    advance();
                    return new Token(TokenType.ADD, fileName, String.valueOf(line), String.valueOf(column), "add");
                }
                else {
                    return null;
                }
            }
            case 'e' -> {
                if (peekAndMatch('q', 1)) {
                    advance();
                    return new Token(TokenType.EQ, fileName, String.valueOf(line), String.valueOf(column), "eq");
                }
                else if (peekAndMatch('l', 1) && peekAndMatch('s', 2) && peekAndMatch('e', 3)) {
                    advance();
                    advance();
                    advance();
                    return new Token(TokenType.ELSE, fileName, String.valueOf(line), String.valueOf(column), "else");
                }
                if (peekAndMatch('n', 1) && peekAndMatch('d', 2)) {
                    advance();
                    advance();
                    return new Token(TokenType.END, fileName, String.valueOf(line), String.valueOf(column), "end");
                }
                else {
                    return null;
                }
            }
            case 'g' -> {
                if (peekAndMatch('t', 1)) {
                    advance();
                    return new Token(TokenType.GT, fileName, String.valueOf(line), String.valueOf(column), "gt");
                }
                else {
                    return null;
                }
            }
            case 'm' -> {
                if (peekAndMatch('u', 1) && peekAndMatch('l', 2)) {
                    advance();
                    advance();
                    return new Token(TokenType.MUL, fileName, String.valueOf(line), String.valueOf(column), "mul");
                }
                if (peekAndMatch('a', 1) && peekAndMatch('i', 2) && peekAndMatch('n', 3)) {
                    advance();
                    advance();
                    advance();
                    return new Token(TokenType.MAIN, fileName, String.valueOf(line), String.valueOf(column), "main");
                }
                else {
                    return null;
                }
            }
            case 'd' -> {
                if (peekAndMatch('i', 1) && peekAndMatch('v', 2)) {
                    advance();
                    advance();
                    return new Token(TokenType.DIV, fileName, String.valueOf(line), String.valueOf(column), "div");
                }
                else {
                    return null;
                }
            }
            case 't' -> {
                if (peekAndMatch('h', 1) && peekAndMatch('e', 2) && peekAndMatch('n', 3)) {
                    advance();
                    advance();
                    advance();
                    return new Token(TokenType.THEN, fileName, String.valueOf(line), String.valueOf(column), "then");
                }
                else if (peekAndMatch('e', 1) && peekAndMatch('x', 2) && peekAndMatch('t', 3)) {
                    advance();
                    advance();
                    advance();
                    return new Token(TokenType.VTEXT, fileName, String.valueOf(line), String.valueOf(column), "text");
                }
                else {
                    return null;
                }
            }
            case 'v' -> {
                if (peekAndMatch('o', 1) && peekAndMatch('i', 2) && peekAndMatch('d', 3)) {
                    advance();
                    advance();
                    advance();
                    return new Token(TokenType.FVOID, fileName, String.valueOf(line), String.valueOf(column), "void");
                }
                else {
                    return null;
                }
            }
            case 'b' -> {
                if (peekAndMatch('e', 1) && peekAndMatch('g', 2) && peekAndMatch('i', 3)
                && peekAndMatch('n', 4)){
                    advance();
                    advance();
                    advance();
                    return new Token(TokenType.BEGIN, fileName, String.valueOf(line), String.valueOf(column), "begin");
                }
                else {
                    return null;
                }
            }
            case 'i' -> {
                if (peekAndMatch('f', 1)) {
                    advance();
                    return new Token(TokenType.IF, fileName, String.valueOf(line), String.valueOf(column), "if");
                }
                else {
                    return null;
                }
            }
            case 'h' -> {
                if (peekAndMatch('a', 1) && peekAndMatch('l', 2) && peekAndMatch('t', 3)) {
                    advance();
                    advance();
                    advance();
                    return new Token(TokenType.HALT, fileName, String.valueOf(line), String.valueOf(column), "halt");
                }
                else {
                    return null;
                }
            }
            case 'p' -> {
                if (peekAndMatch('r', 1) && peekAndMatch('i', 2) && peekAndMatch('n', 3)
                && peekAndMatch('t', 4)) {
                    advance();
                    advance();
                    advance();
                    advance();
                    return new Token(TokenType.PRINT, fileName, String.valueOf(line), String.valueOf(column), "print");
                }
                else {
                    return null;
                }
            }
            case 'V' -> {
                String value = "V";
                // match regex V_[a‒z]([a‒z]|[0‒9])*
                if (peekAndMatch('_', 1)) {
                    advance();
                    value += this.current;
                    advance();
                    if (Character.isLetter(this.source.charAt(this.column))) {
                        value += this.current;
                        advance();
                        while (Character.isLetterOrDigit(this.source.charAt(this.column))) {
                            value += this.current;
                            advance();
                        }
                        return new Token(TokenType.VNAME, fileName, String.valueOf(line), String.valueOf(column), value);
                    }
                } 
                return null;
            }
            case 'F' -> {
                String value = "F";
                // match regex F_[a‒z]([a‒z]|[0‒9])*
                if (peekAndMatch('_', 1)) {
                    advance();
                    value += this.current;
                    advance();
                    if (Character.isLetter(this.source.charAt(this.column))) {
                        value += this.current;
                        advance();
                        while (Character.isLetterOrDigit(this.source.charAt(this.column))) {
                            advance();
                        }
                        return new Token(TokenType.FNAME, fileName, String.valueOf(line), String.valueOf(column), value);
                    }
                }
                return null;
            }
            case '"' -> {
                // match regex max "[A-Z][a-z][a-z][a-z][a-z][a-z][a-z][a-z]", min "[A-Z]"
                // must have a max of 9 characters
                advance();
                if (Character.isUpperCase(this.source.charAt(this.column + 1))) {
                    String value = "";
                    value += this.current;
                    advance();
                    for (int i = 0; i < 9; i++) {
                        if (Character.isLowerCase(this.source.charAt(this.column + 1))) {
                            value += this.current;
                            advance();
                        }
                        else {
                            break;
                        }
                    }
                    advance();
                    //expecting closing quote
                    if (this.source.charAt(this.column) == '"') {
                        return new Token(TokenType.TEXTLIT, fileName, String.valueOf(line), String.valueOf(column), value);
                    }
                }
                return null;
            }
            // match regex [0‒9]
            /*  
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                // match regex 0 |0.([0‒9])* [1‒9] |-0.([0‒9])* [1‒9] | [1‒9]([0‒9])* |-[1‒9]([0‒9])* | [1‒9]([0‒9])*. ([0‒9])* [1‒9] |-[1‒9]([0‒9])*. ([0‒9])* [1‒9]

                if (c == '0') {
                    if (peekAndMatch('.', 1)) {
                        advance();
                        advance();
                        while (Character.isDigit(this.source.charAt(this.column))) {
                            advance();
                        }
                        return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), String.valueOf(column));
                    }
                    return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), String.valueOf(column));
                }
                else {
                    while (Character.isDigit(this.source.charAt(this.column))) {
                        advance();
                    }
                    if (peekAndMatch('.', 1)) {
                        advance();
                        advance();
                        while (Character.isDigit(this.source.charAt(this.column))) {
                            advance();
                        }
                        return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), String.valueOf(column));
                    }
                    return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), String.valueOf(column));
                }
            }*/
            case ' ', '\r', '\t' -> {
                return new Token(TokenType.NULLTYPE, fileName, String.valueOf(line), String.valueOf(column), String.valueOf(c));
            }
            case '\n' -> {
                line++;
                column = 1;
                return new Token(TokenType.NULLTYPE, fileName, String.valueOf(line), String.valueOf(column), String.valueOf(c));
            }
            default -> {
                return null;
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

    private boolean peekAndMatch(char expected, int offset) {
        if (this.column + offset >= this.source.length()) {
            return false;
        }
        return this.source.charAt(this.column + offset) == expected;
    }
}
