package Parser_RPAL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import LexAnalyser.Token;
import LexAnalyser.TokenType;

public class RPAL_parser {
    public List<Token> tokens;
    public List<AST_Node> stack; // stack to store AST.In final completion only one element will be there that is
                                 // root of AST

    public RPAL_parser(List<Token> tokens) {
        this.tokens = tokens;
        stack = new ArrayList<>();
    }

    private void Build_AST(Token token, Node_Type type, int noOfchildren) {

        // tries to implement first child second sibiling tree but failed;

        // System.out.println("in the build_AST method for token " + token.value);
        // AST_Node p = null;

        // for (int i = 0; i < noOfchildren; i++) {
        // AST_Node c = new AST_Node(null, null, null, null);
        // c.value = stack.remove(0).value;
        // c.right = p;
        // p = c;
        // }
        // stack.add(0, new AST_Node(token, type, p, null));

        // System.out.println("Build_AST called for " + token.value);!!!!!for verifying
        // purpose

        AST_Node parent = new AST_Node(token, type);
        for (int i = 0; i < noOfchildren; i++) {
            parent.AddChildren(stack.remove(stack.size() - 1));
        }
        stack.add(parent);
    }

    public List<AST_Node> get_AST() {
        tokens.add(new Token(TokenType.EndOfTokens, "EOF"));
        E();
        if (tokens.get(0).value.equals("EOF")) {
            return stack;
        } else {
            System.out.println("Parsing Failed!!!!");
            return null;
        }
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

    /*
     * Read() will create a single node if <IDENTIFIER>,<INTEGER>,<STRING> type
     * tokens and push into the stack.
     * else just consume token
     */

    private void Read(Token token, Node_Type type) {
        try {
            if (tokens.get(0).value.equals(token.value)) {
                if ((token.type.equals(TokenType.IDENTIFIER)) || (token.type.equals(TokenType.INTEGER))
                        || (token.type.equals(TokenType.STRING))) {
                    // System.out.println("In the read function if condition for token : " +
                    // token.value);
                    stack.add(new AST_Node(token, type));
                    tokens.remove(token);
                } else {
                    // System.out.println("In the read function else condition for token : " +
                    // token.value);

                    tokens.remove(token);
                }
            } else {
                System.out.println("Expected : " + token.value);
            }
        } catch (Exception e) {
            System.out.println("Error while Parsing at Line " + tokens.get(0).line + " \n" + e.getMessage());
        }
    }

    private void E() {
        // System.out.println(tokens.size());

        // System.out.println("in E()");
        switch (tokens.get(0).value) {
            case "let":
                Read(tokens.get(0), Node_Type.LET);// let

                D();// D
                if (!tokens.get(0).value.equals("in")) {
                    System.out.println("'in' expected after let");
                }
                Read(tokens.get(0), Node_Type.IN);// in
                E();

                // System.out.println("E -> 'let' D 'in' E");
                Build_AST(new Token(null, "let"), Node_Type.LET, 2);
                // System.out.println(stack.isEmpty());

                break;
            case "fn":
                Read(tokens.get(0), Node_Type.FN);
                // System.out.println(stack.isEmpty());

                int count_variable_1 = 1;
                Vb();
                while ((tokens.get(0).type.equals(TokenType.IDENTIFIER)) || (tokens.get(0).value.equals("("))) {
                    Vb();
                    count_variable_1++;
                }
                if (!tokens.get(0).value.equals(".")) {
                    System.out.println("'.' expected after");
                }
                Read(tokens.get(0), Node_Type.DOT);
                E();

                // System.out.println("E -> 'fn' Vb+ '.' E");
                Build_AST(new Token(null, "lambda"), Node_Type.LAMBDA, count_variable_1);
                // System.out.println(stack.isEmpty()); //for testing purposes

                break;
            default:
                Ew();
                // System.out.println("E -> Ew");
                break;
        }
    }

    private void Ew() {
        // System.out.println("in Ew()");

        T();
        if (tokens.get(0).value.equals("where")) {

            Read(tokens.get(0), Node_Type.WHERE);
            Dr();

            // System.out.println("Ew -> T 'where' Dr");
            Build_AST(new Token(null, "where"), Node_Type.WHERE, 2);
        }

        // System.out.println("Ew -> T");

    }

    private void T() {
        // System.out.println("in T()");

        Ta();
        if (tokens.get(0).value.equals(",")) {

            int count_variable_2 = 1;
            do {
                Read(tokens.get(0), Node_Type.COMMA);
                Ta();
                count_variable_2++;
            } while (tokens.get(0).value.equals(","));

            // System.out.println("T -> Ta ( ',' Ta )+");
            Build_AST(new Token(null, "tau"), Node_Type.TAU, count_variable_2);
            return;
        }
        // System.out.println("T -> Ta ");
        return;
    }

    private void Ta() {
        // System.out.println("in Ta()");

        Tc();
        // System.out.println("Ta -> Tc");

        while (tokens.get(0).value.equals("aug")) {

            Read(tokens.get(0), Node_Type.AUG);
            Tc();
            // System.out.println("Ta -> Ta 'aug' Tc");
            Build_AST(new Token(null, "aug"), Node_Type.AUG, 2);

        }

    }

    private void Tc() {
        // System.out.println("in Tc()");

        B();
        if (tokens.get(0).value.equals("->")) {

            Read(tokens.get(0), Node_Type.CONDITIONAL);
            Tc();
            if (!tokens.get(0).value.equals("|")) {
                System.out.println("Expected '|' ");

            }
            Read(tokens.get(0), Node_Type.STRAIGHT_BAR);
            Tc();
            // System.out.println("Tc -> B '->' Tc '|' Tc");
            Build_AST(new Token(null, "->"), Node_Type.CONDITIONAL, 3);
        }
        // System.out.println("Tc -> B");
    }

    private void B() {
        // System.out.println("in B()");

        Bt();
        // System.out.println("B -> Bt");

        while (tokens.get(0).value.equals("or")) {

            Read(tokens.get(0), Node_Type.Bool_OR);
            Bt();
            // System.out.println("B ->B'or' Bt");
            Build_AST(new Token(null, "or"), Node_Type.Bool_OR, 2);
        }

    }

    private void Bt() {
        // System.out.println("in Bt()");

        Bs();
        // System.out.println("Bt -> Bs");

        while (tokens.get(0).value.equals("&")) {

            Read(tokens.get(0), Node_Type.Bool_AND);
            Bs();

            // System.out.println("Bt -> Bt '&' Bs");
            Build_AST(new Token(null, "and"), Node_Type.Bool_AND, 2);
        }
    }

    private void Bs() {
        // System.out.println("in Bs()");

        switch (tokens.get(0).value) {
            case "not":

                Read(tokens.get(0), Node_Type.Bool_NOT);
                Bp();

                // System.out.println("Bs -> 'not' Bp");
                Build_AST(new Token(null, "not"), Node_Type.Bool_NOT, 1);
            default:
                Bp();
                // System.out.println("Bs -> Bp");
        }
    }

    private void Bp() {
        // System.out.println("in Bp()");

        List<String> operatorsList = Arrays.asList("gr", ">", "ge", ">=", "ls", "<", "le", "<=", "eq", "ne");
        A();

        if (operatorsList.contains(tokens.get(0).value)) {

            switch (tokens.get(0).value) {
                case "gr":
                    Read(tokens.get(0), Node_Type.GR);
                    A();

                    // System.out.println("Bp -> A 'gr' A");
                    Build_AST(new Token(null, "gr"), Node_Type.GR, 2);
                    break;
                case ">":
                    Read(tokens.get(0), Node_Type.GR);
                    A();

                    // System.out.println("Bp -> A '>' A");
                    Build_AST(new Token(null, "gr"), Node_Type.GR, 2);
                    break;
                case "ge":
                    Read(tokens.get(0), Node_Type.GE);
                    A();

                    // System.out.println("Bp -> A 'ge' A");
                    Build_AST(new Token(null, "ge"), Node_Type.GE, 2);
                    break;
                case ">=":
                    Read(tokens.get(0), Node_Type.GE);
                    A();

                    // System.out.println("Bp -> A '>=' A");
                    Build_AST(new Token(null, "ge"), Node_Type.GE, 2);
                    break;
                case "ls":
                    Read(new Token(null, "ls"), Node_Type.LS);
                    A();

                    // System.out.println("Bp -> A 'ls' A");
                    Build_AST(new Token(null, "ls"), Node_Type.LS, 2);
                    break;
                case "<":
                    Read(tokens.get(0), Node_Type.LS);
                    A();

                    // System.out.println("Bp -> A '<' A");
                    Build_AST(new Token(null, "ls"), Node_Type.LS, 2);
                    break;
                case "<=":
                    Read(tokens.get(0), Node_Type.LS);
                    A();

                    // System.out.println("Bp -> A '<=' A");
                    Build_AST(new Token(null, "le"), Node_Type.LE, 2);
                    break;
                case "le":
                    Read(tokens.get(0), Node_Type.LS);
                    A();

                    // System.out.println("Bp -> A 'le' A");
                    Build_AST(new Token(null, "le"), Node_Type.LE, 2);
                    break;
                case "eq":
                    Read(tokens.get(0), Node_Type.EQ);
                    A();

                    // System.out.println("Bp -> A 'eq' A");
                    Build_AST(new Token(null, "eq"), Node_Type.EQ, 2);
                    break;
                case "ne":
                    Read(tokens.get(0), Node_Type.NE);
                    A();

                    // System.out.println("Bp -> A 'ne' A");
                    Build_AST(new Token(null, "ne"), Node_Type.NE, 2);
                    break;
                default:
                    // System.out.println("Bp -> A");
                    break;

            }
        }
    }

    private void A() {
        // System.out.println("in A()");

        switch (tokens.get(0).value) {
            case "+":
                Read(tokens.get(0), Node_Type.PLUS);
                At();

                // System.out.println("A ->'+' At");
                break;
            case "-":
                Read(tokens.get(0), Node_Type.MINUS);
                At();

                // System.out.println("A ->'-' At");
                Build_AST(new Token(null, "-"), Node_Type.MINUS, 1);
                break;
            default:
                At();
                // System.out.println("A -> At");
                break;
        }
        while (tokens.get(0).value.equals("+") || tokens.get(0).value.equals("-")) {

            if (tokens.get(0).value.equals("+")) {
                Read(tokens.get(0), Node_Type.PLUS);
                At();

                // System.out.println("A ->'+' At");
                Build_AST(new Token(null, "+"), Node_Type.PLUS, 2);
            } else if (tokens.get(0).value.equals("-")) {
                Read(tokens.get(0), Node_Type.MINUS);
                At();

                // System.out.println("A ->'-' At");
                Build_AST(new Token(null, "-"), Node_Type.MINUS, 2);
            }
        }
    }

    private void At() {
        // System.out.println("in At()");

        Af();
        // System.out.println("At -> Af");

        while ((tokens.get(0).value.equals("*")) || (tokens.get(0).value.equals("/"))) {
            if (tokens.get(0).value.equals("*")) {

                Read(tokens.get(0), Node_Type.MUL);
                Af();

                // System.out.println("At -> At '*' Af");
                Build_AST(new Token(null, "*"), Node_Type.MUL, 2);
            } else if (tokens.get(0).value.equals("/")) {

                Read(tokens.get(0), Node_Type.DIVISION);
                Af();

                // System.out.println("At -> At '/' Af");
                Build_AST(new Token(null, "/"), Node_Type.DIVISION, 2);
            }
        }
    }

    private void Af() {
        // System.out.println("in Af()");

        Ap();
        if (tokens.get(0).value.equals("**")) {

            Read(tokens.get(0), Node_Type.POWER);
            Af();

            // System.out.println("Af -> Ap '**' Af");
            Build_AST(new Token(null, "**"), Node_Type.POWER, 2);
        }
        // System.out.println("Af -> Ap");
    }

    private void Ap() {
        // System.out.println("in Ap()");

        R();
        // System.out.println("Ap -> R");

        while (tokens.get(0).value.equals("@")) {

            Read(tokens.get(0), Node_Type.AT);
            if (!tokens.get(0).type.equals(TokenType.IDENTIFIER)) {
                System.out.println("error while parsing at Ap: IDENTIFIER expected in Line " + tokens.get(0).line);
            }
            // need to check this logic
            Read(tokens.get(0), Node_Type.IDENTIFIER);
            R();

            // System.out.println("Ap -> Ap '@' '<IDENTIFIER>' R");
            Build_AST(new Token(null, "@"), Node_Type.AT, 3);// @
        }
    }

    private void R() {
        // System.out.println("in R()");
        Rn();
        // System.out.println("R -> Rn");

        while ((Arrays.asList(TokenType.IDENTIFIER, TokenType.INTEGER, TokenType.STRING).contains(tokens.get(0).type))
                || (Arrays.asList("true", "false", "nil", "(", "dummy").contains(tokens.get(0).value))) {
            Rn();

            // System.out.println("R ->R Rn");
            Token T_gamma = new Token(null, "gamma");
            Build_AST(T_gamma, Node_Type.GAMMA, 2);
        }

    }

    private void Rn() {
        // System.out.println("in Rn()");

        if ((Arrays.asList(TokenType.IDENTIFIER, TokenType.INTEGER, TokenType.STRING).contains(tokens.get(0).type))
                || (Arrays.asList("true", "false", "nil", "(", "dummy").contains(tokens.get(0).value))) {
            switch (tokens.get(0).type) {
                case IDENTIFIER:
                    Read(tokens.get(0), Node_Type.IDENTIFIER);

                    // System.out.println("Rn -> '<IDENTIFIER>'");
                    break;
                case INTEGER:
                    Read(tokens.get(0), Node_Type.INTEGER);

                    // System.out.println("Rn -> '<INTEGER>'");
                    break;
                case STRING:
                    Read(tokens.get(0), Node_Type.STRING);

                    // System.out.println("Rn -> '<STRING>'");
                    break;
                case KEYWORD:
                    if (tokens.get(0).value.equals("true")) {

                        Read(tokens.get(0), Node_Type.T_TRUE);

                        // System.out.println("Rn -> 'true'");
                        Build_AST(new Token(null, "true"), Node_Type.T_TRUE, 0);
                        break;
                    } else if (tokens.get(0).value.equals("false")) {

                        Read(tokens.get(0), Node_Type.T_FALSE);

                        // System.out.println("Rn -> 'false'");
                        Build_AST(new Token(null, "false"), Node_Type.T_FALSE, 0);
                        break;
                    } else if (tokens.get(0).value.equals("nil")) {

                        Read(tokens.get(0), Node_Type.NIL);

                        // System.out.println("Rn -> 'nil'");
                        Build_AST(new Token(null, "nil"), Node_Type.NIL, 0);
                        break;
                    } else if (tokens.get(0).value.equals("dummy")) {

                        Read(tokens.get(0), Node_Type.DUMMY);

                        // System.out.println("Rn -> 'dummy'");
                        Build_AST(new Token(null, "dummy"), Node_Type.DUMMY, 0);
                        break;
                    }
                case PUNCTUATION:
                    if (tokens.get(0).value.equals("(")) {
                        Read(tokens.get(0), Node_Type.LEFT_PAREN);
                        E();
                        if (!tokens.get(0).value.equals(")")) {
                            System.out.println("Expected '('");
                        }
                        Read(tokens.get(0), Node_Type.RIGHT_PAREN);

                        // System.out.println("Rn -> '( E )'");
                        break;
                    } else {
                        System.out.println(
                                "Unexpected Punctuation in Line " + tokens.get(0).line + " !! while parsing at Rn ");
                    }

                default:
                    System.out
                            .println("Error while parsing at Rn : Something went wrong in Line " + tokens.get(0).line);
                    break; // !!!!!!!!need to check how it works

            }
        }

    }

    private void D() {
        // System.out.println("in D()");

        Da();
        if (tokens.get(0).value.equals("within")) {
            Token T_within = new Token(null, "within");
            Read(tokens.get(0), Node_Type.WITHIN);
            D();

            // System.out.println("D -> Da 'within' D");
            Build_AST(T_within, Node_Type.WITHIN, 2);

        }
        // System.out.println("D -> Da");
        /*
         * else{
         * System.out.println("'within' expected : error while parsing at D");
         * }
         */
    }

    private void Da() {
        // System.out.println("in Da()");

        Dr();

        if (tokens.get(0).value.equals("and")) {

            Read(tokens.get(0), Node_Type.AND);
            Dr();
            int count_variable_3 = 1;

            while (tokens.get(0).value.equals("and")) {
                Read(tokens.get(0), Node_Type.AND);
                Dr();
                count_variable_3++;
            }

            // System.out.println("Da -> Dr ( 'and' Dr )+");
            Build_AST(new Token(null, "and"), Node_Type.AND, count_variable_3);
        }
        // System.out.println("Da -> Dr");
    }

    private void Dr() {
        // System.out.println("in Dr()");

        if (tokens.get(0).value.equals("rec")) {

            Read(tokens.get(0), Node_Type.REC);
            Db();

            // System.out.println("Dr -> 'rec' Db");
            Build_AST(new Token(null, "rec"), Node_Type.REC, 1);
        } else {
            Db();
            // System.out.println("Dr -> Db");
        }
    }

    private void Db() {
        // System.out.println("in Db()");

        if (tokens.get(0).value.equals("(")) {
            Read(tokens.get(0), Node_Type.LEFT_PAREN);
            D();
            if (!tokens.get(0).value.equals(")")) {
                System.out.println("Error while parsing at Db: expected ')' in Line " + tokens.get(0).line);
            }
            Read(tokens.get(0), Node_Type.RIGHT_PAREN);

            // System.out.println("Db -> '(' D ')' ");
        } else if (tokens.get(0).type.equals(TokenType.IDENTIFIER)) {

            /*
             * this LL2 is needed because we have <IDENTIFIER> token as first token
             * in one of the Vl production rule .So
             * Db -> Vl ’=’ E => ’=’
             * -> ’<IDENTIFIER>’ Vb+ ’=’ E
             * both have same first set Token <IDENTIFIER>
             * . So instead of one look ahead here only we use 2nd look ahead also
             */

            Token LL2 = tokens.get(1);

            if ((LL2.type.equals(TokenType.IDENTIFIER)) || (LL2.value.equals("("))) {

                Read(tokens.get(0), Node_Type.IDENTIFIER);

                if ((tokens.get(0).type.equals(TokenType.IDENTIFIER)) || (tokens.get(0).value.equals("("))) {
                    Vb();

                    int count_variable_4 = 1;

                    while ((tokens.get(0).type.equals(TokenType.IDENTIFIER)) || (tokens.get(0).value.equals("("))) {
                        Vb();
                        count_variable_4++;
                    }
                    if (!tokens.get(0).value.equals("=")) {
                        System.out.println("Error while parsing at Db: expecting '=' in Line " + tokens.get(0).line);
                    }
                    Read(tokens.get(0), Node_Type.EQUAL);
                    E();

                    // System.out.println("Db -> '<IDENTIFIER>' Vb+ '=' E");
                    Build_AST(new Token(null, "fcn_form"), Node_Type.FCN_FORM, count_variable_4 + 2);
                }

            } else {

                Read(tokens.get(0), Node_Type.EQUAL);
                E();

                // System.out.println("Db -> Vl '=' E");
                Build_AST(new Token(null, "="), Node_Type.EQUAL, 2);

            }

        } else {
            System.out.println(
                    "Error while parsing at Db: expected in Line " + tokens.get(0).line + " '(' or IDENTIFIER");
        }
    }

    private void Vb() {
        // System.out.println("in Vb()");

        if (tokens.get(0).value.equals("(")) {
            // System.out.println("Check 2");

            Read(tokens.get(0), Node_Type.LEFT_PAREN);
            if (tokens.get(0).value.equals(")")) {

                Read(tokens.get(0), Node_Type.RIGHT_PAREN);

                // System.out.println("Vb -> '(' ')'");
                Build_AST(new Token(null, "()"), Node_Type.PARENTHESIS, 0);// need to check children count
            } else if (tokens.get(0).type == TokenType.IDENTIFIER) {
                Vl();
                if (!tokens.get(0).value.equals(")")) {
                    System.out.println("Error while parsing at Vb: expected ')' in Line " + tokens.get(0).line);
                }
                Read(tokens.get(0), Node_Type.RIGHT_PAREN);

                // System.out.println("Vb -> '(' Vl ')'");
            }
        } else {
            Read(tokens.get(0), Node_Type.IDENTIFIER);
            // System.out.println("Vb -> '<IDENTIFIER>'");
        }

    }

    private void Vl() {
        // System.out.println("in Vl()");

        Read(tokens.get(0), Node_Type.IDENTIFIER);

        int count_variable_5 = 0;
        Token T_comma = new Token(null, ",");

        while (tokens.get(0).value.equals(",")) {
            Read(tokens.get(0), Node_Type.COMMA);
            if (tokens.get(0).type != TokenType.IDENTIFIER) {
                System.out.println("Error while Parsing at Vl; expected IDENTIFIER in Line " + tokens.get(0).line);

            }
            Read(tokens.get(0), Node_Type.IDENTIFIER);// !!!!!need to verify is it correct
            count_variable_5++;

        }

        if (count_variable_5 > 0) {

            // System.out.println("Vl -> '<IDENTIFIER>' list ','");
            Build_AST(T_comma, Node_Type.COMMA, count_variable_5 + 1);
            return;
        }
    }

    /* This function is for to print the desired format as mentioned in the pdf */
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
