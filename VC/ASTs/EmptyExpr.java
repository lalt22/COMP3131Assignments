/*
 * EmptyExpr.java 
 *
 * Used for representing an expression that is not explicitly given:
 *
 * See the following test cases given in the spec of Assignment 3.
 *
 * t2.vc
 * t7.vc
 * t43.vc
 * t31.vc
 * t32.vc
 * t34.vc
 * t35.vc
 *
 */

package VC.ASTs;

import VC.Scanner.SourcePosition;

public class EmptyExpr extends Expr {

  public EmptyExpr (SourcePosition thePosition) {
    super (thePosition);
  }

  public Object visit(Visitor v, Object o) {
    return v.visitEmptyExpr(this, o);
  }
}
