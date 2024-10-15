package ScopeAnalyzer;

import java.util.*;
import Utils.Scope;
import Interfaces.TokenType;
import Interfaces.SyntaxTreeNode;
import Interfaces.SymbolTableEntry;

public class ScopeAnalyzer {
    private Scope currentScope;
    private int variableCounter = 0;
    private int functionCounter = 0;
    private List<String> errors = new ArrayList<>();
    private Set<String> reservedKeywords = new HashSet<>(Arrays.asList(
            // Add all reserved keywords from your language specification
            "main", "begin", "end", "num", "text", "void", "if", "then", "else",
            "skip", "halt", "print", "input", "return", "not", "sqrt", "or", "and",
            "eq", "grt", "add", "sub", "mul", "div", "VNAME", "FNAME"));

    public void analyze(SyntaxTreeNode root) {
        // Initialize the global scope
        currentScope = new Scope(null, "global", 0);
        traverse(root);

        // After traversal, you can output errors or proceed
        if (!errors.isEmpty()) {
            System.err.println("Semantic Errors:");

            for (String error : errors) {
                System.err.println(error);
            }
        } else {
            System.out.println("Scope analysis completed successfully.");
        }
    }

    private void traverse(SyntaxTreeNode node) {
        TokenType symbol = node.symbol;

        switch (symbol) {
            case TokenType.PROG:
                handleProgram(node);
                break;
            case TokenType.GLOBVARS:
                handleVariableDeclarations(node, "global");
                break;
            case TokenType.FUNCTIONS:
                handleFunctions(node);
                break;
            case TokenType.DECL:
                handleFunctionDeclaration(node);
                break;
            case TokenType.VNAME:
                handleVariableUsage(node);
                break;
            case TokenType.FNAME:
                handleFunctionUsage(node);
                break;
            default:
                // Traverse children
                for (SyntaxTreeNode child : node.children) {
                    traverse(child);
                }
                break;
        }
    }

    private void handleProgram(SyntaxTreeNode node) {
        // Traverse children nodes
        for (SyntaxTreeNode child : node.children) {
            traverse(child);
        }
    }

    private void handleVariableDeclarations(SyntaxTreeNode node, String varScope) {
        List<SyntaxTreeNode> declarations = node.children;

        // For GLOBVARS, we have pairs of <NUM>/<TEXT> and <VNAME>
        for (int i = 0; i < declarations.size(); i += 2) {
            SyntaxTreeNode typeNode = declarations.get(i);
            SyntaxTreeNode nameNode = declarations.get(i + 1);

            String varType = typeNode.value; // e.g., "num", "text"
            String varName = nameNode.value; // e.g., "V_sum"

            // Remove prefix if necessary
            if (varName.startsWith("V_")) {
                varName = varName.substring(2);
            }

            // Check for redeclaration in current scope
            if (currentScope.containsInCurrentScope(varName)) {
                reportError("Variable '" + varName + "' is already declared in this scope.");
                continue;
            } else if (reservedKeywords.contains(varName)) {
                reportError("Variable name '" + varName + "' is a reserved keyword.");
                continue;
            }

            // Assign unique internal name
            String uniqueName = "v" + (++variableCounter);

            // Create symbol table entry
            SymbolTableEntry entry = new SymbolTableEntry(varName, uniqueName, varType, currentScope.scopeLevel,
                    nameNode, "variable");
            currentScope.addSymbol(entry);

            // Update the variable name in the syntax tree to the unique name
            nameNode.value = uniqueName;
        }
    }

    private void handleFunctions(SyntaxTreeNode node) {
        // Implement function handling if necessary
        // For now, traverse children
        for (SyntaxTreeNode child : node.children) {
            traverse(child);
        }
    }

    private void handleFunctionDeclaration(SyntaxTreeNode node) {
        // Implement function declaration handling based on your syntax tree
        // For now, traverse children
        for (SyntaxTreeNode child : node.children) {
            traverse(child);
        }
    }

    private void handleVariableUsage(SyntaxTreeNode node) {
        String varName = node.value;

        if (varName.startsWith("V_")) {
            varName = varName.substring(2);
        }

        SymbolTableEntry entry = currentScope.lookup(varName);

        if (entry == null) {
            reportError("Variable '" + varName + "' is not declared.");
        } else {
            // Replace the name with the unique internal name
            node.value = entry.uniqueName;
        }
    }

    private void handleFunctionUsage(SyntaxTreeNode node) {
        String funcName = node.value;

        if (funcName.startsWith("F_")) {
            funcName = funcName.substring(2);
        }

        SymbolTableEntry entry = currentScope.lookup(funcName);

        if (entry == null) {
            reportError("Function '" + funcName + "' is not declared.");
        } else {
            // Replace the name with the unique internal name
            node.value = entry.uniqueName;
        }
    }

    private void reportError(String message) {
        errors.add("Semantic Error: " + message);
    }
}
