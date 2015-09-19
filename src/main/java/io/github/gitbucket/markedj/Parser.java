package io.github.gitbucket.markedj;

import io.github.gitbucket.markedj.token.*;

import java.util.Map;
import java.util.Optional;
import java.util.Stack;

public class Parser {

    protected Options options;
    protected Renderer renderer;

    public Parser(Options options, Renderer renderer){
        this.options = options;
        this.renderer = renderer;
    }

    public String parse(Stack<Token> src, Map<String, Lexer.Link> links){
        ParserContext context = new ParserContext(src, links);
        StringBuilder out = new StringBuilder();

        while(context.nextToken().isPresent()){
            out.append(tok(context));
        }

        return out.toString();
    }

    protected String parseText(ParserContext context){
        StringBuilder body = new StringBuilder(((TextToken) context.currentToken()).getText());
        while(true){
            Optional<Token> p = context.peekToken();
            if(!p.isPresent() || !(p.get().getType().equals("TextToken"))){
                break;
            }
            body.append("\n" + ((TextToken) context.nextToken().get()).getText());
        }
        return context.getInlineLexer().output(body.toString());
    }

    protected String tok(ParserContext context){
        switch(context.currentToken().getType()){
            case "SpaceToken": {
                return "";
            }
            case "HrToken": {
                return renderer.hr();
            }
            case "HeadingToken": {
                HeadingToken t = (HeadingToken) context.currentToken();
                return renderer.heading(context.getInlineLexer().output(t.getText()), t.getDepth(), t.getText());
            }
            case "CodeToken": {
                CodeToken t = (CodeToken) context.currentToken();
                return renderer.code(t.getCode(), t.getLang(), t.isEscaped());
            }
            case "TableToken": {
                TableToken t = (TableToken) context.currentToken();
                StringBuilder outCell   = new StringBuilder();
                StringBuilder outHeader = new StringBuilder();
                StringBuilder outBody   = new StringBuilder();

                for(int i = 0; i < t.getHeader().size(); i++){
                    outCell.append(renderer.tablecell(
                            context.getInlineLexer().output(t.getHeader().get(i)), new Renderer.TableCellFlags(true, t.getAlign().get(i))));
                }
                outHeader.append(renderer.tablerow(outCell.toString()));

                for(int i = 0; i < t.getCells().size(); i++){
                    outCell.setLength(0);
                    for(int j = 0; j < t.getCells().get(i).size(); j++){
                        outCell.append(renderer.tablecell(
                                context.getInlineLexer().output(t.getCells().get(i).get(j)), new Renderer.TableCellFlags(false, t.getAlign().get(j))));
                    }
                    outBody.append(renderer.tablerow(outCell.toString()));
                }
                return renderer.table(outHeader.toString(), outBody.toString());
            }
            case "BlockquoteStartToken": {
                StringBuilder body = new StringBuilder();
                while(true){
                    Optional<Token> n = context.nextToken();
                    if(!n.isPresent() || n.get().getType().equals("BlockquoteEndToken")){
                        break;
                    }
                    body.append(tok(context));
                }
                return renderer.blockquote(body.toString());
            }
            case "ListStartToken": {
                ListStartToken t = (ListStartToken) context.currentToken();
                StringBuilder out = new StringBuilder();
                while(true){
                    Optional<Token> n = context.nextToken();
                    if(!n.isPresent() || n.get().getType().equals("ListEndToken")){
                        break;
                    }
                    out.append(tok(context));
                }
                return renderer.list(out.toString(), t.isOrderd());
            }
            case "ListItemStartToken": {
                StringBuilder out = new StringBuilder();
                while(true){
                    Optional<Token> n = context.nextToken();
                    if(!n.isPresent() || n.get().getType().equals("ListItemEndToken")){
                        break;
                    }
                    if(context.currentToken().getType().equals("TextToken")){
                        out.append(parseText(context));
                    } else {
                        out.append(tok(context));
                    }
                }
                return renderer.listitem(out.toString());
            }
            case "LooseItemStartToken": {
                StringBuilder out = new StringBuilder();
                while(true){
                    Optional<Token> n = context.nextToken();
                    if(!n.isPresent() || n.get().getType().equals("ListItemEndToken")){
                        break;
                    }
                    out.append(tok(context));
                }
                return renderer.listitem(out.toString());
            }
            case "HtmlToken": {
                HtmlToken t = (HtmlToken) context.currentToken();
                if(!t.isPre() && !options.isPedantic()){
                    return renderer.html(context.getInlineLexer().output(t.getText()));
                } else {
                    return renderer.html(t.getText());
                }
            }
            case "ParagraphToken": {
                ParagraphToken t = (ParagraphToken) context.currentToken();
                return renderer.paragraph(context.getInlineLexer().output(t.getText()));
            }
            case "TextToken": {
                return renderer.paragraph(parseText(context));
            }
            default: {
                throw new RuntimeException("Unexpected token: " + context.currentToken());
            }
        }
    }

    public class ParserContext {

        private Stack<Token> tokens = new Stack<>();
        private Token token = null;
        private InlineLexer inline;

        public ParserContext(Stack<Token> src, Map<String, Lexer.Link> links){
            // reverse
            tokens = new Stack<>();
            while(!src.isEmpty()){
                tokens.push(src.pop());
            }

            inline = new InlineLexer(Grammer.INLINE_RULES, links, options, renderer);
        }

        public Token currentToken(){
            return token;
        }

        public Optional<Token> nextToken(){
            if(tokens.isEmpty()){
                return Optional.empty();
            } else {
                token = tokens.pop();
                return Optional.of(token);
            }
        }

        public Optional<Token> peekToken(){
            if(tokens.isEmpty()){
                return Optional.empty();
            } else {
                return Optional.of(tokens.get(tokens.size() - 1));
            }
        }

        public InlineLexer getInlineLexer(){
            return inline;
        }
    }
}
