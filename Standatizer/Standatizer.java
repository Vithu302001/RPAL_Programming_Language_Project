package Standatizer;

import Parser_RPAL.AST_Node;
import Parser_RPAL.Node_Type;
import LexAnalyser.Token;
import LexAnalyser.TokenType;
import java.util.ArrayList;

import org.w3c.dom.Node;

public class Standatizer {

    AST_Node root_node;

    Standatizer() {

    }

    // Function to standardize the AST
    void standardizeAST(AST_Node node) {

        if (!node.isStandartized) {
            for (AST_Node child : node.children) {
                // System.out.println("trying to standatize: " + node.value);

                standardizeAST(child);
            }

            switch (node.type) {
                case LET:
                    Let_Standatizer(node);
                    // printTree(node, 0);
                    break;
                case WITHIN:
                    Within_Standatizer(node);
                    break;
                case FCN_FORM:
                    FcnForm_Standatizer(node);
                    // printTree(node, 0);
                    break;
                case AND:
                    And_Standatizer(node);
                    break;
                case WHERE:
                    Where_Standatizer(node);
                    break;
                case REC:
                    Rec_Standatizer(node);
                    break;
                case AT:
                    At_Standatizer(node);
                    break;
                case LAMBDA:
                    multi_param_function_Standatizer(node);
                    printTree(node, 0);
                    break;
                default:
                    // System.out.println("default case " + node.value);
                    break;
            }
            node.isStandartized = true;
        }

    }

    /*
     * LET
     *
     * let => gamma
     * / \ / \
     * = P lambda E
     * / \ / \
     * X E X P
     * 
     */

    void Let_Standatizer(AST_Node node) {
        // System.out.println("Let_Standatizer");
        AST_Node node_gamma = new AST_Node(new Token(null, "gamma"), Node_Type.GAMMA);
        AST_Node node_lambda = new AST_Node(new Token(null, "lambda"), Node_Type.LAMBDA);

        AST_Node P = node.children.get(0); // right childe index 0 and left child index 1.because in arraylist it saved
                                           // in reverse order.
        AST_Node X = node.children.get(1).children.get(1);
        AST_Node E = node.children.get(1).children.get(0);

        node_lambda.children.add(P);
        node_lambda.children.add(X);
        node_gamma.children.add(E);
        node_gamma.children.add(node_lambda);

        node.value = node_gamma.value;
        node.type = node_gamma.type;
        node.children = node_gamma.children;

    }

    /*
     * //within
     * 
     * within => =
     * / \ / \
     * = = X2 gamma
     * / \ / \ / \
     * X1 E1 X2 E2 lambda E1
     * / \
     * X1 E2
     */

    void Within_Standatizer(AST_Node node) {
        // System.out.println("Within_Standatizer");
        AST_Node node_gamma = new AST_Node(new Token(null, "gamma"), Node_Type.GAMMA);
        AST_Node node_lambda = new AST_Node(new Token(null, "lambda"), Node_Type.LAMBDA);
        AST_Node node_equal = new AST_Node(new Token(null, "="), Node_Type.EQUAL);

        AST_Node X1 = node.children.get(1).children.get(1);
        AST_Node E1 = node.children.get(1).children.get(0);

        AST_Node X2 = node.children.get(0).children.get(1);
        AST_Node E2 = node.children.get(0).children.get(0);

        node_lambda.children.add(E2);
        node_lambda.children.add(X1);

        node_gamma.children.add(E1);
        node_gamma.children.add(node_lambda);

        node_equal.children.add(node_gamma);
        node_equal.children.add(X2);

        node.value = node_equal.value;
        node.type = node_equal.type;
        node.children = node_equal.children;

    }

    /*
     * fcn_form
     * fcn_form =
     * / | \ / \
     * P V+ E => P +lambda
     * / \
     * V .E
     */

    void FcnForm_Standatizer(AST_Node node) {
        AST_Node node_equal = new AST_Node(new Token(null, "="), Node_Type.EQUAL);

        AST_Node E = node.children.get(0);
        AST_Node P = node.children.get(node.children.size() - 1);

        AST_Node temp = new AST_Node(new Token(null, "temp"), null);

        node_equal.children.add(temp);
        node_equal.children.add(P);

        AST_Node currNode = node_equal;

        for (int i = node.children.size() - 1; i >= 0; i--) {
            if (!((node.children.get(i).equals(P)) || (node.children.get(i).equals(E)))) {
                AST_Node node_lambda = new AST_Node(new Token(null, "lambda"), Node_Type.LAMBDA);
                AST_Node temp1 = new AST_Node(new Token(null, "temp1"), null);

                node_lambda.children.add(temp1);
                node_lambda.children.add(node.children.get(i));

                currNode.children.set(0, node_lambda);

                currNode = node_lambda;

            }
        }
        currNode.children.set(0, E);

        node.value = node_equal.value;
        node.type = node_equal.type;
        node.children = node_equal.children;

        // System.out.println("finished Fcn_Form standatize");
    }

    /*
     * and
     * 
     * and => =
     * | / \
     * =++ , tau
     * / \ | |
     * X E X++ E++
     * 
     */

