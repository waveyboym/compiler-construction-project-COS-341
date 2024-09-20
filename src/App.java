import Interfaces.ParseNode;
import Interfaces.Token;
import Lexer.Lexer;
import Parser.Parser;
import Utils.FileManager;
import Utils.XMLGenerator;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        // receive file path from command line arguments
        // example: java App src/test.txt
        String path;

        if (args.length != 1) {
            path = "input/main.spl";
        } else {
            path = args[0];
        }

        // read file contents
        String contents = FileManager.readFileAndReturnContents(path);

        try {
            Lexer lexer = new Lexer(contents, path);
            List<Token> tokens = lexer.scanTokens();

            String xmllex = XMLGenerator.generateLEXERXML(tokens);
            FileManager.createAndWriteFile("out/lexer.xml", xmllex);
            
            Parser parser = new Parser(tokens);
            ParseNode pt = parser.parse();
            
            String xmlparse = XMLGenerator.generatePARSERXML(pt);
            FileManager.createAndWriteFile("out/parser.xml", xmlparse);

            System.out.println(pt.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
