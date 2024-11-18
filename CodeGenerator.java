
import absyn.*;

public class CodeGenerator implements AbsynVisitor {
    // Instruction Memory Parameters
    int emitLoc = 0; // tracks where to place next piece of instruction
    int highEmitLoc = 0; // tracks next free available space to place instruction

    // Program Execution Parameters
    int pc = 7;             // tracks address of what instruction to execute next
    int mainEntry;          // entry point address of main function
    int globalOffset = 0;   // bottom of global stack frame
    int gp = 6;             // points to top of global stack frame
    int frameOffset = 0;    // bottom of curr stack frame
    int fp = 5;             // points to top of current stack frame
    int ofpFO;              // reference to callers stack frame
    int retFO = -1;         // offset value where the return address will be stored relative to the frame pointer (-1 so just under fp)
    int initFO = -2;        // offset value from where we begin storing local variables in stack frame
    int ac = 0;             // temporary storage for arithmetic operations
    int ac1 = 1;            // temporary storage for arithmetic operations

    /**
     * Output register-only assembly instruction (operations w/ only registers)
     * op - operation
     * r - register
     * s - register
     * t - register
     * c - comment
     **/

    void emitRO(String op, int r, int s, int t, String c) {
        System.out.printf("%3d: %5s %d, %d, %d", emitLoc, op, r, s, t);
        System.out.printf("\t%s\n", c);
        ++emitLoc;
        if (highEmitLoc < emitLoc)
            highEmitLoc = emitLoc;
    }

    /**
     * Output reg-mem assem instruc (operations w/ register and memory addr)
     * op - operation
     * r - register
     * d - offset
     * s - base register
     * c - comment
     **/
    void emitRM(String op, int r, int d, int s, String c) {
        System.out.printf("%3d: %5s %d, %d(%d)", emitLoc, op, r, d, s);
        System.out.printf("\t%s\n", c);
        ++emitLoc;
        if (highEmitLoc < emitLoc)
            highEmitLoc = emitLoc;
    }

    /**
     * Output reg-mem assem instruc w/ abs addressing
     * op - operation
     * r - register
     * a - abs address
     * c - comment
     **/
    void emitRM_Abs(String op, int r, int a, String c) {
        System.out.printf("%3d: %5s %d, %d(%d)", emitLoc, op, r, a - (emitLoc + 1), pc);
        System.out.printf("\t%s\n", c);
        ++emitLoc;
        if (highEmitLoc < emitLoc)
            highEmitLoc = emitLoc;
    }

    // Moves emitLoc by certain distance
    int emitSkip(int distance) {
        int i = emitLoc; // save curr loc
        emitLoc += distance; // advance
        if (highEmitLoc < emitLoc)
            highEmitLoc = emitLoc;
        return i; // return original
    }

    void emitComment(String c) {
        System.out.printf("* %s\n", c);
    }

    // Allows code generation to move emitLoc back to prev point
    void emitBackup(int loc) {
        if (loc > highEmitLoc)
            emitComment("BUG in emitBackup");
        emitLoc = loc;
    }

    // Restores emitLoc to highest location emmitted so far
    void emitRestore() {
        emitLoc = highEmitLoc;
    }

    /*
     * Routine to generate one line
     * of comment
     */

