/***
 ***
 *** Recogniser.java
 ***
 ***/

package VC.Recogniser;

import VC.Scanner.Scanner;
import VC.Scanner.SourcePosition;
import VC.Scanner.Token;
import VC.ErrorReporter;

public class Recogniser {

    private Scanner scanner;
    private ErrorReporter errorReporter;
    private Token currentToken;

    public Recogniser(Scanner lexer, ErrorReporter reporter) {
        scanner = lexer;
        errorReporter = reporter;
        currentToken = scanner.getToken();


    }

    boolean showLog = false;
    void log(Object o) {
        if (showLog) {
//            System.out.println(o);
        }
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
        throw (new SyntaxError());
    }

// ========================== PROGRAMS ========================

    //change to (func-decl | var-decl)*
    public void parseProgram() {
        log("In recogniser");
        try {
            while (currentToken.kind != Token.EOF) {
                parseFuncOrVarDecl();
            }
            if (currentToken.kind != Token.EOF) {
                syntacticError("\"%\" wrong result type for a function", currentToken.spelling);
            }
        } catch (SyntaxError s) {
        }
    }

    // ========================== DECLARATIONS ========================
    void parseFuncOrVarDecl() throws SyntaxError {
        parseType();
        parseIdent();

        //If func-decl
        if (currentToken.kind == Token.LPAREN) {
            parseParaList();
            parseCompoundStmt();
        }

        //If var-decl
        else {
            if (currentToken.kind == Token.LBRACKET) {
                match(Token.LBRACKET);
                if (currentToken.kind != Token.RBRACKET) {
                    parseIntLiteral();
                }
                match(Token.RBRACKET);
            }
            if (currentToken.kind == Token.EQ) {
                match(Token.EQ);
                parseInitialiser();
            }
            if (currentToken.kind == Token.COMMA) {
                while(currentToken.kind != Token.SEMICOLON) {
                    match(Token.COMMA);
                    parseInitDeclarator();
                }
            }
            match(Token.SEMICOLON);
        }
    }

    void parseInitDeclarator() throws SyntaxError {
        log("In parseInitDeclarator");
        parseDeclarator();
        if (currentToken.kind == Token.EQ) {
            match(Token.EQ);
            parseInitialiser();
        } else if (currentToken.kind == Token.COMMA) {
            match(Token.COMMA);
            parseInitialiser();
        }
    }

    void parseDeclarator() throws SyntaxError {
        parseIdent();
        if (currentToken.kind == Token.LBRACKET) {
            log("Declaring array");
            match(Token.LBRACKET);
            if (currentToken.kind == Token.INTLITERAL) {
                parseIntLiteral();
            }
            match(Token.RBRACKET);
        }
    }

    void parseInitialiser() throws SyntaxError {
        log("Parsing initialiser");
        if (currentToken.kind == Token.LCURLY) {
            log("Declaring array contents");
            match(Token.LCURLY);
            parseExpr();
            if (currentToken.kind == Token.COMMA) {
                while (currentToken.kind != Token.RCURLY) {
                    match(Token.COMMA);
                    log("lkfkldjsakjfjkfasdjkl");
                    parseExpr();
                }
                log("Exited loop");
            }
            match(Token.RCURLY);
        } else {
            parseExpr();
        }
    }

// ======================= STATEMENTS ==============================

    //CHANGE: compound-stmt -> "{" var-decl*stmt* "}"
    void parseCompoundStmt() throws SyntaxError {
        log("Parsing Compound Statement: " + currentToken);
        match(Token.LCURLY);
        while (currentToken.kind != Token.RCURLY) {
            while (currentToken.kind == Token.INT || currentToken.kind == Token.FLOAT || currentToken.kind == Token.BOOLEAN || currentToken.kind == Token.VOID) {
                log("Parsing variable decl");
                parseFuncOrVarDecl();
            }
            parseStmtList();
        }

        match(Token.RCURLY);
        log("Exiting compound statement");
    }
    // Here, a new nonterminal has been introduced to define { stmt } *
    void parseStmtList() throws SyntaxError {
        log("Parsing Statement List:" + currentToken);
        while (currentToken.kind != Token.RCURLY)
            parseStmt();
    }

    //Complete this w if, for, return etc
    void parseStmt() throws SyntaxError {
        log("Parsing statement: " + currentToken);
        switch (currentToken.kind) {
            case Token.IF:
                parseIfStmt();
                break;

            case Token.FOR:
                parseForStmt();
                break;

            case Token.WHILE:
                parseWhileStmt();
                break;

            case Token.BREAK:
                parseBreakStmt();
                break;

            case Token.CONTINUE:
                parseContinueStmt();
                break;

            case Token.RETURN:
                parseReturnStmt();
                break;


            default:
                if (currentToken.kind == Token.LCURLY) {
                    parseCompoundStmt();
                } else {
                    parseExprStmt();
                }
                break;

        }
    }

