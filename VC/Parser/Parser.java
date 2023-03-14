/*
 +--------------+
 + Parser.java  +
 +--------------+
 *
 * PLEASE COMPARE Recogniser.java PROVIDED IN ASSIGNMENT 2 AND Parser.java
 * PROVIDED BELOW TO UNDERSTAND HOW THE FORMER IS MODIFIED TO OBTAIN THE LATTER.
 *
 * This parser for a subset of the VC language is intended to 
 *  demonstrate how to create the AST nodes, including (among others): 
 *  (1) a list (of statements)
 *  (2) a function
 *  (3) a statement (which is an expression statement), 
 *  (4) a unary expression
 *  (5) a binary expression
 *  (6) terminals (identifiers, integer literals and operators)
 *
 * In addition, it also demonstrates how to use the two methods start 
 * and finish to determine the position information for the start and 
 * end of a construct (known as a phrase) corresponding an AST node.
 *
 * NOTE THAT THE POSITION INFORMATION WILL NOT BE MARKED. HOWEVER, IT CAN BE
 * USEFUL TO DEBUG YOUR IMPLEMENTATION.
 *
 *
 * --- 3/3/2023 --- 


program       -> func-decl
func-decl     -> type identifier "(" ")" compound-stmt
type          -> void
identifier    -> ID
// statements
compound-stmt -> "{" stmt* "}" 
stmt          -> expr-stmt
expr-stmt     -> expr? ";"
// expressions 
expr                -> additive-expr
additive-expr       -> multiplicative-expr
                    |  additive-expr "+" multiplicative-expr
                    |  additive-expr "-" multiplicative-expr
multiplicative-expr -> unary-expr
	            |  multiplicative-expr "*" unary-expr
	            |  multiplicative-expr "/" unary-expr
unary-expr          -> "-" unary-expr
		    |  primary-expr

primary-expr        -> identifier
 		    |  INTLITERAL
		    | "(" expr ")"
 */

package VC.Parser;

import VC.ASTs.*;
import VC.ErrorReporter;
import VC.Scanner.Scanner;
import VC.Scanner.SourcePosition;
import VC.Scanner.Token;

public class Parser {

  private Scanner scanner;
  private ErrorReporter errorReporter;
  private Token currentToken;
  private SourcePosition previousTokenPosition;
  private SourcePosition dummyPos = new SourcePosition();

  public Parser (Scanner lexer, ErrorReporter reporter) {
    scanner = lexer;
    errorReporter = reporter;

    previousTokenPosition = new SourcePosition();

    currentToken = scanner.getToken();
  }

  boolean showLog = true;
  void log(Object o) {
    if (showLog) {
      System.out.println(o);
    }
  }

// match checks to see f the current token matches tokenExpected.
// If so, fetches the next token.
// If not, reports a syntactic error.

  void match(int tokenExpected) throws SyntaxError {
    if (currentToken.kind == tokenExpected) {
      previousTokenPosition = currentToken.position;
      currentToken = scanner.getToken();
    } else {
      syntacticError("\"%\" expected here", Token.spell(tokenExpected));
    }
  }

  void accept() {
    previousTokenPosition = currentToken.position;
    currentToken = scanner.getToken();
  }

  void syntacticError(String messageTemplate, String tokenQuoted) throws SyntaxError {
    SourcePosition pos = currentToken.position;
    errorReporter.reportError(messageTemplate, tokenQuoted, pos);
    throw(new SyntaxError());
  }

// start records the position of the start of a phrase.
// This is defined to be the position of the first
// character of the first token of the phrase.

  void start(SourcePosition position) {
    position.lineStart = currentToken.position.lineStart;
    position.charStart = currentToken.position.charStart;
  }

// finish records the position of the end of a phrase.
// This is defined to be the position of the last
// character of the last token of the phrase.

  void finish(SourcePosition position) {
    position.lineFinish = previousTokenPosition.lineFinish;
    position.charFinish = previousTokenPosition.charFinish;
  }

  void copyStart(SourcePosition from, SourcePosition to) {
    to.lineStart = from.lineStart;
    to.charStart = from.charStart;
  }

// ========================== PROGRAMS ========================

