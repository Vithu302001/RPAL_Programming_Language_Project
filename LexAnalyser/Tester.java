package LexAnalyser;

import java.util.List;

public class Tester {
    public static void main(String[] args) {
        LexAnalyser lexicalAnalyser = new LexAnalyser("File1.txt");
        List<Token> tokens = lexicalAnalyser.scanner();

        for (Token token : tokens) {
            System.out.println(token.type + ": " + token.value + " (Line: " + token.line + ")");
        }
    }
}
