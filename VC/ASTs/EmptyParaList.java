/*
 * EmptyParaList.java      
 *
 * Used for representing an empty list of parameters in a function declaration.
 *
 * See t12.vc and its AST (displayed as EmptyPL) given in the spec of Assignment 3.
 */

package VC.ASTs;

import VC.Scanner.SourcePosition;

public class EmptyParaList extends List {

  public EmptyParaList(SourcePosition Position) {
    super (Position);
  }

  public Object visit(Visitor v, Object o) {
    return v.visitEmptyParaList(this, o);
  }

}
