import Interfaces.Token;
import Lexer.Lexer;
import Utils.FileManager;
import Utils.XMLGenerator;
import java.util.List;

public class App {

    public static void printTokens(List<Token> tokens) {
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
    public static void main(String[] args) throws Exception {
        // receive file path from command line arguments
        // example: java App src/test.txt
        String path;

        if (args.length != 1) {
            path = "input/input.txt";
        } else {
            path = args[0];
        }

        // read file contents
        String contents = FileManager.readFileAndReturnContents(path);

        try {
            Lexer lexer = new Lexer(contents, path);
            List<Token> tokens = lexer.scanTokens();
            XMLGenerator xmlGenerator = new XMLGenerator(tokens);
            String xml = xmlGenerator.generateXML();
            FileManager.createAndWriteFile("out/output.xml", xml);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
