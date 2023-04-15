///***
// * Checker.java
// *
// * Thu 09 Mar 2023 11:37:23 AEDT
// *
// ****/
//
//package VC.Checker;
//
//import VC.ASTs.*;
//import VC.Scanner.SourcePosition;
//import VC.ErrorReporter;
//import VC.StdEnvironment;
//
//import java.util.HashMap;
//
//public final class Checker implements Visitor {
//
//  private String errMesg[] = {
//    "*0: main function is missing",
//    "*1: return type of main is not int",
//
//    // defined occurrences of identifiers
//    // for global, local and parameters
//    "*2: identifier redeclared",
//    "*3: identifier declared void",
//    "*4: identifier declared void[]",
//
//    // applied occurrences of identifiers
//    "*5: identifier undeclared",
//
//    // assignments
//    "*6: incompatible type for =",
//    "*7: invalid lvalue in assignment",
//
//     // types for expressions
//    "*8: incompatible type for return",
//    "*9: incompatible type for this binary operator",
//    "*10: incompatible type for this unary operator",
//
//     // scalars
//     "*11: attempt to use an array/function as a scalar",
//
//     // arrays
//     "*12: attempt to use a scalar/function as an array",
//     "*13: wrong type for element in array initialiser",
//     "*14: invalid initialiser: array initialiser for scalar",
//     "*15: invalid initialiser: scalar initialiser for array",
//     "*16: excess elements in array initialiser",
//     "*17: array subscript is not an integer",
//     "*18: array size missing",
//
//     // functions
//     "*19: attempt to reference a scalar/array as a function",
//
//     // conditional expressions in if, for and while
//    "*20: if conditional is not boolean",
//    "*21: for conditional is not boolean",
//    "*22: while conditional is not boolean",
//
//    // break and continue
//    "*23: break must be in a while/for",
//    "*24: continue must be in a while/for",
//
//    // parameters
//    "*25: too many actual parameters",
//    "*26: too few actual parameters",
//    "*27: wrong type for actual parameter",
//
//    // reserved for errors that I may have missed (J. Xue)
//    "*28: misc 1",
//    "*29: misc 2",
//
//    // the following two checks are optional
//    "*30: statement(s) not reached",
//    "*31: missing return statement",
//  };
//
//
//  private SymbolTable idTable;
//  private static SourcePosition dummyPos = new SourcePosition();
//  private ErrorReporter reporter;
//
//
//  //Map of every Identity. True if function, false if var
//  private HashMap<String, Boolean> identFuncOrVar = new HashMap<>();
//
//  //Map of Paramaters to function identity
//  private HashMap<String, List> funcParams = new HashMap<>();
//
//  private Type ScopeType = null;
//
//  // Checks whether the source program, represented by its AST,
//  // satisfies the language's scope rules and type rules.
//  // Also decorates the AST as follows:
//  //  (1) Each applied occurrence of an identifier is linked to
//  //      the corresponding declaration of that identifier.
//  //  (2) Each expression and variable is decorated by its type.
//
//  public Checker (ErrorReporter reporter) {
//    this.reporter = reporter;
//    this.idTable = new SymbolTable ();
//    establishStdEnvironment();
//  }
//
//  boolean showLog = false;
//  void log(Object o) {
//    if(showLog) {
//      System.out.println(o);
//    }
//  }
//
//  public void check(AST ast) {
//    ast.visit(this, null);
//  }
//
//
//  // auxiliary methods
//
//  private void declareVariable(Ident ident, Decl decl) {
//    IdEntry entry = idTable.retrieveOneLevel(ident.spelling);
//    Decl entryDecl = idTable.retrieve(ident.spelling);
//
//    if (entry == null && entryDecl == null) {
//      ; // no problem, first declaration
//    } else {
//      //TYPE COERCION FROM INT TO FLOAT
//      if (decl.T.isFloatType() && entryDecl.T.isIntType()) {
//
//      }
//      else
//        reporter.reportError(errMesg[2] + ": %", ident.spelling, ident.position);
//    }
//
//    idTable.insert(ident.spelling, decl);
//    identFuncOrVar.put(ident.spelling, false);
//    // if a declaration, say, "int i = 1" has also an initialiser (i.e.,
//    // "1" here, then i is both a defined occurrence (i.e., definition)
//    // of i and an applied occurrence (i.e., use) of i. So do the
//    // identification here for i just in case (even it is not used)
//    ident.visit(this, null);
//  }
//
//  private void declareFunction(Ident ident, FuncDecl decl) {
//    IdEntry entry = idTable.retrieveOneLevel(ident.spelling);
//
//    //Check for redeclaration
//    if (entry == null) {
//
//    }
//    else {
//      reporter.reportError(errMesg[2] + ": %", ident.spelling, ident.position);
//    }
//
//    idTable.insert(ident.spelling, decl);
//    identFuncOrVar.put(ident.spelling, true);
//    funcParams.put(ident.spelling, decl.PL);
//    ident.visit(this, null);
//  }
//
//
//  private void updateScopeType(Type type) {
//      ScopeType = type;
//  }
//
//  private Type getScopeType() {
//      return ScopeType;
//  }
//
//  // Programs
//
//  public Object visitProgram(Program ast, Object o) {
//    ast.FL.visit(this, null);
//    //Check for error 0 - main does not exist
//    Decl mainEntry = idTable.retrieve("main");
//    if (mainEntry == null) {
//      reporter.reportError(errMesg[0], "main", ast.position);
//    }
//    else if (!mainEntry.T.isIntType()) {
//      reporter.reportError(errMesg[1], mainEntry.I.spelling, mainEntry.I.position);
//    }
//
//    return null;
//  }
//
//  // Statements
//
//  public Object visitCompoundStmt(CompoundStmt ast, Object o) {
//    log("Visiting compoundStmt");
//    idTable.openScope();
//    //THIS GOES THROUGH THE TREE OF THE COMPOUND STMT
//    ast.DL.visit(this, null);
//    ast.SL.visit(this, null);
//    // Your code goes here
//
//    idTable.closeScope();
//    return null;
//  }
//
//  public Object visitStmtList(StmtList ast, Object o) {
//    log("Visiting stmtList");
//    ast.S.visit(this, o);
//    if (ast.S instanceof ReturnStmt && ast.SL instanceof StmtList)
//      reporter.reportError(errMesg[30], "", ast.SL.position);
//    ast.SL.visit(this, o);
//    return null;
//  }
//
//
//  @Override
//  public Object visitIfStmt(IfStmt ast, Object o) {
//    ast.E.visit(this, null);
//    if (!ast.E.type.equals(StdEnvironment.booleanType)) {
//      reporter.reportError(errMesg[20], dummyI.spelling, dummyPos);
//    }
//    return null;
//  }
//
//  @Override
//  public Object visitWhileStmt(WhileStmt ast, Object o) {
//    ast.E.visit(this, null);
//    if (!ast.E.type.equals(StdEnvironment.booleanType)) {
//      reporter.reportError(errMesg[22], dummyI.spelling, dummyPos);
//    }
//    return null;
//  }
//
//  @Override
//  public Object visitForStmt(ForStmt ast, Object o) {
//    ast.E1.visit(this, null);
//    ast.E2.visit(this, null);
//    ast.E3.visit(this, null);
//    if (!ast.E1.type.equals(StdEnvironment.booleanType)
//      || !ast.E2.type.equals(StdEnvironment.booleanType)
//      || !ast.E3.type.equals(StdEnvironment.booleanType)){
//      reporter.reportError(errMesg[21], dummyI.spelling, dummyPos);
//    }
//    return null;
//  }
//
//  //cover error: 23
//  @Override
//  public Object visitBreakStmt(BreakStmt ast, Object o) {
//    IdEntry entry = idTable.retrieveOneLevel("while");
//    if (entry == null) {
//      entry = idTable.retrieveOneLevel("for");
//      if (entry == null) {
//        reporter.reportError(errMesg[23], "", ast.position);
//      }
//    }
//    return null;
//  }
//
//  //cover error: 24
//  @Override
//  public Object visitContinueStmt(ContinueStmt ast, Object o) {
//    IdEntry entry = idTable.retrieveOneLevel("while");
//    if (entry == null) {
//      entry = idTable.retrieveOneLevel("for");
//      if (entry == null) {
//        reporter.reportError(errMesg[24], "", ast.position);
//      }
//    }
//    return null;
//  }
//
//  @Override
//  public Object visitReturnStmt(ReturnStmt ast, Object o) {
//    ast.E.visit(this, null);
//    Type type = getScopeType();
//    if (!ast.E.type.equals(type)) {
//      reporter.reportError(errMesg[8], dummyI.spelling, ast.position);
//    }
//    return null;
//  }
//
//
//  public Object visitExprStmt(ExprStmt ast, Object o) {
//    log("Visiting exprStmt");
//    ast.E.visit(this, o);
//    return null;
//  }
//
//  @Override
//  public Object visitEmptyCompStmt(EmptyCompStmt ast, Object o) {
//    return null;
//  }
//
//  public Object visitEmptyStmt(EmptyStmt ast, Object o) {
//    return null;
//  }
//
//  public Object visitEmptyStmtList(EmptyStmtList ast, Object o) {
//    return null;
//  }
//
//  @Override
//  public Object visitEmptyArrayExprList(EmptyArrayExprList ast, Object o) {
//    return null;
//  }
//
//  // Expressions
//
//  // Returns the Type denoting the type of the expression. Does
//  // not use the given object.
//
//
//  public Object visitEmptyExpr(EmptyExpr ast, Object o) {
//    ast.type = StdEnvironment.errorType;
//    return ast.type;
//  }
//
//  public Object visitBooleanExpr(BooleanExpr ast, Object o) {
//    ast.type = StdEnvironment.booleanType;
//    return ast.type;
//  }
//
//  public Object visitIntExpr(IntExpr ast, Object o) {
//    log("Visiting intExpr");
//    ast.type = StdEnvironment.intType;
//    return ast.type;
//  }
//
//  public Object visitFloatExpr(FloatExpr ast, Object o) {
//    ast.type = StdEnvironment.floatType;
//    return ast.type;
//  }
//
//  public Object visitStringExpr(StringExpr ast, Object o) {
//    ast.type = StdEnvironment.stringType;
//    return ast.type;
//  }
//
//  @Override
//  public Object visitUnaryExpr(UnaryExpr ast, Object o) {
//    log("visiting unaryExpr with operator: " + ast.O.spelling);
//
//    if (ast.O.spelling.equals("!") ||ast.O.spelling.equals("+") ||ast.O.spelling.equals("-")) {
//      ast.O.visit(this,null);
//    }
//
//    ast.E.visit(this, null);
//    ast.type = ast.E.type;
//
//    if (ast.O.spelling.equals("!")) {
//        if (!ast.E.type.isBooleanType()) {
//          reporter.reportError(errMesg[10], ast.O.spelling, ast.position);
//        }
//    }
//    if (ast.O.spelling.equals("+") || ast.O.spelling.equals("-")) {
//      if (!ast.E.type.isFloatType() && !ast.E.type.isIntType()) {
//        reporter.reportError(errMesg[10], ast.O.spelling, ast.position);
//      }
//    }
//    return ast.type;
//  }
//
//  @Override
//  public Object visitBinaryExpr(BinaryExpr ast, Object o) {
//    log("Visiting BinaryExpr");
//    ast.E1.visit(this, null);
//    ast.E2.visit(this, null);
//    ast.O.visit(this, null);
//    if (ast.E1.type == null || ast.E2.type == null || !ast.E1.type.equals(ast.E2.type)) {
//      reporter.reportError(errMesg[9], ast.O.spelling, ast.O.position);
//    }
//    return null;
//  }
//
//  @Override
//  public Object visitArrayInitExpr(ArrayInitExpr ast, Object o) {
//    log("Visiting arrayInitExpr");
//    ast.IL.visit(this, null);
//    return null;
//  }
//
//  @Override
//  public Object visitArrayExprList(ArrayExprList ast, Object o) {
//    return null;
//  }
//
//  @Override
//  public Object visitArrayExpr(ArrayExpr ast, Object o) {
//    return null;
//  }
//
//  public Object visitVarExpr(VarExpr ast, Object o) {
//    log("Visiting varExpr");
//    ast.type = (Type) ast.V.visit(this, null);
//    if (ast.type == null) {
//      //undeclared, already covered in visitSimVar
//    }
//    else if (ast.type.isArrayType()) {
//      reporter.reportError(errMesg[11], dummyI.spelling, ast.position);
//    }
//    return ast.type;
//  }
//
//  @Override
//  public Object visitCallExpr(CallExpr ast, Object o) {
//    log("Visiting callExpr");
//    Decl entry = idTable.retrieve(ast.I.spelling);
//    if (entry == null) {
//      reporter.reportError(errMesg[5] + ": %", ast.I.spelling, ast.I.position);
//    }
//    else {
//      Boolean isFunction = identFuncOrVar.get(ast.I.spelling);
//      if (!isFunction) {
//        reporter.reportError(errMesg[19] + ": %", ast.I.spelling, ast.I.position);
//      }
//    }
//    List argList = (List) ast.AL.visit(this, null);
//    ArgList fullArgList = null;
//    if (argList != null && !argList.isEmptyArgList()) {
//       fullArgList = (ArgList) argList;
//    }
//
//    ParaList paramList = (ParaList) funcParams.get(ast.I.spelling);
//
//    //never exits loop
////    while(fullArgList != null && fullArgList.A.parent != null) {
////      Arg arg = fullArgList.A;
////      ParaDecl param = paramList.P;
////
//////      if (paramList.isEmptyParaList()) {
//////        reporter.reportError(errMesg[25], param.I.spelling, arg.position);
//////        break;
//////      }
//////
//////      if (!arg.type.equals(param.T)) {
//////        reporter.reportError(errMesg[27], param.I.spelling, arg.position);
//////      }
////      fullArgList = (ArgList) fullArgList.AL.parent;
////      paramList = (ParaList) paramList.PL.parent;
////    }
//    return null;
//  }
//
//  @Override
//  public Object visitAssignExpr(AssignExpr ast, Object o) {
//    log("Visiting AssignExpr");
//    ast.E1.visit(this, null);
//    ast.E2.visit(this, null);
//
//    //JANKY
//    if(!(ast.E1 instanceof VarExpr) || ast.E1.type == null){
//      reporter.reportError(errMesg[7], dummyI.spelling, ast.position);
//    }
////    if (ast.E1.type == null) {
////      reporter.reportError(errMesg[7], dummyI.spelling, ast.position);
////    }
//    else if (!ast.E1.type.equals(ast.E2.type)) {
//      reporter.reportError(errMesg[6], dummyI.spelling, ast.position);
//    }
//    return null;
//  }
//
//  // Declarations
//
//  // Always returns null. Does not use the given object.
//
//  public Object visitFuncDecl(FuncDecl ast, Object o) {
//    log("Visiting funcDecl");
//    declareFunction(ast.I, ast);
//    idTable.openScope();
//    ast.PL.visit(this, null);
//    log("Finished visiting paraDecl");
//    // Your code goes here
//
//    updateScopeType(ast.T);
//
//    // HINT
//    // Pass ast as the 2nd argument (as done below) so that the
//    // formal parameters of the function an be extracted from ast when the
//    // function body is later visited
//
//    ast.S.visit(this, ast);
//
//    return null;
//  }
//
//  public Object visitDeclList(DeclList ast, Object o) {
//    log("Visiting declList");
//    ast.D.visit(this, null);
//    ast.DL.visit(this, null);
//    return null;
//  }
//
//  public Object visitEmptyDeclList(EmptyDeclList ast, Object o) {
//    return null;
//  }
//
//  public Object visitGlobalVarDecl(GlobalVarDecl ast, Object o) {
//    log("Visiting globalVarDecl");
//    declareVariable(ast.I, ast);
//    // fill the rest
//    if (ast.T.isVoidType()) {
//      reporter.reportError(errMesg[3], ast.I.spelling, ast.I.position);
//    }
//    return null;
//  }
//
//  public Object visitLocalVarDecl(LocalVarDecl ast, Object o) {
//    log("visiting localvardecl");
//    declareVariable(ast.I, ast);
//
//    // fill the rest
//    if (ast.T.isVoidType()) {
//      reporter.reportError(errMesg[3], ast.I.spelling, ast.I.position);
//    }
//    return null;
//  }
//
//  // Parameters
//
// // Always returns null. Does not use the given object.
//
//  public Object visitParaList(ParaList ast, Object o) {
//    log("Visiting paraList");
//
//    ast.P.visit(this, null);
//    ast.PL.visit(this, null);
//    return null;
//  }
//
//  public Object visitParaDecl(ParaDecl ast, Object o) {
//    log("Visiting paraDecl");
//
//
//    if (ast.T.isVoidType()) {
//      reporter.reportError(errMesg[3] + ": %", ast.I.spelling, ast.I.position);
//    } else if (ast.T.isArrayType()) {
//     if (((ArrayType) ast.T).T.isVoidType())
//        reporter.reportError(errMesg[4] + ": %", ast.I.spelling, ast.I.position);
//    }
//    declareVariable(ast.I, ast);
//    return null;
//  }
//
//  @Override
//  public Object visitArgList(ArgList ast, Object o) {
//    log("Visiting argList");
//    ast.A.visit(this, null);
//    ast.AL.visit(this, null);
//    return ast.AL;
//  }
//
//  @Override
//  public Object visitArg(Arg ast, Object o) {
//    log("Visiting Arg: " + ast.E.toString());
//    ast.E.visit(this, null);
//    return ast.E;
//  }
//
//  public Object visitEmptyParaList(EmptyParaList ast, Object o) {
//    return null;
//  }
//
//  @Override
//  public Object visitEmptyArgList(EmptyArgList ast, Object o) {
//    return null;
//  }
//
//  // Arguments
//
//  // Your visitor methods for arguments go here
//
//  // Types
//
//  // Returns the type predefined in the standard environment.
//
//  public Object visitErrorType(ErrorType ast, Object o) {
//    return StdEnvironment.errorType;
//  }
//
//  @Override
//  public Object visitSimpleVar(SimpleVar ast, Object o) {
//    log("Visiting simVar");
//    ast.I.visit(this, null);
//    Decl entry = idTable.retrieve(ast.I.spelling);
//    if (identFuncOrVar.containsKey(ast.I.spelling)) {
//      Boolean isFunc = identFuncOrVar.get(ast.I.spelling);
//      if (isFunc) {
//        reporter.reportError(errMesg[11] + ": %", ast.I.spelling, ast.I.position);
//      }
//    }
//
//    if (entry != null) {
//      ast.type = entry.T;
//    }
//    return ast.type;
//  }
//
//  public Object visitBooleanType(BooleanType ast, Object o) {
//    return StdEnvironment.booleanType;
//  }
//
//  public Object visitIntType(IntType ast, Object o) {
//    return StdEnvironment.intType;
//  }
//
//  public Object visitFloatType(FloatType ast, Object o) {
//    return StdEnvironment.floatType;
//  }
//
//  public Object visitStringType(StringType ast, Object o) {
//    return StdEnvironment.stringType;
//  }
//
//  @Override
//  public Object visitArrayType(ArrayType ast, Object o) {
//    return null;
//  }
//
//  public Object visitVoidType(VoidType ast, Object o) {
//    return StdEnvironment.voidType;
//  }
//
//  // Literals, Identifiers and Operators
//
//  public Object visitIdent(Ident I, Object o) {
//    log("Visiting Ident: " + I.spelling);
//
//    Decl binding = idTable.retrieve(I.spelling);
//    if (binding != null)
//      I.decl = binding;
//    else {
//      reporter.reportError(errMesg[5] + ": %", I.spelling, I.position);
//    }
//    return binding;
//  }
//
//  public Object visitBooleanLiteral(BooleanLiteral SL, Object o) {
//    return StdEnvironment.booleanType;
//  }
//
//  public Object visitIntLiteral(IntLiteral IL, Object o) {
//    return StdEnvironment.intType;
//  }
//
//  public Object visitFloatLiteral(FloatLiteral IL, Object o) {
//    return StdEnvironment.floatType;
//  }
//
//  public Object visitStringLiteral(StringLiteral IL, Object o) {
//    return StdEnvironment.stringType;
//  }
//
//  public Object visitOperator(Operator O, Object o) {
//    log("Visiting operator: " + O.spelling);
//    return null;
//  }
//
//  // Creates a small AST to represent the "declaration" of each built-in
//  // function, and enters it in the symbol table.
//
//  private FuncDecl declareStdFunc (Type resultType, String id, List pl) {
//
//    FuncDecl binding;
//
//    binding = new FuncDecl(resultType, new Ident(id, dummyPos), pl,
//           new EmptyStmt(dummyPos), dummyPos);
//    idTable.insert (id, binding);
//    return binding;
//  }
//
//  // Creates small ASTs to represent "declarations" of all
//  // build-in functions.
//  // Inserts these "declarations" into the symbol table.
//
//  private final static Ident dummyI = new Ident("x", dummyPos);
//
//  private void establishStdEnvironment () {
//
//    // Define four primitive types
//    // errorType is assigned to ill-typed expressions
//
//    StdEnvironment.booleanType = new BooleanType(dummyPos);
//    StdEnvironment.intType = new IntType(dummyPos);
//    StdEnvironment.floatType = new FloatType(dummyPos);
//    StdEnvironment.stringType = new StringType(dummyPos);
//    StdEnvironment.voidType = new VoidType(dummyPos);
//    StdEnvironment.errorType = new ErrorType(dummyPos);
//
//    // enter into the declarations for built-in functions into the table
//
//    StdEnvironment.getIntDecl = declareStdFunc( StdEnvironment.intType,
//	"getInt", new EmptyParaList(dummyPos));
//    StdEnvironment.putIntDecl = declareStdFunc( StdEnvironment.voidType,
//	"putInt", new ParaList(
//	new ParaDecl(StdEnvironment.intType, dummyI, dummyPos),
//	new EmptyParaList(dummyPos), dummyPos));
//    StdEnvironment.putIntLnDecl = declareStdFunc( StdEnvironment.voidType,
//	"putIntLn", new ParaList(
//	new ParaDecl(StdEnvironment.intType, dummyI, dummyPos),
//	new EmptyParaList(dummyPos), dummyPos));
//    StdEnvironment.getFloatDecl = declareStdFunc( StdEnvironment.floatType,
//	"getFloat", new EmptyParaList(dummyPos));
//    StdEnvironment.putFloatDecl = declareStdFunc( StdEnvironment.voidType,
//	"putFloat", new ParaList(
//	new ParaDecl(StdEnvironment.floatType, dummyI, dummyPos),
//	new EmptyParaList(dummyPos), dummyPos));
//    StdEnvironment.putFloatLnDecl = declareStdFunc( StdEnvironment.voidType,
//	"putFloatLn", new ParaList(
//	new ParaDecl(StdEnvironment.floatType, dummyI, dummyPos),
//	new EmptyParaList(dummyPos), dummyPos));
//    StdEnvironment.putBoolDecl = declareStdFunc( StdEnvironment.voidType,
//	"putBool", new ParaList(
//	new ParaDecl(StdEnvironment.booleanType, dummyI, dummyPos),
//	new EmptyParaList(dummyPos), dummyPos));
//    StdEnvironment.putBoolLnDecl = declareStdFunc( StdEnvironment.voidType,
//	"putBoolLn", new ParaList(
//	new ParaDecl(StdEnvironment.booleanType, dummyI, dummyPos),
//	new EmptyParaList(dummyPos), dummyPos));
//
//    StdEnvironment.putStringLnDecl = declareStdFunc( StdEnvironment.voidType,
//	"putStringLn", new ParaList(
//	new ParaDecl(StdEnvironment.stringType, dummyI, dummyPos),
//	new EmptyParaList(dummyPos), dummyPos));
//
//    StdEnvironment.putStringDecl = declareStdFunc( StdEnvironment.voidType,
//	"putString", new ParaList(
//	new ParaDecl(StdEnvironment.stringType, dummyI, dummyPos),
//	new EmptyParaList(dummyPos), dummyPos));
//
//    StdEnvironment.putLnDecl = declareStdFunc( StdEnvironment.voidType,
//	"putLn", new EmptyParaList(dummyPos));
//
//  }
//
//
//}
