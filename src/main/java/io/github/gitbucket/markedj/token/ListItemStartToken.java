package io.github.gitbucket.markedj.token;

public class ListItemStartToken implements Token {
    @Override
    public String getType() {
        return "ListItemStartToken";
    }
}
