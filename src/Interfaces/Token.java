package Interfaces;
public class Token {
    public TokenType type;
    public String uuid;
    public String fileName;
    public int Line;
    public int Column;
    public String Value;

    public Token(TokenType type, String fileName, int Line, int Column, String Value) {
        this.uuid = java.util.UUID.randomUUID().toString();
        this.type = type;
        this.fileName = fileName;
        this.Line = Line;
        this.Column = Column;
        this.Value = Value;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", fileName='" + fileName + '\'' +
                ", Line='" + Line + '\'' +
                ", Column='" + Column + '\'' +
                ", Value='" + Value + '\'' +
                '}';
    }
}
