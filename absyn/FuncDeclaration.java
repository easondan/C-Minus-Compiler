package absyn;

public class FuncDeclaration extends Declaration {
    public NameType nameType;
    public String function;
    public VarDecList params;
    public Exp body;

    public FuncDeclaration(int row, int col, NameType result, String func, VarDecList params, Exp body) {
        this.row = row;
        this.col = col;
        this.nameType = result;
        this.body = body;
        this.params = params;
        this.function = func;

    }
    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit( this, level );
    }
}