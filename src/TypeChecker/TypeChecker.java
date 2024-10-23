package TypeChecker;

import java.util.*;
import Utils.Scope;
import Interfaces.TokenType;
import Interfaces.SyntaxTreeNode;
import Interfaces.SymbolTableEntry;

public class TypeChecker {
    private Scope currentScope;
    private SyntaxTreeNode currentFunction;
    private Map<String, Character> typeMap = new HashMap<>();
    private List<String> errors = new ArrayList<>(); // List to store error messages

    public TypeChecker(Scope globalScope) {
        this.currentScope = globalScope;
        initializeTypeMap();
    }

    /**
     * Initializes the type map with basic types.
     */
    private void initializeTypeMap() {
        typeMap.put("num", 'n');
        typeMap.put("text", 't');
        typeMap.put("void", 'v');
        typeMap.put("bool", 'b');
        typeMap.put("comparison", 'c'); // For comparison operators like 'eq', 'grt'
        typeMap.put("add", 'n');
        typeMap.put("sub", 'n');
        typeMap.put("mul", 'n');
        typeMap.put("div", 'n');
        typeMap.put("sqrt", 'n');
        typeMap.put("eq", 'c'); // Comparison operators
        typeMap.put("gt", 'c');
        typeMap.put("and", 'b');
        typeMap.put("or", 'b');
        typeMap.put("not", 'b');
    }

    public boolean typecheck(SyntaxTreeNode node) {
        if (node == null) {
            return true;
        }

        switch (node.symbol) {
            case PROG:
                return typecheckProg(node);
            case GLOBVARS:
                return typecheckGlobvars(node);
            case ALGO:
                return typecheckAlgo(node);
            case FUNCTIONS:
                return typecheckFunctions(node);
            case INSTRUC:
                return typecheckInstruc(node);
            case COMMAND:
                return typecheckCommand(node);
            case ASSIGN:
                return typecheckAssign(node);
            case BRANCH:
                return typecheckBranch(node);
            case CALL:
                return typecheckCall(node);
            case OP:
                return typecheckOp(node);
            case DECL:
                return typecheckDecl(node);
            case HEADER:
                return typecheckHeader(node);
            case BODY:
                return typecheckBody(node);
            case LOCALVARS:
                return typecheckLocvars(node);
            case TERM:
                return typecheckTerm(node);
            case ATOMIC:
                return typecheckAtomic(node);
            case COND:
                return typecheckCond(node);
            case BINOPSIMPLE:
                return typecheckBinopSimple(node);
            case BINOPCOMPOSITE:
                return typecheckBinopComposit(node);
            case UNOPSIMPLE:
                return typecheckUnopSimple(node);
            case ARG:
                return typecheckArg(node);
            case VNAME:
                return typecheckVariable(node); // Handle variable usage
            default:
                // Recursively typecheck all children
                boolean result = true;
                for (SyntaxTreeNode child : node.children) {
                    result &= typecheck(child);
                }
                return result;
        }
    }

    /**
     * Type checks the PROG node.
     */
    private boolean typecheckProg(SyntaxTreeNode node) {
        boolean globvarsCheck = true;
        boolean algoCheck = true;
        boolean functionsCheck = true;

        // Iterate over the children of PROG
        for (SyntaxTreeNode child : node.children) {
            switch (child.symbol) {
                case GLOBVARS:
                    globvarsCheck &= typecheck(child);
                    break;
                case ALGO:
                    algoCheck &= typecheck(child);
                    break;
                case FUNCTIONS:
                    functionsCheck &= typecheck(child);
                    break;
                default:
                    break;
            }
        }

        return globvarsCheck && algoCheck && functionsCheck;
    }

    private boolean typecheckGlobvars(SyntaxTreeNode node) {
        if (node == null || node.children.isEmpty()) {
            // Base case: no more variables
            return true;
        }

        boolean result = true;

        for (SyntaxTreeNode child : node.children) {
            switch (child.symbol) {
                case NUM:
                case TEXT:
                    // Process the variable declaration
                    result &= processVariableDeclaration(child, node);
                    break;
                case GLOBVARS:
                    // Recursively handle nested GLOBVARS
                    result &= typecheckGlobvars(child);
                    break;
                default:
                    // Skip other tokens like COMMA, SEMICOLON, etc.
                    break;
            }
        }

        return result;
    }

