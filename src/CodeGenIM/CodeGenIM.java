package CodeGenIM;

import Interfaces.ParseNode;
import Utils.Scope;

public class CodeGenIM {
    private Scope globalScope;
    private ParseNode pt;
    private int variableCounter = 0;
    public CodeGenIM() {
    }

    public CodeGenIM(Scope globalScope, ParseNode pt){
        this.globalScope = globalScope;
        this.pt = pt;
    }

    public String generateCode(){
        return "IM Code";
    }

    private String genNum(ParseNode node){
        // v = getvalue(num)
        // [place := v]
        ++variableCounter;
        return "t" + variableCounter + ":=" + getvalue(node.token.Value);
    }

    private String getvalue(String number){
        return number;
    }
}
