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
                default -> throw new IllegalArgumentException("Unexpected value: " + command.children.get(0).token.type);
            }
        }else{
            switch(command.children.get(0).nonterminalname){
                case "ASSIGN" -> {
                    sb.append(generateBasicAssign(command.children.get(0)));
                }
                case "CALL" -> {
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
        // expected: EXPR := ATOMIC | FNAME ( ATOMIC, ATMOIC, ATOMIC )
        if(expr.children.get(0).nonterminalname.equals("CONST")){
            return generateBasicAtomic(expr);
        }else{
            StringBuilder sb = new StringBuilder();

            sb.append("CALL ");
            sb.append(expr.children.get(0).token.Value);
            sb.append("(");
            sb.append(generateBasicAtomic(expr.children.get(2)));
            sb.append(", ");
            sb.append(generateBasicAtomic(expr.children.get(4)));
            sb.append(", ");
            sb.append(generateBasicAtomic(expr.children.get(6)));
            sb.append(")");

            return sb.toString();
        }
    }

    private String generateBasicCall(ParseNode call){
        // expected: CALL := FNAME
        // equivalent BASIC syntax code: LN CALL FNAME(arg1, arg2, ...)
        StringBuilder sb = new StringBuilder();

        sb.append(Line()).append(" CALL ");
        sb.append(call.children.get(0).token.Value);
        sb.append("\n");

        return sb.toString();
    }

    private String generateBasicBranch(ParseNode branch){
        // expected: BRANCH := if EXPR then INSTRUC else INSTRUC
        // equivalent BASIC syntax code: LN IF EXPR THEN GOTO LN+10
        StringBuilder sb = new StringBuilder();

        sb.append(Line()).append(" IF ");
        sb.append(generateBasicExpr(branch.children.get(1)));
        sb.append(" THEN GOTO ");
        sb.append(this.line + 10);
        sb.append("\n");

        return sb.toString();
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

}