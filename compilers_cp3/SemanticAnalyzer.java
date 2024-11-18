
// Maintains symbol table and performs type checking
import absyn.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;

public class SemanticAnalyzer implements AbsynVisitor {
    final static int SPACES = 4;
    public static boolean isValid = true;
    private HashMap<String, ArrayList<NodeType>> symbolTable;
    private HashMap<String, ArrayList<NodeType>> funtionDecTable;

    public SemanticAnalyzer() {
        symbolTable = new HashMap<String, ArrayList<NodeType>>();
        funtionDecTable = new HashMap<String, ArrayList<NodeType>>();

        System.out.println("Entering Global Scope:");
    }

    private void printErrorMsg(int row, int col, String msg) {
        System.err.println("Error in line " + (row + 1) + ", " + "column " + (col + 1) + " : " + msg);
        isValid = false;
    }

    private void insert(String name, Dec def, int level) {
        ArrayList<NodeType> list = symbolTable.get(name);
        if (list == null) {
            list = new ArrayList<>();
            symbolTable.put(name, list);
        } else {
            // Check for re-declaration in the same scope
            for (NodeType node : list) {
                if (node.level == level) {
                    indent(level);
                    // never reached cuz there are other dup checks
                    System.out.println("Error: Symbol '" + name + "' is re-declared in the same scope.");
                    return;
                }
            }
        }
        list.add(new NodeType(name, def, level));
    }

    private NodeType lookup(String name) {
        ArrayList<NodeType> list = symbolTable.get(name);
        if (list != null && !list.isEmpty()) {
            for (int i = list.size() - 1; i >= 0; i--) {
                NodeType node = list.get(i);
                return node;
            }
        }
        return null;
    }

    private NodeType lookupFunc(String name) {
        ArrayList<NodeType> list = funtionDecTable.get(name);
        if (list != null && !list.isEmpty()) {
            for (int i = list.size() - 1; i >= 0; i--) {
                NodeType node = list.get(i);
                return node;
            }
        }
        return null;
    }

    private boolean isInteger(Dec dtype) {
        if (dtype instanceof SimpleDec) {
            SimpleDec simpleDec = (SimpleDec) dtype;
            return simpleDec.nameType.INT == NameType.INT;
        }

        if (dtype instanceof ArrayDec) {
            ArrayDec aDec = (ArrayDec) dtype;
            return aDec.nameType.type == NameType.INT;
        }

        if (dtype instanceof FuncDeclaration) {
            FuncDeclaration fDec = (FuncDeclaration) dtype;
            return fDec.nameType.type == NameType.INT;
        }
        return false;
    }

    private boolean isBool(Dec dtype) {
        if (dtype instanceof SimpleDec) {
            SimpleDec sDec = (SimpleDec) dtype;
            return sDec.nameType.type == NameType.BOOL;
        }
        if (dtype instanceof ArrayDec) {
            ArrayDec aDec = (ArrayDec) dtype;
            return aDec.nameType.type == NameType.BOOL;
        }
        if (dtype instanceof FuncDeclaration) {
            FuncDeclaration fDec = (FuncDeclaration) dtype;
            return fDec.nameType.type == NameType.BOOL;
        }
        return false;
    }

    private boolean isVoid(Dec dtype) {
        if (dtype instanceof SimpleDec) {
            SimpleDec sDec = (SimpleDec) dtype;
            return sDec.nameType.type == NameType.VOID;
        }
        if (dtype instanceof ArrayDec) {
            ArrayDec aDec = (ArrayDec) dtype;
            return aDec.nameType.type == NameType.VOID;
        }
        if (dtype instanceof FuncDeclaration) {
            FuncDeclaration fDec = (FuncDeclaration) dtype;
            return fDec.nameType.type == NameType.VOID;
        }
        return false;
    }

    private void indent(int level) {
        for (int i = 0; i < level * SPACES; i++) {
            System.out.print(" ");
        }
    }

    // Visit methods
    public void visit(ExpList exp, int level,boolean isAddr) {
        // indent(level);
        // System.out.println("ExpList hiiii");
        while (exp != null) {
            if (exp.head != null) {
                exp.head.accept(this, level,isAddr);
            }
            exp = exp.tail;
        }
    }

