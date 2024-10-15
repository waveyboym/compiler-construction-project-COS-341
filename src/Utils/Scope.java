package Utils;

import java.util.Map;
import java.util.HashMap;

import Interfaces.SymbolTableEntry;

public class Scope {
    public Scope parent;
    public int scopeLevel;
    public String scopeName;
    public Map<String, SymbolTableEntry> symbols = new HashMap<>();

    public Scope(Scope parent, String scopeName, int scopeLevel) {
        this.parent = parent;
        this.scopeName = scopeName;
        this.scopeLevel = scopeLevel;
    }

    public boolean containsInCurrentScope(String name) {
        return symbols.containsKey(name);
    }

    public SymbolTableEntry lookup(String name) {
        SymbolTableEntry entry = symbols.get(name);

        if (entry != null) {
            return entry;
        } else if (parent != null) {
            return parent.lookup(name);
        }

        return null;
    }

    public void addSymbol(SymbolTableEntry entry) {
        symbols.put(entry.originalName, entry);
    }
}
