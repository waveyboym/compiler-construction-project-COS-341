package Utils;

import Interfaces.ParseNode;
import Interfaces.Token;
import Interfaces.TokenType;
import java.util.List;

public class XMLGenerator {
    public static String generateLEXERXML(List<Token> tokens) {
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

    public static String generatePARSERXML(ParseNode parseTree) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<PARSETREE>\n");
        xml.append(parseTree.toXML(" "));
        xml.append("</PARSETREE>\n");
        return xml.toString();
    }
}
