package io.github.gitbucket.markedj.token;

public class ListStartToken implements Token {

    private boolean orderd;

    public ListStartToken(boolean orderd){
        this.orderd = orderd;
    }

    public boolean isOrderd() {
        return orderd;
    }

    @Override
    public String getType() {
        return "ListStartToken";
    }

}
