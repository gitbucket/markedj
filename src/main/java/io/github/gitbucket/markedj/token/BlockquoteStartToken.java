package io.github.gitbucket.markedj.token;

public class BlockquoteStartToken implements Token {
    @Override
    public String getType() {
        return "BlockquoteStartToken";
    }
}