    private boolean processVariableDeclaration(SyntaxTreeNode typeNode, SyntaxTreeNode parentNode) {
        // Find the corresponding VNAME node
        int typeIndex = parentNode.children.indexOf(typeNode);
        if (typeIndex + 1 >= parentNode.children.size()) {
            reportError("Variable declaration is missing variable name.");
            return false;
        }

        SyntaxTreeNode nameNode = parentNode.children.get(typeIndex + 1);

        if (nameNode.symbol != TokenType.VNAME) {
            reportError("Expected variable name after type declaration.");
            return false;
        }

        String varType = typeNode.value; // e.g., "num", "text"
        String varName = nameNode.value; // e.g., "V_sum"

        // Remove prefix if necessary
        if (varName != null && varName.startsWith("V_")) {
            varName = varName.substring(2);
        }

        // Update the type in the symbol table
        SymbolTableEntry entry = currentScope.lookup(varName);
        if (entry != null) {
            entry.type = String.valueOf(typeMap.getOrDefault(varType, 'u'));
            nameNode.type = entry.type.charAt(0);
        } else {
            reportError("Variable '" + varName + "' is not declared in the current scope.");
            return false;
        }

        return true;
    }

    /**
     * Type checks the ALGO node (main algorithm).
     */
    private boolean typecheckAlgo(SyntaxTreeNode node) {
        // Type check the INSTRUC node inside ALGO
        return typecheck(node.children.get(1)); // INSTRUC
    }

    /**
     * Type checks function declarations.
     */
    private boolean typecheckFunctions(SyntaxTreeNode node) {
        if (node.children.isEmpty()) {
            // Base case: no more functions
            return true;
        }

        boolean declCheck = typecheck(node.children.get(0)); // Type check DECL
        boolean restCheck = true;

        // Recursively type check the rest of the functions
        if (node.children.size() > 1) {
            restCheck = typecheck(node.children.get(1));
        }

        return declCheck && restCheck;
    }

    private boolean typecheckInstruc(SyntaxTreeNode node) {
        if (node.children.isEmpty()) {
            // Base case: no more instructions
            return true;
        }

        boolean commandCheck = typecheck(node.children.get(0)); // Type check COMMAND
        boolean restCheck = true;

        // Recursively type check the rest of the instructions
        if (node.children.size() > 2) {
            restCheck = typecheck(node.children.get(2));
        }

        return commandCheck && restCheck;
    }

    private boolean typecheckCommand(SyntaxTreeNode node) {
        TokenType commandType = node.children.get(0).symbol;

        switch (commandType) {
            case SKIP:
            case HALT:
                return true; // Skip and halt are always valid
            case PRINT:
                return typecheckPrint(node.children.get(1));
            case RETURN:
                return typecheckReturn(node.children.get(1));
            case ASSIGN:
                return typecheckAssign(node.children.get(0));
            case CALL:
                return typecheckCall(node.children.get(0));
            case BRANCH:
                return typecheckBranch(node.children.get(0));
            default:
                reportError("Unknown command type: " + commandType);
                return false;
        }
    }

    private boolean typecheckPrint(SyntaxTreeNode atomic) {
        boolean result = typecheck(atomic);
        char type = typeof(atomic);
        if (result && (type == 'n' || type == 't')) {
            return true;
        } else {
            reportError("Invalid type for print command: Expected 'num' or 'text', found '" + type + "'");
            return false;
        }
    }

    private boolean typecheckBranch(SyntaxTreeNode node) {
        SyntaxTreeNode condNode = node.children.get(1); // Condition
        SyntaxTreeNode algo1Node = node.children.get(3); // Then block
        SyntaxTreeNode algo2Node = node.children.get(5); // Else block

        boolean condCheck = typecheck(condNode);
        char condType = typeof(condNode);

        if (!condCheck || condType != 'b') {
            reportError("Condition in if statement must be boolean");
            return false;
        }

        boolean thenCheck = typecheck(algo1Node);
        boolean elseCheck = typecheck(algo2Node);

        return thenCheck && elseCheck;
    }

