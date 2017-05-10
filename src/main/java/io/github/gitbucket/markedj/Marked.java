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
        whitelist.addAttributes("code", "class");
        whitelist.addAttributes("th", "style");
        whitelist.addAttributes("td", "style");
        whitelist.addAttributes("h1", "id");
        whitelist.addAttributes("h2", "id");
        whitelist.addAttributes("h3", "id");
        whitelist.addAttributes("h4", "id");
        whitelist.addAttributes("h5", "id");
        whitelist.addAttributes("h6", "id");

        return Jsoup.clean(html, whitelist);
    }

}
