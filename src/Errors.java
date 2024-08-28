public class Errors {
    public static String formatError(String filename, int line, int column, char invalidChar, String lineContent) {
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
}