    /**
     * Type checks function declarations (DECL).
     */
    private boolean typecheckDecl(SyntaxTreeNode node) {
        // Retrieve the function name
        SyntaxTreeNode headerNode = node.children.get(0);
        SyntaxTreeNode fnameNode = null;

        for (SyntaxTreeNode child : headerNode.children) {
            if (child.symbol == TokenType.FNAME) {
                fnameNode = child;
                break;
            }
        }

        if (fnameNode == null) {
            reportError("Function declaration is missing a function name.");
            return false;
        }

        // Enter the function's scope
        SymbolTableEntry funcEntry = currentScope.lookup(fnameNode.value);
        if (funcEntry == null) {
            reportError("Function '" + fnameNode.value + "' is not declared in the symbol table.");
            return false;
        }

        // Update currentScope to the function's scope
        currentScope = currentScope.getChildScope(fnameNode.value);
        if (currentScope == null) {
            reportError("Function scope for '" + fnameNode.value + "' not found.");
            return false;
        }

        currentFunction = node; // Set the current function context

        boolean headerCheck = typecheck(headerNode); // HEADER
        boolean bodyCheck = typecheck(node.children.get(1)); // BODY

        currentFunction = null; // Reset the function context

        // Exit the function's scope
        currentScope = currentScope.parent;

        return headerCheck && bodyCheck;
    }

    /**
     * Type checks function headers.
     */
    private boolean typecheckHeader(SyntaxTreeNode node) {
        SyntaxTreeNode ftypNode = null;
        SyntaxTreeNode fnameNode = null;
        List<SyntaxTreeNode> params = new ArrayList<>();

        // Iterate over the HEADER node's children
        List<SyntaxTreeNode> children = node.children;
        for (SyntaxTreeNode child : children) {
            switch (child.symbol) {
                case NUM:
                case TEXT:
                case FVOID:
                    ftypNode = child;
                    break;
                case FNAME:
                    fnameNode = child;
                    break;
                case VNAME:
                    params.add(child);
                    break;
                default:
                    // Skip tokens like LEFT_PAREN, COMMA, RIGHT_PAREN
                    break;
            }
        }

        if (ftypNode == null || fnameNode == null) {
            reportError("Function declaration is missing return type or name.");
            return false;
        }

        char returnType = typeof(ftypNode);

        // Update the function's type in the symbol table
        SymbolTableEntry funcEntry = currentScope.parent.lookup(fnameNode.value);
        if (funcEntry != null) {
            funcEntry.type = String.valueOf(returnType);
        }

        // All parameters are assumed to be of type 'n' (num)
        for (SyntaxTreeNode param : params) {
            // Parameters should be in the current function's scope
            SymbolTableEntry paramEntry = currentScope.lookup(param.value);
            if (paramEntry != null) {
                paramEntry.type = "n"; // Set parameter type to 'n'
                param.type = 'n'; // Update the node's type
            } else {
                reportError("Parameter '" + param.value + "' is not declared in function scope.");
                return false;
            }
        }

        return true;
    }

    /**
     * Type checks function bodies.
     */
    private boolean typecheckBody(SyntaxTreeNode node) {
        // Body consists of PROLOG, LOCVARS, ALGO, EPILOG, SUBFUNCS
        boolean prologCheck = typecheck(node.children.get(0));
        boolean locvarsCheck = typecheck(node.children.get(1));
        boolean algoCheck = typecheck(node.children.get(2));
        boolean epilogCheck = typecheck(node.children.get(3));
        boolean subfuncsCheck = typecheck(node.children.get(4));

        return prologCheck && locvarsCheck && algoCheck && epilogCheck && subfuncsCheck;
    }

    /**
     * Type checks local variable declarations.
     */
    private boolean typecheckLocvars(SyntaxTreeNode node) {
        if (node.children.isEmpty()) {
            return true;
        }

        List<SyntaxTreeNode> declarations = node.children;

        // Process the variable declarations
        for (int i = 0; i < declarations.size(); i += 3) {
            SyntaxTreeNode vtypNode = declarations.get(i); // Type node
            SyntaxTreeNode vnameNode = declarations.get(i + 1); // Variable name node

            char type = typeMap.getOrDefault(vtypNode.value, 'u');
            String varName = vnameNode.value;

            // Update the type in the symbol table
            SymbolTableEntry entry = currentScope.lookup(varName);
            if (entry != null) {
                entry.type = String.valueOf(type);
            }
        }

        return true;
    }

