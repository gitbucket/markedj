package io.github.gitbucket.markedj;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

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

        Safelist safelist = options.getSafelist();

        if(safelist != null) {
            return Jsoup.clean(html, safelist);
        } else {
            return html;
        }
    }

}
