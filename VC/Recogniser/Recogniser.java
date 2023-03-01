/***
 ***
 *** Recogniser.java            
 ***
 ***/
//Eliminate left recursion
//Make VC grammar LL(1)
/* At this stage, this parser accepts a subset of VC defined	by
 * the following grammar. 
 *
 * You need to modify the supplied parsing methods (if necessary) and 
 * add the missing ones to obtain a parser for the VC language.
 *
 * 19-Feb-2022

program       -> func-decl

// declaration

func-decl     -> void identifier "(" ")" compound-stmt

identifier    -> ID

// statements 
compound-stmt -> "{" stmt* "}" 
stmt          -> continue-stmt
    	      |  expr-stmt
continue-stmt -> continue ";"
expr-stmt     -> expr? ";"

// expressions 
expr                -> assignment-expr
assignment-expr     -> additive-expr
additive-expr       -> multiplicative-expr
                    |  additive-expr "+" multiplicative-expr
multiplicative-expr -> unary-expr
	            |  multiplicative-expr "*" unary-expr
unary-expr          -> "-" unary-expr
		    |  primary-expr

primary-expr        -> identifier
 		    |  INTLITERAL
		    | "(" expr ")"
*/

package VC.Recogniser;

import VC.Scanner.Scanner;
import VC.Scanner.SourcePosition;
import VC.Scanner.Token;
import VC.ErrorReporter;

public class Recogniser {

  private Scanner scanner;
  private ErrorReporter errorReporter;
  private Token currentToken;

  public Recogniser (Scanner lexer, ErrorReporter reporter) {
    scanner = lexer;
    errorReporter = reporter;

    currentToken = scanner.getToken();
  }

// match checks to see f the current token matches tokenExpected.
    //currentToken initialised to leftmost token in program
// If so, fetches the next token.
// If not, reports a syntactic error.

  void match(int tokenExpected) throws SyntaxError {
    if (currentToken.kind == tokenExpected) {
      currentToken = scanner.getToken();
    } else {
      syntacticError("\"%\" expected here", Token.spell(tokenExpected));
    }
  }

 // accepts the current token and fetches the next
  void accept() {
    currentToken = scanner.getToken();
  }

  void syntacticError(String messageTemplate, String tokenQuoted) throws SyntaxError {
    SourcePosition pos = currentToken.position;
    errorReporter.reportError(messageTemplate, tokenQuoted, pos);
    throw(new SyntaxError());
  }

// ========================== PROGRAMS ========================

    //change to (func-decl | var-decl)*
  public void parseProgram() {
      System.out.println("In recogniser");


    try {
      parseFuncDecl();
      if (currentToken.kind != Token.EOF) {
        syntacticError("\"%\" wrong result type for a function", currentToken.spelling);
      }
    }
    catch (SyntaxError s) {  }
  }

// ========================== DECLARATIONS ========================
//CHANGE THIS: func-decl -> type identifier para-list compound-stmt
  void parseFuncDecl() throws SyntaxError {
    System.out.println("Parsing FuncDecl: "+ currentToken);


    parseType();
    parseIdent();
    parseParaList();
    parseCompoundStmt();
  }

  void parseVarDecl() throws SyntaxError {

  }

  void parseInitDeclaratorList() throws SyntaxError {

  }

  void parseInitDeclarator() throws SyntaxError {

  }

  void parseDeclarator() throws SyntaxError {

  }

  void parseInitialiser() throws SyntaxError {

  }

// ======================= STATEMENTS ==============================

//CHANGE: compound-stmt -> "{" var-decl*stmt* "}"
  void parseCompoundStmt() throws SyntaxError {
    System.out.println("Parsing Compound Statement: " + currentToken);
    match(Token.LCURLY);
    parseVarDecl();
    parseStmtList();
    match(Token.RCURLY);
  }

 // Here, a new nontermial has been introduced to define { stmt } *
  void parseStmtList() throws SyntaxError {
    System.out.println("Parsing Statement List:" + currentToken);
    while (currentToken.kind != Token.RCURLY) 
      parseStmt();
  }