  public Program parseProgram1() {
    log("In parseProgram");
    Program programAST = null;
    
    SourcePosition programPos = new SourcePosition();
    start(programPos);

    try {
      List dlAST = parseFuncDeclList();
      finish(programPos);
      programAST = new Program(dlAST, programPos); 
      if (currentToken.kind != Token.EOF) {
        syntacticError("\"%\" unknown type", currentToken.spelling);
      }
    }
    catch (SyntaxError s) { return null; }
    return programAST;
  }

  public Program parseProgram() {
    Program programAST = null;
    SourcePosition programPos = new SourcePosition();
    start(programPos);

    if (currentToken.kind == Token.EOF) {
      finish(programPos);
      List emptyList = new EmptyDeclList(programPos);
      programAST = new Program(emptyList, programPos);
      return programAST;
    }
    try {
        Decl funcVarDecl = parseFuncOrVarDecl();
        List declList = new DeclList(funcVarDecl, new EmptyDeclList(programPos), programPos);
        while (currentToken.kind != Token.EOF) {
           funcVarDecl = parseFuncOrVarDecl();
           declList = new DeclList(funcVarDecl, declList, programPos);
        }
        finish(programPos);
        programAST = new Program(declList, programPos);

//      if (currentToken.kind != Token.EOF) {
//        syntacticError("\"%\" unknown type", currentToken.spelling);
//      }
    } catch (SyntaxError s) {return null;}
    return programAST;
  }

// ========================== DECLARATIONS ========================
  Decl parseFuncOrVarDecl() throws SyntaxError {
    log("In parseFuncOrVarDecl");
    SourcePosition fvPos = new SourcePosition();
    start(fvPos);
    Decl fvlAST = null;



    Type tAST = parseType();
    Ident iAST = parseIdent();

    if (currentToken.kind == Token.LPAREN) {
      List plAST = new EmptyParaList(fvPos);
      accept();
      if (currentToken.kind != Token.RPAREN) {
        plAST = parseParaList();
      }
      match(Token.RPAREN);
      Stmt cAST = parseCompoundStmt();
      finish(fvPos);
      fvlAST= new FuncDecl(tAST, iAST, plAST, cAST, fvPos);
    }
    //do varDecl
    else {
      // TODO:
      log("Parsing variable declaration");
    }
    return fvlAST;
  }

  Expr parseVarDeclExpr() throws SyntaxError {
    Expr eAST = null;

    IntLiteral intLiteral = null;

    SourcePosition declPos = new SourcePosition();
    start(declPos);

    if (currentToken.kind == Token.LBRACKET) {
      match(Token.LBRACKET);
      if (currentToken.kind != Token.RBRACKET) {
        intLiteral = parseIntLiteral();
      }
      match(Token.RBRACKET);
    }

    if (currentToken.kind == Token.EQ) {
      match(Token.EQ);
      List initList = parseInitialiser();
      finish(declPos);
      eAST = new ArrayInitExpr(initList,declPos);
    }



    return eAST;
  }

  List parseInitialiser() throws SyntaxError {
    log("Parsing initialiser");
    SourcePosition initPos = new SourcePosition();
    start(initPos);

    List aelAST = new EmptyArrayExprList(initPos);
    Expr aiAST = null;


    if (currentToken.kind == Token.LCURLY) {
      match(Token.LCURLY);
      aiAST = parseExpr();
      aelAST = new ArrayExprList(aiAST, aelAST, initPos);
      if (currentToken.kind == Token.COMMA) {
        while (currentToken.kind != Token.RCURLY) {
          match(Token.COMMA);
          aiAST = parseExpr();
          finish(initPos);
          aelAST = new ArrayExprList(aiAST, aelAST, initPos);
        }
      }
      match(Token.RCURLY);
    } else {
      aiAST = parseExpr();
      finish(initPos);
      aelAST = new ArrayExprList(aiAST, aelAST, initPos);
    }

    return aelAST;
  }


  List parseDeclList() throws SyntaxError {
    List decListAST= null;
    Decl dAST = null;
    SourcePosition decPos = new SourcePosition();
    start(decPos);



    return decListAST;
  }
  List parseFuncDeclList() throws SyntaxError {
    log("In parseFuncDeclList");
    List dlAST = null;
    Decl dAST = null;

    SourcePosition funcPos = new SourcePosition();
    start(funcPos);

    dAST = parseFuncDecl();
    
    if (nextTokenIsType()) {
      dlAST = parseFuncDeclList();
      finish(funcPos);
      dlAST = new DeclList(dAST, dlAST, funcPos);
    } else {
      finish(funcPos);
      dlAST = new DeclList(dAST, new EmptyDeclList(dummyPos), funcPos);
    }
    if (dlAST == null) 
      dlAST = new EmptyDeclList(dummyPos);

    return dlAST;
  }

