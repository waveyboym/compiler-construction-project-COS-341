package Interfaces;

public class LeafNode extends SyntaxTreeNode {
    public SyntaxToken token;

    public LeafNode(String id, SyntaxToken token) {
        this.id = id;
        this.token = token;
        this.symbol = token.type;
    }
}

