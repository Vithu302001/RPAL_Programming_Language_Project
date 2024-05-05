package CSE_Machine;

import Symbols.*;
import java.util.ArrayList;
import java.util.List;

import Parser_RPAL.AST_Node;
import Parser_RPAL.Node_Type;

public class CSEMachineFactory {
    private E e0 = new E(0);
    private int i = 1;
    private int j = 0;

    public CSEMachineFactory() {

    }

    public Symbol getSymbol(AST_Node node) {
        switch (node.value) {
            // unary operators
            case "not":
            case "neg":
                return new Uop(node.value);
            // binary operators
            case "+":
            case "-":
            case "*":
            case "/":
            case "**":
            case "&":
            case "or":
            case "eq":
            case "ne":
            case "ls":
            case "le":
            case "gr":
            case "ge":
            case "aug":
                return new Bop(node.value);
            // gamma
            case "gamma":
                return new Gamma();
            // tau
            case "tau":
                return new Tau(node.children.size());
            // ystar
            case "<Ystar>":
                return new Ystar(); // need to check!!!!!!!!!!!!
            // operands <ID:>, <INT:>, <STR:>, <nil>, <true>, <false>, <dummy>
            default:
                if (node.type.equals(Node_Type.IDENTIFIER)) {
                    return new Id(node.value);
                } else if (node.type.equals(Node_Type.INTEGER)) {
                    return new Int(node.value);
                } else if (node.type.equals(Node_Type.IDENTIFIER)) {
                    return new Str(node.value);
                } else if (node.type.equals(Node_Type.IDENTIFIER)) {
                    return new Tup();
                } else if (node.type.equals(Node_Type.IDENTIFIER)) {
                    return new Bool("true");
                } else if (node.type.equals(Node_Type.IDENTIFIER)) {
                    return new Bool("false");
                } else if (node.type.equals(Node_Type.IDENTIFIER)) {
                    return new Dummy();
                } else {
                    System.out.println("Err node: " + node.value);
                    return new Err();
                }
        }
    }

    public B getB(AST_Node node) {
        B b = new B();
        b.symbols = this.getPreOrderTraverse(node);
        return b;
    }

    public Lambda getLambda(AST_Node node) {
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

    private ArrayList<Symbol> getPreOrderTraverse(AST_Node node) {
        ArrayList<Symbol> symbols = new ArrayList<Symbol>();
        if ("lambda".equals(node.value)) {
            symbols.add(this.getLambda(node));
        } else if ("->".equals(node.value)) {
            symbols.add(this.getDelta(node.children.get(1)));
            symbols.add(this.getDelta(node.children.get(0)));
            symbols.add(new Beta());
            symbols.add(this.getB(node.children.get(2)));
        } else {
            symbols.add(this.getSymbol(node));
            for (int i = node.children.size() - 1; i >= 0; i--) {
                symbols.addAll(this.getPreOrderTraverse(node.children.get(i)));
            }
        }
        return symbols;
    }

    public Delta getDelta(AST_Node node) {
        Delta delta = new Delta(this.j++);
        delta.symbols = this.getPreOrderTraverse(node);
        return delta;
    }

    public ArrayList<Symbol> getControl(List<AST_Node> AST) {
        ArrayList<Symbol> control = new ArrayList<Symbol>();
        control.add(this.e0);
        control.add(this.getDelta(AST.get(0)));
        return control;
    }

    public ArrayList<Symbol> getStack() {
        ArrayList<Symbol> stack = new ArrayList<Symbol>();
        stack.add(this.e0);
        return stack;
    }

    public ArrayList<E> getEnvironment() {
        ArrayList<E> environment = new ArrayList<E>();
        environment.add(this.e0);
        return environment;
    }

    public CSEMachine getCSEMachine(List<AST_Node> AST) {
        return new CSEMachine(this.getControl(AST), this.getStack(), this.getEnvironment());
    }
}
