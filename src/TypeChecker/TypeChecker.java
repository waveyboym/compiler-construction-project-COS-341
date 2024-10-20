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
    }

    /**
     * Starts the type checking process from the root node.
     *
     * @param node The root of the syntax tree.
     * @return True if type checking succeeds, false otherwise.
     */
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
            case SIMPLE:
                return typecheckSimple(node);
            case COMPOSIT:
                return typecheckComposit(node);
            case ARG:
                return typecheckArg(node);
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
        boolean globvarsCheck = false;
        boolean algoCheck = false;
        boolean functionsCheck = false;

        // Iterate over the children of PROG
        for (SyntaxTreeNode child : node.children) {
            switch (child.symbol) {
                case GLOBVARS:
                    globvarsCheck = typecheck(child);
                    break;
                case ALGO:
                    algoCheck = typecheck(child);
                    break;
                case FUNCTIONS:
                    functionsCheck = typecheck(child);
                    break;
                default:
                    break;
            }
        }

        return globvarsCheck && algoCheck && functionsCheck;
    }

    /**
     * Type checks global variable declarations.
     */
    private boolean typecheckGlobvars(SyntaxTreeNode node) {
        if (node.children.isEmpty()) {
            // Base case: no more variables
            return true;
        }

        SyntaxTreeNode vtypNode = node.children.get(0); // Type node (num/text)
        SyntaxTreeNode vnameNode = node.children.get(1); // Variable name node

        char type = typeMap.getOrDefault(vtypNode.value, 'u');
        String varName = vnameNode.value;

        // Update the type in the symbol table
        SymbolTableEntry entry = currentScope.lookup(varName);
        if (entry != null) {
            entry.type = String.valueOf(type);
        }

        // Recursively type check the rest of the global variables
        boolean restCheck = true;
        if (node.children.size() > 3) {
            restCheck = typecheck(node.children.get(3));
        }

        return restCheck;
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

    /**
     * Type checks instruction sequences.
     */
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

    /**
     * Type checks individual commands.
     */
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
                return typecheck(node.children.get(0)); // Delegate to typecheckAssign
            case CALL:
                return typecheck(node.children.get(0)); // Delegate to typecheckCall
            case BRANCH:
                return typecheck(node.children.get(0)); // Delegate to typecheckBranch
            default:
                reportError("Unknown command type: " + commandType);
                return false;
        }
    }

    /**
     * Type checks the PRINT command.
     */
    private boolean typecheckPrint(SyntaxTreeNode atomic) {
        char type = typeof(atomic);
        if (type == 'n' || type == 't') {
            return true;
        } else {
            reportError("Invalid type for print command: Expected 'num' or 'text', found '" + type + "'");
            return false;
        }
    }

    /**
     * Type checks branch (if-then-else) commands.
     */
    private boolean typecheckBranch(SyntaxTreeNode node) {
        SyntaxTreeNode condNode = node.children.get(1); // Condition
        SyntaxTreeNode algo1Node = node.children.get(3); // Then block
        SyntaxTreeNode algo2Node = node.children.get(5); // Else block

        boolean condCheck = typecheck(condNode);
        char condType = typeof(condNode);

        if (condType != 'b') {
            reportError("Condition in if statement must be boolean");
            return false;
        }

        boolean thenCheck = typecheck(algo1Node);
        boolean elseCheck = typecheck(algo2Node);

        return condCheck && thenCheck && elseCheck;
    }

    /**
     * Type checks function declarations (DECL).
     */
    private boolean typecheckDecl(SyntaxTreeNode node) {
        currentFunction = node; // Set the current function context

        boolean headerCheck = typecheck(node.children.get(0)); // HEADER
        boolean bodyCheck = typecheck(node.children.get(1)); // BODY

        currentFunction = null; // Reset the function context
        return headerCheck && bodyCheck;
    }

    /**
     * Type checks function headers.
     */
    private boolean typecheckHeader(SyntaxTreeNode node) {
        SyntaxTreeNode ftypNode = node.children.get(0); // Function return type
        SyntaxTreeNode fnameNode = node.children.get(1); // Function name
        List<SyntaxTreeNode> params = node.children.subList(3, node.children.size() - 1); // Parameters

        char returnType = typeof(ftypNode);
        SymbolTableEntry funcEntry = currentScope.lookup(fnameNode.value);
        if (funcEntry != null) {
            funcEntry.type = String.valueOf(returnType);
        }

        // Check that all parameters are of type 'num'
        for (SyntaxTreeNode param : params) {
            char paramType = typeof(param);
            if (paramType != 'n') {
                reportError("Function parameters must be of type 'num'");
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
        for (int i = 0; i < node.children.size(); i += 3) {
            SyntaxTreeNode vtypNode = node.children.get(i); // Type node
            SyntaxTreeNode vnameNode = node.children.get(i + 1); // Variable name node

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

    /**
     * Type checks atomic expressions.
     */
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

    /**
     * Type checks arguments (ARG nodes).
     */
    private boolean typecheckArg(SyntaxTreeNode node) {
        if (node.children.isEmpty()) {
            reportError("Empty ARG node");
            return false;
        }

        SyntaxTreeNode child = node.children.get(0); // ATOMIC or OP
        boolean result = typecheck(child);
        node.type = typeof(child);
        return result;
    }

    /**
     * Type checks conditions (COND nodes).
     */
    private boolean typecheckCond(SyntaxTreeNode node) {
        if (node.children.isEmpty()) {
            reportError("Empty COND node");
            return false;
        }

        SyntaxTreeNode child = node.children.get(0); // SIMPLE or COMPOSIT
        boolean result = typecheck(child);
        node.type = typeof(child);
        return result;
    }

    /**
     * Type checks simple conditions.
     */
    private boolean typecheckSimple(SyntaxTreeNode node) {
        if (node.children.size() != 6) {
            reportError("Invalid SIMPLE node structure");
            return false;
        }

        SyntaxTreeNode binopNode = node.children.get(0);
        SyntaxTreeNode arg1Node = node.children.get(2);
        SyntaxTreeNode arg2Node = node.children.get(4);

        boolean binopCheck = typecheck(binopNode);
        boolean arg1Check = typecheck(arg1Node);
        boolean arg2Check = typecheck(arg2Node);

        char binopType = typeof(binopNode);
        char arg1Type = typeof(arg1Node);
        char arg2Type = typeof(arg2Node);

        if (binopType == 'b' && arg1Type == 'b' && arg2Type == 'b') {
            node.type = 'b';
            return binopCheck && arg1Check && arg2Check;
        } else if (binopType == 'c' && arg1Type == 'n' && arg2Type == 'n') {
            node.type = 'b';
            return binopCheck && arg1Check && arg2Check;
        } else {
            reportError("Type mismatch in SIMPLE condition");
            node.type = 'u';
            return false;
        }
    }

    /**
     * Type checks composite conditions.
     */
    private boolean typecheckComposit(SyntaxTreeNode node) {
        if (node.children.isEmpty()) {
            reportError("Empty COMPOSIT node");
            return false;
        }

        SyntaxTreeNode firstChild = node.children.get(0);

        if (firstChild.symbol == TokenType.BINOP) {
            // BINOP(SIMPLE1, SIMPLE2)
            if (node.children.size() != 6) {
                reportError("Invalid COMPOSIT node structure with BINOP");
                return false;
            }

            SyntaxTreeNode binopNode = firstChild;
            SyntaxTreeNode simple1Node = node.children.get(2);
            SyntaxTreeNode simple2Node = node.children.get(4);

            boolean binopCheck = typecheck(binopNode);
            boolean simple1Check = typecheck(simple1Node);
            boolean simple2Check = typecheck(simple2Node);

            char binopType = typeof(binopNode);
            char simple1Type = typeof(simple1Node);
            char simple2Type = typeof(simple2Node);

            if (binopType == 'b' && simple1Type == 'b' && simple2Type == 'b') {
                node.type = 'b';
                return binopCheck && simple1Check && simple2Check;
            } else {
                reportError("Type mismatch in COMPOSIT condition with BINOP");
                node.type = 'u';
                return false;
            }
        } else if (firstChild.symbol == TokenType.UNOP) {
            // UNOP(SIMPLE)
            if (node.children.size() != 4) {
                reportError("Invalid COMPOSIT node structure with UNOP");
                return false;
            }

            SyntaxTreeNode unopNode = firstChild;
            SyntaxTreeNode simpleNode = node.children.get(2);

            boolean unopCheck = typecheck(unopNode);
            boolean simpleCheck = typecheck(simpleNode);

            char unopType = typeof(unopNode);
            char simpleType = typeof(simpleNode);

            if (unopType == 'b' && simpleType == 'b') {
                node.type = 'b';
                return unopCheck && simpleCheck;
            } else {
                reportError("Type mismatch in COMPOSIT condition with UNOP");
                node.type = 'u';
                return false;
            }
        } else {
            reportError("Invalid COMPOSIT node");
            return false;
        }
    }

    private boolean typecheckOp(SyntaxTreeNode node) {
        if (node.children.isEmpty()) {
            reportError("Empty OP node");
            return false;
        }

        SyntaxTreeNode opNode = node.children.get(0);

        if (opNode.symbol == TokenType.UNOP) {
            // UNOP(ARG)
            if (node.children.size() != 4) {
                reportError("Invalid OP node structure with UNOP");
                return false;
            }

            SyntaxTreeNode argNode = node.children.get(2); // ARG
            boolean unopCheck = typecheck(opNode);
            boolean argCheck = typecheck(argNode);

            char unopType = typeof(opNode);
            char argType = typeof(argNode);

            if (unopType == argType && (unopType == 'b' || unopType == 'n')) {
                node.type = unopType;
                return unopCheck && argCheck;
            } else {
                reportError("Type mismatch in unary operation");
                node.type = 'u';
                return false;
            }
        } else if (opNode.symbol == TokenType.BINOP) {
            // BINOP(ARG1, ARG2)
            if (node.children.size() != 6) {
                reportError("Invalid OP node structure with BINOP");
                return false;
            }

            SyntaxTreeNode arg1Node = node.children.get(2); // ARG1
            SyntaxTreeNode arg2Node = node.children.get(4); // ARG2

            boolean binopCheck = typecheck(opNode);
            boolean arg1Check = typecheck(arg1Node);
            boolean arg2Check = typecheck(arg2Node);

            char binopType = typeof(opNode);
            char arg1Type = typeof(arg1Node);
            char arg2Type = typeof(arg2Node);

            if (binopType == 'b' && arg1Type == 'b' && arg2Type == 'b') {
                node.type = 'b';
                return binopCheck && arg1Check && arg2Check;
            } else if (binopType == 'n' && arg1Type == 'n' && arg2Type == 'n') {
                node.type = 'n';
                return binopCheck && arg1Check && arg2Check;
            } else if (binopType == 'c' && arg1Type == 'n' && arg2Type == 'n') {
                node.type = 'b'; // Comparison operators return boolean
                return binopCheck && arg1Check && arg2Check;
            } else {
                reportError("Type mismatch in binary operation");
                node.type = 'u';
                return false;
            }
        } else {
            reportError("Invalid OP node");
            return false;
        }
    }

    private boolean typecheckAssign(SyntaxTreeNode node) {
        SyntaxTreeNode vnameNode = node.children.get(0); // Variable name
        SyntaxTreeNode rhsNode = node.children.get(2); // Right-hand side

        char vnameType = typeof(vnameNode);

        if (rhsNode.symbol == TokenType.INPUT) {
            // VNAME < input
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
            if (!termCheck || vnameType != termType) {
                reportError("Type mismatch in assignment to variable '" + vnameNode.value + "'");
                return false;
            }
            return true;
        }
    }

    private boolean typecheckCall(SyntaxTreeNode node) {
        SyntaxTreeNode fnameNode = node.children.get(0); // Function name
        SymbolTableEntry funcEntry = currentScope.lookup(fnameNode.value);

        if (funcEntry == null || (!funcEntry.kind.equals("function") && !funcEntry.kind.equals("builtin"))) {
            reportError("Function '" + fnameNode.value + "' is not declared");
            node.type = 'u';
            return false;
        }

        char functionType = funcEntry.type.charAt(0);

        // Extract arguments (skip commas)
        List<SyntaxTreeNode> args = new ArrayList<>();
        for (int i = 2; i < node.children.size() - 1; i += 2) {
            args.add(node.children.get(i));
        }

        if (args.size() != 3) {
            reportError("Function '" + fnameNode.value + "' requires 3 arguments");
            node.type = 'u';
            return false;
        }

        // Check that all arguments are of type 'num'
        for (SyntaxTreeNode arg : args) {
            boolean argCheck = typecheck(arg);
            if (!argCheck || typeof(arg) != 'n') {
                reportError("Function arguments must be of type 'num'");
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
        SyntaxTreeNode ftypNode = headerNode.children.get(0);

        boolean ftypCheck = typecheck(ftypNode);
        boolean atomicCheck = typecheck(atomic);

        char functionReturnType = typeof(ftypNode);
        char returnType = typeof(atomic);

        if (functionReturnType == 'n' && returnType == 'n') {
            return ftypCheck && atomicCheck;
        } else {
            reportError("Return type mismatch: Expected '" + functionReturnType + "', found '" + returnType + "'");
            return false;
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
            case FTYP:
                node.type = typeMap.getOrDefault(node.value, 'u');
                return node.type;
            case UNOP:
                if (node.value.equals("not")) {
                    node.type = 'b';
                } else if (node.value.equals("sqrt")) {
                    node.type = 'n';
                } else {
                    reportError("Unknown unary operator: " + node.value);
                    node.type = 'u';
                }
                return node.type;
            case BINOP:
                switch (node.value) {
                    case "or":
                    case "and":
                        node.type = 'b';
                        break;
                    case "eq":
                    case "grt":
                        node.type = 'c'; // Comparison operators
                        break;
                    case "add":
                    case "sub":
                    case "mul":
                    case "div":
                        node.type = 'n';
                        break;
                    default:
                        reportError("Unknown binary operator: " + node.value);
                        node.type = 'u';
                        break;
                }
                return node.type;
            case OP:
            case CALL:
            case ATOMIC:
            case TERM:
            case ARG:
            case SIMPLE:
            case COMPOSIT:
                // Type is set during type checking
                return node.type;
            default:
                reportError("Unknown node type: " + node.symbol);
                node.type = 'u';
                return 'u';
        }
    }

    private void reportError(String message) {
        errors.add("Type Error: " + message);
    }

    public List<String> getErrors() {
        return errors;
    }
}
