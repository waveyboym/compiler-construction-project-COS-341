package Tests;

import Utils.Scope;
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

        testCorrectlyTypedCode();
        testTypeMismatchInAssignment();
        testFunctionReturnTypeMismatch();
        testCorrectFunctionCall();
        testFunctionCallIncorrectParams();
        testUndeclaredVariableUsage();
        testSampleProgram();
        // Add more test methods as needed.

        System.out.println("Tests passed: " + testsPassed + "/" + totalTests);
        System.out.println("Tests failed: " + testsFailed + "/" + totalTests);
        System.out.println("Total tests: " + totalTests);
    }

    /**
     * Test: Correctly typed code should pass the type checker.
     */
    public static void testCorrectlyTypedCode() {
        String testName = "testCorrectlyTypedCode";
        totalTests++;

        try {
            // Parse the XML to obtain the syntax tree
            SyntaxTreeParser parser = new SyntaxTreeParser();
            SyntaxTreeNode root = parser.parse("src/Tests/TestCases/TypeChecker/testCorrectlyTypedCode.xml");

            ScopeAnalyzer scopeAnalyzer = new ScopeAnalyzer();
            scopeAnalyzer.analyze(root);

            Scope globalScope = scopeAnalyzer.getGlobalScope();

            // Create the TypeChecker
            TypeChecker typeChecker = new TypeChecker(globalScope);

            // Perform type checking
            boolean result = typeChecker.typecheck(root);

            // Assert that the type checking succeeds
            if (result) {
                System.out.println("\u001B[32m[PASS]\u001B[0m " + testName);
                testsPassed++;
            } else {
                System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Expected type checking to pass.");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }

    /**
     * Test: Type mismatch in assignment should fail the type checker.
     */
    public static void testTypeMismatchInAssignment() {
        String testName = "testTypeMismatchInAssignment";
        totalTests++;

        try {
            // Parse the XML to obtain the syntax tree
            SyntaxTreeParser parser = new SyntaxTreeParser();
            SyntaxTreeNode root = parser.parse("src/Tests/TestCases/TypeChecker/testTypeMismatchInAssignment.xml");

            ScopeAnalyzer scopeAnalyzer = new ScopeAnalyzer();
            scopeAnalyzer.analyze(root);

            Scope globalScope = scopeAnalyzer.getGlobalScope();

            // Create the TypeChecker
            TypeChecker typeChecker = new TypeChecker(globalScope);

            // Perform type checking
            boolean result = typeChecker.typecheck(root);

            // Assert that the type checking fails
            if (!result) {
                System.out.println("\u001B[32m[PASS]\u001B[0m " + testName);
                testsPassed++;
            } else {
                System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Expected type checking to fail.");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }

    /**
     * Test: Function return type mismatch should fail the type checker.
     */
    public static void testFunctionReturnTypeMismatch() {
        String testName = "testFunctionReturnTypeMismatch";
        totalTests++;

        try {
            // Parse the XML to obtain the syntax tree
            SyntaxTreeParser parser = new SyntaxTreeParser();
            SyntaxTreeNode root = parser.parse("src/Tests/TestCases/TypeChecker/testFunctionReturnTypeMismatch.xml");

            ScopeAnalyzer scopeAnalyzer = new ScopeAnalyzer();
            scopeAnalyzer.analyze(root);

            Scope globalScope = scopeAnalyzer.getGlobalScope();

            // Create the TypeChecker
            TypeChecker typeChecker = new TypeChecker(globalScope);

            // Perform type checking
            boolean result = typeChecker.typecheck(root);

            // Assert that the type checking fails
            if (!result) {
                System.out.println("\u001B[32m[PASS]\u001B[0m " + testName);
                testsPassed++;
            } else {
                System.out.println(
                        "\u001B[31m[FAIL]\u001B[0m " + testName
                                + ": Expected type checking to fail due to return type mismatch.");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }

    /**
     * Test: Correct function call with numeric parameters should pass the type
     * checker.
     */
    public static void testCorrectFunctionCall() {
        String testName = "testCorrectFunctionCall";
        totalTests++;

        try {
            // Parse the XML to obtain the syntax tree
            SyntaxTreeParser parser = new SyntaxTreeParser();
            SyntaxTreeNode root = parser.parse("src/Tests/TestCases/TypeChecker/testCorrectFunctionCall.xml");

            ScopeAnalyzer scopeAnalyzer = new ScopeAnalyzer();
            scopeAnalyzer.analyze(root);

            Scope globalScope = scopeAnalyzer.getGlobalScope();

            // Create the TypeChecker
            TypeChecker typeChecker = new TypeChecker(globalScope);

            // Perform type checking
            boolean result = typeChecker.typecheck(root);

            // Assert that the type checking succeeds
            if (result) {
                System.out.println("\u001B[32m[PASS]\u001B[0m " + testName);
                testsPassed++;
            } else {
                System.out.println(
                        "\u001B[31m[FAIL]\u001B[0m " + testName + ": Expected type checking to pass.");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }

    /**
     * Test: Function call with incorrect parameter types should fail the type
     * checker.
     */
    public static void testFunctionCallIncorrectParams() {
        String testName = "testFunctionCallIncorrectParams";
        totalTests++;

        try {
            // Parse the XML to obtain the syntax tree
            SyntaxTreeParser parser = new SyntaxTreeParser();
            SyntaxTreeNode root = parser.parse("src/Tests/TestCases/TypeChecker/testFunctionCallIncorrectParams.xml");

            ScopeAnalyzer scopeAnalyzer = new ScopeAnalyzer();
            scopeAnalyzer.analyze(root);

            Scope globalScope = scopeAnalyzer.getGlobalScope();

            // Create the TypeChecker
            TypeChecker typeChecker = new TypeChecker(globalScope);

            // Perform type checking
            boolean result = typeChecker.typecheck(root);

            // Assert that the type checking fails
            if (!result) {
                System.out.println("\u001B[32m[PASS]\u001B[0m " + testName);
                testsPassed++;
            } else {
                System.out.println(
                        "\u001B[31m[FAIL]\u001B[0m " + testName
                                + ": Expected type checking to fail due to incorrect parameters.");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }

    /**
     * Test: Using an undeclared variable should fail the type checker.
     */
    public static void testUndeclaredVariableUsage() {
        String testName = "testUndeclaredVariableUsage";
        totalTests++;

        try {
            // Parse the XML to obtain the syntax tree
            SyntaxTreeParser parser = new SyntaxTreeParser();
            SyntaxTreeNode root = parser.parse("src/Tests/TestCases/TypeChecker/testUndeclaredVariableUsage.xml");

            ScopeAnalyzer scopeAnalyzer = new ScopeAnalyzer();
            scopeAnalyzer.analyze(root);

            Scope globalScope = scopeAnalyzer.getGlobalScope();

            // Create the TypeChecker
            TypeChecker typeChecker = new TypeChecker(globalScope);

            // Perform type checking
            boolean result = typeChecker.typecheck(root);

            // Assert that the type checking fails
            if (!result) {
                System.out.println("\u001B[32m[PASS]\u001B[0m " + testName);
                testsPassed++;
            } else {
                System.out.println(
                        "\u001B[31m[FAIL]\u001B[0m " + testName
                                + ": Expected type checking to fail due to undeclared variable.");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }

    /**
     * Test: Sample program should pass the type checker.
     */
    public static void testSampleProgram() {
        String testName = "testSampleProgram";
        totalTests++;

        try {
            // Parse the XML to obtain the syntax tree
            SyntaxTreeParser parser = new SyntaxTreeParser();
            SyntaxTreeNode root = parser.parse("src/Tests/TestCases/TypeChecker/testSampleProgram.xml");

            ScopeAnalyzer scopeAnalyzer = new ScopeAnalyzer();
            scopeAnalyzer.analyze(root);

            Scope globalScope = scopeAnalyzer.getGlobalScope();

            // Create the TypeChecker
            TypeChecker typeChecker = new TypeChecker(globalScope);

            // Perform type checking
            boolean result = typeChecker.typecheck(root);

            // Assert that the type checking succeeds
            if (result) {
                System.out.println("\u001B[32m[PASS]\u001B[0m " + testName);
                testsPassed++;
            } else {
                System.out.println(
                        "\u001B[31m[FAIL]\u001B[0m " + testName + ": Expected type checking to pass.");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }
}