    void parseIfStmt() throws SyntaxError {
        match((Token.IF));
        match(Token.LPAREN);
        parseExpr();
        match(Token.RPAREN);
        parseStmt();

        if (currentToken.kind == Token.ELSE) {
            match(Token.ELSE);
            parseStmt();
        }
    }

    void parseForStmt() throws SyntaxError {
        match(Token.FOR);
        match(Token.LPAREN);
        int forSections = 0;
        while (forSections < 3) {
            if (currentToken.kind != Token.SEMICOLON) {
                parseExpr();
                forSections++;
                if (forSections == 3) {
                    break;
                }
            }
            match(Token.SEMICOLON);
        }
        match(Token.RPAREN);
        parseStmt();


    }

    void parseWhileStmt() throws SyntaxError {
        match((Token.WHILE));
        match(Token.LPAREN);
        parseExpr();
        match(Token.RPAREN);
        parseStmt();
    }

    void parseBreakStmt() throws SyntaxError {
        match(Token.BREAK);
        match(Token.SEMICOLON);
    }

    void parseContinueStmt() throws SyntaxError {
        log("Parsing continue statement: " + currentToken);
        match(Token.CONTINUE);
        match(Token.SEMICOLON);

    }

    void parseReturnStmt() throws SyntaxError {
        match(Token.RETURN);

        if (currentToken.kind != Token.SEMICOLON) {
            parseExprStmt();
        }
        else {
            match(Token.SEMICOLON);
        }

    }

    void parseExprStmt() throws SyntaxError {
        log("Parsing expr statement: " + currentToken);
        if (currentToken.kind != Token.SEMICOLON) {
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
        log("Parsing ID: " + currentToken);
        if (currentToken.kind == Token.ID) {
            accept();
        }
    }

    void parseType() throws SyntaxError {
        log("Parsing type declaration");
        if (nextTokenIsType()) {
            log("Type: " + currentToken.spelling);
            accept();
        }
        else {syntacticError("Wrong result type for function", currentToken.spelling);}
    }

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
    //===


// ======================= OPERATORS ======================

    // Call acceptOperator rather than accept().
    // In Assignment 3, an Operator Node will be constructed in here.

    void acceptOperator() throws SyntaxError {

        currentToken = scanner.getToken();
    }


// ======================= EXPRESSIONS ======================

    void parseExpr() throws SyntaxError {
        log("Parsing expression");
        parseAssignExpr();
    }


    void parseAssignExpr() throws SyntaxError {
        while (nextTokenStartsExpr()) {
            log("Current token is: " + currentToken.spelling);
            parseCondOrExpr();
            if (currentToken.kind == Token.EQ) {
                match(Token.EQ);
            }
            else break;
        }
    }

    void parseCondOrExprPrime() throws SyntaxError {
        if (currentToken.kind == Token.OROR) {
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
        if (currentToken.kind == Token.LT || currentToken.kind == Token.LTEQ || currentToken.kind == Token.GT || currentToken.kind == Token.GTEQ) {
            acceptOperator();
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
            acceptOperator();
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
            case Token.MINUS: {
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
        log("Parsing primary expression: " + currentToken.spelling);
        switch (currentToken.kind) {

            case Token.ID:
                log("Primary expr is an ident");
                parseIdent();
                if (currentToken.kind == Token.LPAREN) {
                    parseArgList();
                }
                else if (currentToken.kind == Token.LBRACKET) {
                    match(Token.LBRACKET);
                    parseExpr();
                    match(Token.RBRACKET);
                }
                log("Exiting parsePrimaryExpr w Identifier first");
                break;

            case Token.LPAREN:
                match(Token.LPAREN);
                parseExpr();
                match(Token.RPAREN);
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
                syntacticError("illegal primary expression", currentToken.spelling);

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
        } else
            syntacticError("void expected here", "");
    }

    void parseStringLiteral() throws SyntaxError {
        if (currentToken.kind == Token.STRINGLITERAL) {
            accept();
        } else
            syntacticError("string literal expected here", "");

    }

//================== PARAMETERS ========================
    void parseParaList() throws SyntaxError {
        log("Parsing parameter list");
        match(Token.LPAREN);
        if (nextTokenIsType()) {
            parseProperParaList();
        }
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
        log("Parsing arglist");
        match(Token.LPAREN);
        if (nextTokenStartsExpr()) {
            parseProperArgList();
        }
        match(Token.RPAREN);
    }

    void parseProperArgList() throws SyntaxError {
        log("Parsing proper arglist with token: " + currentToken.spelling);
        parseArg();
        log("Arg parsed");
        while (currentToken.kind != Token.RPAREN) {
            match(Token.COMMA);
            parseArg();
        }
    }

    void parseArg() throws SyntaxError {
        log("Parsing argument: " + currentToken.spelling);
        parseExpr();
    }


}