    /**
     * Type checks terms (TERM nodes).
     */
    private boolean typecheckTerm(SyntaxTreeNode node) {
        if (node.children.isEmpty()) {
            reportError("Empty TERM node");
            return false;
        }

        SyntaxTreeNode child = node.children.get(0); // Can be ATOMIC, CALL, or OP
        boolean result = typecheck(child);
        node.type = typeof(child);
        return result;
    }

    private boolean typecheckAtomic(SyntaxTreeNode node) {
        if (node.children.isEmpty()) {
            reportError("Empty ATOMIC node");
            return false;
        }

        SyntaxTreeNode child = node.children.get(0); // VNAME or CONST
        boolean result = typecheck(child);
        node.type = typeof(child);
        return result;
    }

    private boolean typecheckArg(SyntaxTreeNode node) {
        if (node.children.isEmpty()) {
            reportError("Empty ARG node");
            return false;
        }

        SyntaxTreeNode child = node.children.get(0); // ATOMIC, OP, or another ARG
        boolean result = typecheck(child);
        node.type = typeof(child);
        return result;
    }

    private boolean typecheckCond(SyntaxTreeNode node) {
        if (node.children.isEmpty()) {
            reportError("Empty COND node");
            return false;
        }

        SyntaxTreeNode child = node.children.get(0); // Could be BINOPSIMPLE, BINOPCOMPOSIT, etc.
        boolean result = typecheck(child);
        node.type = typeof(child);
        return result;
    }

    private boolean typecheckOp(SyntaxTreeNode node) {
        if (node.children.isEmpty()) {
            reportError("Empty OP node");
            return false;
        }

        SyntaxTreeNode operatorNode = node.children.get(0); // Operator node
        String operator = operatorNode.value.toLowerCase();

        char operatorType = typeMap.getOrDefault(operator, 'u');

        if (operatorType == 'u') {
            reportError("Unknown operator: " + operator);
            node.type = 'u';
            return false;
        }

        boolean isUnary = operator.equals("not") || operator.equals("sqrt");

        if (isUnary) {
            // Unary operator
            SyntaxTreeNode argNode = node.children.get(2); // Argument node
            boolean argCheck = typecheck(argNode);
            char argType = typeof(argNode);

            if (!argCheck) {
                node.type = 'u';
                return false;
            }

            if ((operatorType == 'n' && argType == 'n') || (operatorType == 'b' && argType == 'b')) {
                node.type = operatorType;
                return true;
            } else {
                reportError("Type mismatch in unary operation '" + operator + "'");
                node.type = 'u';
                return false;
            }
        } else {
            // Binary operator
            SyntaxTreeNode arg1Node = node.children.get(2); // First argument
            SyntaxTreeNode arg2Node = node.children.get(4); // Second argument

            boolean arg1Check = typecheck(arg1Node);
            boolean arg2Check = typecheck(arg2Node);
            char arg1Type = typeof(arg1Node);
            char arg2Type = typeof(arg2Node);

            if (!arg1Check || !arg2Check) {
                node.type = 'u';
                return false;
            }

            if (operatorType == 'n' && arg1Type == 'n' && arg2Type == 'n') {
                node.type = 'n';
                return true;
            } else if (operatorType == 'b' && arg1Type == 'b' && arg2Type == 'b') {
                node.type = 'b';
                return true;
            } else if (operatorType == 'c' && arg1Type == 'n' && arg2Type == 'n') {
                node.type = 'b'; // Comparison operators return boolean
                return true;
            } else {
                reportError("Type mismatch in binary operation '" + operator + "'");
                node.type = 'u';
                return false;
            }
        }
    }

    /**
     * Type checks a BINOPSIMPLE node.
     */
    private boolean typecheckBinopSimple(SyntaxTreeNode node) {
        if (node.children.isEmpty()) {
            reportError("Empty BINOPSIMPLE node");
            return false;
        }

        SyntaxTreeNode child = node.children.get(0); // Could be ATOMIC or another expression
        boolean result = typecheck(child);
        node.type = typeof(child);
        return result;
    }

