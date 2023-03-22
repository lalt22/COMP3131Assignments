/*
 * SimpleVar.java      
 *
 * Used for representing a scalar or an array variable.
 *
 * See the test cases provided in the spec of Assignment 3:
 *
 * t18.vc
 * t46.vc
 *
 */

package VC.ASTs;

import VC.Scanner.SourcePosition;

public class SimpleVar extends Var {

  public Ident I;

  public SimpleVar(Ident idAST, SourcePosition thePosition) {
    super (thePosition);
    I = idAST;
    I.parent = this;
  }

  public Object visit (Visitor v, Object o) {
    return v.visitSimpleVar(this, o);
  }

}