    public void visit(AssignExp exp, int level,boolean isAddr) {
        // indent(level);
        // System.out.println("AssignExp");
        exp.lhs.accept(this, level,isAddr);
        exp.rhs.accept(this, level,isAddr);

        NameType lhsType = null;
        NameType rhsType = null;

        // LEFT HAND SIDE
        if (exp.lhs.var instanceof SimpleVar) {
            SimpleVar simpleVar = (SimpleVar) exp.lhs.var;
            NodeType node = lookup(simpleVar.name);
            if (node == null) {
                indent(level  + 1);
                printErrorMsg(exp.row, exp.col, "Symbol '" + simpleVar.name + "' is not declared.");
                return;
            }
            if (node.def instanceof SimpleDec) {
                SimpleDec simpleDec = (SimpleDec) node.def;
                lhsType = simpleDec.nameType;
            } else if (node.def instanceof ArrayDec) {
                ArrayDec arrayDec = (ArrayDec) node.def;
                lhsType = arrayDec.nameType;
            }
        } else if (exp.lhs.var instanceof IndexVar) {
            IndexVar indexVar = (IndexVar) exp.lhs.var;
            NodeType node = lookup(indexVar.name);
            if (node == null) {
                indent(level  + 1);
                printErrorMsg(exp.row, exp.col, "Symbol '" + indexVar.name + "' is not declared.");
                return;
            }

            if (indexVar.index instanceof CallExp) {
                CallExp funcCall = (CallExp) indexVar.index;
                NodeType funcNode = lookupFunc(funcCall.func); 
                // Check if function call exists
                if (funcNode == null) { 
                    indent(level  + 1);
                    printErrorMsg(exp.row, exp.col, "Function '" + funcCall.func + "' is not declared.");
                    return;
                }
                // Check if function return equal to int
                FuncDeclaration function = (FuncDeclaration) funcNode.def;
                if (function.nameType.type != NameType.INT) {
                    indent(level + 1);
                    printErrorMsg(exp.row, exp.col, "Function '" + funcCall.func + "' does not have INT return type.");
                    return;
                }
            } else if (!(indexVar.index instanceof IntExp)) {
                return;
            }

            if (node.def instanceof ArrayDec) {
                ArrayDec arrayDec = (ArrayDec) node.def;
                lhsType = arrayDec.nameType; // Assuming ArrayDec has elemType for element type
            } 
        } else {
            indent(level  + 1);
            printErrorMsg(exp.row, exp.col, "Invalid left hand side of assignment.");
            return;
        }

        // RIGHT HAND SIDE
        if (exp.rhs instanceof VarExp) {
            VarExp varExp = (VarExp) exp.rhs;
            indent(level);
            System.out.println("RHS");
            if (varExp.var instanceof SimpleVar) {
                SimpleVar simpleVar = (SimpleVar) varExp.var;
                NodeType node = lookup(simpleVar.name);
                if (node == null) {
                    indent(level  + 1);
                    printErrorMsg(exp.row, exp.col, "Symbol '" + simpleVar.name + "' is not declared.");
                    return;
                }
                if (node.def instanceof SimpleDec) {
                    SimpleDec simpleDec = (SimpleDec) node.def;
                    rhsType = simpleDec.nameType;
                } else if (node.def instanceof ArrayDec) {
                    ArrayDec arrayDec = (ArrayDec) node.def;
                    rhsType = arrayDec.nameType;
                }
            } else if (varExp.var instanceof IndexVar) {
                IndexVar indexVar = (IndexVar) varExp.var;
                NodeType node = lookup(indexVar.name);
                if (node == null) {
                    indent(level  + 1);
                    printErrorMsg(exp.row, exp.col, "Symbol '" + indexVar.name + "' is not declared.");
                    return;
                }
                if (node.def instanceof ArrayDec) {
                    ArrayDec arrayDec = (ArrayDec) node.def;
                    rhsType = arrayDec.nameType; // Assuming ArrayDec has elemType for element type
                }
            } else {
                indent(level + 1);
                printErrorMsg(exp.row, exp.col, "Invalid right hand side of assignment.");
                return;
            }
        } else if (exp.rhs instanceof IntExp) {
            IntExp intExp = (IntExp) exp.rhs;
            rhsType = new NameType(-1, -1, NameType.INT);
        } else if (exp.rhs instanceof BoolExp) {
            BoolExp boolExp = (BoolExp) exp.rhs;
            rhsType = new NameType(-1, -1, NameType.BOOL);
        } else if (exp.rhs instanceof NilExp) {
            NilExp nilExp = (NilExp) exp.rhs;
            rhsType = new NameType(-1, -1, NameType.VOID);
        } else if (exp.rhs instanceof CallExp) {
            CallExp callExp = (CallExp) exp.rhs;
            NodeType node = lookupFunc(callExp.func);
            // Checking if the function exists
            if (node == null) {
                indent(level  + 1);
                printErrorMsg(exp.row, exp.col, "Function '" + callExp.func + "' is not declared.");
                return;
            }
            if (node.def instanceof FuncDeclaration) {
                FuncDeclaration funcDec = (FuncDeclaration) node.def;
                rhsType = funcDec.nameType;
            }
        } else if (exp.rhs instanceof OpExp) {
            OpExp opExp = (OpExp) exp.rhs;
            Exp opLeft = opExp.left;
            Exp opRight = opExp.right;

            if (lhsType.type == NameType.INT) {
                // Check if left Operand is equal to LHS expression type of INT
                if (opLeft instanceof CallExp) {
                    CallExp call = (CallExp) opLeft;
                    // Check function call return type
                    NodeType node = lookupFunc(call.func);
                    if (node == null) {
                        indent(level + 1);
                        printErrorMsg(exp.row, exp.col, "Function '" + call.func + "' is not declared.");
                        return;
                    }
                    FuncDeclaration function = (FuncDeclaration) node.def; 
                    if (function.nameType.type != NameType.INT) {
                        indent(level + 1);
                        printErrorMsg(exp.row, exp.col, "Operand Return Type in OpExp is not equal to " + getType(lhsType));
                        return;
                    }
                } else {
                    // Check Exp type is equal to LHS expression type
                    if (!(opLeft instanceof IntExp)) {
                        indent(level + 1);
                        printErrorMsg(exp.row, exp.col, "Operand in OpExp is not equal to " + getType(lhsType));
                        return;
                    }
                }

                if (opRight instanceof CallExp) {
                    // Check function call return type
                    CallExp call = (CallExp) opRight;
                    NodeType node = lookupFunc(call.func);
                    if (node == null) {
                        indent(level + 1);
                        printErrorMsg(exp.row, exp.col, "Function '" + call.func + "' is not declared.");
                        return;
                    }
                    FuncDeclaration function = (FuncDeclaration) node.def;
                    if (function.nameType.type != NameType.INT) {
                        indent(level + 1);
                        printErrorMsg(exp.row, exp.col, "Operand Return Type in OpExp is not equal to " + getType(lhsType));
                        return;
                    }
                } else {
                    if (!(opRight instanceof IntExp)) {
                        indent(level + 1);
                        printErrorMsg(exp.row, exp.col, "Operand in OpExp is not equal to " + getType(lhsType));
                        return;
                    }
                }
            } else if (lhsType.type == NameType.BOOL) {
                if (opLeft instanceof CallExp) {
                    // Check function call return type
                    CallExp call = (CallExp) opLeft;
                    NodeType node = lookupFunc(call.func);
                    if (node == null) {
                        indent(level + 1);
                        printErrorMsg(exp.row, exp.col, "Function '" + call.func + "' is not declared.");
                        return;
                    }
                    FuncDeclaration function = (FuncDeclaration) node.def;
                    if (function.nameType.type != NameType.BOOL) {
                        indent(level + 1);
                        printErrorMsg(exp.row, exp.col, "Operand Return Type in OpExp is not equal to " + getType(lhsType));
                        return;
                    }
                } else {
                    if (!(opLeft instanceof BoolExp)) {
                        indent(level + 1);
                        printErrorMsg(exp.row, exp.col, "Operand in OpExp is not equal to " + getType(lhsType));
                        return;
                    }
                }

                if (opRight instanceof CallExp) {
                    // Check function call return type
                    CallExp call = (CallExp) opRight;
                    NodeType node = lookupFunc(call.func);
                    if (node == null) {
                        indent(level + 1);
                        printErrorMsg(exp.row, exp.col, "Function '" + call.func + "' is not declared.");
                        return;
                    }
                    FuncDeclaration function = (FuncDeclaration) node.def;
                    if (function.nameType.type != NameType.BOOL) {
                        indent(level + 1);
                        printErrorMsg(exp.row, exp.col, "Operand Return Type in OpExp is not equal to " + getType(lhsType));
                        return;
                    }
                } else {
                    if (!(opRight instanceof BoolExp)) {
                        indent(level + 1);
                        printErrorMsg(exp.row, exp.col, "Operand in OpExp is not equal to " + getType(lhsType));
                        return;
                    }
                }
            }
        
            rhsType = lhsType;
        } else {
            indent(level + 1);
            printErrorMsg(exp.row, exp.col, "Invalid right hand side of assignment.");
            return;
        }

        if (lhsType.type != rhsType.type) {
            indent(level + 1);
            printErrorMsg(exp.row, exp.col, "Type mismatch in assignment. " + getType(rhsType) + " cannot be assigned to " + getType(lhsType));
        }
    }

