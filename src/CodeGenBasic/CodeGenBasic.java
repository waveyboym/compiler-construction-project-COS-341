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
        ParseNode GLOBVARS = pt.children.get(1);
        sb.append(generateBasicGlobalVariables(GLOBVARS));

        // ALGO
        ParseNode ALGO = pt.children.get(2);
        sb.append(generateBasicAlgo(ALGO));

        // remove final "\n" which is the last character in the string
        return sb.toString().substring(0, sb.length() - 1);
    }

    private String generateBasicGlobalVariables(ParseNode gbvars){
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
            case NUM -> sb.append(Line()).append(" LET ").append(vname.token.Value).append(" = 0\n");
            case VTEXT -> sb.append(Line()).append(" LET ").append(vname.token.Value).append(" = \"\"\n");
            default -> throw new IllegalArgumentException("Unexpected value: " + vtype.token.type);
        }

        if(gbvars.children.size() > 2){
            sb.append(generateBasicGlobalVariables(gbvars.children.get(3)));
        }

        return sb.toString();
    }

    private String generateBasicAlgo(ParseNode algo){
        // expected: ALGO := begin INSTRUC end
        // ignore begin and end
        return generateBasicInstruc(algo.children.get(1)) + Line() + " END\n";
    }

    private String generateBasicInstruc(ParseNode instruc){
        // expected: INSTRUC := COMMAND ; INSTRUC | ε
        if(instruc.children.isEmpty()){
            return "";
        }

        StringBuilder sb = new StringBuilder();

        sb.append(generateBasicCommand(instruc.children.get(0)));

        if(instruc.children.size() > 2){
            sb.append(generateBasicInstruc(instruc.children.get(2)));
        }

        return sb.toString();
    }

    private String generateBasicCommand(ParseNode command){
        // expected: COMMAND := skip | halt | print ATOMIC | ASSIGN | CALL | BRANCH
        // equivalent BASIC syntax code:
        // skip: LN GOTO LN+10
        // halt: LN END
        // print ATOMIC: LN PRINT ATOMIC
        // ASSIGN: LN VNAME = EXPR
        // CALL: LN CALL FNAME
        // BRANCH: LN IF EXPR THEN GOTO LN+10

        StringBuilder sb = new StringBuilder();

        if(command.children.get(0).type == ParseType.TERMINAL){
            switch(command.children.get(0).token.type){
                case TokenType.SKIP -> sb.append(Line()).append(" GOTO ").append(this.line + 10).append("\n");
                case TokenType.HALT -> sb.append(Line()).append(" END\n");
                case TokenType.PRINT -> {
                    sb.append(Line()).append(" PRINT ");
                    sb.append(generateBasicAtomic(command.children.get(1)));
                    sb.append("\n");
                }
                case TokenType.RETURN -> {
                    sb.append(Line()).append(" RETURN ");
                    sb.append(generateBasicAtomic(command.children.get(1)));
                    sb.append("\n");
                }
                default -> throw new IllegalArgumentException("Unexpected value: " + command.children.get(0).token.type);
            }
        }else{
            switch(command.children.get(0).nonterminalname){
                case "ASSIGN" -> {
                    sb.append(generateBasicAssign(command.children.get(0)));
                }
                case "FNAME" -> {
                    sb.append(generateBasicCall(command.children.get(0)));
                }
                case "BRANCH" -> {
                    sb.append(generateBasicBranch(command.children.get(0)));
                }
                default -> throw new IllegalArgumentException("Unexpected value: " + command.children.get(0).nonterminalname);
            }
        }

        return sb.toString();
    }

    private String generateBasicAtomic(ParseNode atomic){
        // expected: ATOMIC := VNAME | CONST
        if(atomic.children.get(0).nonterminalname.equals("VNAME")){
            return generateBasicVname(atomic.children.get(0));
        }else{
            return geneareBasicConst(atomic.children.get(0));
        }
    }

    private String generateBasicAssign(ParseNode assign){
        // expected: ASSIGN := VNAME = EXPR
        // equivalent BASIC syntax code: LN VNAME = EXPR
        StringBuilder sb = new StringBuilder();

        sb.append(Line()).append(" ");
        if(assign.children.get(1).token.type == TokenType.LESS_THAN_SIGN){
            // we are receiving input from user
            sb.append("INPUT ");
            sb.append(generateBasicVname(assign.children.get(0)));
            sb.append("\n");
        } else {
            sb.append(generateBasicVname(assign.children.get(0)));
            sb.append(" = ");
            sb.append(generateBasicExpr(assign.children.get(2)));
            sb.append("\n");
        }

        return sb.toString();
    }

    private String generateBasicExpr(ParseNode expr){
        // expected: EXPR := ATOMIC | FNAME ( ATOMIC, ATMOIC, ATOMIC ) | OP
        switch (expr.children.get(0).nonterminalname) {
            case "CONST", "VNAME" -> {
                return generateBasicAtomic(expr);
            }
            case "FNAME" -> {
                StringBuilder sb = new StringBuilder();
                ParseNode fname = expr.children.get(0);
                
                sb.append("CALL ");
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

    private String generateBasicCall(ParseNode call){
        // expected: CALL := FNAME
        // equivalent BASIC syntax code: LN CALL FNAME(arg1, arg2, arg3)
        StringBuilder sb = new StringBuilder();

        sb.append(Line()).append(" CALL ");
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

    private String generateBasicBranch(ParseNode branch){
        // expected: BRANCH := if COND then ALGO else ALGO
        // equivalent BASIC syntax code: 
        //LN IF COND THEN GOTO LN+10 ELSE GOTO LN+20
        //LN+10 ALGO1
        //LN+20 GOTO LN+40
        //LN+30 ALGO2
        //LN+40

        StringBuilder sb = new StringBuilder();

        sb.append(Line()).append(" IF ");
        //sb.append(generateBasicCond(branch.children.get(1)));
        sb.append(" THEN GOTO ");
        sb.append(this.line + 10);
        sb.append(" ELSE GOTO ");
        String instruc1 = generateBasicAlgo(branch.children.get(3));
        sb.append(this.line + 10);
        sb.append("\n");
        String instruc2 = Line() + " GOTO ";
        String instruc3 = generateBasicAlgo(branch.children.get(5));
        instruc2 += this.line + 10;
        sb.append("\n");
        sb.append(instruc1);
        sb.append(instruc2);
        sb.append(instruc3);
        sb.append(Line()).append(" GOTO ").append(this.line + 10).append("\n");

        return sb.toString();
    }

    /*private String generateBasicCond(ParseNode cond){
        // expected: COND := UNOP | BINOP
        StringBuilder sb = new StringBuilder();

        sb.append(generateBasicAtomic(cond.children.get(0)));
        sb.append(" ");
        sb.append(cond.children.get(1).token.Value);
        sb.append(" ");
        sb.append(generateBasicAtomic(cond.children.get(2));

        return sb.toString();
    }*/

    /* 
    private String generateBasicUnop(ParseNode unop){
        // expected: UNOP := not|sqrt(BINOP)
        // equivalent BASIC syntax code: 
        return "NOT " + generateBasicCond(unop.children.get(1));
    }*/

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
