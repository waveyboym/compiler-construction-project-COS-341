import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        // receive file path from command line arguments
        String path = args[0];
        // read file contents
        String contents = FileReader.readFileAndReturnContents(path);

        try {
            Lexer lexer = new Lexer(contents);
            List<Token> tokens = lexer.scanTokens();
            for (Token token : tokens) {
                System.out.println(token.type + " " + token);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}