    public String getType(NameType nameType) {
        if (nameType == null) {
            System.out.println("Error: Type is null.");
            return "NULL";
        }

        String typeName;
        int type = nameType.type;
        if (type == 1) {
            typeName = "BOOL";
        } else if (type == 2) {
            typeName = "INT";
        } else if (type == 3) {
            typeName = "VOID";
        } else {
            typeName = "NULL";
        }

        return typeName;
    }

    public void visit(IfExp exp, int level,boolean isAddr) {
        level++;
        indent(level);
        System.out.println("Entering a new If block:");



        exp.test.accept(this, level,isAddr);
        exp.thenpart.accept(this, level,isAddr);

        if (exp.elsepart != null) {
            exp.elsepart.accept(this, level,isAddr);
        }

        if (exp.test == null) {
            indent(level);
            printErrorMsg(exp.row, exp.col, "Condition Cannot be Empty");
        }

        // check if the condition evaluates to a boolean

        checkIfCondition(exp.test, level);

        printScope(level);

        deleteScope(level);

        indent(level);
        System.out.println("Leaving the If block");
        level--;
    }

    private void checkIfCondition(Exp exp, int level) {
        // indent(level);

        Dec dtype = exp.dtype;

        if (exp instanceof IntExp) {
            // check if its only an int like 5
            return;
        }  else if (exp instanceof BoolExp) {
            // check if its only true or false
            return;
        } else if (exp instanceof VarExp) {
            VarExp varExp = (VarExp) exp;
            if (varExp.var instanceof SimpleVar) {
                SimpleVar simpleVar = (SimpleVar) varExp.var;
                NodeType node = lookup(simpleVar.name);
                if (node == null) {
                    indent(level + 1);
                    printErrorMsg(exp.row, exp.col, "Symbol '" + simpleVar.name + "' is not declared.");
                    return;
                }
                if (node.def instanceof SimpleDec) {
                    SimpleDec simpleDec = (SimpleDec) node.def;
                    if (simpleDec.nameType.type != NameType.BOOL && simpleDec.nameType.type != NameType.INT) {
                        indent(level + 1);
                        printErrorMsg(exp.row, exp.col, "Condition can not be of type " + getType(simpleDec.nameType));
                    }
                }
            }
            return;
        }
        else if (exp instanceof OpExp) {
            OpExp opExp = (OpExp) exp;
            // check if left and right are valid

           if (opExp.left instanceof VarExp) {
               // check if the left is a valid variable
                VarExp varExp = (VarExp) opExp.left;
                if (varExp.var instanceof SimpleVar) {
                    SimpleVar simpleVar = (SimpleVar) varExp.var;
                    NodeType node = lookup(simpleVar.name);
                    if (node == null) {
                        printErrorMsg(exp.row, exp.col, "Symbol '" + simpleVar.name + "' is not declared.");
                        return;
                    }
                    if (node.def instanceof SimpleDec) {
                        SimpleDec simpleDec = (SimpleDec) node.def;
                        if (simpleDec.nameType.type != NameType.BOOL && simpleDec.nameType.type != NameType.INT) {
                            printErrorMsg(exp.row, exp.col, "Condition can not be of type " + getType(simpleDec.nameType));
                        }
                    }
                }
           }

            if (opExp.right instanceof VarExp) {
                VarExp varExp = (VarExp) opExp.right;
                if (varExp.var instanceof SimpleVar) {
                    SimpleVar simpleVar = (SimpleVar) varExp.var;
                    NodeType node = lookup(simpleVar.name);
                    if (node == null) {
                        printErrorMsg(exp.row, exp.col, "Symbol '" + simpleVar.name + "' is not declared.");
                        return;
                    }
                    if (node.def instanceof SimpleDec) {
                        SimpleDec simpleDec = (SimpleDec) node.def;
                        if (simpleDec.nameType.type != NameType.BOOL && simpleDec.nameType.type != NameType.INT) {
                            printErrorMsg(exp.row, exp.col, "Condition can not be of type " + getType(simpleDec.nameType));
                        }
                    }
                }
            }

            if (opExp.op > 6 && opExp.op < 14) {
                return;
            }
            return;
        }
        indent(level + 1);
        printErrorMsg(exp.row, exp.col, "Condition must be of type BOOL");
    }

