/*
 * Arg.java               
 *
 * Used for representing an argument in a call.
 *
 * See t14.vc and t15.vc and their ASTs given in the spec of Assignment 3.
 */

package VC.ASTs;

import VC.Scanner.SourcePosition;

public class Arg extends Expr {

  public Expr E;

  public Arg (Expr eAST, SourcePosition position) {
    super (position);
    E = eAST;
    eAST.parent = this;
  }

  public Object visit(Visitor v, Object o) {
    return v.visitArg(this, o);
  }

}
