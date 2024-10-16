package CodeGenBasic;

import Interfaces.ParseNode;
import Interfaces.ParseType;
import Interfaces.TokenType;

public class CodeGenBasic {
    ParseNode pt;
    int line = 0;
    // I would have the vtable and ftable here

    public CodeGenBasic(ParseNode pt) {
        this.pt = pt;
        line = 0;
        // assign vtable and ftable
    }

    private String Line(){
        return String.valueOf(line += 10);
    }

    public String generateCode() {
        // PROG := main GLOBVARS ALGO FUNCTIONS
        StringBuilder sb = new StringBuilder();

        // GLOBVARS
        sb.append(generateBasicGlobalVariables(pt.children.get(1), ""));

        // ALGO
        sb.append(generateBasicAlgo(pt.children.get(2), ""));

        sb.append(Line()).append("\n");

        // FUNCTIONS
        sb.append(generateBasicFunctions(pt.children.get(3), ""));

        // add final END statement
        return sb.append(Line()).append(" END\n").toString();
    }

    private String generateBasicGlobalVariables(ParseNode gbvars, String indent){
        // expected: GLOBVARS := VTYPE VNAME , GLOBVARS | ε
        // equivalent BASIC syntax code: LN LET VNAME = 0
        if(gbvars.children.isEmpty()){
            return "";
        }

        StringBuilder sb = new StringBuilder();

        ParseNode vtype = gbvars.children.get(0);
        ParseNode vname = gbvars.children.get(1);

        if(null == vtype.token.type){
            throw new IllegalArgumentException("Unexpected value: " + vtype.token.type);
        } else switch (vtype.token.type) {
            case NUM -> sb.append(Line()).append(indent).append(" LET ").append(vname.token.Value).append(" = 0\n");
            case VTEXT -> sb.append(Line()).append(indent).append(" LET ").append(vname.token.Value).append(" = \"\"\n");
            default -> throw new IllegalArgumentException("Unexpected value: " + vtype.token.type);
        }

        if(gbvars.children.size() > 2){
            sb.append(generateBasicGlobalVariables(gbvars.children.get(3), indent));
        }

        return sb.toString();
    }

    private String generateBasicAlgo(ParseNode algo, String indent){
        // expected: ALGO := begin INSTRUC end
        // ignore begin and end
        return generateBasicInstruc(algo.children.get(1), indent);
    }

    private String generateBasicFunctions(ParseNode functions, String indent){
        // expected: FUNCTIONS := DECL FUNCTIONS | ε
        if(functions.children.isEmpty()){
            return "";
        }

        StringBuilder sb = new StringBuilder();

        sb.append(generateBasicDecl(functions.children.get(0), indent));

        if(functions.children.size() > 1){
            sb.append(generateBasicFunctions(functions.children.get(1), indent));
        }

        return sb.toString();
    }

    private String generateBasicDecl(ParseNode decl, String indent){
        // expected: DECL := HEADER BODY
        StringBuilder sb = new StringBuilder();

        sb.append(Line()).append(" ").append(generateBasicHeader(decl.children.get(0), indent));
        sb.append(generateBasicBody(decl.children.get(1), indent));

        return sb.toString();
    }

    private String generateBasicHeader(ParseNode header, String indent){
        // expected: HEADER := FTYPE FNAME ( ARG1, ARG2, ARG3 )
        // equivalent BASIC syntax code: SUB FNAME(ARG1, ARG2, ARG3)
        StringBuilder sb = new StringBuilder();

        sb.append(indent);
        sb.append("SUB ");
        sb.append(header.children.get(1).token.Value);
        sb.append("(");
        sb.append(header.children.get(3).token.Value);
        sb.append(", ");
        sb.append(header.children.get(5).token.Value);
        sb.append(", ");
        sb.append(header.children.get(7).token.Value);
        sb.append(")\n");

        return sb.toString();
    }

