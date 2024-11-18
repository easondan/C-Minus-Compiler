package absyn;

public class OpExp extends Exp {
  public final static int PLUS = 1;
  public final static int MINUS = 2;
  public final static int UMINUS = 3;
  public final static int MUL = 4;
  public final static int DIV = 5;
  public final static int EQ = 6;
  public final static int NE = 7;
  public final static int LT = 8;
  public final static int LE = 9;
  public final static int GT = 10;
  public final static int GE = 11;
  public final static int NOT = 12;
  public final static int AND = 13;
  public final static int OR = 14;
  public final static int TILDE = 15;

  public Exp left;
  public int op;
  public Exp right;

  public OpExp(int row, int col, Exp left, int op, Exp right) {
    this.row = row;
    this.col = col;
    this.left = left;
    this.op = op;
    this.right = right;
  }

  public void accept(AbsynVisitor visitor, int level,boolean isAddr) {
    visitor.visit(this, level,isAddr);
  }

  public String getOpSymbol(int row, int col) {
      return switch (this.op) {
          case PLUS -> " + ";
          case MINUS -> " - ";
          case UMINUS -> " - ";
          case MUL -> " * ";
          case DIV -> " / ";
          case EQ -> " = ";
          case NE -> " != ";
          case LT -> " < ";
          case LE -> " <= ";
          case GT -> " > ";
          case GE -> " >= ";
          case NOT -> " ! ";
          case AND -> " && ";
          case OR -> " || ";
          case TILDE -> " ~ ";
          default -> "Unrecognized operator at line " + row + " and column " + col;
      };
  }
}
