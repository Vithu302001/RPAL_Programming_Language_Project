
import java.util.List;
import CSE_Machine.CSEMachine;
import CSE_Machine.CSEMachineFactory;
import LexAnalyser.LexAnalyser;
import LexAnalyser.Token;
import Parser_RPAL.AST_Node;
import Parser_RPAL.RPAL_parser;
import Standatizer.Standatizer;

public class myrpal {

    public static void main(String[] args) {
        
        String[] arguments = args;  // Reading inputs from command line

        if (arguments.length >= 1) {
            // get the input file name from the command line arguments
            String input_file = arguments[0];
            
            //here if argument only contain filename then file must be in the current directory
            LexAnalyser lexicalAnalyser = new LexAnalyser(input_file + ".txt"); 
            List<Token> tokens = lexicalAnalyser.scanner();

            //run parser and get AST from that.
            RPAL_parser parser = new RPAL_parser(tokens);
            List<AST_Node> AST = parser.get_AST();


            if (arguments.length == 2) {
                //only print AST tree 
                if (arguments[1].equals("-ast")) {
                    parser.printTree(AST.get(0), 0);

                //only print Standartized tree
                } else if (arguments[1].equals("-st")) { 
                    try {

                        Standatizer standatizer = new Standatizer();
                        standatizer.standardizeAST(AST.get(0));
                        // System.out.println(AST.get(0).value);
                        standatizer.printTree(AST.get(0), 0);

                    } catch (Exception e) {
                        System.out
                                .println("\n!! Standatizing the tree went wrong! ");
                        //e.printStackTrace();

                    }
                } else {
                    System.out.println("Invalid Command!");
                }
            } else {
                // System.out.println("original output");
                try {

                    Standatizer standatizer = new Standatizer();
                    standatizer.standardizeAST(AST.get(0));

                    CSEMachineFactory CSE_Machine = new CSEMachineFactory(); 
                    CSEMachine machine = CSE_Machine.initialize_CSEMachine(AST); // create cse machine
                    
                    String computed_output = machine.compute_Answer();
                    System.out.println(computed_output);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } else {
            System.out.println("Invalid Command!");
        }
    }
}