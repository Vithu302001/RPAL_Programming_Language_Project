import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import LexAnalyser.LexAnalyser;
import LexAnalyser.Token;
import Parser_RPAL.AST_Node;
import Parser_RPAL.RPAL_parser;
import Standatizer.Standatizer;

public class myrpal {

    public static void main(String[] args) {
        // Reading inputs from command line
        String[] arguments = args;

        if (arguments.length >= 1) {
            // Input file from the command line argument
            String input_file = arguments[0];
            LexAnalyser lexicalAnalyser = new LexAnalyser(input_file + ".txt");
            List<Token> tokens = lexicalAnalyser.scanner();

            RPAL_parser parser = new RPAL_parser(tokens);

            // Instantiate CSE machine
            List<AST_Node> AST = parser.get_AST();
            if (arguments.length == 2) {
                if (arguments[1].equals("-ast")) {
                    parser.printTree(AST.get(0), 0);
                } else if (arguments[1].equals("-st")) {
                    try {
                        Standatizer standatizer = new Standatizer();
                        standatizer.standardizeAST(AST.get(0));
                        // System.out.println(AST.get(0).value);
                        standatizer.printTree(AST.get(0), 0);

                    } catch (Exception e) {
                        System.out
                                .println("\n!! Standatizing the tree went wrong! ");
                        e.printStackTrace();

                    }
                } else {
                    System.out.println("Invalid Command!");
                }
            } else {
                System.out.println("original output");
            }
        } else {
            System.out.println("Invalid Command!");
        }
    }
}