package io.github.gitbucket.markedj.token;

import java.util.Optional;

public class CodeToken implements Token {

    private String code;
    private Optional<String> lang;
    private boolean escaped;

    public CodeToken(String code, Optional<String> lang, boolean escaped){
        this.code = code;
        this.lang = lang;
        this.escaped = escaped;
    }

    public String getCode() {
        return code;
    }

    public Optional<String> getLang() {
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
