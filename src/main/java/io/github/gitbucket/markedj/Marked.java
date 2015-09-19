package io.github.gitbucket.markedj;

public class Marked {

    public static String marked(String src, Options options){
        Lexer lexer = new Lexer(options);
        Lexer.LexerResult result = lexer.lex(src);
        Parser parser = new Parser(options, new Renderer(options));
        return parser.parse(result.getTokens(), result.getLinks());
    }

}
