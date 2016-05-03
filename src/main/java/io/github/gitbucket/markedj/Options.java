package io.github.gitbucket.markedj;

public class Options {

    private boolean gfm = true;
    private boolean tables = true;
    private boolean breaks = false;
    private boolean pedantic = false;
    private boolean sanitize = false;
    // TOOO private Object sanitizer = null;
//    private boolean mangle = false;
//    private boolean smartLists = false;
//    private boolean silent = false;
    // TODO private Object highlight = null;
    private String langPrefix = "lang-";
//    private boolean smartypants = false;
    private String headerPrefix = "";
    private boolean xhtml = false;
    private boolean useNotifications = false;

    public Options setGfm(boolean gfm) {
        this.gfm = gfm;
        return this;
    }

    public Options setTables(boolean tables) {
        this.tables = tables;
        return this;
    }

    public Options setBreaks(boolean breaks) {
        this.breaks = breaks;
        return this;
    }

    public Options setPedantic(boolean pedantic) {
        this.pedantic = pedantic;
        return this;
    }

    public Options setSanitize(boolean sanitize) {
        this.sanitize = sanitize;
        return this;
    }

//    public void setMangle(boolean mangle) {
//        this.mangle = mangle;
//    }
//
//    public void setSmartLists(boolean smartLists) {
//        this.smartLists = smartLists;
//    }

//    public void setSilent(boolean silent) {
//        this.silent = silent;
//    }

    public Options setLangPrefix(String langPrefix) {
        this.langPrefix = langPrefix;
        return this;
    }

//    public void setSmartypants(boolean smartypants) {
//        this.smartypants = smartypants;
//    }

    public Options setHeaderPrefix(String headerPrefix) {
        this.headerPrefix = headerPrefix;
        return this;
    }

    public Options setXhtml(boolean xhtml) {
        this.xhtml = xhtml;
        return this;
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

    public boolean isPedantic() {
        return pedantic;
    }

    public boolean isSanitize() {
        return sanitize;
    }

//    public boolean isMangle() {
//        return mangle;
//    }
//
//    public boolean isSmartLists() {
//        return smartLists;
//    }

//    public boolean isSilent() {
//        return silent;
//    }

    public String getLangPrefix() {
        return langPrefix;
    }

//    public boolean isSmartypants() {
//        return smartypants;
//    }

    public String getHeaderPrefix() {
        return headerPrefix;
    }

    public boolean isXhtml() {
        return xhtml;
    }
    
    public boolean useNotifications() {
        return useNotifications;
    }

    public Options useNotifications(boolean value) {
        useNotifications = value;
        return this;
    }
}
