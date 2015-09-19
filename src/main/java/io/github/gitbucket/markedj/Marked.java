package io.github.gitbucket.markedj;

import io.github.gitbucket.markedj.token.Token;

import java.util.Stack;

public class Marked {

    public static String marked(String src, Options options){
        Lexer lexer = new Lexer(options);
        Stack<Token> tokens = lexer.lex(src);
        Parser parser = new Parser(options, new Renderer(options));
        return parser.parse(tokens);
    }

}
