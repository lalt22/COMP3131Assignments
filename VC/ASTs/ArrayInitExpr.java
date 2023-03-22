/*
 * ArrayInitExpr.java  
 *
 * Used for representing an expression provided for initialising an array
 * variable according to the the following production in the VC grammar:
 *
 * initialiser         -> "{" expr ( "," expr )* "}"
 *
 * Given the following two declarations, where arrays a and b are declared:
 *
 * int a[2] = {1, 2}; 
 * int b = {3,4,5};
 *
 * {1, 2} and {3, 4, 5} are two array initialisation expressions, i.e., ArrayInitExprs.
 *
 * See t43.vc and t44.vc and their ASTs given in the spec of Assignment 3.
 *
 * When an ArrayInitExpr node is drawn, ArrayInitExpr is abbreviated to ArrInitExpr.
 *
 * See also ArrayExprList.java.
 */

package VC.ASTs;

import VC.Scanner.SourcePosition;

public class ArrayInitExpr extends Expr {

  public List IL;

  public ArrayInitExpr (List ilAST, SourcePosition position) {
    super (position);
    IL = ilAST;
    IL.parent = this;
  }

  public Object visit(Visitor v, Object o) {
    return v.visitArrayInitExpr(this, o);
  }

}