    /**
     * Type checks a BINOPCOMPOSIT node.
     */
    private boolean typecheckBinopComposit(SyntaxTreeNode node) {
        if (node.children.size() < 6) {
            reportError("Invalid BINOPCOMPOSIT node structure");
            return false;
        }

        SyntaxTreeNode operatorNode = node.children.get(0); // Operator node (ADD, SUB, MUL, DIV, EQ, GRT, etc.)
        String operator = operatorNode.symbol.name().toLowerCase();

        char operatorType = typeMap.getOrDefault(operator, 'u');

        if (operatorType == 'u') {
            reportError("Unknown operator: " + operator);
            node.type = 'u';
            return false;
        }

        boolean isUnary = operator.equals("not") || operator.equals("sqrt");

        if (isUnary) {
            // Unary operator
            SyntaxTreeNode argNode = node.children.get(2); // Argument node
            boolean argCheck = typecheck(argNode);
            char argType = typeof(argNode);

            if ((operatorType == 'n' && argType == 'n') || (operatorType == 'b' && argType == 'b')) {
                node.type = operatorType;
                return argCheck;
            } else {
                reportError("Type mismatch in unary operation");
                node.type = 'u';
                return false;
            }
        } else {
            // Binary operator
            SyntaxTreeNode arg1Node = node.children.get(2); // First argument
            SyntaxTreeNode arg2Node = node.children.get(4); // Second argument

            boolean arg1Check = typecheck(arg1Node);
            boolean arg2Check = typecheck(arg2Node);
            char arg1Type = typeof(arg1Node);
            char arg2Type = typeof(arg2Node);

            if (operatorType == 'n' && arg1Type == 'n' && arg2Type == 'n') {
                node.type = 'n';
                return arg1Check && arg2Check;
            } else if (operatorType == 'b' && arg1Type == 'b' && arg2Type == 'b') {
                node.type = 'b';
                return arg1Check && arg2Check;
            } else if (operatorType == 'c' && arg1Type == 'n' && arg2Type == 'n') {
                node.type = 'b'; // Comparison operators return boolean
                return arg1Check && arg2Check;
            } else {
                reportError("Type mismatch in binary operation");
                node.type = 'u';
                return false;
            }
        }
    }

    /**
     * Type checks a UNOPSIMPLE node.
     */
    private boolean typecheckUnopSimple(SyntaxTreeNode node) {
        if (node.children.size() < 4) {
            reportError("Invalid UNOPSIMPLE node structure");
            return false;
        }

        SyntaxTreeNode operatorNode = node.children.get(0); // Operator node (NOT, SQRT, etc.)
        String operator = operatorNode.symbol.name().toLowerCase();

        char operatorType = typeMap.getOrDefault(operator, 'u');

        if (operatorType == 'u') {
            reportError("Unknown operator: " + operator);
            node.type = 'u';
            return false;
        }

        SyntaxTreeNode argNode = node.children.get(2); // Argument node
        boolean argCheck = typecheck(argNode);
        char argType = typeof(argNode);

        if ((operatorType == 'n' && argType == 'n') || (operatorType == 'b' && argType == 'b')) {
            node.type = operatorType;
            return argCheck;
        } else {
            reportError("Type mismatch in unary operation");
            node.type = 'u';
            return false;
        }
    }

    private boolean typecheckAssign(SyntaxTreeNode node) {
        SyntaxTreeNode vnameNode = node.children.get(0); // Variable name
        SyntaxTreeNode rhsNode = node.children.get(2); // Right-hand side

        boolean vnameCheck = typecheck(vnameNode);
        char vnameType = typeof(vnameNode);

        if (rhsNode.symbol == TokenType.INPUT) {
            // VNAME < input
            if (!vnameCheck) {
                // Error already reported in typecheck(vnameNode)
                return false;
            }
            if (vnameType == 'n') {
                return true;
            } else {
                reportError("Input can only be assigned to variables of type 'num'");
                return false;
            }
        } else {
            // VNAME = TERM
            boolean termCheck = typecheck(rhsNode);
            char termType = typeof(rhsNode);

            if (!vnameCheck || !termCheck) {
                // Errors already reported in typecheck(vnameNode) or typecheck(rhsNode)
                return false;
            }

            if (vnameType != termType) {
                reportError("Type mismatch in assignment to variable '" + vnameNode.value + "'");
                return false;
            }
            return true;
        }
    }

