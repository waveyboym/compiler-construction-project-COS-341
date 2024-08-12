import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String source;
    private final String fileName;
    private char current;
    private int line, column, seekValue, lineColumn;

    public Lexer(String source, String fileName) {
        this.fileName = fileName;
        this.source = source;
        this.current = source.charAt(0);
        this.line = 1;
        this.column = 0;
        this.seekValue = 0;
    }

    public List<Token> scanTokens() {
        List<Token> tokens = new ArrayList<>();
        while (!isAtEnd()) {
            Token token = processCurrentChar();
            if (token == null){
                System.out.println("Error: Invalid token at line " + line + " column " + column);
                return new ArrayList<>();
            }
            if (token.type != TokenType.NULLTYPE) {
                tokens.add(token);
            }
            this.seekValue = 0;
            advance();
        }
        return tokens;
    }

    private Token processCurrentChar() {
        char c = this.current;
        switch (c) {
            case '(' -> {
                return new Token(TokenType.LEFT_PAREN, fileName, String.valueOf(line), String.valueOf(lineColumn), String.valueOf(c));
            }
            case ')' -> {
                return new Token(TokenType.RIGHT_PAREN, fileName, String.valueOf(line), String.valueOf(lineColumn), String.valueOf(c));
            }
            case '{' -> {
                return new Token(TokenType.LEFT_BRACE, fileName, String.valueOf(line), String.valueOf(lineColumn), String.valueOf(c));
            }
            case '}' -> {
                return new Token(TokenType.RIGHT_BRACE, fileName, String.valueOf(line), String.valueOf(lineColumn), String.valueOf(c));
            }
            case ',' -> {
                return new Token(TokenType.COMMA, fileName, String.valueOf(line), String.valueOf(lineColumn), String.valueOf(c));
            }
            case ';' -> {
                return new Token(TokenType.SEMICOLON, fileName, String.valueOf(line), String.valueOf(lineColumn), String.valueOf(c));
            }
            case 'n' -> {
                if (advanceAndMatch('o') && advanceAndMatch('t')) {
                    return new Token(TokenType.NOT, fileName, String.valueOf(line), String.valueOf(lineColumn), "not");
                }
                else if (goBack(this.seekValue) && advanceAndMatch('u') && advanceAndMatch('m')) {
                    return new Token(TokenType.NUM, fileName, String.valueOf(line), String.valueOf(lineColumn), "num");
                }
                else {
                    return null;
                }
            }
            case 's' -> {
                if (advanceAndMatch('q') && advanceAndMatch('r') && advanceAndMatch('t')) {
                    return new Token(TokenType.SQRT, fileName, String.valueOf(line), String.valueOf(lineColumn), "sqrt");
                }
                else if (goBack(this.seekValue) && advanceAndMatch('k') && advanceAndMatch('i') && advanceAndMatch('p')) {
                    return new Token(TokenType.SKIP, fileName, String.valueOf(line), String.valueOf(lineColumn), "skip");
                }
                else if (goBack(this.seekValue) && advanceAndMatch('u') && advanceAndMatch('b')) {
                    return new Token(TokenType.SUB, fileName, String.valueOf(line), String.valueOf(lineColumn), "sub");
                }
                else {
                    return null;
                }
            }
            case 'o' -> {
                if (advanceAndMatch('r')) {
                    return new Token(TokenType.OR, fileName, String.valueOf(line), String.valueOf(lineColumn), "or");
                }
                else {
                    return null;
                }
            }
            case 'a' -> {
                if (advanceAndMatch('n') && advanceAndMatch('d')) {
                    return new Token(TokenType.AND, fileName, String.valueOf(line), String.valueOf(lineColumn), "and");
                }
                else if (goBack(this.seekValue) && advanceAndMatch('d') && advanceAndMatch('d')) {
                    return new Token(TokenType.ADD, fileName, String.valueOf(line), String.valueOf(lineColumn), "add");
                }
                else {
                    return null;
                }
            }
            case 'e' -> {
                if (advanceAndMatch('q')) {
                    return new Token(TokenType.EQ, fileName, String.valueOf(line), String.valueOf(lineColumn), "eq");
                }
                else if (goBack(this.seekValue) && advanceAndMatch('l') && advanceAndMatch('s') && advanceAndMatch('e')) {
                    return new Token(TokenType.ELSE, fileName, String.valueOf(line), String.valueOf(lineColumn), "else");
                }
                if (goBack(this.seekValue) && advanceAndMatch('n') && advanceAndMatch('d')) {
                    return new Token(TokenType.END, fileName, String.valueOf(line), String.valueOf(lineColumn), "end");
                }
                else {
                    return null;
                }
            }
            case 'g' -> {
                if (advanceAndMatch('t')) {
                    return new Token(TokenType.GT, fileName, String.valueOf(line), String.valueOf(lineColumn), "gt");
                }
                else {
                    return null;
                }
            }
            case 'm' -> {
                if (advanceAndMatch('u') && advanceAndMatch('l')) {
                    return new Token(TokenType.MUL, fileName, String.valueOf(line), String.valueOf(lineColumn), "mul");
                }
                if (goBack(this.seekValue) && advanceAndMatch('a') && advanceAndMatch('i') && advanceAndMatch('n')) {
                    return new Token(TokenType.MAIN, fileName, String.valueOf(line), String.valueOf(lineColumn), "main");
                }
                else {
                    return null;
                }
            }
            case 'd' -> {
                if (advanceAndMatch('i') && advanceAndMatch('v')){
                    return new Token(TokenType.DIV, fileName, String.valueOf(line), String.valueOf(lineColumn), "div");
                }
                else {
                    return null;
                }
            }
            case 't' -> {
                if (advanceAndMatch('h') && advanceAndMatch('e') && advanceAndMatch('n')){
                    return new Token(TokenType.THEN, fileName, String.valueOf(line), String.valueOf(lineColumn), "then");
                }
                else if (goBack(this.seekValue) && advanceAndMatch('e') && advanceAndMatch('x') && advanceAndMatch('t')){
                    return new Token(TokenType.VTEXT, fileName, String.valueOf(line), String.valueOf(lineColumn), "text");
                }
                else {
                    return null;
                }
            }
            case 'v' -> {
                if (advanceAndMatch('o') && advanceAndMatch('i') && advanceAndMatch('d')) {
                    return new Token(TokenType.FVOID, fileName, String.valueOf(line), String.valueOf(lineColumn), "void");
                }
                else {
                    return null;
                }
            }
            case 'b' -> {
                if (advanceAndMatch('e') && advanceAndMatch('g') && advanceAndMatch('i') && advanceAndMatch('n')){
                    return new Token(TokenType.BEGIN, fileName, String.valueOf(line), String.valueOf(lineColumn), "begin");
                }
                else {
                    return null;
                }
            }
            case 'i' -> {
                if (advanceAndMatch('f')) {
                    return new Token(TokenType.IF, fileName, String.valueOf(line), String.valueOf(lineColumn), "if");
                }
                else {
                    return null;
                }
            }
            case 'h' -> {
                if (advanceAndMatch('a') && advanceAndMatch('l') && advanceAndMatch('t')) {
                    return new Token(TokenType.HALT, fileName, String.valueOf(line), String.valueOf(lineColumn), "halt");
                }
                else {
                    return null;
                }
            }
            case 'p' -> {
                if (advanceAndMatch('r') && advanceAndMatch('i') && advanceAndMatch('n') && advanceAndMatch('t')) {
                    return new Token(TokenType.PRINT, fileName, String.valueOf(line), String.valueOf(lineColumn), "print");
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
                        return new Token(TokenType.FNAME, fileName, String.valueOf(line), String.valueOf(lineColumn), value);
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
                        return new Token(TokenType.TEXTLIT, fileName, String.valueOf(line), String.valueOf(lineColumn), value);
                    }
                }
                return null;
            }
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-' -> {
                // match regex ^(0|-?0\.[0-9]*[1-9]|-?[1-9][0-9]*|-?[1-9][0-9]*\.[0-9]*[1-9])$
                goBack(1);
                if (advanceAndMatch('0')) {
                    if (!advanceAndMatch('.')) {
                        return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), String.valueOf(lineColumn), "0");
                    }
                    String value = "0.";
                    while (Character.isDigit(this.source.charAt(this.column))) {
                        value += this.current;
                        advance();
                    }

                    // if last character in value is not equal to 1-9 return null
                    if (value.charAt(value.length() - 1) == '0') {
                        return null;
                    }

                    return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), String.valueOf(lineColumn), value);  
                }
                else if(goBack(this.seekValue) && advanceAndMatch('-') && advanceAndMatch('0')) {
                    if (!advanceAndMatch('.')) {
                        return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), String.valueOf(lineColumn), "-0");
                    }
                    String value = "-0.";
                    while (Character.isDigit(this.source.charAt(this.column))) {
                        value += this.current;
                        advance();
                    }

                    // if last character in value is not equal to 1-9 return null
                    if (value.charAt(value.length() - 1) == '0') {
                        return null;
                    }

                    return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), String.valueOf(lineColumn), value);
                }
                else if(goBack(this.seekValue) && advanceAndMatch('-') && Character.isDigit(this.source.charAt(this.column))) {
                    String value = "-";
                    value += this.current;
                    advance();
                    while (Character.isDigit(this.source.charAt(this.column))) {
                        value += this.current;
                        advance();
                    }
                    if (this.source.charAt(this.column) != '.') {
                        return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), String.valueOf(lineColumn), value);
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
                    return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), String.valueOf(lineColumn), value);
                }
                else if(goBack(this.seekValue) && Character.isDigit(this.source.charAt(this.column))) {
                    String value = "";
                    value += this.current;
                    advance();
                    while (Character.isDigit(this.source.charAt(this.column))) {
                        value += this.current;
                        advance();
                    }
                    if (this.source.charAt(this.column) != '.') {
                        return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), String.valueOf(lineColumn), value);
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
                    return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), String.valueOf(lineColumn), value);
                }
                else {
                    return null;
                }
            }
            case ' ', '\r', '\t', '\0' -> {
                return new Token(TokenType.NULLTYPE, fileName, String.valueOf(line), String.valueOf(lineColumn), String.valueOf(c));
            }
            case '\n' -> {
                line++;
                lineColumn = 0;
                return new Token(TokenType.NULLTYPE, fileName, String.valueOf(line), String.valueOf(lineColumn), String.valueOf(c));
            }
            default -> {
                return null;
            }
        }
    }

    private boolean isAtEnd() { return this.column >= this.source.length() - 1;}

    private char advance() {
        if (this.column + 1 >= this.source.length()) {
            return '\0';
        }
        ++this.lineColumn;
        return this.current = this.source.charAt(++this.column);
    }

    private boolean advanceAndMatch(char expected) {
        char c = advance();
        ++this.seekValue;
        return c == expected;
    }

    private boolean goBack(int steps) {
        this.column -= steps;
        this.seekValue = 0;
        return true;
    }
}
