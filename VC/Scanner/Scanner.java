package VC.Scanner;

import VC.ErrorReporter;

public final class Scanner { 

  private SourceFile sourceFile;
  private boolean debug;

  private ErrorReporter errorReporter;
  private StringBuffer currentSpelling;
  private char currentChar;
  private SourcePosition sourcePos;
  private int colCount;
  private int lineCount;

// =========================================================

  public Scanner(SourceFile source, ErrorReporter reporter) {
    sourceFile = source;
    errorReporter = reporter;
    currentChar = sourceFile.getNextChar();
    debug = false;
    sourcePos = new SourcePosition();

    // you may initialise your counters for line and column numbers here
      colCount = sourcePos.charStart;
      lineCount = sourcePos.lineFinish;
  }

  public void enableDebugging() {
    debug = true;
  }

  // accept gets the next character from the source program.

  private void accept() {

    currentChar = sourceFile.getNextChar();

  // you may save the lexeme of the current token incrementally here
  // you may also increment your line and column counters here
      colCount = colCount + 1;
  }

  private void acceptNoCount() {
      currentChar = sourceFile.getNextChar();
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
  // Tokens: separators, operators, literals, identifiers and keywords
       
    switch (currentChar) {
       // separators 
    case '(':
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
    sourcePos.charStart = 1;
    sourcePos.charFinish = 1;
	return Token.EOF;
    default:
	break;
    }

    accept(); 
    return Token.ERROR;
  }

  void skipSpaceAndComments() {
      if (Character.isWhitespace(currentChar)) {
          acceptNoCount();
      }
      if (currentChar == '/') {
          acceptNoCount();
          if (currentChar == '/') {
              while (currentChar != '\n') {
                  acceptNoCount();
              }
          }
      }
  }

  public Token getToken() {
      System.out.println("Scanning for tokens");
    Token tok;
    int kind;

    // skip white space and comments

   skipSpaceAndComments();

   currentSpelling = new StringBuffer("");

   sourcePos = new SourcePosition();

   // You must record the position of the current token somehow

   kind = nextToken();

   tok = new Token(kind, currentSpelling.toString(), sourcePos);

   // * do not remove these three lines
   if (debug)
     System.out.println(tok);
   return tok;
   }

}