  Decl parseFuncDecl() throws SyntaxError {
    log("In parseFuncDecl");
    Decl fAST = null;

    SourcePosition funcPos = new SourcePosition();
    start(funcPos);
    List fplAST = new EmptyParaList(funcPos);

    Type tAST = parseType();
    Ident iAST = parseIdent();
    match(Token.LPAREN);
    if (currentToken.kind != Token.RPAREN) {
      fplAST = parseParaList();
    }
    match(Token.RPAREN);


    Stmt cAST = parseCompoundStmt();
    finish(funcPos);
    fAST = new FuncDecl(tAST, iAST, fplAST, cAST, funcPos);
    log("Leaving parseFuncDecl");
    return fAST;
  }



//  ======================== TYPES ==========================

  Type parseType() throws SyntaxError {
    log("In parseType");
    Type typeAST = null;

    SourcePosition typePos = new SourcePosition();
    start(typePos);

    if (currentToken.kind == Token.VOID) {
      log("Type " + currentToken.spelling);
      accept();
      finish(typePos);
      typeAST = new VoidType(typePos);
    }
    else if (currentToken.kind == Token.INT) {
      log("Type " + currentToken.spelling);
      accept();
      finish(typePos);
      typeAST = new IntType(typePos);
    }
    else if (currentToken.kind == Token.BOOLEAN) {
      log("Type " + currentToken.spelling);
      accept();
      finish(typePos);
      typeAST = new BooleanType(typePos);
    }
    else if (currentToken.kind == Token.FLOAT) {
      log("Type " + currentToken.spelling);
      accept();
      finish(typePos);
      typeAST = new FloatType(typePos);
    }
    else {return null;}



    return typeAST;
    }


// ======================= STATEMENTS ==============================

  Stmt parseCompoundStmt() throws SyntaxError {
    log("In parseCompoundStmt");
    Stmt cAST = null;

    SourcePosition stmtPos = new SourcePosition();
    start(stmtPos);
    List varDeclAST = new EmptyDeclList(stmtPos);
    List stmtListAST = new EmptyStmtList(stmtPos);


    match(Token.LCURLY);
    while(currentToken.kind != Token.RCURLY) {
      while (nextTokenIsType()) {
        log("Calling funcOrVarDecl from compoundstmt");
        Decl varDecl = parseFuncOrVarDecl();
        varDeclAST = new DeclList(varDecl, varDeclAST, stmtPos);
      }
      stmtListAST = parseStmtList();
    }

    match(Token.RCURLY);
    finish(stmtPos);

    if (stmtListAST instanceof EmptyStmtList && varDeclAST instanceof EmptyDeclList)
      cAST = new EmptyCompStmt(stmtPos);
    else
      cAST = new CompoundStmt(varDeclAST, stmtListAST, stmtPos);

    return cAST;
  }


  List parseStmtList() throws SyntaxError {
    log("In parseStmtList");
    List slAST = null; 

    SourcePosition stmtPos = new SourcePosition();
    start(stmtPos);

    if (currentToken.kind != Token.RCURLY) {
      Stmt sAST = parseStmt();
      {
        if (currentToken.kind != Token.RCURLY) {
          slAST = parseStmtList();
          finish(stmtPos);
          slAST = new StmtList(sAST, slAST, stmtPos);
        } else {
          finish(stmtPos);
          slAST = new StmtList(sAST, new EmptyStmtList(dummyPos), stmtPos);
        }
      }
    }
    else
      slAST = new EmptyStmtList(dummyPos);
    
    return slAST;
  }

  Stmt parseStmt() throws SyntaxError {
    log("In parseStmt");
    Stmt sAST = null;

    switch (currentToken.kind) {
      case Token.IF:
        sAST = parseIfStmt();
        break;

      case Token.FOR:
        sAST = parseForStmt();
        break;

      case Token.WHILE:
        sAST = parseWhileStmt();
        break;

      case Token.BREAK:
        sAST = parseBreakStmt();
        break;

      case Token.CONTINUE:
        sAST = parseContinueStmt();
        break;

      case Token.RETURN:
        sAST = parseReturnStmt();
        break;

      default:
        if (currentToken.kind == Token.LCURLY) {
          sAST = parseCompoundStmt();
        } else {
          sAST = parseExprStmt();
        }
        break;
    }

    return sAST;
  }

