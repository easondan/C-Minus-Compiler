import absyn.*;

public class ShowTreeVisitor implements AbsynVisitor {
  final static int SPACES = 4;

  private void indent(int level) {
    for (int i = 0; i < level * SPACES; i++) {
        System.out.print(" ");
    }
  }

  public void visit(FuncDeclaration funcDec, int level,boolean isAddr) {
    System.out.println("FuncDeclaration: ");
    level++;
    indent(level);
    if (funcDec.nameType != null) {
      System.out.println("Name: " + funcDec.function);
      funcDec.nameType.accept(this, level,isAddr);
    }
    
    if(funcDec.params != null) {
      funcDec.params.accept(this, level,isAddr);
    }
    
    if(funcDec.body != null) {
      funcDec.body.accept(this, level,isAddr);
    }
  }

  /***********  VarDec Subclasses ****************/
  public void visit(SimpleDec simpDec, int level,boolean isAddr){
    indent(level);
    System.out.println("SimpleDec: ");
    level++;

    indent(level);
    System.out.println("Name: " + simpDec.name);
    visit(simpDec.nameType, level,isAddr);
  }
  
  public void visit(ArrayDec arrDec, int level,boolean isAddr ){
    indent(level);
    System.out.println("ArrayDec: ");
    level++;

    visit(arrDec.nameType, level,isAddr);

    indent(level);
    System.out.println("Name: " + arrDec.name);

    indent(level);
    System.out.println("Size: " + arrDec.size);

  }
  /***********  VarDec Subclasses ****************/

  /***********  Exp Subclasses ****************/
  public void visit (AssignExp exp, int level,boolean isAddr){
    indent(level);
    System.out.println("AssignExp: ");

    level++;
    exp.lhs.accept(this, level,isAddr);
    exp.rhs.accept(this, level,isAddr);
  }

  public void visit(IfExp exp, int level,boolean isAddr){
    indent(level);
    System.out.println("IfExp: ");
    level++;

    exp.test.accept(this, level,isAddr);
    exp.thenpart.accept(this, level,isAddr);
    if (exp.elsepart != null) {
      exp.elsepart.accept(this, level,isAddr);
    }
  }

  public void visit(IntExp exp, int level,boolean isAddr){
    indent(level);
    System.out.println("IntExp: " + exp.value);
  }

  public void visit(OpExp exp, int level,boolean isAddr){
    indent(level);
    System.out.print("OpExp:");

    System.out.println(exp.getOpSymbol(exp.row, exp.col));

    level++;
    if (exp.left != null) {
      exp.left.accept(this, level,isAddr);
    }
    exp.right.accept(this, level,isAddr);
  }

  public void visit(VarExp exp, int level,boolean isAddr){
    indent(level);
    System.out.println("VarExp: ");
    if (exp.var != null) {
      level++;
      exp.var.accept(this, level,isAddr);
    }
  }

  public void visit(BoolExp exp, int level,boolean isAddr){
    indent(level);
    System.out.println("BoolExp: " + exp.value);
  }

  public void visit(CallExp exp, int level,boolean isAddr){
    indent(level);
    System.out.println("CallExp: " + exp.func);
    level++;

    if (exp.args != null) {
      exp.args.accept(this, level,isAddr);
    }
  }

  public void visit(CompoundExp cmpExp, int level,boolean isAddr){
    indent(level);
    System.out.println("CompoundExp: ");
    level++;

    if (cmpExp.varDecList != null) {
      cmpExp.varDecList.accept(this, level,isAddr);
    } else {
      System.out.println("cmpstmt NULL");
    }

    if (cmpExp.expList != null) {
      cmpExp.expList.accept(this, level,isAddr);
    } else {
      System.out.println("expList NULL");
    }
  }

  public void visit(WhileExp exp,int level,boolean isAddr){
    indent(level);
    System.out.println("WhileExp: ");
    level++;
    if (exp.test != null) {
      exp.test.accept(this, level,isAddr);
    }
    if (exp.body != null) {
      exp.body.accept(this, level,isAddr);
    }
  }

  public void visit (NilExp exp, int level,boolean isAddr){
    indent(level);
    System.out.println("NilExp: ");
  }

  public void visit (ReturnExp returnExp, int level,boolean isAddr){
    indent(level);
    System.out.println("ReturnExp: ");

    level++;
    if (returnExp.expression != null) {
      returnExp.expression.accept(this, level,isAddr);
    }
  }
  /***********  Exp Subclasses ****************/

  /***********  Misc Classes   ****************/
  public void visit(VarDecList varList, int level,boolean isAddr){
    while (varList != null) {
      if (varList.head != null) {
        varList.head.accept(this, level,isAddr);
      }
      varList = varList.tail;
    }
  }

  public void visit(DecList decList, int level,boolean isAddr){
    while(decList != null) {
      if (decList.head != null) {
        decList.head.accept(this, level,isAddr);
      }
      decList = decList.tail;
    }
  }

  public void visit(ExpList exp, int level,boolean isAddr){
    while (exp != null) {
      if (exp.head != null) {
        exp.head.accept(this, level,isAddr);
      }
      exp = exp.tail;
    }
  }

  public void visit (NameType nameType, int level,boolean isAddr){
    int type = nameType.type;

    indent(level);
    if (type == NameType.BOOL) {
      System.out.println("NameType: BOOL");
    } else if (type == NameType.INT) {
      System.out.println("NameType: INT");
    } else if (type == NameType.VOID) {
      System.out.println("NameType: VOID");
    }
  }
  /***********  Misc Classes   ****************/


  @Override
  public void visit(SimpleVar simpleVar, int level,boolean isAddr) {
    indent(level);
    System.out.println("SimpleVar: " + simpleVar.name);
  }


  public void visit(IndexVar indexVar, int level,boolean isAddr) {
    indent(level);
    System.out.println("IndexVar: " + indexVar.name);
    level++;
    indexVar.index.accept(this, level,isAddr);
  }


}