    public void visit(Absyn trees) {
        emitComment("C-Minus Compilation to TM Code");
        emitComment("File: ");
        emitComment("Standard prelude");
        emitRM("LD", gp, 0, ac, "load gp with maxaddr");
        emitRM("LDA", fp, 0, gp, "copy gp to fp");
        emitRM("ST", ac, 0, ac, "clear value at location " + ac);

        int savedLoc = emitSkip(1);

        emitComment("Jump around i/o routines here");
        emitComment("Code for input routine");
        emitRM("ST", 0, retFO, fp, "store return");
        emitRO("IN", 0, 0, 0, "input");
        emitRM("LD", pc, retFO, fp, "return to caller");

        emitComment("Code for output routine");
        emitRM("ST", 0, retFO, fp, "store return");
        emitRM("LD", 0, initFO, fp, "load output value");
        emitRO("OUT", 0, 0, 0, "output");
        emitRM("LD", pc, retFO, fp, "return to caller");

        int savedLoc2 = emitSkip(0); // point in code where program should continue execution

        emitBackup(savedLoc);
        emitRM_Abs("LDA", pc, savedLoc2, "jump around i/o code");
        emitRestore();
        emitComment("End of standard prelude.");

        visit((DecList) trees, 0, false);

        emitComment("Standard finale");
        emitRM("ST", fp, globalOffset + ofpFO, fp, "push ofp");
        emitRM("LDA", fp, globalOffset, fp, "push frame");
        emitRM("LDA", ac, 1, pc, "load ac with ret ptr");
        emitRM_Abs("LDA", pc, mainEntry, "jump to main loc");
        emitRM("LD", fp, ofpFO, fp, "pop frame");

        emitComment("End of execution");
        emitRO("HALT", 0, 0, 0, "");
    }

    public void visit(ExpList exp, int level, boolean isAddr) {
        while (exp != null) {
            if (exp.head != null) {
                exp.head.accept(this, level, false);
                // emitRM("ST", ac, 0, fp, "store arg val");
            }
            exp = exp.tail;
        }
    }

    public void visit(AssignExp exp, int level, boolean isAddr) {
        emitComment("-> op ");

        VarExp varLhs = exp.lhs;
        if (varLhs.var instanceof SimpleVar) {
            SimpleVar simpleVar = (SimpleVar) varLhs.var;
            visit(simpleVar, level - 1, true);

        } else if (varLhs.var instanceof IndexVar) {
            IndexVar indexVar = (IndexVar) varLhs.var;
            visit(indexVar, level - 1, true);
        }

        Exp varRhs = exp.rhs;
        if (varRhs != null) {
            varRhs.accept(this, level - 2, false);
        }


        emitRM("LD", ac, frameOffset, fp, "op: load left");  
        emitRM("ST", ac, 0, level, "assign: store value");
        emitComment("<- op");
    }

    public void visit(IfExp exp, int level, boolean isAddr) {
        emitComment("-> if");

        if (exp.test != null) {
            exp.test.accept(this, level, false);
        }

        emitComment("if: jump to else belongs here");
        if (exp.thenpart != null) {
            exp.thenpart.accept(this, level, false);
        }

        emitComment("if: jump to end belongs here");
        emitRM("JEQ", ac, 0, pc, "if: jmp to else");
        if (exp.elsepart != null) {
            exp.elsepart.accept(this, level, false);
        }

    }

    public void visit(IntExp exp, int level, boolean isAddr) {
        emitComment("-> constant");
        emitRM("LDC", ac, exp.value, 0, "load const");
        // emitRM("ST", ac, level, fp, "Store Constant Value");
        emitComment("<- constant");
    }

