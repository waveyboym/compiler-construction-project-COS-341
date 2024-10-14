package Interfaces;

import java.util.List;
import java.util.ArrayList;

public class SyntaxTreeNode {
    public String id;     // The value inside <ID>, can be null
    public String value;  // The value inside <VALUE>, can be null
    public TokenType symbol; // The tag name
    public List<SyntaxTreeNode> children = new ArrayList<>();

    public SyntaxTreeNode(TokenType symbol) {
        this.symbol = symbol;
    }

    public void addChild(SyntaxTreeNode child) {
        children.add(child);
    }
}
