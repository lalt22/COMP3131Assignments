/*
 * ArrayExprList.java  
 *
 * Used for representing the list of expressions defined 
 * by the following productions in the VC grammar:
 *
 * initialiser         -> expr
 *                      |  "{" expr ( "," expr )* "}"
 *
 * Given the following array declaration:
 *
 * int a[] = {3, 4, 5};
 *
 * An ArrayExprList will be created to represent the list of three expressions, 
 * 3, 4 and 5, given inside { and }.
 *
 * See t43.vc and t44.vc and their ASTs given in the spec of Assignment 3.
 */

package VC.ASTs;

import VC.Scanner.SourcePosition;

public class ArrayExprList extends List {
  public Expr E;
  public List EL;

  // array index where this element should go
  public int index;

  public ArrayExprList(Expr eAST, List elAST, SourcePosition thePosition) {
    super (thePosition);
    E = eAST;
    EL = elAST;
    E.parent = EL.parent = this;
  }

  public Object visit(Visitor v, Object o) {
    return v.visitArrayExprList(this, o);
  }

}
