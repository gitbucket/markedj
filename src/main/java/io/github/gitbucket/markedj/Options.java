package io.github.gitbucket.markedj;

public class Options {

    private boolean gfm = true;
    private boolean tables = true;
    private boolean breaks = false;
    private boolean sanitize = false;
    // TODO private Object highlight = null;
    private String langPrefix = "lang-";
    private String headerPrefix = "";
    private boolean xhtml = false;

    public void setGfm(boolean gfm) {
        this.gfm = gfm;
    }

    public void setTables(boolean tables) {
        this.tables = tables;
    }

    public void setBreaks(boolean breaks) {
        this.breaks = breaks;
    }

    public void setSanitize(boolean sanitize) {
        this.sanitize = sanitize;
    }

    public void setLangPrefix(String langPrefix) {
        this.langPrefix = langPrefix;
    }

    public void setHeaderPrefix(String headerPrefix) {
        this.headerPrefix = headerPrefix;
    }

    public void setXhtml(boolean xhtml) {
        this.xhtml = xhtml;
    }

    public boolean isGfm() {
        return gfm;
    }

    public boolean isTables() {
        return tables;
    }

    public boolean isBreaks() {
        return breaks;
    }

    public boolean isSanitize() {
        return sanitize;
    }

    public String getLangPrefix() {
        return langPrefix;
    }

    public String getHeaderPrefix() {
        return headerPrefix;
    }

    public boolean isXhtml() {
        return xhtml;
    }
}
