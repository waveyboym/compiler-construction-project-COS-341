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
                if (advanceAndMatch('o') && advanceAndMatch('t')) {
                    return new Token(TokenType.NOT, fileName, String.valueOf(line), String.valueOf(column), "not");
                }
                else if (goBack(2) && advanceAndMatch('u') && advanceAndMatch('m')) {
                    return new Token(TokenType.NUM, fileName, String.valueOf(line), String.valueOf(column), "num");
                }
                else {
                    return null;
                }
            }
            case 's' -> {
                if (advanceAndMatch('q') && advanceAndMatch('r') && advanceAndMatch('t')) {
                    return new Token(TokenType.SQRT, fileName, String.valueOf(line), String.valueOf(column), "sqrt");
                }
                else if (goBack(3) && advanceAndMatch('k') && advanceAndMatch('i') && advanceAndMatch('p')) {
                    return new Token(TokenType.SKIP, fileName, String.valueOf(line), String.valueOf(column), "skip");
                }
                else if (goBack(3) && advanceAndMatch('u') && advanceAndMatch('b')) {
                    return new Token(TokenType.SUB, fileName, String.valueOf(line), String.valueOf(column), "sub");
                }
                else {
                    return null;
                }
            }
            case 'o' -> {
                if (advanceAndMatch('r')) {
                    return new Token(TokenType.OR, fileName, String.valueOf(line), String.valueOf(column), "or");
                }
                else {
                    return null;
                }
            }
            case 'a' -> {
                if (advanceAndMatch('n') && advanceAndMatch('d')) {
                    return new Token(TokenType.AND, fileName, String.valueOf(line), String.valueOf(column), "and");
                }
                else if (goBack(2) && advanceAndMatch('d') && advanceAndMatch('d')) {
                    return new Token(TokenType.ADD, fileName, String.valueOf(line), String.valueOf(column), "add");
                }
                else {
                    return null;
                }
            }
            case 'e' -> {
                if (advanceAndMatch('q')) {
                    return new Token(TokenType.EQ, fileName, String.valueOf(line), String.valueOf(column), "eq");
                }
                else if (goBack(1) && advanceAndMatch('l') && advanceAndMatch('s') && advanceAndMatch('e')) {
                    return new Token(TokenType.ELSE, fileName, String.valueOf(line), String.valueOf(column), "else");
                }
                if (goBack(3) && advanceAndMatch('n') && advanceAndMatch('d')) {
                    return new Token(TokenType.END, fileName, String.valueOf(line), String.valueOf(column), "end");
                }
                else {
                    return null;
                }
            }
            case 'g' -> {
                if (advanceAndMatch('t')) {
                    return new Token(TokenType.GT, fileName, String.valueOf(line), String.valueOf(column), "gt");
                }
                else {
                    return null;
                }
            }
            case 'm' -> {
                if (advanceAndMatch('u') && advanceAndMatch('l')) {
                    return new Token(TokenType.MUL, fileName, String.valueOf(line), String.valueOf(column), "mul");
                }
                if (goBack(2) && advanceAndMatch('a') && advanceAndMatch('i') && advanceAndMatch('n')) {
                    return new Token(TokenType.MAIN, fileName, String.valueOf(line), String.valueOf(column), "main");
                }
                else {
                    return null;
                }
            }
            case 'd' -> {
                if (advanceAndMatch('i') && advanceAndMatch('v')){
                    return new Token(TokenType.DIV, fileName, String.valueOf(line), String.valueOf(column), "div");
                }
                else {
                    return null;
                }
            }
            case 't' -> {
                if (advanceAndMatch('h') && advanceAndMatch('e') && advanceAndMatch('n')){
                    return new Token(TokenType.THEN, fileName, String.valueOf(line), String.valueOf(column), "then");
                }
                else if (goBack(3) && advanceAndMatch('e') && advanceAndMatch('x') && advanceAndMatch('t')){
                    return new Token(TokenType.VTEXT, fileName, String.valueOf(line), String.valueOf(column), "text");
                }
                else {
                    return null;
                }
            }
            case 'v' -> {
                if (advanceAndMatch('o') && advanceAndMatch('i') && advanceAndMatch('d')) {
                    return new Token(TokenType.FVOID, fileName, String.valueOf(line), String.valueOf(column), "void");
                }
                else {
                    return null;
                }
            }
            case 'b' -> {
                if (advanceAndMatch('e') && advanceAndMatch('g') && advanceAndMatch('i') && advanceAndMatch('n')){
                    return new Token(TokenType.BEGIN, fileName, String.valueOf(line), String.valueOf(column), "begin");
                }
                else {
                    return null;
                }
            }
            case 'i' -> {
                if (advanceAndMatch('f')) {
                    return new Token(TokenType.IF, fileName, String.valueOf(line), String.valueOf(column), "if");
                }
                else {
                    return null;
                }
            }
            case 'h' -> {
                if (advanceAndMatch('a') && advanceAndMatch('l') && advanceAndMatch('t')) {
                    return new Token(TokenType.HALT, fileName, String.valueOf(line), String.valueOf(column), "halt");
                }
                else {
                    return null;
                }
            }
            case 'p' -> {
                if (advanceAndMatch('r') && advanceAndMatch('i') && advanceAndMatch('n') && advanceAndMatch('t')) {
                    return new Token(TokenType.PRINT, fileName, String.valueOf(line), String.valueOf(column), "print");
                }
                else {
                    return null;
                }
            }
            case 'V' -> {
                String value = "V";
                // match regex V_[a‒z]([a‒z]|[0‒9])*
                if (advanceAndMatch('_')) {
                    value += this.current;
                    advance();
                    if (Character.isLetter(this.source.charAt(this.column))) {
                        value += this.current;
                        advance();
                        while (Character.isLetterOrDigit(this.source.charAt(this.column))) {
                            value += this.current;
                            advance();
                        }
                        return new Token(TokenType.VNAME, fileName, String.valueOf(line), String.valueOf((char) column), value);
                    }
                } 
                return null;
            }
            case 'F' -> {
                String value = "F";
                // match regex F_[a‒z]([a‒z]|[0‒9])*
                if (advanceAndMatch('_')) {
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
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-' -> {
                // match regex ^(0|-?0\.[0-9]*[1-9]|-?[1-9][0-9]*|-?[1-9][0-9]*\.[0-9]*[1-9])$
                goBack(1);
                if (advanceAndMatch('0') || (advanceAndMatch('-') && advanceAndMatch('0'))) {
                    if (this.source.charAt(this.column) != '.') {
                        return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), String.valueOf(column), "0");
                    }
                    String value = "";
                    value += this.current;
                    while (Character.isDigit(this.source.charAt(this.column))) {
                        value += this.current;
                        advance();
                    }

                    // if last character in value is not equal to 1-9 return null
                    if (value.charAt(value.length() - 1) == '0') {
                        return null;
                    }

                    return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), String.valueOf(column), value);  
                }
                else if(goBack(2) && advanceAndMatch('-') && Character.isDigit(this.source.charAt(this.column))
                && this.source.charAt(this.column) != '0') {
                    String value = "-";
                    value += this.current;
                    advance();
                    while (Character.isDigit(this.source.charAt(this.column))) {
                        value += this.current;
                        advance();
                    }
                    if (this.source.charAt(this.column) != '.') {
                        return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), String.valueOf(column), value);
                    }
                    value += this.current;
                    advance();
                    while (Character.isDigit(this.source.charAt(this.column))) {
                        value += this.current;
                        advance();
                    }
                    if (value.charAt(value.length() - 1) == '0') {
                        return null;
                    }
                    return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), String.valueOf(column), value);
                }
                else {
                    return null;
                }
            }
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

    private boolean advanceAndMatch(char expected) {
        this.column++;
        if (this.column >= this.source.length()) {
            return false;
        }
        return this.source.charAt(this.column) == expected;
    }

    private boolean goBack(int steps) {
        this.column -= steps;
        return true;
    }
}