    public void visit(OpExp exp, int level,boolean isAddr) {
        // indent(level);
        // System.out.println("OpExp");
        if (exp.left != null)
            exp.left.accept(this, level,isAddr);
        exp.right.accept(this, level,isAddr);
    }

    public void visit(VarExp exp, int level,boolean isAddr) {
        // indent(level);
        // System.out.println("VarExp");

        if (exp.var != null) {
            exp.var.accept(this, level,isAddr);
        }
    }

    public void visit(ArrayDec exp, int level,boolean isAddr) {
        Dec def = exp;
        ArrayList<NodeType> defn = symbolTable.get(exp.name);
        if (defn == null) {
            defn = new ArrayList<>();
            symbolTable.put(exp.name, defn);
            // insert(exp.name, exp, level);
        } else {
            for (NodeType node : defn) {
                if (node.level == level) {
                    indent(level + 1);
                    printErrorMsg(exp.row, exp.col, "Symbol '" + exp.name + "' is re-declared in the same scope.");
                    return;
                }
            }
        }
        defn.add(new NodeType(exp.name, def, level));
    }


    /***
         *  Notes:
         *  - VarDecList contains VarDecs -> SimpleDec(NameType) || ArrayDec(NameType)
         *  - ExpList contains -> Exp -> Can result in INT / VOID / BOOL
         *  - Find out what the type of the Exp is and VarDec
         */
    // make a function to check if the function is declared inside the functionTable