    public void visit(OpExp exp, int level, boolean isAddr) {
        emitComment("-> op");

        if (exp.left != null) {
            exp.left.accept(this, level - 1, isAddr);
        }

        if (exp.right != null) {
            exp.right.accept(this, level - 2, isAddr);
        }

        emitRO("LD", 1, frameOffset, fp, "op: load left");
        int op = exp.op;
        String opstr = "op ";

        // FIX ME: Invalid arguments for emitRO function calls
        switch(op) {
            case OpExp.PLUS:
                emitRO(" ADD", 0, 1, 0, opstr + "+");
                break;
            case OpExp.MINUS:
                emitRO(" SUB", 0, 1, 0, opstr + "-");
                break;
            case OpExp.UMINUS:

                break;
            case OpExp.MUL:
                emitRO(" MUL", 0, 1, 0,  opstr + "*");
                break;
            case OpExp.DIV:
                emitRO(" DIV", 0, 1, 0,  opstr + "/");
                break;
            case OpExp.EQ:
                emitRO("SUB", 0, 1, 0, opstr + "==");
                emitRM("JEQ", 0, 2, pc, "br if true");
                emitRO("LDC", 0, 0, 0, "false case");
                emitRO("LDA", 0, 1, 0, "unconditional jmp");
                emitRO("LDC", 0, 1, 0, "true case");
                break;
            case OpExp.NE:
                emitRO("SUB", 0, 1, 0,  opstr + "!=");
                emitRM("JNE", 0, 2, pc, "br if true");
                emitRO("LDC", 0, 0, 0, "false case");
                emitRO("LDA", 0, 1, 0, "unconditional jmp");
                emitRO("LDC", 0, 1, 0, "true case");
                break;
            case OpExp.LT:
                emitRO("SUB", 0, 1, 0,  opstr + "<");
                emitRM("JGE", 0, 2, pc, "br if true");
                emitRO("LDC", 0, 0, 0, "false case");
                emitRO("LDA", 0, 1, 0, "unconditional jmp");
                emitRO("LDC", 0, 1, 0, "true case");
                break;
            case OpExp.LE:

                break;
            case OpExp.GT:
                emitRO("SUB", 0, 1, 0,  opstr + ">");
                emitRM("JGT", 0, 2, pc, "br if true");
                emitRM("LDC", 0, 0, 0, "false case");
                emitRM("LDA", 0, 1, pc, "unconditional jmp");
                emitRM("LDC", 0, 1, 0, "true case");
                break;
            case OpExp.GE:
                emitRO("SUB", 0, 1, 0,  opstr + ">=");
                emitRM("JGE", 0, 2, pc, "br if true");
                emitRM("LDC", 0, 0, 0, "false case");
                emitRM("LDA", 0, 1, pc, "unconditional jmp");
                emitRM("LDC", 0, 1, 0, "true case");
                break;
            case OpExp.NOT:

                break;
            case OpExp.AND:

                break;
            case OpExp.OR:

                break;
            case OpExp.TILDE:

                break;        
        }
        emitComment("<- op");
    }

    public void visit(VarExp exp, int level, boolean isAddr) {
        if (exp.var != null) {
            exp.var.accept(this, level, isAddr);
        }
    }

    public void visit(ArrayDec Dec, int level, boolean isAddr) {
        Dec.offset = level;

        if (Dec.nestLevel == 0) {
            emitComment("processing global array: " + Dec.name);
        } else {
            emitComment("processing local array: " + Dec.name);
        }
    }

    public void visit(BoolExp exp, int level, boolean isAddr) {
        // We'll need to extract the true or false to a 1 or 0 in order to do true or
        // false in assembly
        emitComment("-> boolean ");


        emitRM("LDC", ac, exp.value ? 1 : 0, ac, "Load Boolean Value");
        emitRM("ST", ac, level, fp, "");
        emitComment("<- boolean ");
    }

    public void visit(CallExp exp, int level, boolean isAddr) {
        emitComment("-> call of function: " + exp.func);

        if (exp.args != null) {
            exp.args.accept(this, level, isAddr);
        }

        frameOffset -= 1;
        // System.out.println(exp.dec);
        emitRM("ST", fp, frameOffset, fp, "push ofp");
        emitRM("LDA", fp, frameOffset, fp, "push frame");
        emitRM("LDA", ac, 1, pc, "load ac with ret ptr");
        emitRM_Abs("LDA", pc, 0, "jump to fun loc");
        emitRM("LD", fp, ofpFO, fp, "pop frame");

        emitComment("<- call");
    }

    public void visit(CompoundExp exp, int level, boolean isAddr) {
        emitComment("-> compound statement");

        if (exp.varDecList != null) {
            exp.varDecList.accept(this, level, false);
        }

        if (exp.expList != null) {
            exp.expList.accept(this, level, false);
        }

        emitComment("<- compound statement");

    }

    public void visit(DecList exp, int level, boolean isAddr) {
        while (exp != null) {
            if (exp.head != null) {
                exp.head.accept(this, level, false);
            }
            exp = exp.tail;
        }
    }

