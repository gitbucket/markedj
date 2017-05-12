package io.github.gitbucket.markedj;

import org.jsoup.safety.Whitelist;

public class Options {

    private boolean gfm = true;
    private boolean tables = true;
    private boolean breaks = false;
    private boolean sanitize = false;
    private String langPrefix = "lang-";
    private String headerPrefix = "";
    private Whitelist whitelist = Whitelist.relaxed();
    {
        whitelist.addAttributes(":all", "id", "class", "style");
        whitelist.addTags("input");
        whitelist.addAttributes("input", "type", "checked", "name", "value", "disabled");
        whitelist.addProtocols("a", "href", "#");
    }

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

    public void setWhitelist(Whitelist whitelist){
        this.whitelist = whitelist;
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

    public Whitelist getWhitelist(){
        return whitelist;
    }

}