    private String generateBasicBody(ParseNode body, String indent){
        // expected: BODY := { locvars algo } subfunctions end
        // equivalent BASIC syntax code:
        // LN LOCAL VNAME1, VNAME2
        // LN ALGO
        // LN FUNCTIONS
        // LN END SUB

        StringBuilder sb = new StringBuilder();

        sb.append(Line()).append(indent).append("\t").append(generateBasicLocvars(body.children.get(1)));

        sb.append(generateBasicAlgo(body.children.get(2), indent + "\t"));

        if(body.children.get(4).token == null){
            sb.append(Line()).append(" ").append(generateBasicFunctions(body.children.get(4), indent + "\t"));
        }

        sb.append(Line()).append(" ").append("END SUB\n");

        return sb.toString();
    }

    private String generateBasicLocvars(ParseNode locvars){
        // expected: LOCVARS := VTYPE VNAME , VTYPE VNAME , VTYPE VNAME ,
        // equivalent BASIC syntax code: LN LOCAL VNAME1, VNAME2, VNAME3
        StringBuilder sb = new StringBuilder();

        sb.append(" LOCAL ");
        sb.append(locvars.children.get(1).token.Value);
        sb.append(", ");
        sb.append(locvars.children.get(4).token.Value);
        sb.append(", ");
        sb.append(locvars.children.get(7).token.Value);
        sb.append("\n");

        return sb.toString();
    }

    private String generateBasicInstruc(ParseNode instruc, String indent){
        // expected: INSTRUC := COMMAND ; INSTRUC | ε
        if(instruc.children.isEmpty()){
            return "";
        }

        StringBuilder sb = new StringBuilder();

        sb.append(generateBasicCommand(instruc.children.get(0), indent));

        if(instruc.children.size() > 2){
            sb.append(generateBasicInstruc(instruc.children.get(2), indent));
        }

        return sb.toString();
    }

    private String generateBasicCommand(ParseNode command, String indent){
        // expected: COMMAND := skip | halt | print ATOMIC | ASSIGN | CALL | BRANCH
        // equivalent BASIC syntax code:
        // skip: LN GOTO LN+10
        // halt: LN END
        // print ATOMIC: LN PRINT ATOMIC
        // ASSIGN: LN VNAME = EXPR
        // CALL: LN FNAME
        // BRANCH: LN IF EXPR THEN GOTO LN+10

        StringBuilder sb = new StringBuilder();

        if(command.children.get(0).type == ParseType.TERMINAL){
            switch(command.children.get(0).token.type){
                case TokenType.SKIP -> sb.append(Line()).append(indent).append(" GOTO ").append(this.line + 10).append("\n");
                case TokenType.HALT -> sb.append(Line()).append(indent).append(" END\n");
                case TokenType.PRINT -> {
                    sb.append(Line()).append(indent).append(" PRINT ");
                    sb.append(generateBasicAtomic(command.children.get(1)));
                    sb.append("\n");
                }
                case TokenType.RETURN -> {
                    sb.append(Line()).append(indent).append(" RETURN ");
                    sb.append(generateBasicAtomic(command.children.get(1)));
                    sb.append("\n");
                }
                default -> throw new IllegalArgumentException("Unexpected value: " + command.children.get(0).token.type);
            }
        }else{
            switch(command.children.get(0).nonterminalname){
                case "ASSIGN" -> {
                    sb.append(generateBasicAssign(command.children.get(0), indent));
                }
                case "CALL" -> {
                    sb.append(generateBasicCall(command.children.get(0), indent));
                }
                case "BRANCH" -> {
                    sb.append(generateBasicBranch(command.children.get(0), indent));
                }
                default -> throw new IllegalArgumentException("Unexpected value: " + command.children.get(0).nonterminalname);
            }
        }

        return sb.toString();
    }

    private String generateBasicAtomic(ParseNode atomic){
        // expected: ATOMIC := VNAME | CONST
        if(atomic.children.get(0).token.type == TokenType.VNAME){
            return generateBasicVname(atomic);
        }else{
            return geneareBasicConst(atomic);
        }
    }

    private String generateBasicAssign(ParseNode assign, String indent){
        // expected: ASSIGN := VNAME = EXPR
        // equivalent BASIC syntax code: LN VNAME = EXPR
        StringBuilder sb = new StringBuilder();

        sb.append(Line()).append(indent).append(" ");
        if(assign.children.get(1).token.type == TokenType.LESS_THAN_SIGN){
            // we are receiving input from user
            sb.append("INPUT ");
            sb.append(generateBasicVname(assign));
            sb.append("\n");
        } else {
            sb.append(generateBasicVname(assign));
            sb.append(" = ");
            sb.append(generateBasicExpr(assign.children.get(2)));
            sb.append("\n");
        }

        return sb.toString();
    }