    public void visit(FuncDeclaration exp, int level, boolean isAddr) {
        // level++;
        emitComment("processing function: " + exp.function);
        emitComment("jump around function body here");
         int savedLoc = emitSkip(1); // Save room for jump instruction
        emitRM("ST", ac, retFO, fp, "store return");

        exp.funaddr = emitLoc; // store location of function 
        // System.out.println("FUNADDR: " + Integer.toString(emitLoc));

       

        if (exp.params != null) {
            exp.params.accept(this, level, isAddr);
        }

        if (exp.body != null) {
            exp.body.accept(this, level, isAddr);
        }

        if (exp.function.equals("main")) {
            mainEntry = emitLoc;
        }

        emitRM("LD", pc, retFO, fp, "return to caller");
        int savedLoc2 = emitSkip(0);
        emitBackup(savedLoc);
        emitRM_Abs("LDA", pc, savedLoc2, "jumped around function body");
        emitRestore();
        // emitComment("end of function: " + exp.function);
        
        // level--;
    }

    public void visit(VarDecList exp, int level, boolean isAddr) {
        int argOffset = initFO;
        int nestLevel = level;

        while (exp != null) {
            if (exp.head != null) {
                emitComment(Integer.toString(argOffset));
                exp.head.offset = argOffset;
                exp.head.nestLevel = nestLevel;
                exp.head.accept(this, level, isAddr);

                int size = 1;
                if (exp.head instanceof ArrayDec) {
                    ArrayDec arrayDec = (ArrayDec) exp.head;
                    size += arrayDec.size;
                }
                level -= size;
                argOffset -= 1;
            }
            exp = exp.tail;
        }
        frameOffset = level;
    }

    public void visit(ReturnExp exp, int level, boolean isAddr) {
        emitComment("-> return");

        if (exp.expression != null) {
            exp.expression.accept(this, level, false);
        }
        emitRM("LD", pc, retFO, fp, "return to caller");
        emitComment("<- return");
    }

    public void visit(SimpleDec exp, int level, boolean isAddr) {
        exp.offset = level; // Set the new nest level for simple declaration

        if (exp.nestLevel == 0) {
            emitComment("processing global var: " + exp.name);
        } else {
            emitComment("processing local var: " + exp.name);
        }

    }

    public void visit(WhileExp exp, int level, boolean isAddr) {
        emitComment("-> while");
        emitComment("while: jump after body comes back here");
        
        int savedLoc = emitSkip(1); // current location

        if (exp.test != null) {
            exp.test.accept(this, level, false);
        }

        emitComment("while: jump to end belongs here");
        if (exp.body != null) {
            exp.body.accept(this, level, false);
        }

        int savedLoc2 = emitSkip(0); 
        emitBackup(savedLoc);
        emitRM_Abs("LDA", pc, savedLoc2, "");
        emitRestore();
        emitComment("<- while");
    }

    public void visit(SimpleVar simpleVar, int level, boolean isAddr) {
        emitComment("-> id");
        
        // Refer to slide 47 Lecture 11
        if (isAddr) {
            // Handle lhs var
            emitComment("looking up id:  " + simpleVar.name);

            if (simpleVar.decl == null) {
                emitRM("LDA", ac, 0, fp, "load id address");      // Load address
            } else {
                emitRM("LDA", ac, simpleVar.decl.offset, fp, "load id address");
            }
           
            emitComment("<- id");
            emitRM("ST", ac, frameOffset, fp, "op: push left");
        } else {
            // Handle rhs var
            emitComment("looking up id:  " + simpleVar.name);
            if (simpleVar.decl == null) {
                emitRM("LD", ac, 0, fp, "load id value");
            } else {
                emitRM("LD", ac, simpleVar.decl.offset, fp, "load id value");
            }
            emitComment("<- id");
            emitRM("ST", ac, frameOffset, fp, "op: push left");
        }
    }

    public void visit(IndexVar indexVar, int level, boolean isAddr) {
        emitComment("-> subs");

        indexVar.index.accept(this, level, isAddr);

        emitComment("<- subs");
    }

    public void visit(NameType exp, int level, boolean isAddr) {

    }

    public void visit(NilExp exp, int level, boolean isAddr) {

    }

}
