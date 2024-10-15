import CodeGenBasic.CodeGenBasic;
import Interfaces.ParseNode;
import Interfaces.SyntaxTreeNode;
import Interfaces.Token;
import Lexer.Lexer;
import Parser.Parser;
import ScopeAnalyzer.ScopeAnalyzer;
import Utils.FileManager;
import Utils.SyntaxTreeParser;
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

            System.out.println("Lexing Competed Successfully");

            Parser parser = new Parser(tokens);
            ParseNode pt = parser.parse();

            String xmlparse = XMLGenerator.generatePARSERXML(pt);
            FileManager.createAndWriteFile("out/parser.xml", xmlparse);

            CodeGenBasic cgb = new CodeGenBasic(pt);
            FileManager.writeBasicCode("out/basic.bas", cgb.generateCode());
            System.out.println(pt.toString());

            System.out.println("Parsing Completed Successfully");

            SyntaxTreeParser stp = new SyntaxTreeParser();
            SyntaxTreeNode st = stp.parse("out/parser.xml");

            if (st == null) {
                System.err.println("Failed to parse syntax tree.");
                return;
            }

            // Perform scope analysis
            ScopeAnalyzer scopeAnalyzer = new ScopeAnalyzer();
            scopeAnalyzer.analyze(st);

            System.out.println(st.toString());
            System.out.println("Scope Analysis Completed Successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
