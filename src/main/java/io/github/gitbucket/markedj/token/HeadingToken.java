package io.github.gitbucket.markedj.token;

public class HeadingToken implements Token {

    private int depth;
    private String text;

    public HeadingToken(int depth, String text){
        this.depth = depth;
        this.text = text;
    }

    public int getDepth() {
        return depth;
    }

    public String getText() {
        return text;
    }

    @Override
    public String getType() {
        return "HeadingToken";
    }

}
