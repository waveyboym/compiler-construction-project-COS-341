public class Token {
    public TokenType type;
    public String fileName;
    public String Line;
    public String Column;

    public Token(TokenType type, String fileName, String Line, String Column) {
        this.type = type;
        this.fileName = fileName;
        this.Line = Line;
        this.Column = Column;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", fileName='" + fileName + '\'' +
                ", Line='" + Line + '\'' +
                ", Column='" + Column + '\'' +
                '}';
    }
}
