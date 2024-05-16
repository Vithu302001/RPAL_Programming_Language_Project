package CSE_Machine;

import Symbols.*;
import java.util.ArrayList;
import java.util.List;

import Parser_RPAL.AST_Node;
import Parser_RPAL.Node_Type;

public class CSEMachineFactory {
    private Environment e0 = new Environment(0);
    private int i = 1;
    private int j = 0;

    public CSEMachineFactory() {

    }

    public Symbol getSymbol(AST_Node node) {
        switch (node.value) {

            // for unary operators
            case "not":
            case "neg":
                return new Uop(node.value);
                
            // for binary operators
            case "+":
            case "-":
            case "*":
            case "/":
            case "**":
            case "&":
            case "or":
            case "gr":
            case "ge":
            case "ls":
            case "le":
            case "eq":
            case "ne":
            case "aug":
                return new Binary_Op(node.value);
            // gamma
            case "gamma":
                return new Gamma();
            // tau
            case "tau":
                return new Tau(node.children.size());
            // ystar
            case "Ystar":
                return new Ystar(); // need to check!!!!!!!!!!!!

            // if operands in type of IDETIFIER, INTEGER, STRING, NIL, TRUE, FALSE, DUMMY
            default:
                if (node.type.equals(Node_Type.IDENTIFIER)) {
                    return new Id(node.value);
                } else if (node.type.equals(Node_Type.STRING)) {
                    return new Str(node.value);
                } else if (node.type.equals(Node_Type.INTEGER)) {
                    return new Int(node.value);
                } else if (node.type.equals(Node_Type.NIL)) {
                    return new Tup();
                } else if (node.type.equals(Node_Type.T_FALSE)) {
                    return new Bool("false");
                } else if (node.type.equals(Node_Type.T_TRUE)) {
                    return new Bool("true");
                } else if (node.type.equals(Node_Type.DUMMY)) {
                    return new Dummy();
                } else {
                    System.out.println("Error node: " + node.value);
                    return new Error_Msg();
                }
        }
    }

    public B get_B(AST_Node node) {
        B Sym_B = new B();
        Sym_B.symbols = this.preOrder_Traversal(node);
        return Sym_B;
    }

    public Lambda get_Lambda(AST_Node node) {
        Lambda lambda = new Lambda(this.i++);

        lambda.setDelta(this.getDelta(node.children.get(0)));
        if (",".equals(node.children.get(node.children.size() - 1).value)) {
            for (int i = node.children.get(node.children.size() - 1).children.size() - 1; i >= 0; i--) {
                lambda.identifiers.add(new Id(node.children.get(node.children.size() - 1).children.get(i).value));
            }
        } else {
            lambda.identifiers.add(
                    new Id(node.children.get(node.children.size() - 1).value));
        }
        return lambda;
    }

    private ArrayList<Symbol> preOrder_Traversal(AST_Node node) {
        ArrayList<Symbol> symbols = new ArrayList<Symbol>();
        if ("lambda".equals(node.value)) {
            symbols.add(this.get_Lambda(node));
        } else if ("->".equals(node.value)) {
            symbols.add(this.getDelta(node.children.get(1)));
            symbols.add(this.getDelta(node.children.get(0)));
            symbols.add(new Beta());
            symbols.add(this.get_B(node.children.get(2)));
        } else {
            symbols.add(this.getSymbol(node));
            for (int i = node.children.size() - 1; i >= 0; i--) {
                symbols.addAll(this.preOrder_Traversal(node.children.get(i)));
            }
        }
        return symbols;
    }

    public Delta getDelta(AST_Node node) {
        Delta delta = new Delta(this.j++);
        delta.symbols = this.preOrder_Traversal(node);
        return delta;
    }

    public ArrayList<Symbol> initialize_Control_Struct(List<AST_Node> AST) {
        ArrayList<Symbol> control = new ArrayList<Symbol>();
        control.add(this.e0);
        control.add(this.getDelta(AST.get(0)));
        return control;
    }

    public ArrayList<Symbol> initialize_Stack() {
        ArrayList<Symbol> stack = new ArrayList<Symbol>();
        stack.add(this.e0);
        return stack;
    }

    public ArrayList<Environment> getEnvironment() {
        ArrayList<Environment> environment = new ArrayList<Environment>();
        environment.add(this.e0);
        return environment;
    }

    public CSEMachine initialize_CSEMachine(List<AST_Node> AST) {
        return new CSEMachine(this.initialize_Control_Struct(AST), this.initialize_Stack(), this.getEnvironment());
    }
}
