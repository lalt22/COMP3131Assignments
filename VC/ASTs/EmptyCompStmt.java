/*
 * EmptyCompStmt.java      
 *
 * Used for representing an empty compound statement, i.e., { }.
 *
 * See t4.vc and t5.vc and their ASTs given in the spec of Assignment 3.
 */

package VC.ASTs;

import VC.Scanner.SourcePosition;

public class EmptyCompStmt extends Stmt {

  public EmptyCompStmt(SourcePosition Position) {
    super (Position);
  }

  public Object visit(Visitor v, Object o) {
    return v.visitEmptyCompStmt(this, o);
  }
}