  Stmt parseIfStmt() throws SyntaxError {
    Stmt ifAST = null;
    SourcePosition stmtPos = new SourcePosition();
    start(stmtPos);

    match(Token.IF);
    match(Token.LPAREN);
    Expr eAST = parseExpr();
    match(Token.RPAREN);
    Stmt sAST = parseStmt();
    Stmt sAST2 = null;

    if (currentToken.kind == Token.ELSE) {
      match(Token.ELSE);
      sAST2 = parseStmt();
      finish(stmtPos);
      ifAST = new IfStmt(eAST, sAST, sAST2, stmtPos);
      return ifAST;
    }
    finish(stmtPos);
    ifAST = new IfStmt(eAST, sAST, stmtPos);
    return  ifAST;
  }

  Stmt parseForStmt() throws SyntaxError {
    Stmt forAST = null;
    SourcePosition stmtPos = new SourcePosition();
    start(stmtPos);
    Expr eAST1 = null;
    Expr eAST2 = null;
    Expr eAST3 = null;
    match(Token.FOR);
    match(Token.LPAREN);
    int forSections = 0;
    while (forSections < 3) {
      if (currentToken.kind != Token.SEMICOLON) {
        if (forSections == 0) {
          eAST1 = parseExpr();
        }
        if (forSections == 1) {
          eAST2 = parseExpr();
        }
        if (forSections == 2) {
          eAST3 = parseExpr();
        }
        parseExpr();
        forSections++;
        if (forSections == 3) {
          break;
        }
      }
      match(Token.SEMICOLON);
    }
    match(Token.RPAREN);
    Stmt sAST = parseStmt();
    finish(stmtPos);
    forAST = new ForStmt(eAST1, eAST2, eAST3, sAST, stmtPos);

    return forAST;
  }

  Stmt parseWhileStmt() throws SyntaxError {
    Stmt whileStmt = null;
    SourcePosition stmtPos = new SourcePosition();
    start(stmtPos);

    match((Token.WHILE));
    match(Token.LPAREN);
    Expr eAST = parseExpr();
    match(Token.RPAREN);
    Stmt sAST = parseStmt();

    finish(stmtPos);
    whileStmt = new WhileStmt(eAST, sAST, stmtPos);
    return whileStmt;
  }

  Stmt parseBreakStmt() throws SyntaxError {
    Stmt breakStmt = null;
    SourcePosition stmtPos = new SourcePosition();
    start(stmtPos);
    match(Token.BREAK);
    match(Token.SEMICOLON);

    finish(stmtPos);
    breakStmt = new BreakStmt(stmtPos);
    return breakStmt;
  }

  Stmt parseContinueStmt() throws SyntaxError {
    Stmt continueStmt = null;
    SourcePosition stmtPos = new SourcePosition();
    start(stmtPos);
    match(Token.CONTINUE);
    match(Token.SEMICOLON);
    finish(stmtPos);
    continueStmt = new ContinueStmt(stmtPos);
    return continueStmt;
  }

  Stmt parseReturnStmt() throws SyntaxError{
    Stmt returnStmt = null;
    SourcePosition stmtPos = new SourcePosition();
    start(stmtPos);

    match(Token.RETURN);
    if (currentToken.kind != Token.SEMICOLON) {
      Expr eAST = parseExpr();
      match(Token.SEMICOLON);
      finish(stmtPos);
      returnStmt = new ReturnStmt(eAST, stmtPos);
      return returnStmt;
    }
    match(Token.SEMICOLON);
    finish(stmtPos);
    return returnStmt;
  }

