package Interfaces;

public class SymbolTableEntry {
    public String type; // e.g., num, text, void
    public String kind; // e.g., variable, function
    public int scopeLevel;
    public String uniqueName;
    public String originalName;
    public SyntaxTreeNode declarationNode;

    public SymbolTableEntry(String originalName, String uniqueName, String type, int scopeLevel, SyntaxTreeNode declarationNode, String kind) {
        this.type = type;
        this.kind = kind;
        this.uniqueName = uniqueName;
        this.scopeLevel = scopeLevel;
        this.originalName = originalName;
        this.declarationNode = declarationNode;
    }
}
