public enum TokenType {
    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, SEMICOLON, EQUAL_SIGN, LESS_THAN_SIGN,

    // Unary operators.
    NOT, SQRT,

    // Binary operators.
    OR, AND, EQ, GT, ADD, SUB, MUL, DIV,

    // Variable Types and Function Return Types
    NUM, VTEXT, FVOID,

    // Variables.
    VNAME, FNAME,
    
    // Literals.
    NUMLIT, TEXTLIT,

    // Keywords.
    BEGIN, END, IF, THEN, ELSE, SKIP, HALT, PRINT, 
    INPUT, MAIN, 

    // Null, this token will be discarded in the lexing phase
    NULLTYPE,

    EOF
}
