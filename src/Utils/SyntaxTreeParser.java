package Utils;

import org.w3c.dom.*;
import java.io.File;
import Interfaces.TokenType;
import Interfaces.SyntaxTreeNode;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class SyntaxTreeParser {
    public SyntaxTreeNode parse(String xmlFilePath) {
        try {
            // Initialize XML parser
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = builder.parse(new File(xmlFilePath));
            doc.getDocumentElement().normalize();

            // Get the root element (PARSETREE)
            Element rootElement = doc.getDocumentElement();

            if (!rootElement.getTagName().equals("PARSETREE")) {
                throw new Exception("Invalid syntax tree XML file.");
            }

            // Parse the PROG element
            NodeList childNodes = rootElement.getChildNodes();

            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element progElement = (Element) node;

                    return parseNode(progElement);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private SyntaxTreeNode parseNode(Element element) {
        TokenType symbol = TokenType.valueOf(element.getTagName());
        SyntaxTreeNode treeNode = new SyntaxTreeNode(symbol);

        boolean hasChildren = false;
        NodeList childNodes = element.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                hasChildren = true;

                Element childElement = (Element) node;
                String childTag = childElement.getTagName();

                if (childTag.equals("ID")) {
                    String id = childElement.getTextContent();
                    treeNode.id = id;
                } else if (childTag.equals("VALUE")) {
                    String value = childElement.getTextContent();
                    treeNode.value = value;
                } else {
                    // Recursive call for other elements
                    SyntaxTreeNode childTreeNode = parseNode(childElement);
                    treeNode.addChild(childTreeNode);
                }
            }
        }

        // Handle text nodes (e.g., whitespace)
        if (!hasChildren && element.getTextContent() != null && !element.getTextContent().trim().isEmpty()) {
            treeNode.value = element.getTextContent().trim();
        }

        return treeNode;
    }
}
