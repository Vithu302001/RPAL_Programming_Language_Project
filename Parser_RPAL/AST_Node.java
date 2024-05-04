package Parser_RPAL;

import java.util.ArrayList;

import LexAnalyser.Token;

public class AST_Node {
    public AST_Node parent = null;
    public String value;
    public Node_Type type;
    // public AST_Node left;
    // public AST_Node right;
    public boolean isStandartized = false;
    public ArrayList<AST_Node> children = new ArrayList<AST_Node>(); // to store childrens of the node.This is no longer
                                                                     // a binary tree.

    // public AST_Node(Token token, Node_Type type, AST_Node left, AST_Node right) {
    // this.value = token.value;
    // this.type = type;
    // // this.left = left;
    // // this.right = right;

    // }

    public AST_Node(Token token, Node_Type type) {
        this.value = token.value;
        this.type = type;
    }

    public void AddChildren(AST_Node child) {// helper method for adding children nodes to parent node
        this.children.add(child);
        child.parent = this;
    }
}
