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

        // First pass: Collect function declarations
        collectFunctionDeclarations(root);

        // Second pass: Full scope analysis
        traverse(root);

        // After traversal, output errors or proceed
        if (!errors.isEmpty()) {
            System.err.println("Semantic Errors:");
            for (String error : errors) {
                System.err.println(error);
            }
        } else {
            System.out.println("Scope analysis completed successfully.");
        }
    }

    private void collectFunctionDeclarations(SyntaxTreeNode node) {
        if (node.symbol == TokenType.FUNCTIONS) {
            for (SyntaxTreeNode child : node.children) {
                if (child.symbol == TokenType.DECL) {
                    handleFunctionDeclaration(child, true);
                }
            }
        } else {
            for (SyntaxTreeNode child : node.children) {
                collectFunctionDeclarations(child);
            }
        }
    }

    private void traverse(SyntaxTreeNode node) {
        TokenType symbol = node.symbol;

        switch (symbol) {
            case PROG:
                handleProgram(node);
                break;
            case GLOBVARS:
            case LOCALVARS:
                handleVariableDeclarations(node, symbol == TokenType.GLOBVARS ? "global" : "local");
                break;
            case FUNCTIONS:
                handleFunctions(node);
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
            case FNAME:
                // This might be a function usage outside of CALL
                break;
            default:
                for (SyntaxTreeNode child : node.children) {
                    traverse(child);
                }
                break;
        }
    }

    private void handleProgram(SyntaxTreeNode node) {
        for (SyntaxTreeNode child : node.children) {
            traverse(child);
        }
    }

    private void handleVariableDeclarations(SyntaxTreeNode node, String varScope) {
        if (node == null) {
            return;
        }

        if (node.symbol == TokenType.GLOBVARS || node.symbol == TokenType.LOCALVARS) {
            List<SyntaxTreeNode> declarations = node.children;

            if (declarations.isEmpty()) {
                return;
            }

            // Process the first variable declaration
            SyntaxTreeNode typeNode = declarations.get(0); // NUM or TEXT
            SyntaxTreeNode nameNode = declarations.get(1); // VNAME

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
                SymbolTableEntry entry = new SymbolTableEntry(varName, uniqueName, varType, currentScope.scopeLevel,
                        nameNode, "variable");
                currentScope.addSymbol(entry);

                // Update the variable name in the syntax tree to the unique name
                nameNode.value = uniqueName;
            }

            // Now, process the rest of the variables if any
            if (declarations.size() > 3) {
                // Assuming the COMMA is at index 2, and GLOBVARS at index 3
                SyntaxTreeNode restNode = declarations.get(3); // GLOBVARS
                handleVariableDeclarations(restNode, varScope);
            }
        }
    }

    private void handleFunctions(SyntaxTreeNode node) {
        for (SyntaxTreeNode child : node.children) {
            if (child.symbol == TokenType.DECL) {
                handleFunctionDeclaration(child, false);
            } else {
                traverse(child);
            }
        }
    }

    private void handleFunctionDeclaration(SyntaxTreeNode node, boolean firstPass) {
        // Extract HEADER and BODY
        SyntaxTreeNode bodyNode = null;
        SyntaxTreeNode headerNode = null;

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
            if (child.symbol == TokenType.NUM || child.symbol == TokenType.TEXT || child.symbol == TokenType.VOID) {
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

        // Check for redeclaration in current scope
        if (currentScope.containsInCurrentScope(funcName)) {
            reportError("Function '" + funcName + "' is already declared in this scope.");
        } else if (reservedKeywords.contains(funcName)) {
            reportError("Function name '" + funcName + "' is a reserved keyword.");
        } else if (currentScope.lookup(funcName) != null) {
            reportError("Function name '" + funcName + "' conflicts with a variable name.");
        } else if (funcName.equals(currentScope.scopeName)) {
            reportError("Function name '" + funcName + "' is the same as its parent scope name.");
        } else if (currentScope.hasSiblingScope(funcName)) {
            reportError("Function name '" + funcName + "' is the same as one of its sibling scopes.");
        } else {
            // Assign unique internal name
            String uniqueName = "f" + (++functionCounter);

            // Create symbol table entry
            SymbolTableEntry entry = new SymbolTableEntry(funcName, uniqueName, funcType, currentScope.scopeLevel, node,
                    "function");
            currentScope.addSymbol(entry);

            // Update the function name in the syntax tree to the unique name
            for (SyntaxTreeNode child : headerNode.children) {
                if (child.symbol == TokenType.FNAME) {
                    child.value = uniqueName;
                    break;
                }
            }

            if (!firstPass) {
                // Enter new function scope
                Scope functionScope = new Scope(currentScope, funcName, currentScope.scopeLevel + 1);
                currentScope = functionScope;

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
                        paramNode.value = uniqueParamName;
                    }
                }

                // Handle local variables in LOCVARS
                for (SyntaxTreeNode child : bodyNode.children) {
                    if (child.symbol == TokenType.LOCALVARS) {
                        handleVariableDeclarations(child, "local");
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
                    child.value = entry.uniqueName;
                }

                // Check for recursive call to main
                if (funcName.equals("main")) {
                    reportError("Recursive call to main is not allowed.");
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