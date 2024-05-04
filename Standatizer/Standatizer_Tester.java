package Standatizer;

import LexAnalyser.LexAnalyser;
import LexAnalyser.Token;
import Parser_RPAL.AST_Node;
import Parser_RPAL.RPAL_parser;
import java.util.List;

public class Standatizer_Tester {
    public static void main(String[] args) {
        LexAnalyser lexicalAnalyser = new LexAnalyser("File1.txt");
        List<Token> tokens = lexicalAnalyser.scanner();

        RPAL_parser parser = new RPAL_parser(tokens);

        try {
            List<AST_Node> AST = parser.get_AST();
            Standatizer standatizer = new Standatizer();
            standatizer.standardizeAST(AST.get(0));
            // System.out.println(AST.get(0).value);
            standatizer.printTree(AST.get(0), 0);

        } catch (Exception e) {
            System.out
                    .println("\n!! Standatizing the tree went wrong! ");
            System.out.println(e.getLocalizedMessage());

        }
    }

}
