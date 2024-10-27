package absyn;

public class VarExp extends Exp {
  public Var var;

  public VarExp(int row, int col, Var variable) {
    this.row = row;
    this.col = col;
    this.var = variable;
  }

  public void accept(AbsynVisitor visitor, int level) {
    visitor.visit(this, level);
  }
}
