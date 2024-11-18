package absyn;

public class SimpleDec extends VarDec {
    public NameType nameType;
    public String name;

    public SimpleDec(int row, int col, NameType type, String name) {
        this.row = row;
        this.col = col;
        this.nameType = type;
        this.name = name;
    }

    public void accept(AbsynVisitor visitor, int level,boolean isAddr) {
        visitor.visit(this, level,isAddr);
      }
}