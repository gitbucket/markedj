package io.github.gitbucket.markedj.token;

public class CodeToken implements Token {

    private String code;
    private String lang;
    private boolean escaped;

    public CodeToken(String code, String lang, boolean escaped){
        this.code = code;
        this.lang = lang;
        this.escaped = escaped;
    }

    public String getCode() {
        return code;
    }

    public String getLang() {
        return lang;
    }

    public boolean isEscaped() {
        return escaped;
    }

    @Override
    public String getType() {
        return "CodeToken";
    }

}
