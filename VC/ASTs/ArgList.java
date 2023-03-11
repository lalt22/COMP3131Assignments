/*
 * ArgList.java  
 *
 * Used for representing a non-empty list of arguments in a call.
 *
 * See t15.vc and its AST given in the spec of Assignment 3.
 */

package VC.ASTs;

import VC.Scanner.SourcePosition;

public class ArgList extends List {
  public Arg A;
  public List AL;

  public ArgList(Arg aAST, List alAST, SourcePosition thePosition) {
    super (thePosition);
    A = aAST;
    AL = alAST;
    A.parent = AL.parent = this;
  }

  public Object visit(Visitor v, Object o) {
    return v.visitArgList(this, o);
  }

}
