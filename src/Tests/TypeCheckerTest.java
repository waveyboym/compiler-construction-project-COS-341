package Tests;

import Utils.Scope;
import Lexer.Lexer;
import Parser.Parser;
import java.util.List;
import Interfaces.Token;
import Utils.FileManager;
import Utils.XMLGenerator;
import Interfaces.ParseNode;
import Utils.SyntaxTreeParser;
import TypeChecker.TypeChecker;
import Interfaces.SyntaxTreeNode;
import ScopeAnalyzer.ScopeAnalyzer;

public class TypeCheckerTest {

    private static int totalTests = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("Running TypeChecker tests...");

        runTest("validNumericAssignment.txt", true, null);
        runTest("invalidStringToNumericAssignment.txt", false, "Type Error: Type mismatch in assignment to variable 'x'");
        runTest("validFunctionReturn.txt", true, null);
        runTest("invalidReturnType.txt", false, "Type Error: Return type mismatch: Expected 'n', found 't'");
        runTest("numericInputAssignment.txt", true, null);
        runTest("invalidTextInputAssignment.txt", false, "Type Error: Input can only be assigned to variables of type 'num'");
        runTest("validBinaryOperation.txt", true, null);
        runTest("mismatchedBinaryOperation.txt", false, "Type Error: Type mismatch in binary operation 'add'");
        runTest("validConditionalBranch.txt", true, null);
        runTest("invalidConditionalBranch.txt", false, "Type Error: Condition in if statement must be boolean");
        runTest("validFunctionCall.txt", true, null);
        runTest("invalidFunctionCall.txt", false, "Type Error: Function arguments must be of type 'num'.");
        runTest("validUnaryOperation.txt", true, null);
        runTest("invalidUnaryOperation.txt", false, "Type Error: Type mismatch in unary operation 'sqrt'");

        System.out.println("Tests passed: " + testsPassed + "/" + totalTests);
        System.out.println("Tests failed: " + testsFailed + "/" + totalTests);
        System.out.println("Total tests: " + totalTests);
    }

    private static SyntaxTreeNode getSyntaxTree(String fileName) {
        String contents = FileManager.readFileAndReturnContents("src/Tests/TestCases/TypeChecker/" + fileName);

        Lexer lexer = new Lexer(contents, "path");
        List<Token> tokens = lexer.scanTokens();

        Parser parser = new Parser(tokens);
        ParseNode pt = parser.parse();

        String xml = XMLGenerator.generatePARSERXML(pt);
        FileManager.createAndWriteFile("src/Tests/TestCases/TypeChecker/type.xml", xml);

        SyntaxTreeParser stp = new SyntaxTreeParser();
        return stp.parse("src/Tests/TestCases/TypeChecker/type.xml");
    }

    private static void runTest(String fileName, boolean expectedResult, String expectedError) {
        totalTests++;
        try {
            SyntaxTreeNode root = getSyntaxTree(fileName);

            ScopeAnalyzer scopeAnalyzer = new ScopeAnalyzer();
            scopeAnalyzer.analyze(root);

            Scope globalScope = scopeAnalyzer.getGlobalScope();
            TypeChecker typeChecker = new TypeChecker(globalScope);
            boolean result = typeChecker.typecheck(root);

            if (result == expectedResult) {
                if (expectedError == null || typeChecker.getErrors().contains(expectedError)) {
                    System.out.println("\u001B[32m[PASS]\u001B[0m " + fileName);
                    testsPassed++;
                } else {
                    System.out.println("\u001B[31m[FAIL]\u001B[0m " + fileName +
                            ": Expected error not found.");
                    testsFailed++;
                }
            } else {
                System.out.println("\u001B[31m[FAIL]\u001B[0m " + fileName +
                        ": Expected " + expectedResult);
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + fileName + ": Exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }
}
