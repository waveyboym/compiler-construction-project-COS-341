package Interfaces;

import java.util.List;
import java.util.ArrayList;

public abstract class SyntaxTreeNode {
    public String id;
    public TokenType symbol;
    public SyntaxTreeNode parent;
    public List<SyntaxTreeNode> children = new ArrayList<>();

    public void addChild(SyntaxTreeNode child) {
        children.add(child);
        child.parent = this;
    }
}