  Stmt parseExprStmt() throws SyntaxError {
    log("In parseExprStmt");
    Stmt sAST = null;

    SourcePosition stmtPos = new SourcePosition();
    start(stmtPos);

    if (currentToken.kind != Token.SEMICOLON) {
        Expr eAST = parseExpr();
        match(Token.SEMICOLON);
        finish(stmtPos);
        sAST = new ExprStmt(eAST, stmtPos);
    } else {
      match(Token.SEMICOLON);
      finish(stmtPos);
      sAST = new ExprStmt(new EmptyExpr(dummyPos), stmtPos);
    }
    return sAST;
  }

// ======================= PARAMETERS =======================
  boolean nextTokenIsType() {
    return currentToken.kind == Token.INT
            || currentToken.kind == Token.FLOAT
            || currentToken.kind == Token.BOOLEAN
            || currentToken.kind == Token.VOID;
  }

  boolean nextTokenStartsExpr() {
    return currentToken.kind == Token.PLUS
            || currentToken.kind == Token.MINUS
            || currentToken.kind == Token.NOT
            || currentToken.kind == Token.LPAREN
            || currentToken.kind == Token.ID
            || currentToken.kind == Token.INTLITERAL
            || currentToken.kind == Token.FLOATLITERAL
            || currentToken.kind == Token.BOOLEANLITERAL
            || currentToken.kind == Token.STRINGLITERAL;

  }

  //POSITION INFO MIGHT BE FUNKY
  List parseParaList() throws SyntaxError {
    log("In parseParaList");
    SourcePosition formalsStartPos = new SourcePosition();
    start(formalsStartPos);
    List formalsAST = new EmptyParaList(formalsStartPos);

    ParaDecl paraDec = parseParaDecl();
    finish(formalsStartPos);
    formalsAST = new ParaList(paraDec, formalsAST, formalsStartPos);
    while(currentToken.kind == Token.COMMA) {
      accept();
      SourcePosition formalsPos = new SourcePosition();
      copyStart(formalsStartPos, formalsPos);
      paraDec = parseParaDecl();
      finish(formalsPos);
      formalsAST = new ParaList(paraDec, formalsAST, formalsPos);
    }

    return formalsAST;
  }


  //CHANGE TO INCLUDE INTLITERAL
  ParaDecl parseParaDecl() throws SyntaxError {
    log("In parseParaDecl");
    ParaDecl pAST = null;
    SourcePosition paradeclPos = new SourcePosition();
    start(paradeclPos);
    Type tAST = parseType();
    Ident iAST = parseIdent();
    finish(paradeclPos);
    pAST = new ParaDecl(tAST, iAST, paradeclPos);
    return pAST;
  }

  List parseArgList() throws SyntaxError {
    SourcePosition arglPos = new SourcePosition();
    start(arglPos);

    List arglAST = new EmptyArgList(arglPos);
    match(Token.LPAREN);
    Arg argAST = parseArg();
    arglAST = new ArgList(argAST, arglAST, arglPos);
    while (currentToken.kind == Token.COMMA) {
      accept();
      argAST = parseArg();
      arglAST = new ArgList(argAST, arglAST, arglPos);
    }

    return arglAST;
  }



  Arg parseArg() throws SyntaxError {
    Arg pArg = null;
    SourcePosition pPos = new SourcePosition();
    start(pPos);

    Expr eAST = parseExpr();
    finish(pPos);
    pArg = new Arg(eAST, pPos);
    return pArg;
  }
// ======================= EXPRESSIONS ======================


  Expr parseExpr() throws SyntaxError {
    log("In parseExpr");
    Expr exprAST = null;
    exprAST = parseAssignmentExpr();
    return exprAST;
  }

  Expr parseAssignmentExpr() throws SyntaxError {
    Expr exprAST = null;
    Expr expr2AST = null;

    SourcePosition assStartPos = new SourcePosition();
    start(assStartPos);

    exprAST = parseCondOrExpr();

    while(currentToken.kind == Token.EQ) {
      Operator opAST = acceptOperator();
      expr2AST = parseCondOrExpr();

      SourcePosition assPos = new SourcePosition();
      copyStart(assStartPos, assPos);
      finish(assPos);

      exprAST = new AssignExpr(exprAST, expr2AST, assPos);
    }
    return exprAST;
  }


  Expr parseCondOrExpr() throws SyntaxError {
    Expr exprAST = null;
    Expr expr2AST = null;

    SourcePosition orStartPos = new SourcePosition();
    start(orStartPos);

    exprAST = parseCondAndExpr();

    //cond-or-expr'
    while(currentToken.kind == Token.OROR) {
      Operator opAST = acceptOperator();
      expr2AST = parseCondAndExpr();

      SourcePosition orPos = new SourcePosition();
      copyStart(orStartPos, orPos);
      finish(orPos);

      exprAST = new BinaryExpr(exprAST, opAST, expr2AST, orPos);
    }
    return exprAST;
  }

