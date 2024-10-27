package absyn;

public class ReturnExp extends Exp {
    public Exp expression;
    
    public ReturnExp(int row, int col, Exp exp) {
        this.row = row;
        this.col = col;
        this.expression = exp;
    }

    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level);
    }
}