package Interfaces;

public enum TokenType {
    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, SEMICOLON, EQUAL_SIGN, LESS_THAN_SIGN,

    // Unary operators.
    UNOP, NOT, SQRT,

    // Binary operators.
    BINOP, OR, AND, EQ, GT, ADD, SUB, MUL, DIV,

    // Variable Types and Function Return Types
    NUM, VTEXT, FVOID, TEXT, VOID,

    // Variables.
    VNAME, FNAME,
    
    // Literals.
    NUMLIT, TEXTLIT,

    // Keywords.
    BEGIN, END, IF, THEN, ELSE, SKIP, HALT, PRINT, 
    INPUT, MAIN, RETURN,

    // Lexer tags
    PROG, PARSETREE, GLOBVARS, ALGO, INSTRUC, 
    COMMAND, BRANCH, VALUE, ID, CONST, ATOMIC, 
    BINOPSIMPLE, BINOPCOMPOSITE, UNOPSIMPLE,
    FUNCTIONS, DECL, LOCALVARS, CALL, HEADER, 
    BODY, ASSIGN, TERM, OP, ARG, FTYP, SIMPLE, 
    COMPOSIT, COND, 

    // Null, this token will be discarded in the lexing phase
    NULLTYPE,

    EOF
}
