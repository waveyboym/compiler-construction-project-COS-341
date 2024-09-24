package Utils;

import Interfaces.*;
import java.io.File;
import java.util.Map;
import org.w3c.dom.*;
import java.util.HashMap;
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

            // Parse the root element
            Element rootElement = doc.getDocumentElement();

            if (!rootElement.getTagName().equals("SYNTREE")) {
                throw new Exception("Invalid syntax tree XML file.");
            }

            // Parse ROOT node
            Element rootNodeElement = (Element) rootElement.getElementsByTagName("ROOT").item(0);
            SyntaxTreeNode rootNode = parseInnerNode(rootNodeElement);

            // Build a map of ID to nodes
            Map<String, SyntaxTreeNode> nodeMap = new HashMap<>();
            nodeMap.put(rootNode.id, rootNode);

            // Parse INNERNODES
            NodeList innerNodes = rootElement.getElementsByTagName("IN");

            for (int i = 0; i < innerNodes.getLength(); i++) {
                Element innerNodeElement = (Element) innerNodes.item(i);
                InnerNode innerNode = parseInnerNode(innerNodeElement);
                nodeMap.put(innerNode.id, innerNode);
            }

            // Parse LEAFNODES
            NodeList leafNodes = rootElement.getElementsByTagName("LEAF");

            for (int i = 0; i < leafNodes.getLength(); i++) {
                Element leafNodeElement = (Element) leafNodes.item(i);
                LeafNode leafNode = parseLeafNode(leafNodeElement);
                nodeMap.put(leafNode.id, leafNode);
            }

            // Build the tree by connecting parent and children
            for (SyntaxTreeNode node : nodeMap.values()) {
                if (node instanceof InnerNode) {
                    InnerNode innerNode = (InnerNode) node;
                    Element element = findElementById(doc, innerNode.id);
                    NodeList childIds = ((Element) element.getElementsByTagName("CHILDREN").item(0)).getElementsByTagName("ID");
                    
                    for (int i = 0; i < childIds.getLength(); i++) {
                        String childId = childIds.item(i).getTextContent();
                        SyntaxTreeNode childNode = nodeMap.get(childId);
                        innerNode.addChild(childNode);
                    }
                }
            }

            return rootNode;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private InnerNode parseInnerNode(Element element) {
        String id = element.getElementsByTagName("UNID").item(0).getTextContent();
        TokenType symbol = TokenType.valueOf(element.getElementsByTagName("SYMB").item(0).getTextContent());
        return new InnerNode(id, symbol);
    }

    private LeafNode parseLeafNode(Element element) {
        String id = element.getElementsByTagName("UNID").item(0).getTextContent();
        Element terminalElement = (Element) element.getElementsByTagName("TERMINAL").item(0);

        // Parse token from TERMINAL element
        String tokenId = terminalElement.getElementsByTagName("ID").item(0).getTextContent();
        TokenType type = TokenType.valueOf(terminalElement.getElementsByTagName("CLASS").item(0).getTextContent());
        String word = terminalElement.getElementsByTagName("WORD").item(0).getTextContent();

        SyntaxToken token = new SyntaxToken(tokenId, type, word);
        return new LeafNode(id, token);
    }

    private Element findElementById(Document doc, String id) {
        NodeList nodes = doc.getElementsByTagName("*");

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node instanceof Element) {
                Element element = (Element) node;

                if (element.getElementsByTagName("UNID").getLength() > 0) {
                    String nodeId = element.getElementsByTagName("UNID").item(0).getTextContent();
                    if (nodeId == id) return element;
                }
            }
        }

        return null;
    }
}
