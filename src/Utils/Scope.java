package Utils;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import Interfaces.SymbolTableEntry;

public class Scope {
    public Scope parent;
    public int scopeLevel;
    public String scopeName;
    public Map<String, SymbolTableEntry> symbols = new HashMap<>();
    public List<Scope> childScopes = new ArrayList<>();

    public Scope(Scope parent, String scopeName, int scopeLevel) {
        this.parent = parent;
        this.scopeName = scopeName;
        this.scopeLevel = scopeLevel;
        if (parent != null) {
            parent.childScopes.add(this);
        }
    }

    public boolean containsInCurrentScope(String name) {
        return symbols.containsKey(name);
    }

    public Scope getChildScope(String name) {
        for (Scope child : childScopes) {
            if (child.scopeName.equals(name)) {
                return child;
            }
        }
        return null;
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

    public SymbolTableEntry lookupFunction(String name) {
        SymbolTableEntry entry = symbols.get(name);
        if (entry != null && entry.kind.equals("function")) {
            return entry;
        } else if (parent != null) {
            return parent.lookupFunction(name);
        }
        return null;
    }

    public void addSymbol(SymbolTableEntry entry) {
        symbols.put(entry.originalName, entry);
    }

    public boolean hasSiblingScope(String name) {
        if (parent != null) {
            for (Scope sibling : parent.childScopes) {
                if (sibling != this && sibling.scopeName.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }
}