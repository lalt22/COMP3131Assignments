/*
 * FloatExpr.java       
 *
 * Used for representing an expression that consists of a single FlowLiteral.
 *
 *
 * See t23.vc.
 *
 */

package VC.ASTs;

import VC.Scanner.SourcePosition;

public class FloatExpr extends Expr {

  public FloatLiteral FL;

  public FloatExpr(FloatLiteral flAST, SourcePosition Position) {
    super (Position);
    FL = flAST;
    FL.parent = this;
  }

  public Object visit(Visitor v, Object o) {
    return v.visitFloatExpr(this, o);
  }

}
