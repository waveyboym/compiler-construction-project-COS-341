package Utils;

import java.util.List;

import Interfaces.Token;

public class Print {
     public static void printTokens(List<Token> tokens) {
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
