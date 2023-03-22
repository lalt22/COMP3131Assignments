/*
 * EmptyArrayExprList.java      
 *
 * Used for representing an empty ArrayExprList.
 *
 * See t43.vc and t44.vc and their ASTs given in the spec of Assignment 3.
 * 
 */

package VC.ASTs;

import VC.Scanner.SourcePosition;

public class EmptyArrayExprList extends List {

  public EmptyArrayExprList(SourcePosition Position) {
    super (Position);
  }

  public Object visit(Visitor v, Object o) {
    return v.visitEmptyArrayExprList(this, o);
  }

}
