package io.github.gitbucket.markedj;

import io.github.gitbucket.markedj.token.*;

import java.util.Optional;
import java.util.Stack;

public class Parser {

    protected Options options;
    protected Renderer renderer;

    protected Stack<Token> tokens;
    protected Token token;
    protected InlineLexer inline;


    public Parser(Options options, Renderer renderer){
        this.options = options;
        this.renderer = renderer;
    }

    public String parse(Stack<Token> src){
        InlineLexer inline = new InlineLexer(Grammer.INLINE_RULES, options, renderer);

        StringBuilder out = new StringBuilder();

        // reverse
        tokens = new Stack<Token>();
        src.stream().forEach(e -> tokens.push(e));

        while(next().isPresent()){
            out.append(tok());
        }

        return out.toString();
    }

    protected Optional<Token> next(){
        if(tokens.isEmpty()){
            return Optional.empty();
        } else {
            token = tokens.pop();
            return Optional.of(token);
        }
    }

    protected Optional<Token> peek(){
        if(tokens.isEmpty()){
            return Optional.empty();
        } else {
            return Optional.of(tokens.get(tokens.size() - 1));
        }
    }

    protected String parseText(){
        StringBuilder body = new StringBuilder(((TextToken) token).getText());
        while(true){
            Optional<Token> p = peek();
            if(!p.isPresent() || !(p.get().getType().equals("TextToken"))){
                break;
            }
            body.append("\n" + ((TextToken) next().get()).getText());
        }
        return inline.output(body.toString());
    }

    protected String tok(){
        switch(token.getType()){
            case "SpaceToken": {
                return "";
            }
            case "HrToken": {
                return renderer.hr();
            }
            case "HeadingToken": {
                HeadingToken t = (HeadingToken) token;
                return renderer.heading(inline.output(t.getText()), t.getDepth(), t.getText());
            }
            case "CodeToken": {
                CodeToken t = (CodeToken) token;
                return renderer.code(t.getCode(), t.getLang(), t.isEscaped());
            }
            case "TableToken": {
                TableToken t = (TableToken) token;
                StringBuilder outCell   = new StringBuilder();
                StringBuilder outHeader = new StringBuilder();
                StringBuilder outBody   = new StringBuilder();

                for(int i = 0; i < t.getHeader().size(); i++){
                    outCell.append(renderer.tablecell(
                            inline.output(t.getHeader().get(i)), new Renderer.TableCellFlags(true, t.getAlign().get(i))));
                }
                outHeader.append(renderer.tablerow(outCell.toString()));

                for(int i = 0; i < t.getCells().size(); i++){
                    outCell.setLength(0);
                    for(int j = 0; j < t.getCells().get(i).size(); j++){
                        outCell.append(renderer.tablecell(
                                inline.output(t.getCells().get(i).get(j)), new Renderer.TableCellFlags(false, t.getAlign().get(j))));
                    }
                    outBody.append(renderer.tablerow(outCell.toString()));
                }
                return renderer.table(outHeader.toString(), outBody.toString());
            }
            case "BlockquoteStartToken": {
                StringBuilder body = new StringBuilder();
                while(true){
                    Optional<Token> n = next();
                    if(!n.isPresent() || n.get().getType().equals("BlockquoteEndToken")){
                        break;
                    }
                    body.append(tok());
                }
                return renderer.blockquote(body.toString());
            }
            case "ListStartToken": {
                ListStartToken t = (ListStartToken) token;
                StringBuilder out = new StringBuilder();
                while(true){
                    Optional<Token> n = next();
                    if(!n.isPresent() || n.get().getType().equals("ListEndToken")){
                        break;
                    }
                    out.append(tok());
                }
                return renderer.list(out.toString(), t.isOrderd());
            }
            case "ListItemStartToken": {
                StringBuilder out = new StringBuilder();
                while(true){
                    Optional<Token> n = next();
                    if(!n.isPresent() || n.get().getType().equals("ListItemEndToken")){
                        break;
                    }
                    if(token.getType().equals("TextToken")){
                        out.append(parseText());
                    } else {
                        out.append(tok());
                    }
                }
                return renderer.listitem(out.toString());
            }
            case "LooseItemStartToken": {
                StringBuilder out = new StringBuilder();
                while(true){
                    Optional<Token> n = next();
                    if(!n.isPresent() || n.get().getType().equals("ListItemEndToken")){
                        break;
                    }
                    out.append(tok());
                }
                return renderer.listitem(out.toString());
            }
            case "HtmlToken": {
                HtmlToken t = (HtmlToken) token;
                if(!t.isPre() && !options.isPedantic()){
                    return renderer.html(inline.output(t.getText()));
                } else {
                    return renderer.html(t.getText());
                }
            }
            case "TextToken": {
                return renderer.paragraph(parseText());
            }
            default: {
                throw new RuntimeException("Unexpected token: " + token);
            }
        }
    }

}
