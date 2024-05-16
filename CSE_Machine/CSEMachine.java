package CSE_Machine;

import Symbols.*;
import java.util.ArrayList;

public class CSEMachine {
    private ArrayList<Symbol> control;
    private ArrayList<Symbol> stack;
    private ArrayList<Environment> environment;

    public CSEMachine(ArrayList<Symbol> control, ArrayList<Symbol> stack, ArrayList<Environment> environment) {
        this.setControl(control);
        this.setStack(stack);
        this.setEnvironment(environment);
    }

    public void setControl(ArrayList<Symbol> control) {
        this.control = control;
    }

    public void setStack(ArrayList<Symbol> stack) {
        this.stack = stack;
    }

    public void setEnvironment(ArrayList<Environment> environment) {
        this.environment = environment;
    }



    /*
     * ############### implementation of CSE Machine Rules #####################
     */
    public void execute() throws Exception {
        Environment curr_Env = this.environment.get(0);
        int j = 1;
        while (!control.isEmpty()) {
            
            // pop last element of the control
            Symbol curr_Symbol = control.remove(control.size() - 1);
            
            //############### rule no. 1 ##################

            if (curr_Symbol instanceof Id) {
                this.stack.add(0, curr_Env.lookup((Id) curr_Symbol));

            //################ rule no. 2  ################

            } else if (curr_Symbol instanceof Lambda) {
                Lambda lambda = (Lambda) curr_Symbol;
                lambda.setEnvironment(curr_Env.getIndex());
                this.stack.add(0, lambda);

                //################ rule no. 3, 4, 10, 11, 12 & 13  #####################

            } else if (curr_Symbol instanceof Gamma) {
                Symbol nextSymbol = this.stack.remove(0);// Symbol at the top of stack
                
                //#################### lambda (rule no. 4 & 11) ######################

                if (nextSymbol instanceof Lambda) {
                    Lambda lambda = (Lambda) nextSymbol;
                    Environment e = new Environment(j++);
                    if (lambda.identifiers.size() == 1) {
                        e.values.put(lambda.identifiers.get(0), this.stack.get(0));
                        this.stack.remove(0);
                    } else {
                        Tup tup = (Tup) this.stack.get(0);
                        this.stack.remove(0);
                        int i = 0;
                        for (Id id : lambda.identifiers) {
                            e.values.put(id, tup.symbols.get(i++));
                        }
                    }
                    for (Environment environment : this.environment) {
                        if (environment.getIndex() == lambda.getEnvironment()) {
                            e.setParent(environment);
                        }
                    }
                    curr_Env = e;
                    this.control.add(e);
                    this.control.add(lambda.getDelta());
                    this.stack.add(0, e);
                    this.environment.add(e);

                    // ################### tup (rule no. 10)  #############################

                } else if (nextSymbol instanceof Tup) {
                    Tup tup = (Tup) nextSymbol;
                    int i = Integer.parseInt(this.stack.get(0).getData());
                    this.stack.remove(0);
                    this.stack.add(0, tup.symbols.get(i - 1));

                    // ###################  Ystar (rule no. 12)  ##########################
                } else if (nextSymbol instanceof Ystar) {
                    Lambda lambda = (Lambda) this.stack.get(0);
                    this.stack.remove(0);
                    Eta eta = new Eta();
                    eta.setIndex(lambda.getIndex());
                    eta.setEnvironment(lambda.getEnvironment());
                    eta.setIdentifier(lambda.identifiers.get(0));
                    eta.setLambda(lambda);
                    this.stack.add(0, eta);

                    // ######################## eta (rule no. 13)  ################################
                } else if (nextSymbol instanceof Eta) {
                    Eta eta = (Eta) nextSymbol;
                    Lambda lambda = eta.get_Lambda();
                    this.control.add(new Gamma());
                    this.control.add(new Gamma());
                    this.stack.add(0, eta);
                    this.stack.add(0, lambda);

                    // #####################  built_in functions in RPAL ##############################
                } else {
                    if ("Print".equals(nextSymbol.getData())) {
                        // !!!! do nothing  !!!!!!!!!

                    } else if ("Stem".equals(nextSymbol.getData())) {
                        Symbol s = this.stack.remove(0);
                        s.setData(s.getData().substring(0, 1));
                        this.stack.add(0, s);
                        
                    } else if ("Order".equals(nextSymbol.getData())) {
                        Tup tup = (Tup) this.stack.remove(0);
                        Int n = new Int(Integer.toString(tup.symbols.size()));
                        this.stack.add(0, n);

                    } else if ("Stern".equals(nextSymbol.getData())) {
                        Symbol s = this.stack.remove(0);
                        s.setData(s.getData().substring(1));
                        this.stack.add(0, s);

                    } else if ("Conc".equals(nextSymbol.getData())) {
                        Symbol s1 = this.stack.remove(0);
                        Symbol s2 = this.stack.remove(0);
                        s1.setData(s1.getData() + s2.getData());
                        this.stack.add(0, s1);

                    } else if ("Isinteger".equals(nextSymbol.getData())) {
                        if (this.stack.get(0) instanceof Int) {
                            this.stack.add(0, new Bool("true"));
                        } else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);

                    } else if ("Isstring".equals(nextSymbol.getData())) {
                        if (this.stack.get(0) instanceof Str) {
                            this.stack.add(0, new Bool("true"));
                        } else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);

                    } else if ("Istuple".equals(nextSymbol.getData())) {
                        if (this.stack.get(0) instanceof Tup) {
                            this.stack.add(0, new Bool("true"));
                        } else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);

                    } else if ("Isdummy".equals(nextSymbol.getData())) {
                        if (this.stack.get(0) instanceof Dummy) {
                            this.stack.add(0, new Bool("true"));
                        } else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);

                    } else if ("Istruthvalue".equals(nextSymbol.getData())) {
                        if (this.stack.get(0) instanceof Bool) {
                            this.stack.add(0, new Bool("true"));
                        } else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);

                    } else if ("Isfunction".equals(nextSymbol.getData())) {
                        if (this.stack.get(0) instanceof Lambda) {
                            this.stack.add(0, new Bool("true"));
                        } else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);

                    } else if ("Null".equals(nextSymbol.getData())) {
                        // implement
                    } else if ("Itos".equals(nextSymbol.getData())) {
                        // implement
                    }
                }
                // ########################## rule no. 5  ###############################
            } else if (curr_Symbol instanceof Environment) {
                // System.out.println(this.stack.size());
                // System.out.println(this.stack.get(0).getData());

                this.stack.remove(1);

                this.environment.get(((Environment) curr_Symbol).getIndex()).setIsRemoved(true);
                int env_size = this.environment.size();

                while (env_size > 0) {
                    if (!this.environment.get(env_size - 1).getIsRemoved()) {
                        curr_Env = this.environment.get(env_size - 1);
                        break;
                    } else {
                        env_size--;
                    }
                }

            // ###################### rule no. 6 & 7  ##############################

            } else if (curr_Symbol instanceof Rator) {

                if (curr_Symbol instanceof Uop) {
                    Symbol operator = curr_Symbol;
                    Symbol rand = this.stack.remove(0);
                    stack.add(0, this.applyUnaryOperation(operator, rand));
                }

                if (curr_Symbol instanceof Binary_Op) {
                    Symbol operator = curr_Symbol;
                    Symbol operand1 = this.stack.remove(0);
                    Symbol operand2 = this.stack.remove(0);
                    
                    this.stack.add(0, this.apply_BinOp(operator, operand1, operand2));
                }

            // ########################  rule no. 8   ################################
            } else if (curr_Symbol instanceof Beta) {
                if (Boolean.parseBoolean(this.stack.get(0).getData())) {
                    this.control.remove(control.size() - 1);
                } else {
                    this.control.remove(control.size() - 2);
                }
                this.stack.remove(0);

            // ########################### rule no. 9 ###############################

            } else if (curr_Symbol instanceof Tau) {
                Tau tau = (Tau) curr_Symbol;
                Tup tup = new Tup();
                for (int i = 0; i < tau.getN(); i++) {
                    tup.symbols.add(this.stack.remove(0));
                }
                this.stack.add(0, tup);

            } else if (curr_Symbol instanceof Delta) {
                this.control.addAll(((Delta) curr_Symbol).symbols);

            } else if (curr_Symbol instanceof B) {
                this.control.addAll(((B) curr_Symbol).symbols);

            } else {
                this.stack.add(0, curr_Symbol);
            }
        }
    }

    public void printControl() {
        System.out.print("Control: ");

        for (Symbol symbol : this.control) {
            System.out.print(symbol.getData());
            if (symbol instanceof Lambda) {
                System.out.print(((Lambda) symbol).getIndex());
            } else if (symbol instanceof Eta) {
                System.out.print(((Eta) symbol).getIndex());
            } else if (symbol instanceof Delta) {
                System.out.print(((Delta) symbol).getIndex());
            } else if (symbol instanceof Environment) {
                System.out.print(((Environment) symbol).getIndex());
            }
            System.out.print(",");
        }
        System.out.println();
    }

    public void printStack() {

        System.out.print("Stack: ");
        for (Symbol symbol : this.stack) {
            System.out.print(symbol.getData());
            if (symbol instanceof Lambda) {
                System.out.print(((Lambda) symbol).getIndex());
            } else if (symbol instanceof Eta) {
                System.out.print(((Eta) symbol).getIndex());
            } else if (symbol instanceof Delta) {
                System.out.print(((Delta) symbol).getIndex());
            } else if (symbol instanceof Environment) {
                System.out.print(((Environment) symbol).getIndex());
            }
            System.out.print(",");
        }
        System.out.println();
    }

    public void printEnvironment() {

        for (Symbol symbol : this.environment) {
            System.out.print("e" + ((Environment) symbol).getIndex() + " --> ");
            if (((Environment) symbol).getIndex() != 0) {
                System.out.println("e" + ((Environment) symbol).getParent().getIndex());
            } else {
                System.out.println();
            }
        }
    }

    public Symbol applyUnaryOperation(Symbol operator, Symbol rand) {

        if ("not".equals(operator.getData())) {
            boolean val = Boolean.parseBoolean(rand.getData());
            return new Bool(Boolean.toString(!val));
        }else if ("neg".equals(operator.getData())) {
            int val = Integer.parseInt(rand.getData());
            return new Int(Integer.toString(-1 * val));
        } else {
            return new Error_Msg();
        }
    }

    public Symbol apply_BinOp(Symbol operator, Symbol operand1, Symbol operand2) {

        if ("*".equals(operator.getData())) {
            int val1 = Integer.parseInt(operand1.getData());
            int val2 = Integer.parseInt(operand2.getData());
            return new Int(Integer.toString(val1 * val2));
        } else if ("/".equals(operator.getData())) {
            int val1 = Integer.parseInt(operand1.getData());
            int val2 = Integer.parseInt(operand2.getData());
            return new Int(Integer.toString(val1 / val2));
        } else if ("**".equals(operator.getData())) {
            int val1 = Integer.parseInt(operand1.getData());
            int val2 = Integer.parseInt(operand2.getData());
            return new Int(Integer.toString((int) Math.pow(val1, val2)));
        }else if ("+".equals(operator.getData())) {
            int val1 = Integer.parseInt(operand1.getData());
            int val2 = Integer.parseInt(operand2.getData());
            return new Int(Integer.toString(val1 + val2));
        } else if ("-".equals(operator.getData())) {
            int val1 = Integer.parseInt(operand1.getData());
            int val2 = Integer.parseInt(operand2.getData());
            return new Int(Integer.toString(val1 - val2));
        
        } else if ("&".equals(operator.getData())) {
            boolean val1 = Boolean.parseBoolean(operand1.getData());
            boolean val2 = Boolean.parseBoolean(operand2.getData());
            return new Bool(Boolean.toString(val1 && val2));
        } else if ("or".equals(operator.getData())) {
            boolean val1 = Boolean.parseBoolean(operand1.getData());
            boolean val2 = Boolean.parseBoolean(operand2.getData());
            return new Bool(Boolean.toString(val1 || val2));
        } else if ("gr".equals(operator.getData())) {
            int val1 = Integer.parseInt(operand1.getData());
            int val2 = Integer.parseInt(operand2.getData());
            return new Bool(Boolean.toString(val1 > val2));
        } else if ("ge".equals(operator.getData())) {
            int val1 = Integer.parseInt(operand1.getData());
            int val2 = Integer.parseInt(operand2.getData());
            return new Bool(Boolean.toString(val1 >= val2));
        } else if ("ls".equals(operator.getData())) {
            int val1 = Integer.parseInt(operand1.getData());
            int val2 = Integer.parseInt(operand2.getData());
            return new Bool(Boolean.toString(val1 < val2));
        } else if ("le".equals(operator.getData())) {
            int val1 = Integer.parseInt(operand1.getData());
            String s1 = operand2.getData();
            int val2 = Integer.parseInt(s1);
            return new Bool(Boolean.toString(val1 <= val2));
        } else if ("eq".equals(operator.getData())) {
            String val1 = operand1.getData();
            String val2 = operand2.getData();
            return new Bool(Boolean.toString(val1.equals(val2)));
        } else if ("ne".equals(operator.getData())) {
            String val1 = operand1.getData();
            String val2 = operand2.getData();
            return new Bool(Boolean.toString(!val1.equals(val2)));
        } else if ("aug".equals(operator.getData())) {
            if (operand2 instanceof Tup) {
                ((Tup) operand1).symbols.addAll(((Tup) operand2).symbols);
            } else {
                ((Tup) operand1).symbols.add(operand2);
            }
            return operand1;
        } else {
            return new Error_Msg();
        }
    }

    public String get_Tup_Value(Tup tup) {
        String temp = "(";
        for (Symbol symbol : tup.symbols) {
            if (symbol instanceof Tup) {
                temp = temp + this.get_Tup_Value((Tup) symbol) + ", ";
            } else {
                temp = temp + symbol.getData() + ", ";
            }
        }
        temp = temp.substring(0, temp.length() - 2) + ")";
        return temp;
    }

    public String compute_Answer() throws Exception {
        this.execute();
        if (stack.get(0) instanceof Tup) {
            return this.get_Tup_Value((Tup) stack.get(0));
        }
        return stack.get(0).getData();
    }
}
