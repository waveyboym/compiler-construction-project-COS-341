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
                    .append(expected)
                    .append("\n");

        // Add the entire line content
        errorBuilder.append(lineContent)
                    .append("\n");

        // Create the indicator line
        for (int i = 0; i < token.Column; i++) {
            errorBuilder.append(" ");
        }
        errorBuilder.append("^\n");

        return errorBuilder.toString();
    }
}
