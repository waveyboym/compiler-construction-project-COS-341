import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private Token current;
    private int index;
    private String currentLine;
    
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.index = 0;
        this.current = tokens.get(this.index);
        this.currentLine = this.current.Value;
    }
    
    private Token advance(){
        if (this.index < this.tokens.size()) {
            while(this.current.type == TokenType.NULLTYPE && this.index < this.tokens.size()){
                this.index++;
                this.current = this.tokens.get(this.index);
                this.currentLine += this.current.Value;
            }
            return this.current;
        } else {
            return null;
        }
    }

    public ParseNode parse() {
        return parsePROG();
    }

    private ParseNode parsePROG() {
        ParseNode node = new ParseNode();

        // main
        if (this.current.type != TokenType.MAIN) {
            throw new RuntimeException(Errors.formatParserError(this.current, "main", currentLine));
        }
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // globvars
        node.addChild(parseGLOBVARS());

        // algo
        node.addChild(parseAlgo());

        // functions
        node.addChild(parseFUNCTIONS());

        return node;
    }

    private ParseNode parseGLOBVARS() {
        ParseNode node = new ParseNode();

        // VTYPE
        if (this.current.type != TokenType.NUM && this.current.type != TokenType.VTEXT) {
            throw new RuntimeException(Errors.formatParserError(this.current, "VTYPE", currentLine));
        }
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // VNAME
        if (this.current.type != TokenType.VNAME) {
            throw new RuntimeException(Errors.formatParserError(this.current, "VNAME", currentLine));
        }
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // if "," then parse GLOBVARS else return node
        if (this.current.type == TokenType.COMMA) {
            this.advance();
            node.addChild(parseGLOBVARS());
        }

        return node;
    }

    private ParseNode parseAlgo(){
        ParseNode node = new ParseNode();

        // begin
        if (this.current.type != TokenType.BEGIN) {
            throw new RuntimeException(Errors.formatParserError(this.current, "begin", currentLine));
        }

        // instruc
        this.advance();
        node.addChild(parseInstruc());

        // end
        if (this.current.type != TokenType.END) {
            throw new RuntimeException(Errors.formatParserError(this.current, "end", currentLine));
        }

        return node;
    }

    private ParseNode parseFUNCTIONS(){
        ParseNode node = new ParseNode();

        // function

        return node;
    }

    private ParseNode parseInstruc(){
        ParseNode node = new ParseNode();

        // command
        node.addChild(parseCommand());

        // if ";" then parse Instruc else return node
        if (this.current.type == TokenType.SEMICOLON) {
            this.advance();
            node.addChild(parseInstruc());
        }

        return node;
    }

    private ParseNode parseCommand(){
        ParseNode node = new ParseNode();

        if(null == this.current.type){
            throw new RuntimeException(Errors.formatParserError(this.current, "SKIP, HALT, PRINT, VNAME, FNAME, IF, INPUT", currentLine));
        }
        else switch (this.current.type) {
            case SKIP -> {
                node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
                return node;
            }
            case HALT -> {
                node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
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
                node.addChild(parseVNAME());
                return node;
            }
            case FNAME -> {
                //CALL
                node.addChild(parseFNAME());
                return node;
            }
            case IF -> {
                //IF
                node.addChild(parseIF());
                return node;
            }
            default -> throw new RuntimeException(Errors.formatParserError(this.current, "SKIP, HALT, PRINT, VNAME, FNAME, IF, INPUT", currentLine));
        }
    }

    private ParseNode parseAtomic(){
        ParseNode node = new ParseNode();

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
        ParseNode node = new ParseNode();

        if(this.current.type != TokenType.VNAME){
            throw new RuntimeException(Errors.formatParserError(this.current, "VNAME", currentLine));
        }
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        return node;
    }

    private ParseNode parseFNAME(){
        ParseNode node = new ParseNode();

        if(this.current.type != TokenType.FNAME){
            throw new RuntimeException(Errors.formatParserError(this.current, "FNAME", currentLine));
        }
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        return node;
    }

    private ParseNode parseCONST(){
        ParseNode node = new ParseNode();

        if(this.current.type != TokenType.NUM && this.current.type != TokenType.TEXTLIT){
            throw new RuntimeException(Errors.formatParserError(this.current, "NUM or TEXTLIT", currentLine));
        }
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        return node;
    }

    private ParseNode parseIF(){
        ParseNode node = new ParseNode();

        // if
        if (this.current.type != TokenType.IF) {
            throw new RuntimeException(Errors.formatParserError(this.current, "if", currentLine));
        }
        node.addChild(new ParseNode(this.current, ParseType.TERMINAL));
        this.advance();

        // condition
        node.addChild(parseCondition());

        // then
        if (this.current.type != TokenType.THEN) {
            throw new RuntimeException(Errors.formatParserError(this.current, "then", currentLine));
        }
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
        ParseNode node = new ParseNode();

        // unop or binop
        if(this.current.type == TokenType.NOT || this.current.type == TokenType.SQRT){
            node.addChild(parseUnop());
        }
        else if(this.current.type == TokenType.OR || this.current.type == TokenType.AND || this.current.type == TokenType.EQ || this.current.type == TokenType.GT
                || this.current.type == TokenType.ADD || this.current.type == TokenType.SUB || this.current.type == TokenType.MUL || this.current.type == TokenType.DIV){
            node.addChild(parseBinop());
        }
        else{
            throw new RuntimeException(Errors.formatParserError(this.current, "NOT, SQRT, OR, AND, EQ, GT, ADD, SUB, MUL, DIV", currentLine));
        }
    }

}
