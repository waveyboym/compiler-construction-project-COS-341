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

    public TypeChecker(Scope globalScope) {
        this.currentScope = globalScope;
        initializeTypeMap();
    }

    private void initializeTypeMap() {
        typeMap.put("num", 'n');
        typeMap.put("text", 't');
        typeMap.put("void", 'v');
        typeMap.put("bool", 'b');
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
            default:
                return true;
        }
    }

    private boolean typecheckProg(SyntaxTreeNode node) {
        boolean globvarsCheck = false;
        boolean algoCheck = false;
        boolean functionsCheck = false;

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

    private boolean typecheckGlobvars(SyntaxTreeNode node) {
        if (node.children.isEmpty()) {
            return true;
        }

        SyntaxTreeNode vtypNode = node.children.get(0);
        SyntaxTreeNode vnameNode = node.children.get(1);

        char type = typeMap.get(vtypNode.value);
        String varName = vnameNode.value;

        SymbolTableEntry entry = currentScope.lookup(varName);
        if (entry != null) {
            entry.type = String.valueOf(type);
        }

        boolean restCheck = true;
        if (node.children.size() > 3) {
            restCheck = typecheck(node.children.get(3));
        }

        return restCheck;
    }

    private boolean typecheckAlgo(SyntaxTreeNode node) {
        return typecheck(node.children.get(1)); // INSTRUC
    }

    private boolean typecheckFunctions(SyntaxTreeNode node) {
        if (node.children.isEmpty()) {
            return true;
        }

        boolean declCheck = typecheck(node.children.get(0));
        boolean restCheck = true;

        if (node.children.size() > 1) {
            restCheck = typecheck(node.children.get(1));
        }

        return declCheck && restCheck;
    }

    private boolean typecheckInstruc(SyntaxTreeNode node) {
        if (node.children.isEmpty()) {
            return true;
        }

        boolean commandCheck = typecheck(node.children.get(0));
        boolean restCheck = true;

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
                return true;
            case PRINT:
                return typecheckPrint(node.children.get(1));
            case RETURN:
                return typecheckReturn(node.children.get(1));
            case ASSIGN:
                return typecheck(node.children.get(0));
            case CALL:
                return typecheck(node.children.get(0));
            case BRANCH:
                return typecheck(node.children.get(0));
            default:
                return false;
        }
    }

    private boolean typecheckPrint(SyntaxTreeNode atomic) {
        char type = typeof(atomic);
        return type == 'n' || type == 't';
    }

    private boolean typecheckReturn(SyntaxTreeNode atomic) {
        if (currentFunction == null) {
            return false; // Return statement outside of a function
        }

        char functionReturnType = typeof(currentFunction.children.get(0).children.get(0)); // FTYP
        char returnType = typeof(atomic);

        return functionReturnType == returnType;
    }

    private boolean typecheckAssign(SyntaxTreeNode node) {
        SyntaxTreeNode vnameNode = node.children.get(0);
        SyntaxTreeNode rhsNode = node.children.get(2);

        if (rhsNode.symbol == TokenType.INPUT) {
            return typeof(vnameNode) == 'n';
        } else {
            return typeof(vnameNode) == typeof(rhsNode);
        }
    }

    private boolean typecheckBranch(SyntaxTreeNode node) {
        SyntaxTreeNode condNode = node.children.get(1);
        SyntaxTreeNode algo1Node = node.children.get(3);
        SyntaxTreeNode algo2Node = node.children.get(5);

        return typeof(condNode) == 'b' && typecheck(algo1Node) && typecheck(algo2Node);
    }

    private boolean typecheckCall(SyntaxTreeNode node) {
        SyntaxTreeNode fnameNode = node.children.get(0);
        List<SyntaxTreeNode> args = node.children.subList(2, node.children.size() - 1);

        SymbolTableEntry funcEntry = currentScope.lookup(fnameNode.value);
        if (funcEntry == null || !funcEntry.type.equals("function")) {
            return false;
        }

        if (args.size() != 3) {
            return false;
        }

        for (SyntaxTreeNode arg : args) {
            if (typeof(arg) != 'n') {
                return false;
            }
        }

        return true;
    }

    private boolean typecheckOp(SyntaxTreeNode node) {
        String opType = node.children.get(0).value;
        List<SyntaxTreeNode> args = node.children.subList(2, node.children.size() - 1);

        if (opType.equals("UNOP")) {
            if (args.size() != 1) {
                return false;
            }
            char unopType = typeof(node.children.get(0));
            char argType = typeof(args.get(0));
            return (unopType == 'b' && argType == 'b') || (unopType == 'n' && argType == 'n');
        } else if (opType.equals("BINOP")) {
            if (args.size() != 2) {
                return false;
            }
            char binopType = typeof(node.children.get(0));
            char arg1Type = typeof(args.get(0));
            char arg2Type = typeof(args.get(1));

            if (binopType == 'b') {
                return arg1Type == 'b' && arg2Type == 'b';
            } else if (binopType == 'n') {
                return arg1Type == 'n' && arg2Type == 'n';
            } else if (binopType == 'c') {
                return arg1Type == 'n' && arg2Type == 'n';
            }
        }

        return false;
    }

    private boolean typecheckDecl(SyntaxTreeNode node) {
        currentFunction = node; // Set the current function when entering a function declaration
        boolean headerCheck = typecheck(node.children.get(0));
        boolean bodyCheck = typecheck(node.children.get(1));
        currentFunction = null; // Reset the current function when exiting
        return headerCheck && bodyCheck;
    }

    private boolean typecheckHeader(SyntaxTreeNode node) {
        SyntaxTreeNode ftypNode = node.children.get(0);
        SyntaxTreeNode fnameNode = node.children.get(1);
        List<SyntaxTreeNode> params = node.children.subList(3, node.children.size() - 1);

        char returnType = typeof(ftypNode);
        SymbolTableEntry funcEntry = currentScope.lookup(fnameNode.value);
        if (funcEntry != null) {
            funcEntry.type = String.valueOf(returnType);
        }

        for (SyntaxTreeNode param : params) {
            if (typeof(param) != 'n') {
                return false;
            }
        }

        return true;
    }

    private boolean typecheckBody(SyntaxTreeNode node) {
        boolean prologCheck = typecheck(node.children.get(0));
        boolean locvarsCheck = typecheck(node.children.get(1));
        boolean algoCheck = typecheck(node.children.get(2));
        boolean epilogCheck = typecheck(node.children.get(3));
        boolean subfuncsCheck = typecheck(node.children.get(4));

        return prologCheck && locvarsCheck && algoCheck && epilogCheck && subfuncsCheck;
    }

    private boolean typecheckLocvars(SyntaxTreeNode node) {
        for (int i = 0; i < node.children.size(); i += 3) {
            SyntaxTreeNode vtypNode = node.children.get(i);
            SyntaxTreeNode vnameNode = node.children.get(i + 1);

            char type = typeMap.get(vtypNode.value);
            String varName = vnameNode.value;

            SymbolTableEntry entry = currentScope.lookup(varName);
            if (entry != null) {
                entry.type = String.valueOf(type);
            }
        }

        return true;
    }

    private char typeof(SyntaxTreeNode node) {
        switch (node.symbol) {
            case VNAME:
            case FNAME:
                SymbolTableEntry entry = currentScope.lookup(node.value);
                return entry != null ? entry.type.charAt(0) : 'u';
            case CONST:
                return node.value.startsWith("N_") ? 'n' : 't';
            case FTYP:
                return typeMap.get(node.value);
            case UNOP:
                return node.value.equals("not") ? 'b' : 'n';
            case BINOP:
                switch (node.value) {
                    case "or":
                    case "and":
                        return 'b';
                    case "eq":
                    case "grt":
                        return 'c';
                    default:
                        return 'n';
                }
            case OP:
            case CALL:
                return typecheckOp(node) ? 'n' : 'u';
            case COND:
            case SIMPLE:
            case COMPOSIT:
                return 'b';
            default:
                return 'u';
        }
    }
}