    private String generateBasicExpr(ParseNode expr){
        // expected: EXPR := ATOMIC | FNAME ( ATOMIC, ATMOIC, ATOMIC ) | OP
        if(expr.children.get(0).token != null){
            return generateBasicAtomic(expr);
        }
        switch (expr.children.get(0).nonterminalname) {
            case "CALL" -> {
                StringBuilder sb = new StringBuilder();
                ParseNode fname = expr.children.get(0);
                
                sb.append(fname.children.get(0).token.Value);
                sb.append("(");
                sb.append(generateBasicAtomic(fname.children.get(2)));
                sb.append(", ");
                sb.append(generateBasicAtomic(fname.children.get(4)));
                sb.append(", ");
                sb.append(generateBasicAtomic(fname.children.get(6)));
                sb.append(")");
                
                return sb.toString();
            }
            default -> {
                String op = generateBasicOP(expr.children.get(0));
                // remove the first and last character which are "(" and ")" to conform to the expected output
                return op.substring(1, op.length() - 1);
            }
        }
    }

    private String generateBasicOP(ParseNode op){
        // expected: OP := OR, AND, EQ, GT, ADD, SUB, MUL, DIV (ARG1, ARG2) | NOT, SQRT (ARG)
        // equivalent BASIC syntax code: (ARG OP ARG)
        StringBuilder sb = new StringBuilder();

        switch (op.children.get(0).token.type) {
            case NOT -> {
                sb.append("(");
                sb.append("NOT ");
                sb.append(generateBasicArg(op.children.get(2)));
                sb.append(")");
                return sb.toString();
            }
            case SQRT -> {
                sb.append("(");
                sb.append("SQRT ");
                sb.append("(");
                sb.append(generateBasicArg(op.children.get(2)));
                sb.append(")");
                sb.append(")");
                return sb.toString();
            }
            default -> {
                sb.append("(");
                sb.append(generateBasicArg(op.children.get(2)));
                sb.append(" ");
                sb.append(generateBasicOPrepr(op.children.get(0).token.type));
                sb.append(" ");
                sb.append(generateBasicArg(op.children.get(4)));
                sb.append(")");
            }
        }

        return sb.toString();
    }

    private String generateBasicArg(ParseNode arg){
        // expected: ARG := ATOMIC | OP
        if(arg.children.get(0).nonterminalname.equals("ATOMIC")){
            return generateBasicAtomic(arg.children.get(0));
        }else{
            return generateBasicOP(arg.children.get(0));
        }
    }

    private String generateBasicCall(ParseNode call, String indent){
        // expected: CALL := FNAME
        // equivalent BASIC syntax code: LN FNAME(arg1, arg2, arg3)
        StringBuilder sb = new StringBuilder();

        sb.append(Line()).append(indent).append(" ");
        sb.append(call.children.get(0).token.Value);
        sb.append("(");
        sb.append(generateBasicAtomic(call.children.get(2)));
        sb.append(", ");
        sb.append(generateBasicAtomic(call.children.get(4)));
        sb.append(", ");
        sb.append(generateBasicAtomic(call.children.get(6)));
        sb.append(")");
        sb.append("\n");

        return sb.toString();
    }

    private String generateBasicBranch(ParseNode branch, String indent){
        // expected: BRANCH := if COND then ALGO else ALGO
        // equivalent BASIC syntax code: 
        //LN IF COND THEN GOTO LN+10 ELSE GOTO LN+20
        //LN+10 ALGO1
        //LN+20 GOTO LN+40
        //LN+30 ALGO2
        //LN+40

        StringBuilder sb = new StringBuilder();

        sb.append(Line()).append(indent).append(" IF ");
        sb.append(generateBasicCond(branch.children.get(1)));
        sb.append(" THEN GOTO ");
        sb.append(this.line + 10);
        sb.append(" ELSE GOTO ");
        String instruc1 = generateBasicAlgo(branch.children.get(3), indent + "\t");
        String instruc2 = Line() + indent + " GOTO ";
        sb.append(this.line + 10);
        String instruc3 = generateBasicAlgo(branch.children.get(5), indent + "\t");
        instruc2 += String.valueOf((this.line + 10));
        sb.append("\n");
        sb.append(instruc1);
        sb.append(instruc2);
        sb.append("\n");
        sb.append(instruc3);
        sb.append(Line()).append(indent).append(" GOTO ").append(this.line + 10).append("\n");
        sb.append(Line()).append(indent).append(" ENDIF\n");

        return sb.toString();
    }