  //Complete this w if, for, return etc
  void parseStmt() throws SyntaxError {
    System.out.println("Parsing statement: "+ currentToken);
    switch (currentToken.kind) {

    case Token.CONTINUE:
      parseContinueStmt();
      break;


    default:
      parseCompoundStmt();
      break;

    }
  }
    void parseIfStmt() throws SyntaxError {

    }

    void parseForStmt() throws SyntaxError {

    }

    void parseWhileStmt() throws SyntaxError {

    }

    void parseBreakStmt() throws SyntaxError {

    }

  void parseContinueStmt() throws SyntaxError {
      System.out.println("Parsing continue statement: "+ currentToken);
    match(Token.CONTINUE);
    match(Token.SEMICOLON);

  }

  void parseReturnStmt() throws SyntaxError {

  }

  //what is going on here
  void parseExprStmt() throws SyntaxError {
    System.out.println("Parsing expr statement: " + currentToken);
    if (currentToken.kind == Token.ID
        || currentToken.kind == Token.INTLITERAL
        || currentToken.kind == Token.MINUS
        || currentToken.kind == Token.LPAREN) {
        parseExpr();
        match(Token.SEMICOLON);
    } else {
      match(Token.SEMICOLON);
    }
  }


// ======================= IDENTIFIERS ======================

 // Call parseIdent rather than match(Token.ID). 
 // In Assignment 3, an Identifier node will be constructed in here.


  void parseIdent() throws SyntaxError {
    System.out.println("Parsing ID: " + currentToken);
    if (currentToken.kind == Token.ID) {
      accept();
    } else 
      syntacticError("identifier expected here", "");
  }

  void parseType() throws SyntaxError {
      System.out.println("Parsing type declaration");
      if (currentToken.kind == Token.INT ||currentToken.kind == Token.FLOAT ||currentToken.kind == Token.BOOLEAN ||currentToken.kind == Token.VOID) {
          System.out.println("Type: " + currentToken.spelling);
          accept();
      }
  }

// ======================= OPERATORS ======================

 // Call acceptOperator rather than accept(). 
 // In Assignment 3, an Operator Node will be constructed in here.

  void acceptOperator() throws SyntaxError {

    currentToken = scanner.getToken();
  }


// ======================= EXPRESSIONS ======================

  void parseExpr() throws SyntaxError {
    parseAssignExpr();
  }


  //CHANGE: assignment-expr -> (cond-or-expr "=")*cond-or-expr
  void parseAssignExpr() throws SyntaxError {

    parseAdditiveExpr();

  }

  void parseCondOrExprPrime() throws SyntaxError {
      if (currentToken.kind == Token.OROR) {
          accept();
          parseCondAndExpr();
          parseCondOrExprPrime();
      }
  }
  void parseCondOrExpr() throws SyntaxError {
        parseCondAndExpr();
        parseCondOrExprPrime();
  }

  void parseCondAndExprPrime() throws SyntaxError {
      if (currentToken.kind == Token.ANDAND) {
          accept();
          parseEqualityExpr();
          parseCondAndExprPrime();
      }
  }
  void parseCondAndExpr() throws SyntaxError {
        parseEqualityExpr();
        parseCondAndExprPrime();
  }

  void parseEqualityExprPrime() throws SyntaxError {
      if (currentToken.kind == Token.EQEQ || currentToken.kind == Token.NOTEQ) {
          accept();
          parseRelExpr();
          parseEqualityExprPrime();
      }
  }
  void parseEqualityExpr() throws SyntaxError {
        parseRelExpr();
        parseEqualityExprPrime();
  }

  void parseRelExprPrime() throws SyntaxError {
      if (currentToken.kind == Token.LT ||currentToken.kind == Token.LTEQ ||currentToken.kind == Token.GT ||currentToken.kind == Token.GTEQ) {
          accept();
          parseAdditiveExpr();
          parseRelExprPrime();
      }


  }
  void parseRelExpr() throws SyntaxError {
        parseAdditiveExpr();
        parseRelExprPrime();
  }

