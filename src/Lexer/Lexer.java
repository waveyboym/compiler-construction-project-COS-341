package Lexer;

import Interfaces.Token;
import Interfaces.TokenType;
import Utils.Errors;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String source;
    private final String fileName;
    private char current;
    private int line, column, seekValue, lineColumn;
    private String currentLine;

    public Lexer(String source, String fileName) {
        this.fileName = fileName;
        this.source = source;
        this.current = source.charAt(0);
        this.line = 1;
        this.column = 0;
        this.seekValue = 0;
        this.currentLine = "";
    }

    public List<Token> scanTokens() {
        this.currentLine += this.current;
        List<Token> tokens = new ArrayList<>();
        while (!isAtEnd()) {
            Token token = processCurrentChar();
            if (token == null){
                throw new RuntimeException(Errors.formatLexerError(fileName, line, column, current, currentLine));
            }
            this.seekValue = 0;
            advance();

            // add token to list if not null
            tokens.add(token);
        }
        return tokens;
    }

    private Token processCurrentChar() {
        char c = this.current;
        switch (c) {
            case '(' -> {
                return new Token(TokenType.LEFT_PAREN, fileName, String.valueOf(line), lineColumn, String.valueOf(c));
            }
            case ')' -> {
                return new Token(TokenType.RIGHT_PAREN, fileName, String.valueOf(line), lineColumn, String.valueOf(c));
            }
            case '{' -> {
                return new Token(TokenType.LEFT_BRACE, fileName, String.valueOf(line), lineColumn, String.valueOf(c));
            }
            case '}' -> {
                return new Token(TokenType.RIGHT_BRACE, fileName, String.valueOf(line), lineColumn, String.valueOf(c));
            }
            case ',' -> {
                return new Token(TokenType.COMMA, fileName, String.valueOf(line), lineColumn, String.valueOf(c));
            }
            case ';' -> {
                return new Token(TokenType.SEMICOLON, fileName, String.valueOf(line), lineColumn, String.valueOf(c));
            }
            case '=' -> {
                return new Token(TokenType.EQUAL_SIGN, fileName, String.valueOf(line), lineColumn, String.valueOf(c));
            }
            case '<' -> {
                return new Token(TokenType.LESS_THAN_SIGN, fileName, String.valueOf(line), lineColumn, String.valueOf(c));
            }
            case 'n' -> {
                if (advanceAndMatch('o') && advanceAndMatch('t')) {
                    return new Token(TokenType.NOT, fileName, String.valueOf(line), lineColumn, "not");
                }
                else if (goBack(this.seekValue) && advanceAndMatch('u') && advanceAndMatch('m')) {
                    return new Token(TokenType.NUM, fileName, String.valueOf(line), lineColumn, "num");
                }
                else {
                    return null;
                }
            }
            case 's' -> {
                if (advanceAndMatch('q') && advanceAndMatch('r') && advanceAndMatch('t')) {
                    return new Token(TokenType.SQRT, fileName, String.valueOf(line), lineColumn, "sqrt");
                }
                else if (goBack(this.seekValue) && advanceAndMatch('k') && advanceAndMatch('i') && advanceAndMatch('p')) {
                    return new Token(TokenType.SKIP, fileName, String.valueOf(line), lineColumn, "skip");
                }
                else if (goBack(this.seekValue) && advanceAndMatch('u') && advanceAndMatch('b')) {
                    return new Token(TokenType.SUB, fileName, String.valueOf(line), lineColumn, "sub");
                }
                else {
                    return null;
                }
            }
            case 'o' -> {
                if (advanceAndMatch('r')) {
                    return new Token(TokenType.OR, fileName, String.valueOf(line), lineColumn, "or");
                }
                else {
                    return null;
                }
            }
            case 'a' -> {
                if (advanceAndMatch('n') && advanceAndMatch('d')) {
                    return new Token(TokenType.AND, fileName, String.valueOf(line), lineColumn, "and");
                }
                else if (goBack(this.seekValue) && advanceAndMatch('d') && advanceAndMatch('d')) {
                    return new Token(TokenType.ADD, fileName, String.valueOf(line), lineColumn, "add");
                }
                else {
                    return null;
                }
            }
            case 'e' -> {
                if (advanceAndMatch('q')) {
                    return new Token(TokenType.EQ, fileName, String.valueOf(line), lineColumn, "eq");
                }
                else if (goBack(this.seekValue) && advanceAndMatch('l') && advanceAndMatch('s') && advanceAndMatch('e')) {
                    return new Token(TokenType.ELSE, fileName, String.valueOf(line), lineColumn, "else");
                }
                if (goBack(this.seekValue) && advanceAndMatch('n') && advanceAndMatch('d')) {
                    return new Token(TokenType.END, fileName, String.valueOf(line), lineColumn, "end");
                }
                else {
                    return null;
                }
            }
            case 'g' -> {
                if (advanceAndMatch('r') && advanceAndMatch('t')) {
                    return new Token(TokenType.GT, fileName, String.valueOf(line), lineColumn, "grt");
                }
                else {
                    return null;
                }
            }
            case 'm' -> {
                if (advanceAndMatch('u') && advanceAndMatch('l')) {
                    return new Token(TokenType.MUL, fileName, String.valueOf(line), lineColumn, "mul");
                }
                if (goBack(this.seekValue) && advanceAndMatch('a') && advanceAndMatch('i') && advanceAndMatch('n')) {
                    return new Token(TokenType.MAIN, fileName, String.valueOf(line), lineColumn, "main");
                }
                else {
                    return null;
                }
            }
            case 'd' -> {
                if (advanceAndMatch('i') && advanceAndMatch('v')){
                    return new Token(TokenType.DIV, fileName, String.valueOf(line), lineColumn, "div");
                }
                else {
                    return null;
                }
            }
            case 't' -> {
                if (advanceAndMatch('h') && advanceAndMatch('e') && advanceAndMatch('n')){
                    return new Token(TokenType.THEN, fileName, String.valueOf(line), lineColumn, "then");
                }
                else if (goBack(this.seekValue) && advanceAndMatch('e') && advanceAndMatch('x') && advanceAndMatch('t')){
                    return new Token(TokenType.VTEXT, fileName, String.valueOf(line), lineColumn, "text");
                }
                else {
                    return null;
                }
            }
            case 'v' -> {
                if (advanceAndMatch('o') && advanceAndMatch('i') && advanceAndMatch('d')) {
                    return new Token(TokenType.FVOID, fileName, String.valueOf(line), lineColumn, "void");
                }
                else {
                    return null;
                }
            }
            case 'b' -> {
                if (advanceAndMatch('e') && advanceAndMatch('g') && advanceAndMatch('i') && advanceAndMatch('n')){
                    return new Token(TokenType.BEGIN, fileName, String.valueOf(line), lineColumn, "begin");
                }
                else {
                    return null;
                }
            }
            case 'i' -> {
                if(advanceAndMatch('n') && advanceAndMatch('p') && advanceAndMatch('u') && advanceAndMatch('t')){
                    return new Token(TokenType.INPUT, fileName, String.valueOf(line), lineColumn, "input");
                }
                else if (goBack(this.seekValue) && advanceAndMatch('f')) {
                    return new Token(TokenType.IF, fileName, String.valueOf(line), lineColumn, "if");
                }
                else {
                    return null;
                }
            }
            case 'h' -> {
                if (advanceAndMatch('a') && advanceAndMatch('l') && advanceAndMatch('t')) {
                    return new Token(TokenType.HALT, fileName, String.valueOf(line), lineColumn, "halt");
                }
                else {
                    return null;
                }
            }
            case 'p' -> {
                if (advanceAndMatch('r') && advanceAndMatch('i') && advanceAndMatch('n') && advanceAndMatch('t')) {
                    return new Token(TokenType.PRINT, fileName, String.valueOf(line), lineColumn, "print");
                }
                else {
                    return null;
                }
            }
            case 'r' -> {
                if (advanceAndMatch('e') && advanceAndMatch('t') && advanceAndMatch('u') && advanceAndMatch('r') && advanceAndMatch('n')) {
                    return new Token(TokenType.RETURN, fileName, String.valueOf(line), lineColumn, "return");
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
                    if (Character.isLetter(this.current)) {
                        value += this.current;
                        advance();
                        while (Character.isLetterOrDigit(this.current)) {
                            value += this.current;
                            advance();
                        }
                        goBack(1);
                        return new Token(TokenType.VNAME, fileName, String.valueOf(line), lineColumn, value);
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
                    if (Character.isLetter(this.current)) {
                        value += this.current;
                        advance();
                        while (Character.isLetterOrDigit(this.current)) {
                            value += this.current;
                            advance();
                        }
                        goBack(1);
                        return new Token(TokenType.FNAME, fileName, String.valueOf(line), lineColumn, value);
                    }
                }
                return null;
            }
            case '"' -> {
                // match regex max "[A-Z][a-z][a-z][a-z][a-z][a-z][a-z][a-z]", min "[A-Z]"
                // must have a max of 9 characters
                advance();
                if (Character.isUpperCase(this.current)) {
                    String value = "";
                    value += this.current;
                    advance();
                    for (int i = 0; i < 9; i++) {
                        if (Character.isLowerCase(this.current)) {
                            value += this.current;
                            advance();
                        }
                        else {
                            break;
                        }
                    }
                    //expecting closing quote
                    if (this.current == '"') {
                        return new Token(TokenType.TEXTLIT, fileName, String.valueOf(line), lineColumn, value);
                    }
                }
                return null;
            }
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-' -> {
                // match regex ^(0|-?0\.[0-9]*[1-9]|-?[1-9][0-9]*|-?[1-9][0-9]*\.[0-9]*[1-9])$
                if (this.current == '0') {
                    if (!advanceAndMatch('.')) {
                        goBack(1);
                        return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), lineColumn, "0");
                    }
                    String value = "0.";
                    while (Character.isDigit(this.current)) {
                        value += this.current;
                        advance();
                    }

                    if (this.current >= '1' && this.current <= '9') {
                        return null;
                    }

                    // if last character in value is not equal to 1-9 return null
                    if (value.charAt(value.length() - 1) == '0') {
                        return null;
                    }

                    goBack(1);
                    return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), lineColumn, value);  
                }
                else if(this.current == '-' && advanceAndMatch('0')) {
                    if (!advanceAndMatch('.')) {
                        goBack(1);
                        return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), lineColumn, "-0");
                    }
                    String value = "-0.";
                    while (Character.isDigit(this.current)) {
                        value += this.current;
                        advance();
                    }

                    if (this.current >= '1' && this.current <= '9') {
                        return null;
                    }

                    // if last character in value is not equal to 1-9 return null
                    if (value.charAt(value.length() - 1) == '0') {
                        return null;
                    }

                    goBack(1);
                    return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), lineColumn, value);
                }
                else if(goBack(this.seekValue) && this.current == '-'  && this.advanceAndMatchDigit()) {
                    String value = "-";
                    value += this.current;
                    advance();
                    while (Character.isDigit(this.current)) {
                        value += this.current;
                        advance();
                    }
                    if (this.current != '.') {
                        goBack(1);
                        return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), lineColumn, value);
                    }
                    value += this.current;
                    advance();
                    while (Character.isDigit(this.current)) {
                        value += this.current;
                        advance();
                    }

                    if (this.current >= '1' && this.current <= '9') {
                        return null;
                    }

                    if (value.charAt(value.length() - 1) == '0') {
                        return null;
                    }

                    goBack(1);
                    return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), lineColumn, value);
                }
                else if(goBack(this.seekValue) && Character.isDigit(this.current)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(this.current);
                    advance();
                    while (Character.isDigit(this.current)) {
                        sb.append(this.current);
                        advance();
                    }
                    if (this.current != '.') {
                        goBack(1);
                        return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), lineColumn, sb.toString());
                    }
                    sb.append(this.current);
                    advance();
                    while (Character.isDigit(this.current)) {
                        sb.append(this.current);
                        advance();
                    }

                    if (this.current >= '1' && this.current <= '9') {
                        return null;
                    }

                    if (sb.charAt(sb.length() - 1) == '0') {
                        return null;
                    }

                    goBack(1);
                    return new Token(TokenType.NUMLIT, fileName, String.valueOf(line), lineColumn, sb.toString());
                }
                else {
                    return null;
                }
            }
            case ' ', '\r', '\t', '\0' -> {
                return new Token(TokenType.NULLTYPE, fileName, String.valueOf(line), lineColumn, String.valueOf(c));
            }
            case '\n' -> {
                line++;
                lineColumn = 0;
                return new Token(TokenType.NULLTYPE, fileName, String.valueOf(line), lineColumn, String.valueOf(c));
            }
            default -> {
                return null;
            }
        }
    }

    private boolean isAtEnd() { return this.column >= this.source.length() - 1;}

    private char advance() {
        if (this.column + 1 >= this.source.length()) {
            this.current = '\0';
            return '\0';
        }
        ++this.lineColumn;
        ++this.column;
        this.currentLine += this.source.charAt(this.column);
        return this.current = this.source.charAt(this.column);
    }

    private boolean advanceAndMatch(char expected) {
        char c = advance();
        ++this.seekValue;
        return c == expected;
    }

    private boolean advanceAndMatchDigit() {
        char c = advance();
        ++this.seekValue;
        return Character.isDigit(c);
    }

    private boolean goBack(int steps) {
        this.column -= steps;
        this.current = this.source.charAt(this.column);
        this.seekValue = 0;
        // pop currentLine by steps

        this.currentLine = this.currentLine.substring(0, this.column + 1);
        return true;
    }
}