  Expr parseCondAndExpr() throws SyntaxError {
    Expr exprAST = null;
    Expr expr2AST = null;

    SourcePosition condAndStartPos = new SourcePosition();
    start(condAndStartPos);

    exprAST = parseEqualityExpr();

      //cond-and-expr'
    while (currentToken.kind == Token.ANDAND) {
      Operator opAST = acceptOperator();
      expr2AST = parseEqualityExpr();

      SourcePosition condAndPos = new SourcePosition();
      copyStart(condAndStartPos, condAndPos);
      finish(condAndPos);

      exprAST = new BinaryExpr(exprAST, opAST, expr2AST, condAndPos);
    }
    return exprAST;
  }



  Expr parseEqualityExpr() throws SyntaxError {
    Expr exprAST = null;
    Expr expr2AST = null;

    SourcePosition eqStartPos = new SourcePosition();
    start(eqStartPos);

    exprAST = parseRelExpr();

    //eq-expr'
    while (currentToken.kind == Token.EQEQ
          || currentToken.kind == Token.NOTEQ) {
      Operator opAST = acceptOperator();
      expr2AST = parseRelExpr();

      SourcePosition eqPos = new SourcePosition();
      copyStart(eqStartPos, eqPos);
      finish(eqPos);
      exprAST = new BinaryExpr(exprAST, opAST, expr2AST, eqPos);
    }
    return exprAST;
  }



  Expr parseRelExpr() throws SyntaxError {
    Expr exprAST = null;
    Expr expr2AST = null;
    SourcePosition relStartPos = new SourcePosition();
    start(relStartPos);
    exprAST = parseAdditiveExpr();
    //rel-expr'
    while (currentToken.kind == Token.LT
            || currentToken. kind == Token.LTEQ
            || currentToken.kind == Token.GT
            || currentToken.kind == Token.GTEQ) {
      Operator opAST = acceptOperator();
      expr2AST = parseAdditiveExpr();

      SourcePosition relPos = new SourcePosition();
      copyStart(relStartPos, relPos);
      finish(relPos);
      exprAST = new BinaryExpr(exprAST, opAST, expr2AST, relPos);
    }
    return exprAST;
  }


  Expr parseAdditiveExpr() throws SyntaxError {
    Expr exprAST = null;

    SourcePosition addStartPos = new SourcePosition();
    start(addStartPos);

    exprAST = parseMultiplicativeExpr();
    //add-expr'
    while (currentToken.kind == Token.PLUS
           || currentToken.kind == Token.MINUS) {
      Operator opAST = acceptOperator();
      Expr e2AST = parseMultiplicativeExpr();

      SourcePosition addPos = new SourcePosition();
      copyStart(addStartPos, addPos);
      finish(addPos);

      exprAST = new BinaryExpr(exprAST, opAST, e2AST, addPos);
    }
    return exprAST;
  }

  Expr parseMultiplicativeExpr() throws SyntaxError {

    Expr exprAST = null;

    SourcePosition multStartPos = new SourcePosition();
    start(multStartPos);

    exprAST = parseUnaryExpr();
    while (currentToken.kind == Token.MULT
           || currentToken.kind == Token.DIV) {
      Operator opAST = acceptOperator();
      Expr e2AST = parseUnaryExpr();

      SourcePosition multPos = new SourcePosition();
      copyStart(multStartPos, multPos);
      finish(multPos);

      exprAST = new BinaryExpr(exprAST, opAST, e2AST, multPos);
    }
    return exprAST;
  }

  Expr parseUnaryExpr() throws SyntaxError {

    Expr exprAST = null;

    SourcePosition unaryPos = new SourcePosition();
    start(unaryPos);

    switch (currentToken.kind) {
      case Token.MINUS: case Token.PLUS: case Token.NOT:
        {
          Operator opAST = acceptOperator();
          Expr e2AST = parseUnaryExpr();
          finish(unaryPos);
          exprAST = new UnaryExpr(opAST, e2AST, unaryPos);
        }
        break;

      default:
        exprAST = parsePrimaryExpr();
        break;
       
    }
    return exprAST;
  }

