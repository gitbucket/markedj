package io.github.gitbucket.markedj.token;

public class ListEndToken implements Token {
    @Override
    public String getType() {
        return "ListEndToken";
    }
}
