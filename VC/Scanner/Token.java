/*
 * Token.java   
 */

// ====== PLEASE DO NOT MODIFY THIS FILE =====

package VC.Scanner;

public final class Token extends Object {

  public int kind;
  public String spelling; // lexeme 
  public SourcePosition position;

  public Token(int kind, String spelling, SourcePosition position) {

    if (kind == Token.ID) {
      
      //currentKind = the kind we are checking against the list of keywords
      int currentKind = firstReservedWord;
      //we are searching for the type of this token
      boolean searching = true;

      while (searching) {
        //check if the input string is in the list of keywords
        int comparison = keywords[currentKind].compareTo(spelling);
        if (comparison == 0) {
          //if yes, set kind to current kind
          this.kind = currentKind;
          //no longer searching for type of token
          searching = false;
        } else if (comparison > 0 || currentKind == lastReservedWord) /*if doesnt match a keyword and on last reserved word, kind is not a keyword */ {
          this.kind = Token.ID;
          searching = false;
        } else {
          currentKind++;
        }
      }
    } else
      this.kind = kind;

    this.spelling = spelling;
    this.position = position;

  }

  public static String spell (int kind) {
    return keywords[kind];
  }

  public String toString() {
    return "Kind = " + kind + " [" + spell(kind) + 
          "], spelling = \"" + spelling + "\", position = " + position;
  }

  // Token classes...

  public static final int

    // reserved words - must be in alphabetical order...
    BOOLEAN		= 0,
    BREAK		= 1,
    CONTINUE		= 2,
    ELSE		= 3,
    FLOAT 		= 4,
    FOR                 = 5,
    IF			= 6,
    INT                 = 7,
    RETURN		= 8,
    VOID		= 9,
    WHILE		= 10,

    // operators
    PLUS		= 11,
    MINUS		= 12,
    MULT		= 13,
    DIV			= 14,
    NOT			= 15,
    NOTEQ		= 16,
    EQ			= 17,
    EQEQ		= 18,
    LT			= 19,
    LTEQ		= 20,
    GT			= 21,
    GTEQ		= 22,
    ANDAND		= 23,
    OROR		= 24,

    // separators
    LCURLY		= 25,
    RCURLY		= 26,
    LPAREN		= 27,
    RPAREN		= 28,
    LBRACKET 		= 29,
    RBRACKET            = 30,
    SEMICOLON		= 31,
    COMMA		= 32,

    // identifiers
    ID			= 33,

    // literals
    INTLITERAL 		= 34,
    FLOATLITERAL	= 35,
    BOOLEANLITERAL	= 36,
    STRINGLITERAL	= 37,


    // special tokens...
    ERROR		= 38,
    EOF			= 39;

  private static String[] keywords = new String[] {
    "boolean",
    "break",
    "continue",
    "else",
    "float",
    "for",
    "if",
    "int",
    "return",
    "void",
    "while",
    "+",
    "-",
    "*",
    "/",
    "!",
    "!=",
    "=",
    "==",
    "<",
    "<=",
    ">",
    ">=",
    "&&",
    "||",
    "{",
    "}",
    "(",
    ")",
    "[",
    "]",
    ";",
    ",",
    "<id>",
    "<int-literal>",
    "<float-literal>",
    "<boolean-literal>",
    "<string-literal>",
    "<error>",
    "$"
  };

    private final static int      firstReservedWord = Token.BOOLEAN,
    			          lastReservedWord  = Token.WHILE;
}
