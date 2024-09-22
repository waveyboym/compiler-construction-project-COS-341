package Utils;

import Interfaces.Token;
import Interfaces.TokenType;

public class Errors {
    public static String formatLexerError(String filename, int line, int column, char invalidChar, String lineContent) {
        StringBuilder errorBuilder = new StringBuilder();

        // Add the file name, line, column, and error message
        errorBuilder.append(filename)
                    .append(":")
                    .append(line)
                    .append(":")
                    .append(column)
                    .append(": error: disallowed character '")
                    .append(invalidChar)
                    .append("'\n");

        // Add the entire line content
        errorBuilder.append(lineContent)
                    .append("\n");

        // Create the indicator line
        for (int i = 0; i < column; i++) {
            errorBuilder.append(" ");
        }
        errorBuilder.append("^\n");

        return errorBuilder.toString();
    }

    public static String formatParserError(Token token, String expected, String lineContent) {
        StringBuilder errorBuilder = new StringBuilder();

        // Add the file name, line, column, and error message
        errorBuilder.append(token.fileName)
                .append(":")
                .append(token.Line)
                .append(":")
                .append(token.Column)
                .append(": error: expected ")
                .append("\"")
                .append(expected)
                .append("\"")
                .append(" not: ")
                .append("\"")
                .append(token.Value)
                .append("\"")
                .append("\n");

        // Add the entire line content
        errorBuilder.append(lineContent)
                    .append("\n");

        // Create the indicator line
        for (int i = 1; i < token.Column; i++) {
            errorBuilder.append(" ");
        }
        errorBuilder.append("^\n");

        return errorBuilder.toString();
    }

    public static String stringRepresentation(TokenType type){
        return switch (type) {
            case LEFT_PAREN -> "(";
            case RIGHT_PAREN -> ")";
            case LEFT_BRACE -> "{";
            case RIGHT_BRACE -> "}";
            case COMMA -> ",";
            case SEMICOLON -> ";";
            case EQUAL_SIGN -> "=";
            case LESS_THAN_SIGN -> "<";
            case NOT -> "not";
            case SQRT -> "sqrt";
            case OR -> "or";
            case AND -> "and";
            case EQ -> "eq";
            case GT -> "grt";
            case ADD -> "add";
            case SUB -> "sub";
            case MUL -> "mul";
            case DIV -> "div";
            case NUM -> "num";
            case VTEXT -> "text";
            case FVOID -> "void";
            case VNAME -> "V_sum for eg";
            case FNAME -> "F_sum for eg";
            case NUMLIT -> "3, 4, 5 for eg";
            case TEXTLIT -> "\"hello\" for eg";
            case BEGIN -> "begin";
            case END -> "end";
            case IF -> "if";
            case THEN -> "then";
            case ELSE -> "else";
            case SKIP -> "skip";
            case HALT -> "halt";
            case PRINT -> "print";
            case INPUT -> "input";
            case MAIN -> "main";
            case RETURN -> "return";
            // nulltype is an "invisible" character and not user facing
            //case NULLTYPE -> "nulltype";
            // eof is an "invisible" character and not user facing
            //case EOF -> "eof";
            default -> "unknown";
        };
    }
}
