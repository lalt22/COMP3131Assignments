/*
 * IntExpr.java       
 *
 * Used for representing an expression that consists of a single IntLiteral.
 *
 * See, for example, t16.vc and t17.vc.
 *
 */

package VC.ASTs;

import VC.Scanner.SourcePosition;

public class IntExpr extends Expr {

  public IntLiteral IL;

  public IntExpr(IntLiteral ilAST, SourcePosition Position) {
    super (Position);
    IL = ilAST;
    IL.parent = this;
  }

  public Object visit(Visitor v, Object o) {
    return v.visitIntExpr(this, o);
  }

}
