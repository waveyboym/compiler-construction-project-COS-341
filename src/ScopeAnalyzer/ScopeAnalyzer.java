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
            "main", "begin", "end", "num", "text", "void", "if", "then", "else",
            "skip", "halt", "print", "input", "return", "not", "sqrt", "or", "and",
            "eq", "grt", "add", "sub", "mul", "div"));

    public void analyze(SyntaxTreeNode root) {
        // Initialize the global scope
        currentScope = new Scope(null, "global", 0);

        // First pass: Collect function declarations at the global scope
        collectFunctionDeclarations(root);

        // Second pass: Full scope analysis
        traverse(root);

        // After traversal, output errors or proceed
        if (!errors.isEmpty()) {
            System.err.println("Semantic Errors:");
            for (String error : errors) {
                System.err.println(error);
            }
        }
    }

    public Scope getGlobalScope() {
        return currentScope;
    }

    /**
     * Collects function declarations in the current scope.
     */
    private void collectFunctionDeclarations(SyntaxTreeNode node) {
        if (node == null) {
            return;
        }

        if (node.symbol == TokenType.DECL) {
            // Handle function declaration in the current scope
            handleFunctionDeclaration(node, true);
        } else {
            // Recurse into children nodes
            for (SyntaxTreeNode child : node.children) {
                collectFunctionDeclarations(child);
            }
        }
    }

    public List<String> getErrors() {
        return errors;
    }

    /**
     * Second pass: Traverse the syntax tree for full scope analysis.
     */
    private void traverse(SyntaxTreeNode node) {
        if (node == null) {
            return;
        }

        TokenType symbol = node.symbol;

        switch (symbol) {
            case PROG:
                handleProgram(node);
                break;
            case GLOBVARS:
                handleGlobalVariableDeclarations(node);
                break;
            case LOCALVARS:
                handleLocalVariableDeclarations(node);
                break;
            case DECL:
                handleFunctionDeclaration(node, false);
                break;
            case CALL:
                handleFunctionCall(node);
                break;
            case VNAME:
                handleVariableUsage(node);
                break;
            default:
                // Recurse into children nodes
                for (SyntaxTreeNode child : node.children) {
                    traverse(child);
                }
                break;
        }
    }

    private void handleProgram(SyntaxTreeNode node) {
        // Traverse all children of the program node
        for (SyntaxTreeNode child : node.children) {
            traverse(child);
        }
    }

    /**
     * Handles global variable declarations.
     */
    private void handleGlobalVariableDeclarations(SyntaxTreeNode node) {
        handleVariableDeclarations(node, "global");
    }

    /**
     * Handles local variable declarations (must have exactly 3 variables with 3
     * commas).
     */
    private void handleLocalVariableDeclarations(SyntaxTreeNode node) {
        if (node == null || node.children.isEmpty()) {
            return;
        }

        List<SyntaxTreeNode> declarations = node.children;
        int i = 0;
        int varCount = 0;

        while (i < declarations.size() && varCount < 3) {
            SyntaxTreeNode typeNode = null;
            SyntaxTreeNode nameNode = null;

            // Expecting a type node (NUM or VTEXT)
            SyntaxTreeNode currentNode = declarations.get(i);
            if (currentNode.symbol == TokenType.NUM || currentNode.symbol == TokenType.VTEXT) {
                typeNode = currentNode;
                i++;
            } else {
                reportError("Expected type declaration (num or text), found: " + currentNode.symbol);
                i++;
                continue;
            }

            // Expecting a variable name node (VNAME)
            if (i < declarations.size()) {
                currentNode = declarations.get(i);
                if (currentNode.symbol == TokenType.VNAME) {
                    nameNode = currentNode;
                    i++;
                } else {
                    reportError("Expected variable name after type, found: " + currentNode.symbol);
                    i++;
                    continue;
                }
            } else {
                reportError("Incomplete variable declaration, missing variable name.");
                break;
            }

            // Process the variable declaration
            processVariableDeclaration(typeNode, nameNode);

            varCount++;

            // Expecting a comma
            if (i < declarations.size()) {
                currentNode = declarations.get(i);
                if (currentNode.symbol == TokenType.COMMA) {
                    i++;
                    // Continue to next declaration
                } else {
                    reportError("Expected comma after variable declaration, found: " + currentNode.symbol);
                    // Possibly break or continue, depending on grammar
                }
            }
        }

        // Check if there are more than 3 variables
        if (varCount > 3 || varCount < 3) {
            reportError("LOCALVARS must have exactly 3 variables.");
        }
    }

    /**
     * Handles variable declarations for GLOBVARS.
     */
    private void handleVariableDeclarations(SyntaxTreeNode node, String varScope) {
        if (node == null || node.children.isEmpty()) {
            return;
        }

        for (SyntaxTreeNode child : node.children) {
            switch (child.symbol) {
                case NUM:
                case VTEXT:
                    // Process the variable declaration
                    processVariableDeclaration(node, varScope);
                    return; // Return after processing the declaration
                case GLOBVARS:
                    // Recursively handle nested GLOBVARS
                    handleVariableDeclarations(child, varScope);
                    break;
                default:
                    // Skip other tokens like COMMA, SEMICOLON, etc.
                    break;
            }
        }
    }

    /**
     * Processes a single variable declaration.
     */
    private void processVariableDeclaration(SyntaxTreeNode typeNode, SyntaxTreeNode nameNode) {
        String varType = typeNode.value; // e.g., "num", "text"
        String varName = nameNode.value; // e.g., "V_sum"

        // Remove prefix if necessary
        if (varName != null && varName.startsWith("V_")) {
            varName = varName.substring(2);
        }

        // Check for redeclaration
        if (varName != null && currentScope.containsInCurrentScope(varName)) {
            reportError("Variable '" + varName + "' is already declared in this scope.");
        } else if (varName != null && reservedKeywords.contains(varName)) {
            reportError("Variable name '" + varName + "' is a reserved keyword.");
        } else if (varName != null && currentScope.lookupFunction(varName) != null) {
            reportError("Variable name '" + varName + "' conflicts with a function name.");
        } else if (varName != null) {
            // Assign unique internal name
            String uniqueName = "v" + (++variableCounter);

            // Create symbol table entry
            SymbolTableEntry entry = new SymbolTableEntry(varName, uniqueName, varType,
                    currentScope.scopeLevel, nameNode, "variable");
            currentScope.addSymbol(entry);

            // Update the variable name in the syntax tree to the unique name
            nameNode.value = varName;
        }
    }

    /**
     * Processes variable declarations for GLOBVARS (overloaded method).
     */
    private void processVariableDeclaration(SyntaxTreeNode node, String varScope) {
        List<SyntaxTreeNode> declarations = node.children;

        SyntaxTreeNode typeNode = null; // NUM or VTEXT
        SyntaxTreeNode nameNode = null; // VNAME

        // Extract type and name nodes
        for (SyntaxTreeNode child : declarations) {
            switch (child.symbol) {
                case NUM:
                case VTEXT:
                    typeNode = child;
                    break;
                case VNAME:
                    nameNode = child;
                    break;
                case COMMA:
                    // Handle any nested declarations after a comma
                    int index = declarations.indexOf(child);
                    if (index + 1 < declarations.size()) {
                        SyntaxTreeNode nextNode = declarations.get(index + 1);
                        if (nextNode.symbol == TokenType.GLOBVARS) {
                            handleVariableDeclarations(nextNode, varScope);
                        }
                    }
                    break;
                default:
                    // Skip other tokens
                    break;
            }
        }

        if (typeNode != null && nameNode != null) {
            processVariableDeclaration(typeNode, nameNode);
        }
    }

    private void handleFunctionDeclaration(SyntaxTreeNode node, boolean firstPass) {
        if (node == null) {
            return;
        }

        // Extract HEADER and BODY
        SyntaxTreeNode headerNode = null;
        SyntaxTreeNode bodyNode = null;

        for (SyntaxTreeNode child : node.children) {
            if (child.symbol == TokenType.HEADER) {
                headerNode = child;
            } else if (child.symbol == TokenType.BODY) {
                bodyNode = child;
            }
        }

        if (headerNode == null || bodyNode == null) {
            reportError("Function declaration is missing HEADER or BODY.");
            return;
        }

        // Extract function name and type from HEADER
        String funcName = null;
        String funcType = null;
        List<SyntaxTreeNode> params = new ArrayList<>();

        for (SyntaxTreeNode child : headerNode.children) {
            if (child.symbol == TokenType.NUM || child.symbol == TokenType.TEXT || child.symbol == TokenType.FVOID) {
                funcType = child.value;
            } else if (child.symbol == TokenType.FNAME) {
                funcName = child.value;
                if (funcName != null && funcName.startsWith("F_")) {
                    funcName = funcName.substring(2);
                }
            } else if (child.symbol == TokenType.VNAME) {
                params.add(child);
            }
        }

        if (funcName == null || funcType == null) {
            reportError("Function declaration is missing name or type.");
            return;
        }

        if (firstPass) {
            // First pass: Register the function in the current scope
            if (currentScope.containsInCurrentScope(funcName)) {
                reportError("Function '" + funcName + "' is already declared in this scope.");
            } else if (reservedKeywords.contains(funcName)) {
                reportError("Function name '" + funcName + "' is a reserved keyword.");
            } else if (currentScope.lookup(funcName) != null) {
                reportError("Function name '" + funcName + "' conflicts with a variable or function name.");
            } else {
                // Assign unique internal name
                String uniqueName = "f" + (++functionCounter);

                // Create symbol table entry
                SymbolTableEntry entry = new SymbolTableEntry(funcName, uniqueName, funcType,
                        currentScope.scopeLevel, node, "function");
                currentScope.addSymbol(entry);

                // Update the function name in the syntax tree to the unique name
                for (SyntaxTreeNode child : headerNode.children) {
                    if (child.symbol == TokenType.FNAME) {
                        child.value = funcName;
                        break;
                    }
                }
            }
        } else {
            // Second pass: Process the function body

            // Retrieve the function's symbol table entry
            SymbolTableEntry entry = currentScope.lookup(funcName);
            if (entry == null) {
                reportError("Function '" + funcName + "' was not registered in the first pass.");
                return;
            }

            // Enter new function scope
            Scope functionScope = new Scope(currentScope, funcName, currentScope.scopeLevel + 1);
            currentScope = functionScope;

            // Collect function declarations in the new scope (nested functions)
            collectFunctionDeclarations(bodyNode);

            // Handle function parameters (treated as local variables)
            for (SyntaxTreeNode paramNode : params) {
                String paramName = paramNode.value;
                if (paramName != null && paramName.startsWith("V_")) {
                    paramName = paramName.substring(2);
                }

                if (currentScope.containsInCurrentScope(paramName)) {
                    reportError("Parameter '" + paramName + "' is already declared in this scope.");
                } else if (reservedKeywords.contains(paramName)) {
                    reportError("Parameter name '" + paramName + "' is a reserved keyword.");
                } else {
                    // Assign unique internal name
                    String uniqueParamName = "v" + (++variableCounter);

                    // Create symbol table entry
                    SymbolTableEntry paramEntry = new SymbolTableEntry(paramName, uniqueParamName, "param",
                            currentScope.scopeLevel, paramNode, "variable");
                    currentScope.addSymbol(paramEntry);

                    // Update the parameter name in the syntax tree to the unique name
                    paramNode.value = paramName;
                }
            }

            // Traverse the function body
            for (SyntaxTreeNode child : bodyNode.children) {
                traverse(child);
            }

            // Exit function scope
            currentScope = currentScope.parent;
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
            node.value = entry.originalName;
        }
    }

    private void handleFunctionCall(SyntaxTreeNode node) {
        // Extract function name
        String funcName = null;

        for (SyntaxTreeNode child : node.children) {
            if (child.symbol == TokenType.FNAME) {
                funcName = child.value;

                if (funcName != null && funcName.startsWith("F_")) {
                    funcName = funcName.substring(2);
                }

                SymbolTableEntry entry = currentScope.lookupFunction(funcName);

                if (entry == null) {
                    reportError("Function '" + funcName + "' is not declared.");
                } else {
                    // Replace the name with the unique internal name
                    child.value = entry.originalName;
                }

                // Check for recursive call to main
                if (funcName.equals("main")) {
                    reportError("Recursive call to 'main' is not allowed.");
                }

                break;
            }
        }

        // Traverse the arguments
        for (SyntaxTreeNode child : node.children) {
            if (child.symbol != TokenType.FNAME) {
                traverse(child);
            }
        }
    }

    private void reportError(String message) {
        errors.add("Semantic Error: " + message);
    }
}