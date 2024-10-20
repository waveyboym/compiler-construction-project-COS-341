import Lexer.Lexer;
import Utils.Scope;
import Parser.Parser;
import Interfaces.Token;
import Utils.FileManager;
import Utils.XMLGenerator;
import Interfaces.ParseNode;
import Utils.SyntaxTreeParser;
import TypeChecker.TypeChecker;
import CodeGenBasic.CodeGenBasic;
import Interfaces.SyntaxTreeNode;
import ScopeAnalyzer.ScopeAnalyzer;

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

            System.out.println("Parsing Completed Successfully");

            SyntaxTreeParser stp = new SyntaxTreeParser();
            SyntaxTreeNode st = stp.parse("out/parser.xml");

            if (st == null) {
                System.err.println("Failed to parse syntax tree.");
                return;
            }

            ScopeAnalyzer scopeAnalyzer = new ScopeAnalyzer();
            scopeAnalyzer.analyze(st);

            System.out.println("Scope Analysis Completed Successfully");

            Scope globalScope = scopeAnalyzer.getGlobalScope();
            TypeChecker typeChecker = new TypeChecker(globalScope);
            boolean result = typeChecker.typecheck(st);

            if (result) {
                System.out.println("Type checking passed.");

                CodeGenBasic cgb = new CodeGenBasic(pt);
                FileManager.writeBasicCode("out/basic.bas", cgb.generateCode());

                System.out.println("Code Generation Completed Successfully");
            } else {
                System.out.println("Type checking failed with errors:");

                for (String error : typeChecker.getErrors()) {
                    System.out.println(error);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
