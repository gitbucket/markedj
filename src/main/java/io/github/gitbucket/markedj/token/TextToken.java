package io.github.gitbucket.markedj.token;

public class TextToken implements  Token {

    private String text;

    public TextToken(String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String getType() {
        return "TextToken";
    }
}
