package absyn;

public class ArrayDec extends VarDec {
  public NameType nameType;
  public String name;
  public int size;

  public ArrayDec(int row, int col, NameType type, String name, int size) {
    this.row = row;
    this.col = col;
    this.nameType = type;
    this.name = name;
    this.size = size;
  }

   public void accept(AbsynVisitor visitor, int level,boolean isAddr) {
    visitor.visit(this, level,isAddr);
  }
}