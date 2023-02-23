package VC.Scanner;

import VC.ErrorReporter;

public final class Scanner {

    private SourceFile sourceFile;
    private boolean debug;

    private ErrorReporter errorReporter;
    private StringBuffer currentSpelling;
    private char currentChar;

    private int linePos;

    private int colPos;
    private SourcePosition sourcePos;

// =========================================================

    public Scanner(SourceFile source, ErrorReporter reporter) {
        sourceFile = source;
        errorReporter = reporter;
        currentChar = sourceFile.getNextChar();
        debug = false;
        linePos = 1;
        colPos = 1;

        // you may initialise your counters for line and column numbers here
    }

    public void enableDebugging() {
        debug = true;
    }

    // accept gets the next character from the source program.

    private void accept() {
        if (currentChar == '\n') {
            linePos++;
            colPos = 1;
        }
        else {
            colPos++;
        }
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

//        System.out.println("Identifying character " + currentChar);
        switch (currentChar) {
            //DIV
            case '/':
                accept();
                currentSpelling.append((Token.spell(Token.DIV)));
                return Token.DIV;

                // separators
            case '(':
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
        }
        accept();
        return Token.ERROR;
    }

    boolean doCurAndNextCharsMatch(char current, char next) {
        return currentChar == current && inspectChar(1) == next;
    }
  void skipSpaceAndComments() {
            boolean inSingleLineComment = false;
            boolean inMultiLineComment = false;
            while (true) {
                if (inSingleLineComment && currentChar == '\n') {
                    accept();
                    break;
                }
                if (Character.isWhitespace(currentChar)) {
                    accept();
                }
                if (doCurAndNextCharsMatch('/', '/')) {
                    inSingleLineComment = true;
                }
                if (doCurAndNextCharsMatch('/', '*')) {
                    inMultiLineComment = true;
                }
                if (inSingleLineComment || inMultiLineComment) {
                    if (doCurAndNextCharsMatch('*', '/')) {
                        accept();
                        accept();
                        inMultiLineComment = false;
                        continue;
                    }
                    if (inspectChar(1) == SourceFile.eof) {
                        System.out.println("ERROR: Unterminated Comment");
                        break;
                    }
                    accept();
                }
                else {
                    break;
                }
            }
  }

    public Token getToken() {
        Token tok;
        int kind;

        // skip white space and comments

        skipSpaceAndComments();

        currentSpelling = new StringBuffer("");

//        sourcePos = new SourcePosition();
        int initialColPosition = colPos;

        // You must record the position of the current token somehow
        kind = nextToken();
        int tokenLength = colPos - initialColPosition;
        //EOF stuff
        if (tokenLength == 0) {
            tokenLength = 1; //LOL
        }
        sourcePos = new SourcePosition(linePos, initialColPosition, initialColPosition + tokenLength -1);

        tok = new Token(kind, currentSpelling.toString(), sourcePos);

        // * do not remove these three lines
        if (debug)
            System.out.println(tok);
        return tok;
    }


}
