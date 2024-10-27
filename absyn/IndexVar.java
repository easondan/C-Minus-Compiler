package absyn;

public class IndexVar extends Var {
    public String name;
    public Exp index;

    public IndexVar (int row, int col,String name,Exp index){
        this.row = row;
        this.col = col;
        this.index = index;
        this.name = name;
    }
    
    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level);
    }
}
