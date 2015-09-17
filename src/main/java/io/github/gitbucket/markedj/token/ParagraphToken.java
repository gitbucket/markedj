package io.github.gitbucket.markedj.token;

public class ParagraphToken implements  Token {

    private String text;

    public ParagraphToken(String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
