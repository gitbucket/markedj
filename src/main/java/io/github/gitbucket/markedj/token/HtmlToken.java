package io.github.gitbucket.markedj.token;

public class HtmlToken implements Token {

    private String text;
    private boolean pre;

    public HtmlToken(String text, boolean pre){
        this.text = text;
        this.pre = pre;
    }

    public String getText() {
        return text;
    }

    public boolean isPre() {
        return pre;
    }

    @Override
    public String getType() {
        return "HtmlToken";
    }

}