    private NodeType lookupFunction(String name) {
        ArrayList<NodeType> list = funtionDecTable.get(name);
        if (list != null && !list.isEmpty()) {
            for (int i = list.size() - 1; i >= 0; i--) {
                NodeType node = list.get(i);
                return node;
            }
        }
        return null;
    }

    public void visit(CallExp exp, int level,boolean isAddr) {
        // Check if function exists in symbol table
        NodeType node = lookupFunc(exp.func);
        if (node == null) {
            indent(level + 1);
            printErrorMsg(exp.row, exp.col, "Function '" + exp.func + "' is not declared.");
            return;
        } 

        // Check if the CallExp args equal the function parameter lists
        FuncDeclaration func = (FuncDeclaration) node.def;
        exp.dec = func;
        VarDecList params = func.params;
        ExpList args = exp.args; 

        while ((params != null) && (args != null)) {
            VarDec currParam = params.head;
            Exp currArg = args.head;

            if (currParam instanceof SimpleDec) {
                SimpleDec paramDec = (SimpleDec) currParam;
                NameType typeParam = paramDec.nameType;

                // System.out.println("Checking instances..");
                if (currArg instanceof BoolExp) {
                    if (typeParam.type != 1) {
                        indent(level + 1);
                        printErrorMsg(exp.row, exp.col, "Invalid argument type for function " + exp.func + ". Expected " + getType(typeParam));
                        return;
                    }
                } else if (currArg instanceof IntExp) {
                    if (typeParam.type != 2) {
                        indent(level + 1);
                        printErrorMsg(exp.row, exp.col, "Invalid argument type for function " + exp.func + ". Expected " + getType(typeParam));
                        return;
                    }
                }
            }
            params = params.tail;
            args = args.tail;
        }

        if (params != null) {
            indent(level + 1);
            printErrorMsg(exp.row, exp.col, "Missing arguments for function " + exp.func + ".");
        } else if (args != null) {
            indent(level + 1);
            printErrorMsg(exp.row, exp.col, "Too manys arguments for function " + exp.func + ".");
        } 

        if (exp.args != null) {
            exp.args.accept(this, level,isAddr);
        }
    }

