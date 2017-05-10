package io.github.gitbucket.markedj;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

public class Marked {

    public static String marked(String src){
        Options options = new Options();
        return marked(src, options, new Renderer(options));
    }

    public static String marked(String src, Options options){
        return marked(src, options, new Renderer(options));
    }

    public static String marked(String src, Options options, Renderer renderer){
        Lexer lexer = new Lexer(options);
        Lexer.LexerResult result = lexer.lex(src);
        Parser parser = new Parser(options, renderer);
        String html = parser.parse(result.getTokens(), result.getLinks());

        Whitelist whitelist = Whitelist.relaxed();
        whitelist.addAttributes(":all", "id", "class", "style");
        whitelist.addTags("input");
        whitelist.addAttributes("input", "type", "checked", "name", "value", "disabled");
        whitelist.addProtocols("a", "href", "#");

        return Jsoup.clean(html, whitelist);
    }

}
