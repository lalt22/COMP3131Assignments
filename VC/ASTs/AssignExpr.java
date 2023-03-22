/*
 * AssignExpr.java       
 *
 * Used for representing an assignment expr defined in the VC grammar.
 *
 * See t24b.vc and its corresponding AST in the spec of Assignment 3.
 */

package VC.ASTs;

import VC.Scanner.SourcePosition;

public class AssignExpr extends Expr {

  /*
   * Given an Assignment expression: LHS = RHS, E1 will be set to point
   * to the AST for LHS and E2 to piont to the AST for RHS.
   */
  public Expr E1, E2;

  public AssignExpr (Expr e1AST, Expr e2AST, SourcePosition Position) {
    super (Position);
    E1 = e1AST;
    E2 = e2AST;
    E1.parent = E2.parent = this;
  }

  public Object visit(Visitor v, Object o) {
    return v.visitAssignExpr(this, o);
  }

}
