package io.github.gitbucket.markedj.token;

import java.util.List;
import java.util.Optional;

public class TableToken implements  Token {

    private List<String> header;
    private List<Optional<String>> align;
    private List<List<String>> cells;

    public TableToken(List<String> header, List<Optional<String>> align, List<List<String>> cells){
        this.header = header;
        this.align = align;
        this.cells = cells;
    }

    public List<String> getHeader() {
        return header;
    }

    public List<Optional<String>> getAlign() {
        return align;
    }

    public List<List<String>> getCells() {
        return cells;
    }
}
