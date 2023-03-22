/*
 * EmptyDeclList.java      
 *
 * Used for representing an empty DeclList.
 *
 * See t43.vc and t44.vc and their ASTs given in the spec of Assignment 3.
 *
 */

package VC.ASTs;

import VC.Scanner.SourcePosition;

public class EmptyDeclList extends List {

  public EmptyDeclList(SourcePosition Position) {
    super (Position);
  }

  public Object visit(Visitor v, Object o) {
    return v.visitEmptyDeclList(this, o);
  }

}
