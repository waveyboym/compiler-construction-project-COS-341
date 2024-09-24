package Interfaces;

public class SyntaxToken {
    public String id;
    public String word;
    public TokenType type;

    public SyntaxToken(String id, TokenType type, String word) {
        this.id = id;
        this.type = type;
        this.word = word;
    }
}
