package Tests;

import Lexer.Lexer;
import Parser.Parser;
import java.util.List;
import Interfaces.Token;
import Utils.FileManager;
import Utils.XMLGenerator;
import Interfaces.ParseNode;
import Utils.SyntaxTreeParser;
import Interfaces.SyntaxTreeNode;
import ScopeAnalyzer.ScopeAnalyzer;

public class ScopeAnalyzerTest {

    private static int totalTests = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("Running ScopeAnalyzer tests...");

        runTest("redeclarationInSameScope.txt", "Semantic Error: Variable 'a' is already declared in this scope.");
        runTest("variableUsageInOuterScope.txt", null);
        runTest("nearestVariableDeclaration.txt", null);
        runTest("undeclaredVariable.txt", "Semantic Error: Variable 'y' is not declared.");
        runTest("variableConflictsFunction.txt",
                "Semantic Error: Variable 'func' is already declared in this scope.");
        runTest("variableReservedWord.txt", "Semantic Error: Variable name 'halt' is a reserved keyword.");
        runTest("variablesSameNameDifferentScopes.txt", null);
        runTest("childScopeSameAsParent.txt",
                "Semantic Error: Child scope 'F_func' cannot have the same name as its parent scope.");
        runTest("siblingScopesSameName.txt", "Semantic Error: Sibling scopes 'F_func1' cannot have the same name.");
        runTest("callToImmediateChildScope.txt", null);
        runTest("recursiveCallFunction.txt", null);
        runTest("recursiveCallMain.txt", "Semantic Error: Recursive call to 'main' is not allowed.");

        System.out.println("Tests passed: " + testsPassed + "/" + totalTests);
        System.out.println("Tests failed: " + testsFailed + "/" + totalTests);
        System.out.println("Total tests: " + totalTests);
    }

    private static void runTest(String fileName, String expectedError) {
        totalTests++;

        try {
            SyntaxTreeNode root = getSyntaxTree(fileName);
            ScopeAnalyzer analyzer = new ScopeAnalyzer();
            analyzer.analyze(root);

            List<String> errors = analyzer.getErrors();

            if (expectedError == null) {
                if (errors.isEmpty()) {
                    System.out.println("\u001B[32m[PASS]\u001B[0m " + fileName);
                    testsPassed++;
                } else {
                    System.out.println("\u001B[31m[FAIL]\u001B[0m " + fileName + ": Unexpected errors: " + errors);
                    testsFailed++;
                }
            } else {
                if (errors.contains(expectedError)) {
                    System.out.println("\u001B[32m[PASS]\u001B[0m " + fileName);
                    testsPassed++;
                } else {
                    System.out.println("\u001B[31m[FAIL]\u001B[0m " + fileName + ": Expected error not found.");
                    testsFailed++;
                }
            }
        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + fileName + ": Exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }

    private static SyntaxTreeNode getSyntaxTree(String fileName) {
        String contents = FileManager.readFileAndReturnContents("src/Tests/TestCases/ScopeAnalyzer/" + fileName);

        Lexer lexer = new Lexer(contents, "path");
        List<Token> tokens = lexer.scanTokens();

        Parser parser = new Parser(tokens);
        ParseNode pt = parser.parse();

        String xml = XMLGenerator.generatePARSERXML(pt);
        FileManager.createAndWriteFile("src/Tests/TestCases/ScopeAnalyzer/scope.xml", xml);

        SyntaxTreeParser stp = new SyntaxTreeParser();
        return stp.parse("src/Tests/TestCases/ScopeAnalyzer/scope.xml");
    }
}
