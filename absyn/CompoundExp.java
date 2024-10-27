package absyn;

public class CompoundExp extends Exp {
    public VarDecList varDecList;
    public ExpList expList;

    public CompoundExp(int row, int col, VarDecList decs, ExpList exps) {
        this.row = row;
        this.col = col;
        this.varDecList = decs;
        this.expList = exps;
    }

    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level);
    }
}