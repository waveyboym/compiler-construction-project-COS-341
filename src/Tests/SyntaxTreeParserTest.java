package Tests;

import Interfaces.TokenType;
import Utils.SyntaxTreeParser;
import Interfaces.SyntaxTreeNode;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class SyntaxTreeParserTest {

    private static int totalTests = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("Running SyntaxTreeParser tests...");

        testValidSyntaxTree();
        testInvalidSyntaxTree();
        testMissingElements();
        testUnexpectedTag();

        System.out.println("Tests passed: " + testsPassed + "/" + totalTests);
        System.out.println("Tests failed: " + testsFailed + "/" + totalTests);
        System.out.println("Total tests: " + totalTests);
    }

    /**
     * Test parsing a valid syntax tree XML.
     */
    public static void testValidSyntaxTree() {
        String testName = "testValidSyntaxTree";
        totalTests++;

        // Create a sample XML content representing a valid syntax tree
        String xmlContent = "<PARSETREE>\n" +
                "  <PROG>\n" +
                "    <MAIN>\n" +
                "      <BEGIN>\n" +
                "        <PRINT>\n" +
                "          <VALUE>Hello, World!</VALUE>\n" +
                "        </PRINT>\n" +
                "      </BEGIN>\n" +
                "    </MAIN>\n" +
                "  </PROG>\n" +
                "</PARSETREE>";

        try {
            File tempFile = File.createTempFile("syntax_tree_valid_test", ".xml");

            BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
            bw.write(xmlContent);
            bw.close();

            // Parse the XML file using SyntaxTreeParser
            SyntaxTreeParser parser = new SyntaxTreeParser();
            SyntaxTreeNode root = parser.parse(tempFile.getAbsolutePath());

            // Validate the parsed syntax tree
            if (root != null && root.symbol == TokenType.PROG) {
                System.out.println("\u001B[32m[PASS]\u001B[0m " + testName);
                testsPassed++;
            } else {
                System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Root is null or has incorrect symbol.");
                testsFailed++;
            }

            tempFile.delete();
        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }

    /**
     * Test parsing an invalid syntax tree XML.
     */
    public static void testInvalidSyntaxTree() {
        String testName = "testInvalidSyntaxTree";
        totalTests++;

        // Create a sample XML content representing an invalid syntax tree (malformed
        // XML)
        String xmlContent = "<PARSETREE>\n" +
                "  <PROG>\n" +
                "    <MAIN>\n" +
                "      <BEGIN>\n" +
                "        <PRINT>\n" +
                "          <VALUE>Hello, World!</VALUE>\n" +
                "        </PRINT>\n" +
                "      <!-- Missing closing tags -->\n";

        try {
            File tempFile = File.createTempFile("syntax_tree_invalid_test", ".xml");

            BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
            bw.write(xmlContent);
            bw.close();

            SyntaxTreeParser parser = new SyntaxTreeParser();
            SyntaxTreeNode root = parser.parse(tempFile.getAbsolutePath());

            // Since the XML is invalid, we expect the parser to return null or throw an
            // exception
            if (root == null) {
                System.out.println("\u001B[32m[PASS]\u001B[0m " + testName);
                testsPassed++;
            } else {
                System.out
                        .println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Parser should not return a valid tree.");
                testsFailed++;
            }

            tempFile.delete();
        } catch (Exception e) {
            // If an exception occurs, that's expected for invalid XML
            System.out.println("\u001B[32m[PASS]\u001B[0m " + testName + ": Exception occurred as expected.");
            testsPassed++;
        }
    }

    /**
     * Test parsing a syntax tree XML with missing elements.
     */
    public static void testMissingElements() {
        String testName = "testMissingElements";
        totalTests++;

        // Create a sample XML content with missing required elements (e.g., missing
        // <PROG>)
        String xmlContent = "<PARSETREE>\n" +
                "  <!-- Missing PROG element -->\n" +
                "  <MAIN>\n" +
                "    <BEGIN>\n" +
                "      <PRINT>\n" +
                "        <VALUE>Hello, World!</VALUE>\n" +
                "      </PRINT>\n" +
                "    </BEGIN>\n" +
                "  </MAIN>\n" +
                "</PARSETREE>";

        try {
            File tempFile = File.createTempFile("syntax_tree_missing_elements_test", ".xml");

            BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
            bw.write(xmlContent);
            bw.close();

            SyntaxTreeParser parser = new SyntaxTreeParser();
            SyntaxTreeNode root = parser.parse(tempFile.getAbsolutePath());

            System.out.println(root.symbol);

            // Check if the missing elements are handled
            if (root != null && root.symbol == TokenType.PROG) {
                System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Root is null or has incorrect symbol.");
                testsFailed++;
            } else {
                System.out.println("\u001B[32m[PASS]\u001B[0m " + testName + ": Missing elements handled.");
                testsPassed++;
            }

            tempFile.delete();
        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }

    /**
     * Test parsing a syntax tree XML with unexpected tags.
     */
    public static void testUnexpectedTag() {
        String testName = "testUnexpectedTag";
        totalTests++;

        // Create a sample XML content with an unexpected tag
        String xmlContent = "<PARSETREE>\n" +
                "  <PROG>\n" +
                "    <MAIN>\n" +
                "      <BEGIN>\n" +
                "        <unexpectedTag>\n" + // This tag is not expected
                "          <VALUE>Hello, World!</VALUE>\n" +
                "        </unexpectedTag>\n" +
                "      </BEGIN>\n" +
                "    </MAIN>\n" +
                "  </PROG>\n" +
                "</PARSETREE>";

        try {
            File tempFile = File.createTempFile("syntax_tree_unexpected_tag_test", ".xml");

            BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
            bw.write(xmlContent);
            bw.close();

            SyntaxTreeParser parser = new SyntaxTreeParser();
            SyntaxTreeNode root = parser.parse(tempFile.getAbsolutePath());

            // Since the XML has an unexpected tag, we need to see how the parser handles it
            if (root == null) {
                boolean hasUnexpectedTag = checkForSymbol(root, TokenType.valueOf("unexpectedTag"));

                if (hasUnexpectedTag) {
                    System.out.println(
                            "\u001B[31m[FAIL]\u001B[0m " + testName + ": Parser should not recognize 'unexpectedTag'.");
                    testsFailed++;
                } else {
                    System.out.println("\u001B[32m[PASS]\u001B[0m " + testName + ": Parser ignored unexpected tag.");
                    testsPassed++;
                }
            } else {
                System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Root is null.");
                testsFailed++;
            }

            tempFile.delete();
        } catch (IllegalArgumentException e) {
            // If an exception occurs due to undefined TokenType, it's acceptable
            System.out.println("\u001B[32m[PASS]\u001B[0m " + testName
                    + ": Exception occurred as expected for undefined TokenType.");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("\u001B[31m[FAIL]\u001B[0m " + testName + ": Unexpected exception occurred.");
            e.printStackTrace();
            testsFailed++;
        }
    }

    // Helper method to check if a symbol exists in the syntax tree
    public static boolean checkForSymbol(SyntaxTreeNode node, TokenType symbol) {
        if (node.symbol == symbol) {
            return true;
        }

        for (SyntaxTreeNode child : node.children) {
            if (checkForSymbol(child, symbol)) {
                return true;
            }
        }

        return false;
    }

    // Helper method to print the syntax tree (if needed)
    public static void printSyntaxTree(SyntaxTreeNode node, int level) {
        if (node == null)
            return;

        String indent = "  ".repeat(level);

        System.out.println(indent + node.symbol +
                (node.id != null ? " ID: " + node.id : "") +
                (node.value != null ? " Value: " + node.value : ""));

        for (SyntaxTreeNode child : node.children) {
            printSyntaxTree(child, level + 1);
        }
    }
}
