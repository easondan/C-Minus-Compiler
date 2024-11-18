package absyn;

public class FuncDeclaration extends Dec {
    public NameType nameType;
    public String function;
    public VarDecList params;
    public Exp body;
    public int funaddr;

    public FuncDeclaration(int row, int col, NameType result, String func, VarDecList params, Exp body) {
        this.row = row;
        this.col = col;
        this.nameType = result;
        this.body = body;
        this.params = params;
        this.function = func;

    }
    public void accept(AbsynVisitor visitor, int level,boolean isAddr) {
        visitor.visit(this, level,isAddr);
      }
}