package io.github.gitbucket.markedj.token;

public class BlockquoteEndToken implements Token {
    @Override
    public String getType() {
        return "BlockquoteEndToken";
    }
}
