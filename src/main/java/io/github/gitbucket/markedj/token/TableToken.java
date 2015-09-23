package io.github.gitbucket.markedj.token;

import java.util.List;

public class TableToken implements  Token {

    private List<String> header;
    private List<String> align;
    private List<List<String>> cells;

    public TableToken(List<String> header, List<String> align, List<List<String>> cells){
        this.header = header;
        this.align = align;
        this.cells = cells;
    }

    public List<String> getHeader() {
        return header;
    }

    public List<String> getAlign() {
        return align;
    }

    public List<List<String>> getCells() {
        return cells;
    }

    @Override
    public String getType() {
        return "TableToken";
    }

}
