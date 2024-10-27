package absyn;

public class NameType extends Exp {
    public int type;
    public final static int BOOL = 1;
    public final static int INT = 2;
    public final static int VOID = 3;

    public NameType(int row, int col, int type) {
        this.row = row;
        this.col = col;
        this.type = type;
    }
    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level);
      }
}