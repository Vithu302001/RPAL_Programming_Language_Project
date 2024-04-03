package Parser_RPAL;

import java.util.ArrayList;
import java.util.List;

import LexAnalyser.LexAnalyser;
import LexAnalyser.Token;
import LexAnalyser.TokenType;

public class Parser_Tester {
    public static void main(String[] args) {

        LexAnalyser lexicalAnalyser = new LexAnalyser("File1.txt");
        List<Token> tokens = lexicalAnalyser.scanner();

        RPAL_parser parser = new RPAL_parser(tokens);
        try {
            List<AST_Node> AST = parser.get_AST();
            parser.printTree(AST.get(0), 0);

        } catch (Exception e) {
            System.out
                    .println("\n!! Your code have issues.Resolve it and try to compile again !! ");
            // for (Token token : tokens) {
            // System.out.println(token.type + ": " + token.value + " (Line: " + token.line
            // + ")");
        }

        // }
        /*
         * } catch (Exception e) {
         * System.out.println(AST.isEmpty());
         * for (AST_Node ast : AST) {
         * System.out.println(ast.value);
         * }
         * }
         */

    }

}
