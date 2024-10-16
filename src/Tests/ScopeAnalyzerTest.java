package Tests;

import java.util.List;
import java.util.ArrayList;
import Interfaces.TokenType;
import Interfaces.SyntaxTreeNode;
import ScopeAnalyzer.ScopeAnalyzer;

public class ScopeAnalyzerTest {

    private static int totalTests = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("Running ScopeAnalyzer tests...");

        testVariableRedeclarationInSameScope();
        testVariableUsageInOuterScope();
        testNearestVariableDeclarationUsed();
        testUndeclaredVariableUsage();
        testVariableNameConflictsWithFunctionName();
        testVariableNameIsReservedWord();
        testVariablesWithSameNameInDifferentScopes();
        testChildScopeNameNotSameAsParent();
        testSiblingScopesWithSameName();
        testCallToImmediateChildScope();
        testRecursiveCallToFunction();
        testRecursiveCallToMainNotAllowed();

        System.out.println("Tests passed: " + testsPassed + "/" + totalTests);
        System.out.println("Tests failed: " + testsFailed + "/" + totalTests);
        System.out.println("Total tests: " + totalTests);
    }

    /**
     * Test: No variable name may be declared more than once in the same scope.
     */
    public static void testVariableRedeclarationInSameScope() {
        String testName = "testVariableRedeclarationInSameScope";
        totalTests++;

        try {
            // Create syntax tree nodes
            SyntaxTreeNode root = createProgNode();
            SyntaxTreeNode main = createMainNode();

            main.children.add(createVarDeclNode("num", "V_x"));
            main.children.add(createVarDeclNode("num", "V_x")); // Redeclaration
            root.children.add(main);

            // Analyze
            ScopeAnalyzer analyzer = new ScopeAnalyzer();
            analyzer.analyze(root);

            List<String> errors = analyzer.getErrors();

            if (errors.contains("Semantic Error: Variable 'x' is already declared in this scope.")) {
                System.out.println("\u001B[32m[PASS]\u001B[0m " + testName);
                testsPassed++;
            } else {
                System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Expected redeclaration error.");
                testsFailed++;
            }

        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }

    /**
     * Test: The declaration of a used variable name must be found either within the
     * same scope or in an outer scope.
     */
    public static void testVariableUsageInOuterScope() {
        String testName = "testVariableUsageInOuterScope";
        totalTests++;

        try {
            // Create syntax tree nodes
            SyntaxTreeNode root = createProgNode();
            SyntaxTreeNode main = createMainNode();

            main.children.add(createVarDeclNode("num", "V_globalVar"));

            // Function that uses V_globalVar
            SyntaxTreeNode functions = new SyntaxTreeNode(TokenType.FUNCTIONS);
            SyntaxTreeNode funcDecl = createFunctionDeclNode("num", "F_sampleFunction", new ArrayList<>());

            // Function body uses V_globalVar
            SyntaxTreeNode funcBody = funcDecl.children.get(1); // BODY
            SyntaxTreeNode instruc = new SyntaxTreeNode(TokenType.INSTRUC);
            SyntaxTreeNode assign = createAssignNode("V_globalVar", "10");
            instruc.children.add(assign);
            funcBody.children.add(instruc);

            functions.children.add(funcDecl);
            root.children.add(main);
            root.children.add(functions);

            // Analyze
            ScopeAnalyzer analyzer = new ScopeAnalyzer();
            analyzer.analyze(root);

            List<String> errors = analyzer.getErrors();

            if (errors.isEmpty()) {
                System.out.println("\u001B[32m[PASS]\u001B[0m " + testName);
                testsPassed++;
            } else {
                System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Unexpected errors: " + errors);
                testsFailed++;
            }

        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }

    /**
     * Test: If a used variable name has two declarations in two different scopes,
     * the nearest declaration is used.
     */
    public static void testNearestVariableDeclarationUsed() {
        String testName = "testNearestVariableDeclarationUsed";
        totalTests++;

        try {
            // Create syntax tree nodes
            SyntaxTreeNode root = createProgNode();
            SyntaxTreeNode main = createMainNode();

            // Global variable V_var
            main.children.add(createVarDeclNode("num", "V_var"));

            // Function with local V_var
            SyntaxTreeNode functions = new SyntaxTreeNode(TokenType.FUNCTIONS);
            SyntaxTreeNode funcDecl = createFunctionDeclNode("num", "F_sampleFunction", new ArrayList<>());

            // Local variable V_var
            SyntaxTreeNode funcBody = funcDecl.children.get(1); // BODY
            funcBody.children.add(createVarDeclNodeLocal("num", "V_var"));

            // Function body uses V_var
            SyntaxTreeNode instruc = new SyntaxTreeNode(TokenType.INSTRUC);
            SyntaxTreeNode assign = createAssignNode("V_var", "20");
            instruc.children.add(assign);
            funcBody.children.add(instruc);

            functions.children.add(funcDecl);
            root.children.add(main);
            root.children.add(functions);

            // Analyze
            ScopeAnalyzer analyzer = new ScopeAnalyzer();
            analyzer.analyze(root);

            List<String> errors = analyzer.getErrors();

            if (errors.isEmpty()) {
                System.out.println("\u001B[32m[PASS]\u001B[0m " + testName);
                testsPassed++;
            } else {
                System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Unexpected errors: " + errors);
                testsFailed++;
            }

        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }

    /**
     * Test: Every used variable name must be declared.
     */
    public static void testUndeclaredVariableUsage() {
        String testName = "testUndeclaredVariableUsage";
        totalTests++;

        try {
            // Create syntax tree nodes
            SyntaxTreeNode root = createProgNode();
            SyntaxTreeNode main = createMainNode();

            // Use undeclared variable V_x
            SyntaxTreeNode instruc = new SyntaxTreeNode(TokenType.INSTRUC);
            SyntaxTreeNode assign = createAssignNode("V_x", "10");
            instruc.children.add(assign);
            main.children.add(instruc);

            root.children.add(main);

            // Analyze
            ScopeAnalyzer analyzer = new ScopeAnalyzer();
            analyzer.analyze(root);

            List<String> errors = analyzer.getErrors();

            if (errors.contains("Semantic Error: Variable 'x' is not declared.")) {
                System.out.println("\u001B[32m[PASS]\u001B[0m " + testName);
                testsPassed++;
            } else {
                System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Expected undeclared variable error.");
                testsFailed++;
            }

        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }

    /**
     * Test: No variable name can have a function name.
     */
    public static void testVariableNameConflictsWithFunctionName() {
        String testName = "testVariableNameConflictsWithFunctionName";
        totalTests++;

        try {
            // Create syntax tree nodes
            SyntaxTreeNode root = createProgNode();
            SyntaxTreeNode main = createMainNode();

            // Function F_myFunction
            SyntaxTreeNode functions = new SyntaxTreeNode(TokenType.FUNCTIONS);
            SyntaxTreeNode funcDecl = createFunctionDeclNode("num", "F_myFunction", new ArrayList<>());
            functions.children.add(funcDecl);

            // Variable V_myFunction
            main.children.add(createVarDeclNode("num", "V_myFunction"));

            root.children.add(main);
            root.children.add(functions);

            // Analyze
            ScopeAnalyzer analyzer = new ScopeAnalyzer();
            analyzer.analyze(root);

            List<String> errors = analyzer.getErrors();

            if (errors.contains("Semantic Error: Variable 'myFunction' is already declared in this scope.")) {
                System.out.println("\u001B[32m[PASS]\u001B[0m " + testName);
                testsPassed++;
            } else {
                System.out.println(
                        "\u001B[31m[FAIL]\u001B[0m " + testName + ": Expected variable-function name conflict error.");
                testsFailed++;
            }

        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }

    /**
     * Test: No variable name can be a reserved word.
     */
    public static void testVariableNameIsReservedWord() {
        String testName = "testVariableNameIsReservedWord";
        totalTests++;

        try {
            // Create syntax tree nodes
            SyntaxTreeNode root = createProgNode();
            SyntaxTreeNode main = createMainNode();

            // Variable V_if (reserved word)
            main.children.add(createVarDeclNode("num", "V_if"));

            root.children.add(main);

            // Analyze
            ScopeAnalyzer analyzer = new ScopeAnalyzer();
            analyzer.analyze(root);

            List<String> errors = analyzer.getErrors();

            if (errors.contains("Semantic Error: Variable name 'if' is a reserved keyword.")) {
                System.out.println("\u001B[32m[PASS]\u001B[0m " + testName);
                testsPassed++;
            } else {
                System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Expected reserved keyword error.");
                testsFailed++;
            }

        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }

    /**
     * Test: Two variable names with the same name are different if they are
     * declared in different scopes.
     */
    public static void testVariablesWithSameNameInDifferentScopes() {
        String testName = "testVariablesWithSameNameInDifferentScopes";
        totalTests++;

        try {
            // Create syntax tree nodes
            SyntaxTreeNode root = createProgNode();
            SyntaxTreeNode main = createMainNode();

            // Global variable V_x
            main.children.add(createVarDeclNode("num", "V_x"));

            // Function with local V_x
            SyntaxTreeNode functions = new SyntaxTreeNode(TokenType.FUNCTIONS);
            SyntaxTreeNode funcDecl = createFunctionDeclNode("num", "F_myFunction", new ArrayList<>());

            // Local variable V_x
            SyntaxTreeNode funcBody = funcDecl.children.get(1); // BODY
            funcBody.children.add(createVarDeclNodeLocal("num", "V_x"));

            // Function body uses V_x
            SyntaxTreeNode instruc = new SyntaxTreeNode(TokenType.INSTRUC);
            SyntaxTreeNode assign = createAssignNode("V_x", "20");
            instruc.children.add(assign);
            funcBody.children.add(instruc);

            functions.children.add(funcDecl);
            root.children.add(main);
            root.children.add(functions);

            // Analyze
            ScopeAnalyzer analyzer = new ScopeAnalyzer();
            analyzer.analyze(root);

            List<String> errors = analyzer.getErrors();

            if (errors.isEmpty()) {
                System.out.println("\u001B[32m[PASS]\u001B[0m " + testName);
                testsPassed++;
            } else {
                System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Unexpected errors: " + errors);
                testsFailed++;
            }

        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }

    /**
     * Test: A child scope may not have the same name as its immediate parent scope.
     */
    public static void testChildScopeNameNotSameAsParent() {
        String testName = "testChildScopeNameNotSameAsParent";
        totalTests++;

        try {
            // Create syntax tree nodes
            SyntaxTreeNode root = createProgNode();
            SyntaxTreeNode main = createMainNode();

            // Function F_parentFunction
            SyntaxTreeNode functions = new SyntaxTreeNode(TokenType.FUNCTIONS);
            SyntaxTreeNode parentFuncDecl = createFunctionDeclNode("num", "F_parentFunction", new ArrayList<>());

            // Assuming the language allows nested function declarations
            // Within F_parentFunction, define another function with the same name

            SyntaxTreeNode parentFuncBody = parentFuncDecl.children.get(1); // BODY

            // Create a DECL node inside the parent function body
            SyntaxTreeNode childFunctions = new SyntaxTreeNode(TokenType.FUNCTIONS);
            SyntaxTreeNode childFuncDecl = createFunctionDeclNode("num", "F_parentFunction", new ArrayList<>());
            childFunctions.children.add(childFuncDecl);

            // Add the child function declarations to the parent function's body
            parentFuncBody.children.add(childFunctions);

            functions.children.add(parentFuncDecl);

            root.children.add(main);
            root.children.add(functions);

            // Analyze
            ScopeAnalyzer analyzer = new ScopeAnalyzer();
            analyzer.analyze(root);

            List<String> errors = analyzer.getErrors();

            if (errors.contains(
                    "Semantic Error: Function name 'parentFunction' conflicts with a variable or function name.")) {
                System.out.println("\u001B[32m[PASS]\u001B[0m " + testName);
                testsPassed++;
            } else {
                System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Expected scope name conflict error.");
                testsFailed++;
            }

        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }

    /**
     * Test: A child scope may not have the same name as its siblings under the same
     * parent scope.
     */
    public static void testSiblingScopesWithSameName() {
        String testName = "testSiblingScopesWithSameName";
        totalTests++;

        try {
            // Create syntax tree nodes
            SyntaxTreeNode root = createProgNode();
            SyntaxTreeNode main = createMainNode();

            // Two functions with the same name F_duplicateFunction
            SyntaxTreeNode functions = new SyntaxTreeNode(TokenType.FUNCTIONS);
            SyntaxTreeNode funcDecl1 = createFunctionDeclNode("num", "F_duplicateFunction", new ArrayList<>());
            SyntaxTreeNode funcDecl2 = createFunctionDeclNode("num", "F_duplicateFunction", new ArrayList<>());
            functions.children.add(funcDecl1);
            functions.children.add(funcDecl2);

            root.children.add(main);
            root.children.add(functions);

            // Analyze
            ScopeAnalyzer analyzer = new ScopeAnalyzer();
            analyzer.analyze(root);

            List<String> errors = analyzer.getErrors();

            if (errors.contains("Semantic Error: Function 'duplicateFunction' is already declared in this scope.")) {
                System.out.println("\u001B[32m[PASS]\u001B[0m " + testName);
                testsPassed++;
            } else {
                System.out
                        .println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Expected function redeclaration error.");
                testsFailed++;
            }

        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }

    /**
     * Test: A call command may refer to an immediate child scope.
     */
    public static void testCallToImmediateChildScope() {
        String testName = "testCallToImmediateChildScope";
        totalTests++;

        try {
            // Create syntax tree nodes
            SyntaxTreeNode root = createProgNode();
            SyntaxTreeNode main = createMainNode();

            // Function F_childFunction
            SyntaxTreeNode functions = new SyntaxTreeNode(TokenType.FUNCTIONS);
            SyntaxTreeNode funcDecl = createFunctionDeclNode("void", "F_childFunction", new ArrayList<>());
            functions.children.add(funcDecl);

            // main calls F_childFunction
            SyntaxTreeNode instruc = new SyntaxTreeNode(TokenType.INSTRUC);
            SyntaxTreeNode call = createCallNode("F_childFunction");
            instruc.children.add(call);
            main.children.add(instruc);

            root.children.add(main);
            root.children.add(functions);

            // Analyze
            ScopeAnalyzer analyzer = new ScopeAnalyzer();
            analyzer.analyze(root);

            List<String> errors = analyzer.getErrors();

            if (errors.isEmpty()) {
                System.out.println("\u001B[32m[PASS]\u001B[0m " + testName);
                testsPassed++;
            } else {
                System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Unexpected errors: " + errors);
                testsFailed++;
            }

        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }

    /**
     * Test: A call command may refer to its own scope (recursion).
     */
    public static void testRecursiveCallToFunction() {
        String testName = "testRecursiveCallToFunction";
        totalTests++;

        try {
            // Create syntax tree nodes
            SyntaxTreeNode root = createProgNode();
            SyntaxTreeNode main = createMainNode();

            // Function F_recursiveFunction calls itself
            SyntaxTreeNode functions = new SyntaxTreeNode(TokenType.FUNCTIONS);
            SyntaxTreeNode funcDecl = createFunctionDeclNode("num", "F_recursiveFunction", new ArrayList<>());

            // Function body calls F_recursiveFunction
            SyntaxTreeNode funcBody = funcDecl.children.get(1); // BODY
            SyntaxTreeNode instruc = new SyntaxTreeNode(TokenType.INSTRUC);
            SyntaxTreeNode call = createCallNode("F_recursiveFunction");
            instruc.children.add(call);
            funcBody.children.add(instruc);

            functions.children.add(funcDecl);
            root.children.add(main);
            root.children.add(functions);

            // Analyze
            ScopeAnalyzer analyzer = new ScopeAnalyzer();
            analyzer.analyze(root);

            List<String> errors = analyzer.getErrors();

            if (errors.isEmpty()) {
                System.out.println("\u001B[32m[PASS]\u001B[0m " + testName);
                testsPassed++;
            } else {
                System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Unexpected errors: " + errors);
                testsFailed++;
            }

        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }

    /**
     * Test: There may be no recursive call to main.
     */
    public static void testRecursiveCallToMainNotAllowed() {
        String testName = "testRecursiveCallToMainNotAllowed";
        totalTests++;

        try {
            // Create syntax tree nodes
            SyntaxTreeNode root = createProgNode();
            SyntaxTreeNode main = createMainNode();

            // main calls F_main (itself)
            SyntaxTreeNode instruc = new SyntaxTreeNode(TokenType.INSTRUC);
            SyntaxTreeNode call = createCallNode("F_main");
            instruc.children.add(call);
            main.children.add(instruc);

            root.children.add(main);

            // Analyze
            ScopeAnalyzer analyzer = new ScopeAnalyzer();
            analyzer.analyze(root);

            List<String> errors = analyzer.getErrors();

            if (errors.contains("Semantic Error: Recursive call to 'main' is not allowed.")) {
                System.out.println("\u001B[32m[PASS]\u001B[0m " + testName);
                testsPassed++;
            } else {
                System.out
                        .println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Expected recursive call to main error.");
                testsFailed++;
            }

        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }

    // Helper methods to create syntax tree nodes

    private static SyntaxTreeNode createProgNode() {
        return new SyntaxTreeNode(TokenType.PROG);
    }

    private static SyntaxTreeNode createMainNode() {
        return new SyntaxTreeNode(TokenType.MAIN);
    }

    private static SyntaxTreeNode createVarDeclNode(String type, String name) {
        SyntaxTreeNode typeNode = new SyntaxTreeNode(TokenType.valueOf(type.toUpperCase()), type);
        SyntaxTreeNode nameNode = new SyntaxTreeNode(TokenType.VNAME, name);
        SyntaxTreeNode varDecl = new SyntaxTreeNode(TokenType.GLOBVARS);
        varDecl.children.add(typeNode);
        varDecl.children.add(nameNode);
        return varDecl;
    }

    private static SyntaxTreeNode createVarDeclNodeLocal(String type, String name) {
        SyntaxTreeNode typeNode = new SyntaxTreeNode(TokenType.valueOf(type.toUpperCase()), type);
        SyntaxTreeNode nameNode = new SyntaxTreeNode(TokenType.VNAME, name);
        SyntaxTreeNode varDecl = new SyntaxTreeNode(TokenType.LOCALVARS);
        varDecl.children.add(typeNode);
        varDecl.children.add(nameNode);
        return varDecl;
    }

    private static SyntaxTreeNode createFunctionDeclNode(String returnType, String name, List<SyntaxTreeNode> params) {
        SyntaxTreeNode decl = new SyntaxTreeNode(TokenType.DECL);
        SyntaxTreeNode header = new SyntaxTreeNode(TokenType.HEADER);
        SyntaxTreeNode retTypeNode = new SyntaxTreeNode(TokenType.valueOf(returnType.toUpperCase()), returnType);
        SyntaxTreeNode fnameNode = new SyntaxTreeNode(TokenType.FNAME, name);
        header.children.add(retTypeNode);
        header.children.add(fnameNode);
        header.children.addAll(params);

        SyntaxTreeNode body = new SyntaxTreeNode(TokenType.BODY);

        decl.children.add(header);
        decl.children.add(body);
        return decl;
    }

    private static SyntaxTreeNode createAssignNode(String varName, String value) {
        SyntaxTreeNode assign = new SyntaxTreeNode(TokenType.ASSIGN);
        SyntaxTreeNode vnameNode = new SyntaxTreeNode(TokenType.VNAME, varName);
        SyntaxTreeNode valueNode = new SyntaxTreeNode(TokenType.VALUE, value);
        assign.children.add(vnameNode);
        assign.children.add(valueNode);
        return assign;
    }

    private static SyntaxTreeNode createCallNode(String funcName) {
        SyntaxTreeNode call = new SyntaxTreeNode(TokenType.CALL);
        SyntaxTreeNode fnameNode = new SyntaxTreeNode(TokenType.FNAME, funcName);
        call.children.add(fnameNode);
        return call;
    }
}
