package Utils;
import java.util.List;

import Interfaces.Token;
import Interfaces.TokenType;

public class XMLGenerator {
    private final List<Token> tokens;

    public XMLGenerator(List<Token> tokens) {
        this.tokens = tokens;
    }

    public String generateXML() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<TOKENSTREAM>\n");
        for (Token token : tokens) {
            if (token.type == TokenType.NULLTYPE) {
                continue;
            }
            xml.append("  <TOK>\n");
            xml.append("    <ID>").append(token.uuid).append("</ID>\n");
            xml.append("    <CLASS>").append(token.type).append("</CLASS>\n");
            xml.append("    <WORD>").append(token.Value).append("</WORD>\n");
            xml.append("  </TOK>\n");
        }
        xml.append("</TOKENSTREAM>\n");
        return xml.toString();
    }
}
