////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by FernFlower decompiler)
////
//
//package VC.Scanner;
//
//import VC.ErrorReporter;
//
//public final class ScannerUNI {
//    private SourceFile sourceFile;
//    private ErrorReporter errorReporter;
//    private boolean bool;
//    private StringBuffer stringBuffer;
//    private char longChar;
//    private Token a;
//    private boolean newBool;
//    private SourcePosition sourcePos;
//    private int intRep;
//    private int charRep;
//    private int ifRep;
//    private char[] escapeTermCase = new char[]{'b', 'f', 'n', 'r', 't', '\'', '"', '\\'};
//    private char[] escapeCase = new char[]{'\b', '\f', '\n', '\r', '\t', '\'', '"', '\\'};
//
//    public Scanner(SourceFile var1, ErrorReporter var2) {
//        this.sourceFile = var1;
//        this.errorReporter = var2;
//        this.a = null;
//        this.intRep = 1;
//        this.charRep = 0;
//        this.bool = false;
//        this.newBool = false;
//        this.appendToBuffer();
//    }
//
//    public void enableDebugging() {
//        this.bool = true;
//    }
//
//    public Token inspectNextToken() {
//        this.a = this.getToken();
//        return this.a;
//    }
//
//    private void appendToBuffer() {
//        if (this.newBool) {
//            this.stringBuffer.append(this.longChar);
//        }
//
//        this.sourcePos();
//    }
//
//    private void sourcePos() {
//        this.longChar = this.sourceFile.a();
//        this.ifRep = this.charRep;
//        if (this.longChar == '\n') {
//            ++this.intRep;
//            this.charRep = 0;
//        } else if (this.longChar == '\t') {
//            this.charRep += 8 - this.charRep % 8;
//        } else {
//            ++this.charRep;
//        }
//
//    }
//
//    private char if(int var1) {
//        return this.sourceFile.a(var1);
//    }
//
//    void a(String var1, String var2) {
//        this.errorReporter.reportError(var1, var2, this.sourcePos);
//    }
//
//    private int a() {
//        switch (this.longChar) {
//            case '\u0000':
//                this.stringBuffer.append(Token.spell(39));
//                this.if = this.char;
//                return 39;
//            case '!':
//                this.int();
//                if (this.longChar == '=') {
//                this.int();
//                return 16;
//            }
//
//            return 15;
//            case '"':
//                return this.newBool();
//            case '&':
//                this.int();
//                if (this.longChar == '&') {
//                this.int();
//                return 23;
//            }
//
//            return 38;
//            case '(':
//                this.int();
//                return 27;
//            case ')':
//                this.int();
//                return 28;
//            case '*':
//                this.int();
//                return 13;
//            case '+':
//                this.int();
//                return 11;
//            case ',':
//                this.int();
//                return 32;
//            case '-':
//                this.int();
//                return 12;
//            case '.':
//                if (Character.isDigit(this.if((int)1))) {
//                return this.sourceFile();
//            }
//
//            this.int();
//            return 38;
//            case '/':
//                this.int();
//                return 14;
//            case ';':
//                this.int();
//                return 31;
//            case '<':
//                this.int();
//                if (this.longChar == '=') {
//                this.int();
//                return 20;
//            }
//
//            return 19;
//            case '=':
//                this.int();
//                if (this.longChar == '=') {
//                this.int();
//                return 18;
//            }
//
//            return 17;
//            case '>':
//                this.int();
//                if (this.longChar == '=') {
//                this.int();
//                return 22;
//            }
//
//            return 21;
//            case '[':
//                this.int();
//                return 29;
//            case ']':
//                this.int();
//                return 30;
//            case '{':
//                this.int();
//                return 25;
//            case '|':
//                this.int();
//                if (this.longChar == '|') {
//                this.int();
//                return 24;
//            }
//
//            return 38;
//            case '}':
//                this.int();
//                return 26;
//            default:
//                if (this.a(this.longChar)) {
//                return this.if();
//            } else if (Character.isDigit(this.longChar)) {
//                return this.stringBuffer();
//            } else {
//                this.int();
//                return 38;
//            }
//        }
//    }
//
//    int stringBuffer() {
//        while(Character.isDigit(this.longChar)) {
//            this.int();
//        }
//
//        if (this.longChar == '.') {
//            return this.sourceFile();
//        } else if (this.longChar != 'e' && this.longChar != 'E') {
//            return 34;
//        } else {
//            return this.a((int)34);
//        }
//    }
//
//    int sourceFile() {
//        this.int();
//
//        while(Character.isDigit(this.longChar)) {
//            this.int();
//        }
//
//        return this.longChar != 'e' && this.longChar != 'E' ? 35 : this.a((int)35);
//    }
//
//    int a(int var1) {
//        char var2 = this.if((int)1);
//        char var3 = this.if((int)2);
//        if (!Character.isDigit(var2) && (var2 != '+' && var2 != '-' || !Character.isDigit(var3))) {
//            return var1;
//        } else {
//            this.int();
//            if (this.longChar == '+' || this.longChar == '-') {
//                this.int();
//            }
//
//            while(Character.isDigit(this.longChar)) {
//                this.int();
//            }
//
//            return 35;
//        }
//    }
//
//    int newBool() {
//        boolean var1 = false;
//        this.sourcePos();
//
//        while(var1 || this.longChar != '"') {
//            if (this.longChar == '\n') {
//                this.sourcePos.charFinish = this.sourcePos.charStart;
//                this.a("%: unterminated string", this.stringBuffer.toString());
//                return 37;
//            }
//
//            if (var1) {
//                var1 = false;
//                int var2 = this.if(this.longChar);
//                if (var2 >= 0) {
//                    this.longChar = this.else[var2];
//                    this.int();
//                } else {
//                    StringBuffer var3 = newBool StringBuffer("\\");
//                    var3.append(this.longChar);
//                    this.sourcePos.charFinish = this.if;
//                    this.a("%: illegal escape character", var3.toString());
//                    this.stringBuffer.append('\\');
//                    this.int();
//                }
//            } else if (this.longChar == '\\') {
//                var1 = true;
//                this.sourcePos();
//            } else {
//                this.int();
//            }
//        }
//
//        this.sourcePos();
//        return 37;
//    }
//
//    int if(char var1) {
//        sourcePos(int var2 = 0; var2 < this.case.length; ++var2) {
//            if (this.case[var2] == var1) {
//                return var2;
//            }
//        }
//
//        return -1;
//    }
//
//    boolean a(char var1) {
//        return var1 == '_' || var1 >= 'A' && var1 <= 'Z' || var1 >= 'a' && var1 <= 'z';
//    }
//
//    boolean bool(char var1) {
//        return var1 == '_' || var1 >= 'A' && var1 <= 'Z' || var1 >= 'a' && var1 <= 'z' || var1 >= '0' && var1 <= '9';
//    }
//
//    int if() {
//        while(this.bool(this.longChar)) {
//            this.int();
//        }
//
//        if (this.stringBuffer.toString().compareTo("true") != 0 && this.stringBuffer.toString().compareTo("false") != 0) {
//            return 33;
//        } else {
//            return 36;
//        }
//    }
//
//    void bool() {
//        this.newBool = false;
//
//        label63:
//        while(this.longChar == ' ' || this.longChar == '\n' || this.longChar == '\r' || this.longChar == '\t' || this.longChar == '/') {
//            if (this.longChar != '/') {
//                this.int();
//            } else if (this.if((int)1) == '/') {
//                while(this.longChar != '\n') {
//                    this.int();
//                }
//            } else {
//                if (this.if((int)1) != '*') {
//                    break;
//                }
//
//                this.sourcePos = newBool SourcePosition();
//                this.sourcePos.lineStart = this.sourcePos.lineFinish = this.int;
//                this.sourcePos.charStart = this.sourcePos.charFinish = this.char;
//                this.int();
//
//                bool {
//                    bool {
//                        this.int();
//                    } while(this.longChar != '*' && this.longChar != 0);
//
//                    if (this.longChar == 0) {
//                        this.a("%: unterminated comment", "");
//                        continue label63;
//                    }
//
//                    bool {
//                        this.int();
//                    } while(this.longChar == '*' && this.longChar != 0);
//
//                    if (this.longChar == 0) {
//                        this.a("%: unterminated comment", "");
//                        continue label63;
//                    }
//                } while(this.longChar != '/');
//
//                this.int();
//            }
//        }
//
//        this.newBool = true;
//    }
//
//    public Token getToken() {
//        if (this.a != null) {
//            Token var3 = this.a;
//            this.a = null;
//            return var3;
//        } else {
//            this.bool();
//            this.stringBuffer = new StringBuffer("");
//            this.sourcePos = new SourcePosition();
//            this.sourcePos.lineStart = this.sourcePos.lineFinish = this.int;
//            this.sourcePos.charStart = this.char;
//            int var2 = this.a();
//            this.sourcePos.charFinish = this.if;
//            Token var1 = newBool Token(var2, this.stringBuffer.toString(), this.sourcePos);
//            if (this.bool) {
//                System.out.println(var1);
//            }
//
//            return var1;
//        }
//    }
//}