  Expr parsePrimaryExpr() throws SyntaxError {
    log("In parsePrimaryExpr");
    Expr exprAST = null;

    SourcePosition primPos = new SourcePosition();
    start(primPos);

    switch (currentToken.kind) {

      case Token.ID:
        Ident iAST = parseIdent();
        if (currentToken.kind == Token.LPAREN) {
          List argList = parseArgList();
          finish(primPos);
          exprAST = new CallExpr(iAST, argList, primPos);
        }
        else if (currentToken.kind == Token.LBRACKET){
          match(Token.LBRACKET);
          Expr expr2AST = parseExpr();
          finish(primPos);
          Var vAST = new SimpleVar(iAST, primPos);
          exprAST = new ArrayExpr(vAST, expr2AST, primPos);
        }
        else {
          finish(primPos);
          Var simVAST = new SimpleVar(iAST, primPos);
          exprAST = new VarExpr(simVAST, primPos);
        }
        break;

      case Token.LPAREN:
        {
          accept();
          exprAST = parseExpr();
	      match(Token.RPAREN);
        }
        break;

      case Token.INTLITERAL:
        IntLiteral ilAST = parseIntLiteral();
        finish(primPos);
        exprAST = new IntExpr(ilAST, primPos);
        break;

      case Token.FLOATLITERAL:
        FloatLiteral flAST = parseFloatLiteral();
        finish(primPos);
        exprAST = new FloatExpr(flAST, primPos);
        break;

      case Token.BOOLEANLITERAL:
        BooleanLiteral blAST = parseBooleanLiteral();
        finish(primPos);
        exprAST = new BooleanExpr(blAST, primPos);
        break;

      case Token.STRINGLITERAL:
        StringLiteral slAST = parseStringLiteral();
        finish(primPos);
        exprAST = new StringExpr(slAST, primPos);
        break;

      default:
        syntacticError("illegal primary expression", currentToken.spelling);
       
    }
    return exprAST;
  }

// ========================== ID, OPERATOR and LITERALS ========================

  Ident parseIdent() throws SyntaxError {
    log("In parseIdent");
    Ident I = null; 

    if (currentToken.kind == Token.ID) {
      previousTokenPosition = currentToken.position;
      String spelling = currentToken.spelling;
      I = new Ident(spelling, previousTokenPosition);
      currentToken = scanner.getToken();
    } else 
      syntacticError("identifier expected here", "");
    return I;
  }

// acceptOperator parses an operator, and constructs a leaf AST for it

  Operator acceptOperator() throws SyntaxError {
    Operator O = null;

    previousTokenPosition = currentToken.position;
    String spelling = currentToken.spelling;
    O = new Operator(spelling, previousTokenPosition);
    currentToken = scanner.getToken();
    return O;
  }


  IntLiteral parseIntLiteral() throws SyntaxError {
    IntLiteral IL = null;

    if (currentToken.kind == Token.INTLITERAL) {
      String spelling = currentToken.spelling;
      accept();
      IL = new IntLiteral(spelling, previousTokenPosition);
    } else 
      syntacticError("integer literal expected here", "");
    return IL;
  }

  FloatLiteral parseFloatLiteral() throws SyntaxError {
    FloatLiteral FL = null;

    if (currentToken.kind == Token.FLOATLITERAL) {
      String spelling = currentToken.spelling;
      accept();
      FL = new FloatLiteral(spelling, previousTokenPosition);
    } else 
      syntacticError("float literal expected here", "");
    return FL;
  }

  BooleanLiteral parseBooleanLiteral() throws SyntaxError {
    BooleanLiteral BL = null;

    if (currentToken.kind == Token.BOOLEANLITERAL) {
      String spelling = currentToken.spelling;
      accept();
      BL = new BooleanLiteral(spelling, previousTokenPosition);
    } else 
      syntacticError("boolean literal expected here", "");
    return BL;
  }

  StringLiteral parseStringLiteral() throws SyntaxError {
    StringLiteral SL = null;
    if (currentToken.kind == Token.STRINGLITERAL) {
      String spelling = currentToken.spelling;
      accept();
      SL = new StringLiteral(spelling, previousTokenPosition);
    } else {
      syntacticError("string literal expected here", "");
    }
    return SL;
  }
}

