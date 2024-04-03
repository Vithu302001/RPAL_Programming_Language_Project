package LexAnalyser;

import Parser_RPAL.Node_Type;

public class Token {
    public TokenType type;
    public String value;
    public int line;

    public Token(TokenType type, String value, int line) {
        this.type = type;
        this.value = value;
        this.line = line;

    }

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public Token() {
        this.type = null;
        this.value = null;

    }

}