    private boolean typecheckCall(SyntaxTreeNode node) {
        SyntaxTreeNode fnameNode = null;
        List<SyntaxTreeNode> args = new ArrayList<>();

        // Iterate over the CALL node's children
        List<SyntaxTreeNode> children = node.children;
        for (SyntaxTreeNode child : children) {
            switch (child.symbol) {
                case FNAME:
                    fnameNode = child;
                    break;
                case VNAME:
                case ATOMIC:
                case TERM:
                    args.add(child);
                    break;
                default:
                    // Skip tokens like LEFT_PAREN, COMMA, RIGHT_PAREN
                    break;
            }
        }

        if (fnameNode == null) {
            reportError("Function call is missing function name.");
            node.type = 'u';
            return false;
        }

        // Look up the function in the symbol table
        SymbolTableEntry funcEntry = currentScope.lookupFunction(fnameNode.value);
        if (funcEntry == null) {
            reportError("Function '" + fnameNode.value + "' is not declared.");
            node.type = 'u';
            return false;
        }

        char functionType = funcEntry.type.charAt(0);

        // Type-check each argument
        for (SyntaxTreeNode arg : args) {
            boolean argCheck = typecheck(arg);
            if (!argCheck || typeof(arg) != 'n') {
                reportError("Function arguments must be of type 'num'.");
                node.type = 'u';
                return false;
            }
        }

        node.type = functionType;
        return true;
    }

    private boolean typecheckReturn(SyntaxTreeNode atomic) {
        if (currentFunction == null) {
            reportError("Return statement outside of a function");
            return false;
        }

        SyntaxTreeNode headerNode = currentFunction.children.get(0);
        SyntaxTreeNode ftypNode = null;

        // Find the function return type in the HEADER node
        for (SyntaxTreeNode child : headerNode.children) {
            if (child.symbol == TokenType.NUM || child.symbol == TokenType.TEXT || child.symbol == TokenType.FVOID) {
                ftypNode = child;
                break;
            }
        }

        if (ftypNode == null) {
            reportError("Function declaration is missing return type.");
            return false;
        }

        boolean atomicCheck = typecheck(atomic);

        char functionReturnType = typeof(ftypNode);
        char returnType = typeof(atomic);

        if (!atomicCheck) {
            return false;
        }

        if (functionReturnType == 'n' && returnType == 'n') {
            return true;
        } else {
            reportError("Return type mismatch: Expected '" + functionReturnType + "', found '" + returnType + "'");
            return false;
        }
    }

    /**
     * Type checks variable usage.
     */
    private boolean typecheckVariable(SyntaxTreeNode node) {
        // Look up the variable in the symbol table starting from the current scope
        SymbolTableEntry entry = currentScope.lookup(node.value);
        if (entry == null) {
            reportError("Variable '" + node.value + "' is not declared in the current scope or any parent scope.");
            node.type = 'u';
            return false;
        } else {
            node.type = entry.type.charAt(0);
            return true;
        }
    }

    private char typeof(SyntaxTreeNode node) {
        if (node == null) {
            return 'u';
        }

        if (node.type != '\0' && node.type != 'u') {
            // Return cached type if already computed
            return node.type;
        }

        switch (node.symbol) {
            case VNAME:
                SymbolTableEntry entry = currentScope.lookup(node.value);
                if (entry != null) {
                    node.type = entry.type.charAt(0);
                    return node.type;
                } else {
                    reportError("Variable '" + node.value + "' is not declared");
                    node.type = 'u';
                    return 'u';
                }
            case TEXTLIT:
                node.type = 't';
                return node.type;
            case NUMLIT:
                node.type = 'n';
                return node.type;
            case NUM:
            case TEXT:
            case FVOID:
                node.type = typeMap.getOrDefault(node.value, 'u');
                return node.type;
            default:
                if (isOperatorNode(node)) {
                    String operator = node.symbol.name().toLowerCase();
                    char opType = typeMap.getOrDefault(operator, 'u');
                    if (opType == 'c') {
                        return 'c'; // Comparison operator
                    } else {
                        node.type = opType;
                        return node.type;
                    }
                } else {
                    // For other nodes, the type should have been set during type checking
                    return node.type;
                }
        }
    }

    /**
     * Helper method to check if a node is an operator node.
     */
    private boolean isOperatorNode(SyntaxTreeNode node) {
        return typeMap.containsKey(node.symbol.name().toLowerCase());
    }

    private void reportError(String message) {
        errors.add("Type Error: " + message);
    }

    public List<String> getErrors() {
        return errors;
    }
}
