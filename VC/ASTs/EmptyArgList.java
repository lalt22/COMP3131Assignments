/*
 * EmptyArgList.java      
 *
 * Used for representing an empty list of arguments in a call. 
 *
 * See t14.vc and its AST (displayed as EmptyAL) given in the spec of Assignment 3.
 */

package VC.ASTs;

import VC.Scanner.SourcePosition;

public class EmptyArgList extends List {

  public EmptyArgList(SourcePosition position) {
    super (position);
  }

  public Object visit(Visitor v, Object o) {
    return v.visitEmptyArgList(this, o);
  }

}