    void And_Standatizer(AST_Node node) {
        AST_Node node_equal = new AST_Node(new Token(null, "="), Node_Type.EQUAL);
        AST_Node node_comma = new AST_Node(new Token(null, ","), Node_Type.COMMA);
        AST_Node node_tau = new AST_Node(new Token(null, "tau"), Node_Type.TAU);

        node_equal.children.add(node_tau);
        node_equal.children.add(node_comma);

        for (AST_Node equal_node : node.children) {
            AST_Node X = equal_node.children.get(1);
            AST_Node E = equal_node.children.get(0);

            node_comma.children.add(X);
            node_tau.children.add(E);
        }

        node.value = node_equal.value;
        node.type = node_equal.type;
        node.children = node_equal.children;
    }

    /*
     * where
     * 
     * gamma where
     * / \ / \
     * lambda E <= P =
     * / \ / \
     * X P X E
     * 
     */

    void Where_Standatizer(AST_Node node) {
        AST_Node node_gamma = new AST_Node(new Token(null, "gamma"), Node_Type.GAMMA);
        AST_Node node_lambda = new AST_Node(new Token(null, "lambda"), Node_Type.LAMBDA);

        AST_Node E = node.children.get(0).children.get(0);
        AST_Node X = node.children.get(0).children.get(1);

        AST_Node P = node.children.get(1);

        node_lambda.children.add(P);
        node_lambda.children.add(X);

        node_gamma.children.add(E);
        node_gamma.children.add(node_lambda);

        node.value = node_gamma.value;
        node.type = node_gamma.type;
        node.children = node_gamma.children;
    }

    /*
     * rec
     * 
     * rec => =
     * | / \
     * = X gamma
     * / \ / \
     * X E Ystar lambda
     * / \
     * X E
     */

    void Rec_Standatizer(AST_Node node) {
        AST_Node node_gamma = new AST_Node(new Token(null, "gamma"), Node_Type.GAMMA);
        AST_Node node_lambda = new AST_Node(new Token(null, "lambda"), Node_Type.LAMBDA);
        AST_Node Ystar = new AST_Node(new Token(null, "Ystar"), Node_Type.YSTAR);
        AST_Node node_equal = new AST_Node(new Token(null, "="), Node_Type.EQUAL);

        AST_Node X = node.children.get(0).children.get(1);
        AST_Node E = node.children.get(0).children.get(0);

        node_lambda.children.add(E);
        node_lambda.children.add(X);

        node_gamma.children.add(node_lambda);
        node_gamma.children.add(Ystar);

        node_equal.children.add(node_gamma);
        node_equal.children.add(X);

        node.value = node_equal.value;
        node.type = node_equal.type;
        node.children = node_equal.children;
    }

    /*
     * @
     * 
     * @ => gamma
     * / | \ / \
     * E1 N E2 gamma E2
     * / \
     * N E1
     */

    void At_Standatizer(AST_Node node) {
        AST_Node node_gamma_root = new AST_Node(new Token(null, "gamma"), Node_Type.GAMMA);
        AST_Node node_gamma = new AST_Node(new Token(null, "gamma"), Node_Type.GAMMA);

        AST_Node E1 = node.children.get(2);
        AST_Node N = node.children.get(1);
        AST_Node E2 = node.children.get(0);

        node_gamma.children.add(E1);
        node_gamma.children.add(N);

        node_gamma_root.children.add(E2);
        node_gamma_root.children.add(node_gamma);

        node.value = node_gamma_root.value;
        node.type = node_gamma_root.type;
        node.children = node_gamma_root.children;
    }

    void multi_param_function_Standatizer(AST_Node node) {

        System.out.println("standatizing multi param lambda");
        AST_Node node_lambda_top = new AST_Node(new Token(null, "lambda"), Node_Type.LAMBDA);

        AST_Node current_node = node_lambda_top;

        for (int i = node.children.size() - 1; i >= 1; i--) {
            AST_Node node_lambda = new AST_Node(new Token(null, "lambda"), Node_Type.LAMBDA);
            AST_Node temp = new AST_Node(new Token(null, "temp"), null);

            node_lambda.children.add(temp);
            node_lambda.children.add(node.children.get(i));

            current_node.children.add(node_lambda);
            current_node = node_lambda;
        }
        // System.out.println("here 1");
        current_node.children.set(0, node.children.get(0));
        // System.out.println("here 2");
        node.value = node_lambda_top.value;
        node.type = node_lambda_top.type;
        node.children = node_lambda_top.children;

        System.out.println("succesfully standatized lambda");

    }

    public void printTree(AST_Node node, int level) {
        StringBuilder AST_Tree_String_Format = new StringBuilder();
        for (int i = 0; i < level; i++) {
            AST_Tree_String_Format.append(".");
        }
        AST_Tree_String_Format.append(addStrings(node));
        System.out.println(AST_Tree_String_Format.toString());

        if (!node.children.isEmpty()) {
            for (int i = node.children.size() - 1; i >= 0; i--) {
                printTree(node.children.get(i), level + 1);
            }
        }
    }

    private String addStrings(AST_Node node) {
        switch (node.type) {
            case IDENTIFIER:
                return "<ID:" + node.value + ">";

            case INTEGER:
                return "<INT:" + node.value + ">";

            case STRING:
                return "<STR:" + node.value + ">";

            case T_TRUE:
                return "<" + node.value + ">";

            case T_FALSE:
                return "<" + node.value + ">";

            case NIL:
                return "<" + node.value + ">";

            case DUMMY:
                return "<" + node.value + ">";

            case FCN_FORM:
                return "function_form";

            default:
                return node.value;
        }
    }

}