    public void visit(CompoundExp exp, int level,boolean isAddr) {
        // indent(level);
        // System.out.println("Compund Expression");
        if (exp.varDecList != null) {
            exp.varDecList.accept(this, level,isAddr);
        }

        if (exp.expList != null) {
            exp.expList.accept(this, level,isAddr);
        }
    }

    public void visit(DecList decList, int level,boolean isAddr) {
        // indent(level);
        while (decList != null) {
            if (decList.head != null) {
                decList.head.accept(this, level,isAddr);
            }
            decList = decList.tail;
        }

        printScope(level);
        printScopeFunc(level + 1);
        System.out.println("Leaving the global scope");
    }

    public void visit(FuncDeclaration funcDec, int level,boolean isAddr) {
        level++;
        indent(level);
        System.out.println("Entering scope(" + (level-1) + ") for function " + funcDec.function);
        ArrayList<NodeType> functionDec = funtionDecTable.get(funcDec.function);

        if (functionDec == null) {
            functionDec = new ArrayList<>();
            funtionDecTable.put(funcDec.function, functionDec);
        } else {
            for (NodeType node : functionDec) {
                if (node.level == level) {
                    indent(level + 1);
                    printErrorMsg(funcDec.row, funcDec.col, "Function '" + funcDec.function + "' is re-declared in the same scope.");
                    return;
                }
            }
        }
        functionDec.add(new NodeType(funcDec.function, funcDec, level));

        if (funcDec.params != null) {
            funcDec.params.accept(this, level,isAddr);
        }

        // check the return type of the function matches the return type of the body
        if (funcDec.body != null) {
            CompoundExp body = (CompoundExp) funcDec.body;
            if (body.expList != null) {
                ExpList expList = body.expList;
                while (expList != null) {
                    if (expList.head instanceof ReturnExp) {
                        ReturnExp returnExp = (ReturnExp) expList.head;
                        if (returnExp.expression != null) {
                            if (returnExp.expression instanceof CallExp) {
                                CallExp callExp = (CallExp) returnExp.expression;
                                NodeType node = lookupFunc(callExp.func);
                                if (node == null) {
                                    indent(level + 1);
                                    printErrorMsg(funcDec.row, funcDec.col, "Function '" + callExp.func + "' is not declared.");
                                    return;
                                }
                                FuncDeclaration func = (FuncDeclaration) node.def;
                                if (func.nameType.type != funcDec.nameType.type) {
                                    indent(level + 1);
                                    printErrorMsg(funcDec.row, funcDec.col, "Return type of function '" + funcDec.function + "' does not match the function declaration.");
                                    return;
                                }
                            } else {
                                // check if the return type of the function matches the return type of the body
                                if (returnExp.expression instanceof IntExp) {
                                    if (funcDec.nameType.type != NameType.INT) {
                                        indent(level + 1);
                                        printErrorMsg(funcDec.row, funcDec.col, "Return type of function '" + funcDec.function + "' does not match the function declaration.");
                                        return;
                                    }
                                } else if (returnExp.expression instanceof BoolExp) {
                                    if (funcDec.nameType.type != NameType.BOOL) {
                                        indent(level + 1);
                                        printErrorMsg(funcDec.row, funcDec.col, "Return type of function '" + funcDec.function + "' does not match the function declaration.");
                                        return;
                                    }
                                } else if (returnExp.expression instanceof NilExp) {
                                    if (funcDec.nameType.type != NameType.VOID) {
                                        indent(level + 1);
                                        printErrorMsg(funcDec.row, funcDec.col, "Return type of function '" + funcDec.function + "' does not match the function declaration.");
                                        return;
                                    }
                                } else {
                                    indent(level + 1);
                                    printErrorMsg(funcDec.row, funcDec.col, "Return type of function '" + funcDec.function + "' does not match the function declaration.");
                                    return;
                                }
                            }
                        }
                    }
                    expList = expList.tail;
                }
            }
        }

        if (funcDec.body != null) {
            funcDec.body.accept(this, level,isAddr);
        }


        printScope(level);
        printScopeFunc(level + 1);
        deleteScope(level);
        indent(level);
        System.out.println("Leaving scope for function " + funcDec.function);

        level--;
    }