  void parseAdditiveExprPrime() throws SyntaxError {
      if (currentToken.kind == Token.PLUS || currentToken.kind == Token.MINUS) {
          acceptOperator();
          parseMultiplicativeExpr();
          parseAdditiveExprPrime();
      }
      
    }
  void parseAdditiveExpr() throws SyntaxError {

    parseMultiplicativeExpr();
    parseAdditiveExprPrime();

  }

  void parseMultiplicativeExprPrime() throws SyntaxError {
      if (currentToken.kind == Token.MULT || currentToken.kind == Token.DIV) {
          accept();
          parseUnaryExpr();
          parseMultiplicativeExprPrime();
      }
  }
  void parseMultiplicativeExpr() throws SyntaxError {

    parseUnaryExpr();
    parseMultiplicativeExprPrime();
  }

  void parseUnaryExpr() throws SyntaxError {

    switch (currentToken.kind) {
      case Token.MINUS:
        {
          acceptOperator();
          parseUnaryExpr();
        }
        break;

        case Token.PLUS:
            acceptOperator();
            parseUnaryExpr();
            break;

        case Token.NOT:
            accept();
            parseUnaryExpr();
            break;

      default:
        parsePrimaryExpr();
        break;
       
    }
  }

  void parsePrimaryExpr() throws SyntaxError {
    switch (currentToken.kind) {

      case Token.ID:
        parseIdent();
        break;

      case Token.LPAREN:
        {
          accept();
          parseExpr();
	  match(Token.RPAREN);
        }
        break;

      case Token.INTLITERAL:
        parseIntLiteral();
        break;

        case Token.FLOATLITERAL:
            parseFloatLiteral();
            break;

        case Token.BOOLEANLITERAL:
            parseBooleanLiteral();
            break;

        case Token.STRINGLITERAL:
            parseStringLiteral();
            break;

      default:
        syntacticError("illegal parimary expression", currentToken.spelling);
       
    }
  }

// ========================== LITERALS ========================

  // Call these methods rather than accept().  In Assignment 3, 
  // literal AST nodes will be constructed inside these methods. 

  void parseIntLiteral() throws SyntaxError {

    if (currentToken.kind == Token.INTLITERAL) {
      accept();
    } else 
      syntacticError("integer literal expected here", "");
  }

  void parseFloatLiteral() throws SyntaxError {

    if (currentToken.kind == Token.FLOATLITERAL) {
      accept();
    } else 
      syntacticError("float literal expected here", "");
  }

  void parseBooleanLiteral() throws SyntaxError {

    if (currentToken.kind == Token.BOOLEANLITERAL) {
      accept();
    } else 
      syntacticError("boolean literal expected here", "");
  }

  void parseVoidLiteral() throws SyntaxError {
      if (currentToken.kind == Token.VOID) {
          accept();
      }else
          syntacticError("void expected here", "");
  }

  void parseStringLiteral() throws SyntaxError {
      if (currentToken.kind == Token.STRINGLITERAL) {
          accept();
      }else
          syntacticError("string literal expected here", "");

  }

  //===================== PARAMETERS ========================
    void parseParaList() throws SyntaxError {
      System.out.println("Parsing parameter list");
        match(Token.LPAREN);
        parseProperParaList();
        match(Token.RPAREN);
    }

    void parseProperParaList() throws SyntaxError {
        parseParaDecl();
        while (currentToken.kind != Token.RPAREN) {
            match(Token.COMMA);
            parseParaDecl();
        }
    }

    void parseParaDecl() throws SyntaxError {
        parseType();
        parseDeclarator();
    }

    void parseArgList() throws SyntaxError {
        match(Token.LPAREN);
        parseProperArgList();
        match(Token.RPAREN);
    }

    void parseProperArgList() throws SyntaxError {
        parseArg();
        while (currentToken.kind != Token.RPAREN) {
            parseArg();
        }
    }

    void parseArg() throws SyntaxError {
        parseExpr();
    }



}