    private String generateBasicCond(ParseNode cond){
        // expected: COND := UNOP | BINOP
        StringBuilder sb = new StringBuilder();

        if(cond.children.get(0).nonterminalname.equals("UNOPSIMPLE")){
            sb.append(generateBasicUnop(cond.children.get(0)));
        }else{
            sb.append(generateBasicBinopComposite(cond.children.get(0)));
        }

        String op = sb.toString();
        // remove the first and last character which are "(" and ")" to conform to the expected output
        return op.substring(1, op.length() - 1);
    }

    private String generateBasicUnop(ParseNode unop){
        // expected: UNOP := not|sqrt(BINOP)
        // equivalent BASIC syntax code: (NOT ARG) | (SQRT (ARG))
        StringBuilder sb = new StringBuilder();

        if(unop.children.get(0).token.type == TokenType.NOT){
            sb.append("(");
            sb.append("NOT ");
            sb.append(generateBasicArg(unop.children.get(1)));
            sb.append(")");
        }else{
            sb.append("(");
            sb.append("SQRT ");
            sb.append("(");
            sb.append(generateBasicBinop(unop.children.get(2)));
            sb.append(")");
            sb.append(")");
        }

        return sb.toString();
    }

    private String generateBasicBinopComposite(ParseNode binop){
        // expected: BINOP := ATOMIC RELOP ATOMIC
        // equivalent BASIC syntax code: 
        // (ARG RELOP ARG)
        StringBuilder sb = new StringBuilder();

        sb.append("(");
        sb.append(generateBasicBinop(binop.children.get(2)));
        sb.append(" ");
        sb.append(generateBasicOPrepr(binop.children.get(0).token.type));
        sb.append(" ");
        sb.append(generateBasicBinop(binop.children.get(4)));
        sb.append(")");

        return sb.toString();
    }

    private String generateBasicBinop(ParseNode binop){
        if(binop.children.get(0).nonterminalname.equals("ATOMIC")){
            return generateBasicAtomic(binop.children.get(0));
        }else{
            return generateBasicOP(binop.children.get(0));
        }
    }

    private String generateBasicVname(ParseNode vname){
        // expected: VNAME := ID
        // equivalent BASIC syntax code: ID
        return vname.children.get(0).token.Value;
    }

    private String geneareBasicConst(ParseNode constant){
        // expected: CONST := NUMLIT | TEXTLIT
        // equivalent BASIC syntax code: 0 | "TEXT"
        if(constant.children.get(0).type == ParseType.TERMINAL && constant.children.get(0).token.type == TokenType.NUMLIT){
            return constant.children.get(0).token.Value;
        }else if(constant.children.get(0).type == ParseType.TERMINAL && constant.children.get(0).token.type == TokenType.TEXTLIT){
            return "\"" + constant.children.get(0).token.Value + "\"";
        }else{
            throw new IllegalArgumentException("Unexpected value: " + constant.children.get(0).token.type);
        }
    }

    private String generateBasicOPrepr(TokenType type){
        switch (type) {
            case NOT -> {
                return "NOT";
            }
            case SQRT -> {
                return "SQR";
            }
            case OR -> {
                return "OR";
            }
            case AND -> {
                return "AND";
            }
            case EQ -> {
                return "=";
            }
            case GT -> {
                return ">";
            }
            case ADD -> {
                return "+";
            }
            case SUB -> {
                return "-";
            }
            case MUL -> {
                return "*";
            }
            case DIV -> {
                return "/";
            }
            default -> {
                throw new IllegalArgumentException("Unexpected value: " + type);
            }
        }
    }
}