    public void visit(ReturnExp exp, int level,boolean isAddr) {
        if (exp.expression != null) {
            exp.expression.accept(this, level,isAddr);
        }

        return;

    }


    public void visit(SimpleDec exp, int level,boolean isAddr) {
        // // indent(level);
        // // System.out.println("SimpleDec");

        ArrayList<NodeType> defn = symbolTable.get(exp.name);
        if (defn != null && !defn.isEmpty()) {
            if (defn.get(0).level == level) {
                indent(level + 1);
                printErrorMsg(exp.row, exp.col, "Symbol '" + exp.name + "' is re-declared in the same scope.");
            } else {
                defn.add(new NodeType(exp.name, exp, level));
                symbolTable.put(exp.name, defn);
            }
        } else {
            insert(exp.name, exp, level);
        }
    }

    public void visit(VarDecList exp, int level,boolean isAddr) {
        // indent(level);
        // System.out.println("VarDecList");
        while (exp != null) {
            if (exp.head != null) {
                exp.head.accept(this, level,isAddr);
            }
            exp = exp.tail;
        }
    }

    public void visit(WhileExp exp, int level,boolean isAddr) {
        level++;
        indent(level);
        System.out.println("Entering a new While block");
        level++;
        if (exp.test != null)
            exp.test.accept(this, level,isAddr);
        if (exp.body != null)
            exp.body.accept(this, level,isAddr);
        if (exp.test instanceof NilExp) {
            indent(level + 1);
            printErrorMsg(exp.row, exp.col,  "Condition must be of type BOOL");
        }

        printScope(level);

        Iterator<Map.Entry<String, ArrayList<NodeType>>> iterator = symbolTable.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ArrayList<NodeType>> entry = iterator.next();
            ArrayList<NodeType> definitions = entry.getValue();

            Iterator<NodeType> listIterator = definitions.iterator();
            while (listIterator.hasNext()) {
                NodeType node = listIterator.next();
                if (node.level == level) {
                    listIterator.remove();
                }
            }

            if (definitions.isEmpty()) {
                iterator.remove(); // Remove the entry if the list is empty
            }
        }


