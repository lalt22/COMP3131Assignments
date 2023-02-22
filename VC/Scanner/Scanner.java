package VC.Scanner;

import VC.ErrorReporter;

public final class Scanner {

    private SourceFile sourceFile;
    private boolean debug;

    private ErrorReporter errorReporter;
    private StringBuffer currentSpelling;
    private char currentChar;
    private SourcePosition sourcePos;

// =========================================================

    public Scanner(SourceFile source, ErrorReporter reporter) {
        sourceFile = source;
        errorReporter = reporter;
        currentChar = sourceFile.getNextChar();
        debug = false;

        // you may initialise your counters for line and column numbers here
    }

    public void enableDebugging() {
        debug = true;
    }

    // accept gets the next character from the source program.

    private void accept() {

        currentChar = sourceFile.getNextChar();

        // you may save the lexeme of the current token incrementally here
        // you may also increment your line and column counters here
    }

    private void acceptNoCount() {
        currentChar = sourceFile.getNextChar();
    }

    private void eatChars(int count) {
        for (int i = 0; i < count; i++) {
            accept();
        }
        System.out.println("Eating " + count + " chars. Current char: " + currentChar);
    }

    // inspectChar returns the n-th character after currentChar
    // in the input stream.
    //
    // If there are fewer than nthChar characters between currentChar
    // and the end of file marker, SourceFile.eof is returned.
    //
    // Both currentChar and the current position in the input stream
    // are *not* changed. Therefore, a subsequent call to accept()
    // will always return the next char after currentChar.

    private char inspectChar(int nthChar) {
        return sourceFile.inspectChar(nthChar);
    }

    private int nextToken() {
        // Tokens: separators, operators, literals, identifiers and keyworods
        System.out.println("Identifying character " + currentChar);
        char inspectedChar;
        switch (currentChar) {
            //comments OR DIV
            case '/':
                inspectedChar = inspectChar(1);
                //RULE OUT COMMENTS
                if (inspectedChar == '/') {
                    System.out.println("Comment detected, ignoring");
                    int i = 2;
                    while (true) {
                        inspectedChar = inspectChar(i);
                        if (inspectedChar == '\n' || inspectedChar == SourceFile.eof) {
                            break;
                        }
                        i++;
                    }
                    eatChars(i);
                    break;
                }
                else if (currentChar == '*') {
                    System.out.println("Start of comment detected");
                    acceptNoCount();
                    while (currentChar != '*') {
                        acceptNoCount();
                    }
                    if (currentChar == '*') {
                        while (currentChar == '*') {
                            acceptNoCount();
                        }
                        if (currentChar == '/') {
                            System.out.println("Valid comment detected, ignoring");
                            acceptNoCount();
                        }
                    }
                }
                //If not comment, DIV token
                else {
                    accept();
                    currentSpelling.append((Token.spell(Token.DIV)));
                    return Token.DIV;
                }
                System.out.println("Ended case for /");
                // separators
            case '(':
                System.out.println("Detecting LPAREN");
                currentSpelling.append(Token.spell(Token.LPAREN));
                accept();
                return Token.LPAREN;
            case '.':
                //  attempting to recognise a float

            case '|':
                accept();
                if (currentChar == '|') {
                    accept();
                    return Token.OROR;
                } else {
                    return Token.ERROR;
                }

                //operators
            case '*':
                accept();
                currentSpelling.append(Token.spell(Token.MULT));
                return Token.MULT;

            //String literals
            case '"':
                //attempt to recognise string literal
                System.out.println("Scanning string literal");
                accept();
                currentSpelling.append(currentChar);
                //scan until next "
                while (currentChar != '"') {
                    accept();
                    currentSpelling.append(currentChar);
                    if (currentChar == SourceFile.eof) {
                        currentSpelling.equals(Token.spell(Token.EOF));
                        break;
                    }
                }
                accept();
                System.out.println("Valid string literal");
                return Token.STRINGLITERAL;
            // ....
            case SourceFile.eof:
                currentSpelling.append(Token.spell(Token.EOF));
                return Token.EOF;
            default:
                //whitespace
                if (Character.isWhitespace(currentChar)) {
                    System.out.println("Whitespace detected, ignoring");
                    acceptNoCount();
                }
                break;
        }

        accept();
        return Token.ERROR;
    }

//  void skipSpaceAndComments() {
//      while (currentChar != SourceFile.eof) {
//          if (currentChar == '/') {
//              acceptNoCount();
//              //detect comments structured like this
//              if (currentChar == '/') {
//
//                  acceptNoCount();
//              }
//              /*detect comments structured like this*/
//              if (currentChar == '*') {
//                  System.out.println("Start of comment detected");
//                  char prevChar = currentChar;
//                  while (currentChar != '/') {
//                      prevChar = currentChar;
//                      acceptNoCount();
//                  }
//                  System.out.println(prevChar);
//                  System.out.println(currentChar);
//                  if (currentChar == '/' && prevChar == '*') {
//                      System.out.println("Valid comment detected, ignoring");
//                  } else {
//                      System.out.println("Error, invalid comment");
//                  }
//              }
//          }
//          System.out.println(currentChar);
//          acceptNoCount();
//      }
//    System.out.println("exiting skipwhitespace");
//  }

    public Token getToken() {
        Token tok;
        int kind;

        // skip white space and comments

//   skipSpaceAndComments();

        currentSpelling = new StringBuffer("");

        sourcePos = new SourcePosition();

        // You must record the position of the current token somehow
        System.out.println("Calling nextToken");
        kind = nextToken();

        tok = new Token(kind, currentSpelling.toString(), sourcePos);

        // * do not remove these three lines
        if (debug)
            System.out.println(tok);
        return tok;
    }


}
