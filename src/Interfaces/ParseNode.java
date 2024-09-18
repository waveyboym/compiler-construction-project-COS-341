package Interfaces;

import java.util.ArrayList;
import java.util.List;

public class ParseNode {
    public ParseType type;
    public Token token;
    public String nonterminalname;
    public List<ParseNode> children;

    public ParseNode(String ntn) {
        this.token = null;
        this.type = ParseType.NONTERMINAL;
        this.nonterminalname = ntn;
        this.children = new ArrayList<>();
    }

    public ParseNode(Token token, ParseType type) {
        this.token = token;
        this.type = type;
        this.children = new ArrayList<>();
    }

    public ParseNode(Token token, List<ParseNode> children) {
        this.token = token;
        this.children = children;
    }

    public void addChild(ParseNode child) {
        // check if child is null and discard if null
        if(child == null){
            return;
        }
        this.children.add(child);
    }

    @Override
    public String toString() {
        return toString("", true);
    }

    private String toString(String prefix, boolean isTail) {
        StringBuilder sb = new StringBuilder();
        if(token != null) {
            sb.append(prefix).append(isTail ? "└── " : "├── ").append(token.toString()).append("\n");
        } else {
            sb.append(prefix).append(isTail ? "└── " : "├── ").append(this.nonterminalname).append("\n");
        }
        if (children != null) {
            for (int i = 0; i < children.size() - 1; i++) {
                sb.append(children.get(i).toString(prefix + (isTail ? "    " : "│   "), false));
            }
            if (!children.isEmpty()) {
                sb.append(children.get(children.size() - 1).toString(prefix + (isTail ? "    " : "│   "), true));
            }
        }
        return sb.toString();
    }
}