        level--;
        indent(level);
        level--;
        System.out.println("Leaving the While block");
    }

    public void visit(SimpleVar exp, int level,boolean isAddr) {
        // indent(level);
        ArrayList <NodeType> definition = symbolTable.get(exp.name);

        if (definition.get(0).def instanceof SimpleDec) {
            SimpleDec simpleDec = (SimpleDec) definition.get(0).def;
            exp.decl = simpleDec;
            System.out.println(exp.decl.offset);

        }
    }

    public void visit(IndexVar exp, int level,boolean isAddr) {
        if (exp.index instanceof BoolExp) {
            indent(level + 1);
            printErrorMsg(exp.row, exp.col, "Index cannot be of type BOOL");
        }
        exp.index.accept(this, level,isAddr);
    }

    public void printScope(int level) {
        // indent(level);
        // System.out.println("Printing Symbol Table for scope (" + level + ")");
        for (String key : symbolTable.keySet()) {
            ArrayList<NodeType> list = symbolTable.get(key);
            if (list == null) {
                continue;
            }
            for (NodeType node : list) {
                if (node.level == level) {
                    Dec def = node.def;
                    indent(level);
                    checkDec(def, level);
                }
            }
        }
    }

    public void printScopeFunc(int level) {
        // indent(level);
        // System.out.println("Printing scope function");
        for (String key : funtionDecTable.keySet()) {
            ArrayList<NodeType> list = funtionDecTable.get(key);
            if (list == null) {
                continue;
            }
            // System.out.println("Printing function");
            for (NodeType node : list) {
                if (node.level == level) {
                    FuncDeclaration def = (FuncDeclaration) node.def;
                
                    int funcType = def.nameType.type;
                    if (funcType == NameType.INT) {
                        indent(level);
                        System.out.println(def.function + getParamListString(def.params) + " -> int");
                    } else if (funcType == NameType.BOOL) {
                        indent(level);
                        System.out.println(def.function + getParamListString(def.params) + " -> bool");
                    } else if (funcType == NameType.VOID) {
                        indent(level);
                        System.out.println(def.function + "() -> void");
                    }
                }
            }
        }
    }

    private String getParamListString(VarDecList params) {
        String paramStr = "(";

        while (params != null) {
            if (params.head instanceof SimpleDec) {
                SimpleDec simpleDec = (SimpleDec) params.head;
                int type = simpleDec.nameType.type;
                if (type == NameType.INT) {
                    paramStr += "int,";
                } else if (type == NameType.BOOL) {
                    paramStr += "bool,";
                }
            } else if (params.head instanceof ArrayDec) {
                ArrayDec arrayDec = (ArrayDec) params.head;
                int type = arrayDec.nameType.type;
                if (type == NameType.INT) {
                    paramStr += "int[" + arrayDec.size + "]";
                } else if (type == NameType.BOOL) {
                      paramStr += "bool[" + arrayDec.size + "]";
                }
            }

            params = params.tail;
        }

        paramStr += ")";
        return paramStr;
    }


    private void checkDec(Dec dec, int level) {
        if (dec instanceof SimpleDec) {
            SimpleDec simpleDec = (SimpleDec) dec;
            if (simpleDec.nameType.type == NameType.INT) {
                indent(level);
                System.out.println(simpleDec.name + ": " + "int");
            } else if (simpleDec.nameType.type == NameType.BOOL) {
                indent(level);
                System.out.println(simpleDec.name + ": " + "bool");
            } else if (simpleDec.nameType.type == NameType.VOID) {
                indent(level);
                System.out.println(simpleDec.name + ": " + "void");
            }
        } else if (dec instanceof ArrayDec) {
            ArrayDec arrayDec = (ArrayDec) dec;
            if (arrayDec.nameType.type == NameType.INT) {
                indent(level);
                System.out.println(arrayDec.name + ": " + "int[" + arrayDec.size + "]");
            }
        }
    }

    public void visit(IntExp exp, int level,boolean isAddr) {
        // indent(level);
        // System.out.println("IntExp");
    }

    public void visit(BoolExp exp, int level,boolean isAddr) {
        // indent(level);
        // System.out.println("BoolExp");
    }

    public void visit(NilExp exp, int level,boolean isAddr) {
        // indent(level);
        // System.out.println("NilExp");
    }

    public void visit(NameType exp, int level, boolean isAddr) {
        indent(level);
        System.out.println("NameType");
    }

    public void deleteScope(int level){
        Iterator<Map.Entry<String, ArrayList<NodeType>>> iterator = symbolTable.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ArrayList<NodeType>> entry = iterator.next();
            ArrayList<NodeType> definitions = entry.getValue();

            Iterator<NodeType> listIterator = definitions.iterator();
            while (listIterator.hasNext()) {
                NodeType node = listIterator.next();
                if (node.level == level) {
                    // print the name of what is being deleted 
                    listIterator.remove();
                }
            }

        }

    }
}