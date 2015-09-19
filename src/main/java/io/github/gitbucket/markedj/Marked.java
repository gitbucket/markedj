package io.github.gitbucket.markedj;

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
        return parser.parse(result.getTokens(), result.getLinks());
    }

}
