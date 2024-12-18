package Parser;
import java.util.List;

import Interfaces.ParseNode;
import Interfaces.ParseType;
import Interfaces.Token;
import Interfaces.TokenType;
import Utils.Errors;

public class Parser {
    private final List<Token> tokens;
    private Token current;
    private int index;
    private int line;
    private String currentLine;
    
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.index = -1;
        this.line = 0;
        this.current = null;
        this.currentLine = "";
        this.advance();
    }
    
    private Token advance(){
        if (this.index + 1 < this.tokens.size()) {
            this.index++;
            this.current = this.tokens.get(this.index);
            if(this.current.Line != this.line){
                this.line = this.current.Line;
                this.currentLine = this.current.Value;
            } else {
                this.currentLine += this.current.Value;
            }

            while(this.current.type == TokenType.NULLTYPE && this.index + 1 < this.tokens.size()){
                this.index++;
                this.current = this.tokens.get(this.index);
                if(this.current.Line != this.line){
                    this.line = this.current.Line;
                    this.currentLine = this.current.Value;
                } else {
                    this.currentLine += this.current.Value;
                }
            }

            if(this.current.type == TokenType.NULLTYPE){
                this.current = new Token(TokenType.EOF, this.current.fileName, this.current.Line, this.current.Column, "EOF");
            }

            return this.current;
        } else if( this.current.type == TokenType.END){
            this.current = new Token(TokenType.EOF, this.current.fileName, this.current.Line, this.current.Column, "EOF");
            return this.current;
        }
        else {
            // throw error that we reached unexpected EOF without fully building the syntax tree
            throw new RuntimeException(Errors.formatParserError(this.current, "Reached unexpected EOF with incomplete parse tree", currentLine));
        }
    }

    private void matchType(TokenType type){
        if(this.current.type != type){
            throw new RuntimeException(Errors.formatParserError(this.current, Errors.stringRepresentation(type), currentLine));
        }
    }

    private void matchTwoTypes(TokenType type1, TokenType type2){
        if(this.current.type != type1 && this.current.type != type2){
            throw new RuntimeException(Errors.formatParserError(this.current, Errors.stringRepresentation(type1) + " or " + Errors.stringRepresentation(type2), currentLine));
        }
    }

    public ParseNode parse() {
        return parsePROG();
    }

    private ParseNode parsePROG() {
        ParseNode node = new ParseNode("PROG");

        // main
        matchType(TokenType.MAIN);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // globvars
        ParseNode gbVars = parseGLOBVARS();
        if(gbVars != null){
            node.addChild(gbVars);
        } else {
            node.addChild(new ParseNode("GLOBVARS"));
        }

        // algo
        node.addChild(parseAlgo());

        // functions
        ParseNode functions = parseFUNCTIONS();
        if(functions != null){
            node.addChild(functions);
        } else {
            node.addChild(new ParseNode("FUNCTIONS"));
        }

        return node;
    }

    private ParseNode parseGLOBVARS() {
        ParseNode node = new ParseNode("GLOBVARS");

        if(this.current.type == TokenType.BEGIN){
            return null;
        }

        // VTYPE
        matchTwoTypes(TokenType.NUM, TokenType.VTEXT);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // VNAME
        matchType(TokenType.VNAME);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // if "," then parse GLOBVARS else return node
        if (this.current.type == TokenType.COMMA) {
            node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
            this.advance();
            node.addChild(parseGLOBVARS());
        }

        return node;
    }

    private ParseNode parseAlgo(){
        ParseNode node = new ParseNode("ALGO");

        // begin
        matchType(TokenType.BEGIN);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // instruc
        node.addChild(parseInstruc());

        // end
        matchType(TokenType.END);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        return node;
    }

    private ParseNode parseFUNCTIONS(){
        ParseNode node = new ParseNode("FUNCTIONS");

        if(this.current.type == TokenType.END || this.current.type == TokenType.EOF){
            return null;
        }

        ParseNode decl = new ParseNode("DECL");

        // match header
        decl.addChild(parseFuncHeader());

        // match body
        decl.addChild(parseFuncBody());

        node.addChild(decl);

        // match another header type
        if(this.current.type == TokenType.NUM || this.current.type == TokenType.FVOID){
            node.addChild(parseFUNCTIONS());
        }

        return node;
    }

    private ParseNode parseFuncHeader(){
        ParseNode node = new ParseNode("HEADER");

        // match ftype
        matchTwoTypes(TokenType.NUM, TokenType.FVOID);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // match fname
        matchType(TokenType.FNAME);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // (
        matchType(TokenType.LEFT_PAREN);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // vname 1
        matchType(TokenType.VNAME);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // ,
        matchType(TokenType.COMMA);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // vname 2
        matchType(TokenType.VNAME);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // ,
        matchType(TokenType.COMMA);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // vname 3
        matchType(TokenType.VNAME);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // )
        matchType(TokenType.RIGHT_PAREN);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        return node;
    }

    private ParseNode parseFuncBody(){
        ParseNode node = new ParseNode("BODY");

        // {
        matchType(TokenType.LEFT_BRACE);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // locvars
        node.addChild(parseLocalVars());

        // algo
        node.addChild(parseAlgo());

        // }
        matchType(TokenType.RIGHT_BRACE);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // subfunctions
        if(this.current.type == TokenType.NUM || this.current.type == TokenType.FVOID){
            node.addChild(parseFUNCTIONS());
        }

        // end
        matchType(TokenType.END);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        return node;
    }

    private ParseNode parseLocalVars(){
        ParseNode node = new ParseNode("LOCALVARS");

        // VTYPE
        matchTwoTypes(TokenType.NUM, TokenType.VTEXT);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // VNAME
        matchType(TokenType.VNAME);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // ,
        matchType(TokenType.COMMA);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // VTYPE
        matchTwoTypes(TokenType.NUM, TokenType.VTEXT);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // VNAME
        matchType(TokenType.VNAME);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // ,
        matchType(TokenType.COMMA);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // VTYPE
        matchTwoTypes(TokenType.NUM, TokenType.VTEXT);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // VNAME
        matchType(TokenType.VNAME);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // ,
        matchType(TokenType.COMMA);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        return node;
    }

    private ParseNode parseInstruc(){
        ParseNode node = new ParseNode("INSTRUC");

        if(this.current.type == TokenType.END){
            return null;
        }

        // command
        node.addChild(parseCommand());

        // semi-colon
        matchType(TokenType.SEMICOLON);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();
        node.addChild(parseInstruc());

        return node;
    }

    private ParseNode parseCommand(){
        ParseNode node = new ParseNode("COMMAND");

        switch (this.current.type) {
            case SKIP -> {
                node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
                this.advance();
                return node;
            }
            case HALT -> {
                node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
                this.advance();
                return node;
            }
            case PRINT -> {
                node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
                this.advance();
                node.addChild(parseAtomic());
                return node;
            }
            case VNAME -> {
                //ASSIGN
                node.addChild(parseVNAMEASSIGN());
                return node;
            }
            case FNAME -> {
                //CALL
                node.addChild(parseFNAMECALL());
                return node;
            }
            case IF -> {
                //IF
                node.addChild(parseIF());
                return node;
            }
            case RETURN -> {
                //RETURN
                node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
                this.advance();
                node.addChild(parseAtomic());
                return node;
            }
            default -> throw new RuntimeException(Errors.formatParserError(this.current, "SKIP, HALT, PRINT, VNAME, FNAME, IF, INPUT", currentLine));
        }
    }

    private ParseNode parseVNAMEASSIGN(){
        ParseNode node = new ParseNode("ASSIGN");

        // VNAME
        node.addChild(parseVNAME());

        // < or =
        TokenType type = this.current.type;
        matchTwoTypes(TokenType.LESS_THAN_SIGN, TokenType.EQUAL_SIGN);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        if(type == TokenType.LESS_THAN_SIGN){
            // <
            // expect input
            matchType(TokenType.INPUT);
            node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
            this.advance();
        }
        else{
            // =
            // TERM
            node.addChild(parseTerm());
        }

        return node;
    }

    private ParseNode parseAtomic(){
        ParseNode node = new ParseNode("ATOMIC");

        if(this.current.type == TokenType.VNAME){
            node.addChild(parseVNAME());
            return node;
        }
        else{
            node.addChild(parseCONST());
            return node;
        }
    }

    private ParseNode parseVNAME(){
        matchType(TokenType.VNAME);
        ParseNode node = new ParseNode(this.current, ParseType.TERMINAL);
        this.advance();
        return node;
    }

    private ParseNode parseFNAMECALL(){
        ParseNode node = new ParseNode("CALL");

        // FNAME
        matchType(TokenType.FNAME);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // (
        matchType(TokenType.LEFT_PAREN);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // ATOMIC
        node.addChild(parseAtomic());

        // ,
        matchType(TokenType.COMMA);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // ATOMIC
        node.addChild(parseAtomic());

        // ,
        matchType(TokenType.COMMA);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // ATOMIC
        node.addChild(parseAtomic());

        // )
        matchType(TokenType.RIGHT_PAREN);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        return node;
    }

    private ParseNode parseCONST(){
        matchTwoTypes(TokenType.NUMLIT, TokenType.TEXTLIT);
        ParseNode node = new ParseNode(this.current, ParseType.TERMINAL);
        this.advance();
        return node;
    }

    private ParseNode parseTerm(){
        ParseNode node = new ParseNode("TERM");

        switch (this.current.type){
            case NUMLIT, TEXTLIT -> {
                node.addChild(parseCONST());
                break;
            }
            case VNAME -> {
                node.addChild(parseVNAME());
                break;
            }
            case FNAME -> {
                node.addChild(parseFNAMECALL());
                break;
            }
            default -> node.addChild(parseOp());
        }

        return node;
    }

    private ParseNode parseOp(){
        ParseNode node = new ParseNode("OP");

        switch (this.current.type) {// unop or binop
            case NOT, SQRT -> {
                // expect unop
                node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
                this.advance();

                // advance and expect (
                matchType(TokenType.LEFT_PAREN);
                node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
                this.advance();

                node.addChild(parseArg());

                // advance and expect )
                matchType(TokenType.RIGHT_PAREN);
                node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
                this.advance();
            }
            case OR, AND, EQ, GT, ADD, SUB, MUL, DIV -> {
                // expect binop
                node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
                this.advance();

                // advance and expect (
                matchType(TokenType.LEFT_PAREN);
                node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
                this.advance();

                node.addChild(parseArg());

                // advance and expect ,
                matchType(TokenType.COMMA);
                node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
                this.advance();

                node.addChild(parseArg());

                // advance and expect )
                matchType(TokenType.RIGHT_PAREN);
                node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
                this.advance();
            }
            default -> throw new RuntimeException(Errors.formatParserError(this.current, "NOT, SQRT, OR, AND, EQ, GT, ADD, SUB, MUL, DIV", currentLine));
        }

        return node;
    }

    private ParseNode parseArg(){
        ParseNode node = new ParseNode("ARG");

        switch (this.current.type) {// unop or binop
            case NOT, SQRT, OR, AND, EQ, GT, ADD, SUB, MUL, DIV -> node.addChild(parseOp());
            default -> node.addChild(parseAtomic());
        }

        return node;
    }

    private ParseNode parseIF(){
        ParseNode node = new ParseNode("BRANCH");

        // if
        matchType(TokenType.IF);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // condition
        node.addChild(parseCondition());

        // then
        matchType(TokenType.THEN);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // instruc
        node.addChild(parseAlgo());

        // else
        if (this.current.type == TokenType.ELSE) {
            node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
            this.advance();
            node.addChild(parseAlgo());
        }

        return node;
    }

    private ParseNode parseCondition(){
        ParseNode node = new ParseNode("COND");

        switch (this.current.type) {// unop or binop
            case NOT, SQRT -> node.addChild(parseUnopBuilder());
            case OR, AND, EQ, GT, ADD, SUB, MUL, DIV -> node.addChild(parseBinopBuilder(true));
            default -> throw new RuntimeException(Errors.formatParserError(this.current, "NOT, SQRT, OR, AND, EQ, GT, ADD, SUB, MUL, DIV", currentLine));
        }

        return node;
    }

    private ParseNode parseBinop(Boolean recursive){
        ParseNode node = new ParseNode("BINOPSIMPLE");

        if((this.current.type == TokenType.OR || this.current.type == TokenType.AND || this.current.type == TokenType.EQ || this.current.type == TokenType.GT
                || this.current.type == TokenType.ADD || this.current.type == TokenType.SUB || this.current.type == TokenType.MUL || this.current.type == TokenType.DIV)
                && recursive){
            node.addChild(parseBinopBuilder(false));
        } else {
            node.addChild(parseAtomic());
        }

        return node;
    }

    private ParseNode parseUnopBuilder(){
        ParseNode node = new ParseNode("UNOPSIMPLE");

        // expect unop
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // advance and expect (
        matchType(TokenType.LEFT_PAREN);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        if((this.current.type != TokenType.OR && this.current.type != TokenType.AND 
        && this.current.type != TokenType.EQ && this.current.type != TokenType.GT
        && this.current.type != TokenType.ADD && this.current.type != TokenType.SUB 
        && this.current.type != TokenType.MUL && this.current.type != TokenType.DIV)){
            throw new RuntimeException(Errors.formatParserError(this.current, "OR, AND, EQ, GT, ADD, SUB, MUL, DIV", currentLine));
        } 
        node.addChild(parseBinopBuilder(false));

        // advance and expect )
        matchType(TokenType.RIGHT_PAREN);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        return node;
    }

    private ParseNode parseBinopBuilder(Boolean recursive){
        ParseNode node = new ParseNode("BINOPCOMPOSITE");
        
        // expect binop
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // advance and expect (
        matchType(TokenType.LEFT_PAREN);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        node.addChild(parseBinop(recursive));

        // advance and expect ,
        matchType(TokenType.COMMA);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        node.addChild(parseBinop(recursive));

        // advance and expect )
        matchType(TokenType.RIGHT_PAREN);
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        return node;
    }

}
