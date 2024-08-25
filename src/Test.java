import java.util.List;

public class Test {
    private static int testsPassed = 0;
    private static int testsFailed = 0;
    private static int totalTests = 0;
    public static void main(String[] args) {
        System.out.println("Running tests...");
        testTokenClass();
        testLexerClass();

        System.out.println("Tests passed: " + testsPassed + "/" + totalTests);
        System.out.println("Tests failed: " + testsFailed + "/" + totalTests);
        System.out.println("Tests total: " + totalTests);
    }

    private static void testTokenClass(){
        Token token = new Token(TokenType.INPUT, "test.txt", "1", 0, "test");
        if(token.type == TokenType.INPUT && token.fileName.equals("test.txt") 
        && token.Line.equals("1") && token.Column == 0 && token.Value.equals("test")){
            System.out.println("[PASS] Token class test passed");
            testsPassed++;
        } else {
            System.out.println("[FAIL] Token class test failed");
            testsFailed++;
        }
        totalTests++;
    }

    public static void printTokens(List<Token> tokens) {
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    private static void testLexerClass(){
        String[] programs = {
            // Program 1: Simple global variable assignment and print
            "main num V_x, text V_y begin V_x < input ; V_y = \"Hello\" ; print V_y ; end",
            
            // Program 2: Function call with global variables and conditionals
            "main num V_x, num V_y begin V_x = 5 ; V_y = 10 ; if eq(V_x, V_y) then halt else F_addition(V_x, V_y, V_x) ; print V_x ; end " +
            "num F_addition(num V_a, num V_b, num V_c) { num V_sum, num V_dummy1, num V_dummy2, " +
            "begin V_sum = add(V_a, V_b) ; V_c = V_sum ; end }",
        
            // Program 3: Nested operations and branch statement
            "main num V_z begin V_z = mul(add(3, 2), 7) ; if grt(V_z, 10) then print V_z else halt ; end",
        
            //Program 4: Function with a call and a branch inside
            "main num V_result begin V_result = 0 ; F_compute(2, 4, V_result) ; if grt(V_result, 0) then print V_result else halt ; end " +
            "num F_compute(num V_x, num V_y, num V_z) { num V_sum, num V_prod, num V_dummy, " +
            "begin V_sum = add(V_x, V_y) ; V_z = mul(V_sum, 2) ; end }",
        
            // Program 5: Skip and Halt commands
            "main begin skip ; halt ; end"
        };

        String [][] expectedValues = {
            {
                "main", "num", "V_x", ",", "text", "V_y", "begin", "V_x", "<", "input", ";", "V_y", "=", "Hello", ";", "print", "V_y", ";", "end"
            },
            {
                "main", "num", "V_x", ",", "num", "V_y", "begin", "V_x", "=", "5", ";", "V_y", "=", "10", ";", "if", "eq", "(", "V_x", ",", "V_y", ")", "then", "halt", "else", "F_addition", "(", "V_x", ",", "V_y", ",", "V_x", ")", ";", "print", "V_x", ";", "end",
                "num", "F_addition", "(", "num", "V_a", ",", "num", "V_b", ",", "num", "V_c", ")", "{", "num", "V_sum", ",", "num", "V_dummy1", ",", "num", "V_dummy2", ",", "begin", "V_sum", "=", "add", "(", "V_a", ",", "V_b", ")", ";", "V_c", "=", "V_sum", ";", "end", "}"
            },
            {
                "main", "num", "V_z", "begin", "V_z", "=", "mul", "(", "add", "(", "3", ",", "2", ")", ",", "7", ")", ";", "if", "grt", "(", "V_z", ",", "10", ")", "then", "print", "V_z", "else", "halt", ";", "end"
            },
            {
                "main", "num", "V_result", "begin", "V_result", "=", "0", ";", "F_compute", "(", "2", ",", "4", ",", "V_result", ")", ";", "if", "grt", "(", "V_result", ",", "0", ")", "then", "print", "V_result", "else", "halt", ";", "end",
                "num", "F_compute", "(", "num", "V_x", ",", "num", "V_y", ",", "num", "V_z", ")", "{", "num", "V_sum", ",", "num", "V_prod", ",", "num", "V_dummy", ",", "begin", "V_sum", "=", "add", "(", "V_x", ",", "V_y", ")", ";", "V_z", "=", "mul", "(", "V_sum", ",", "2", ")", ";", "end", "}"
            },
            {
                "main", "begin", "skip", ";", "halt", ";", "end"
            },
        };

        for (int i = 0; i < programs.length; ++i) {
            try {
                Lexer lexer = new Lexer(programs[i], "test.txt");
                List<Token> tokens = lexer.scanTokens();

                int j = 0;

                for (Token token : tokens) {
                    if(!token.Value.equals(expectedValues[i][j])){
                        System.out.println("[FAIL] Test failed on expected: " + expectedValues[i][j] + " actual: " + token.Value);
                        System.out.println("Row: " + i + " and Column: " + j);
                        break;
                    }
                    ++j;
                }
                
                if(j == tokens.size()){
                    System.out.println("[PASS] Lexer test " + (i + 1) + " passed");
                    testsPassed++;
                } else {
                    System.out.println("[FAIL] Lexer test " + (i + 1) + " failed");
                    testsFailed++;
                }
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
            totalTests++;
        }
    }
}