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

    private boolean validEscapeChars(char esc) {
        if (esc == 't' ||esc == 'n' ||esc == 'r' || esc == '\'' ||esc == '"' || esc == '\\') {
            return true;
        }
        return false;
    }

    private int nextToken() {
        // Tokens: separators, operators, literals, identifiers and keyworods

//        System.out.println("Identifying character " + currentChar);
        switch (currentChar) {
                // separators
            case '(':
                currentSpelling.append(Token.spell(Token.LPAREN));
                accept();
                return Token.LPAREN;
            case ')':
                currentSpelling.append(Token.spell(Token.RPAREN));
                accept();
                return Token.RPAREN;

            case '{':
                currentSpelling.append(Token.spell(Token.LCURLY));
                accept();
                return Token.LCURLY;
            case '}':
                currentSpelling.append(Token.spell(Token.RCURLY));
                accept();
                return Token.RCURLY;
            case '[':
                currentSpelling.append(Token.spell(Token.LBRACKET));
                accept();
                return Token.LBRACKET;
            case ']':
                currentSpelling.append(Token.spell(Token.RBRACKET));
                accept();
                return Token.RBRACKET;

            //operators
            case '+':
                currentSpelling.append(Token.spell(Token.PLUS));
                accept();
                return Token.PLUS;
            case '-':
                currentSpelling.append(Token.spell(Token.MINUS));
                accept();
                return Token.MINUS;
            case '/':
                accept();
                currentSpelling.append((Token.spell(Token.DIV)));
                return Token.DIV;
            case '!':
                if (inspectChar(1) == '=') {
                    currentSpelling.append(Token.spell(Token.NOTEQ));
                    accept();
                    return Token.NOTEQ;
                }
                else {
                    currentSpelling.append(Token.spell(Token.NOT));
                    accept();
                    return Token.NOT;
                }
            case '=':
                if (inspectChar(1) == '=') {
                    currentSpelling.append(Token.spell(Token.EQEQ));
                    accept();
                    return Token.EQEQ;
                }
                else {
                    currentSpelling.append(Token.spell(Token.EQ));
                    accept();
                    return Token.EQ;
                }
            case '<':
                if (inspectChar(1) == '=') {
                    currentSpelling.append(Token.spell(Token.LTEQ));
                    accept();
                    return Token.LTEQ;
                }
                else {
                    currentSpelling.append(Token.spell(Token.LT));
                    accept();
                    return Token.LT;
                }
            case '>':
                if (inspectChar(1) == '=') {
                    currentSpelling.append(Token.spell(Token.GTEQ));
                    accept();
                    return Token.GTEQ;
                }
                else {
                    currentSpelling.append(Token.spell(Token.GT));
                    accept();
                    return Token.GT;
                }

            case '|':
                accept();
                if (currentChar == '|') {
                    accept();
                    return Token.OROR;
                } else {
                    currentSpelling.append('|');
                    return Token.ERROR;
                }

            case '&':
                accept();
                if (currentChar == '&') {
                    accept();
                    return Token.ANDAND;
                }
                else {
                    currentSpelling.append('&');
                    return Token.ERROR;
                }

            case '*':
                accept();
                currentSpelling.append(Token.spell(Token.MULT));
                return Token.MULT;
            //commas, semicolons etc
            case ';':
                currentSpelling.append(Token.spell(Token.SEMICOLON));
                accept();
                return Token.SEMICOLON;
            case ',':
                currentSpelling.append(Token.spell(Token.COMMA));
                accept();
                return Token.COMMA;


            //String literals
            case '"':
                //attempt to recognise string literal
                accept();
                currentSpelling.append(currentChar);
                //lookahead until next "
                int i = 1;
                while (true) {
                    char inspectedChar = inspectChar(i);

                    //ERROR CATCHING
                    if (inspectedChar == SourceFile.eof || inspectedChar == '\n') {
                        System.out.println("ERROR: " + currentSpelling + ": unterminated string");
                        int j = 1;
                        while (j < i) {
                            accept();
                            j++;
                        }
                        accept();
                        return Token.STRINGLITERAL;
                    }
                    if (inspectedChar == '\\') {

                        char escapeChar = inspectChar(i + 1);

                        //CATCH ESCAPE ERRORS
                        if (!validEscapeChars(escapeChar)) {
                            System.out.println("Error: \\" + escapeChar + ": illegal escape character");
                            currentSpelling.delete(0, currentSpelling.length());
                            while (currentChar != '"') {
                                currentSpelling.append(currentChar);
                                accept();
                            }
                            accept();
                            return Token.STRINGLITERAL;
                        }
                        else {
                            for (int m = 0; m < i; m++) {
                                accept();
                            }
                            System.out.println("Current char: |" + currentChar +"| " + "Escape char: |" + escapeChar + "| " + currentSpelling);
                            if (escapeChar == 't') {
                                currentSpelling.append('\t');
                            }
                        }
                    }
                    if (inspectedChar == '"') {
                        break;
                    }
                    currentSpelling.append(inspectedChar);
                    i++;
                }
                int j = 1;
                while (j < i) {
                    accept();
                    j++;
                }
                //get brackets
                accept();
                accept();
                return Token.STRINGLITERAL;
            // ....
            case SourceFile.eof:
                currentSpelling.append(Token.spell(Token.EOF));
                return Token.EOF;
            default:
                //keywords
                if (currentChar >= 'a' && currentChar <= 'z') {
                    String keywordString = new String();
                    keywordString = keywordString + currentChar;
                    accept();
                    while (currentChar >= 'a' && currentChar <= 'z') {
                        keywordString = keywordString + currentChar;
                        accept();
                    }
                    currentSpelling.append(keywordString);
                    return Token.ID;
                }

                //numbers
                if (Character.isDigit(currentChar)) {
                    currentSpelling.append(currentChar);
                    //  attempting to recognise a float
                    if (inspectChar(1) == '.') {
                        int initialCol = colPos;
                        currentSpelling.append('.');
                        int k = 2;
                        while (true) {
                            char lookAhead = inspectChar(k);
                            if (Character.isDigit(lookAhead)) {
                                currentSpelling.append(lookAhead);
                            }
                            else {
                                break;
                            }
                            k++;
                        }

                        accept();
                        int diff = k - initialCol;
                        for (int l = 0; l < diff; l++) {
                            accept();
                        }
                        return Token.FLOATLITERAL;
                    }
                    else {
                        currentSpelling.delete(0, currentSpelling.length());
                        currentSpelling.append(currentChar);
                        accept();
                        return Token.INTLITERAL;
                    }
                }

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
                    inSingleLineComment = false;
                    continue;
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
                        String posStr = linePos + "(1).." + linePos + "(1)";
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
