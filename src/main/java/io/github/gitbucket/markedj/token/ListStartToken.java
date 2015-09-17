package io.github.gitbucket.markedj.token;

public class ListStartToken {

    private boolean orderd;

    public ListStartToken(boolean orderd){
        this.orderd = orderd;
    }

    public boolean isOrderd() {
        return orderd;
    }
}
