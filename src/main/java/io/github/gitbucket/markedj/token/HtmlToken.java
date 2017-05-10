package io.github.gitbucket.markedj.token;

public class HtmlToken implements Token {

    private String text;

    public HtmlToken(String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String getType() {
        return "HtmlToken";
    }

}
