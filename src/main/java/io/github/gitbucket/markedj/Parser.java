package io.github.gitbucket.markedj;

import io.github.gitbucket.markedj.extension.Extension;
import io.github.gitbucket.markedj.rule.Rule;
import io.github.gitbucket.markedj.token.*;

import java.util.Map;
import java.util.Optional;
import java.util.Stack;

public class Parser {

    protected Options options;
    protected Renderer renderer;
    protected Map<String, Rule> rules;

    public Parser(Options options, Renderer renderer){
        this.options = options;
        this.renderer = renderer;
    }

    public String parse(Stack<Token> src, Map<String, Lexer.Link> links){
        if(options.isGfm()){
            if(options.isBreaks()){
                rules = Grammer.INLINE_BREAKS_RULES;
            } else {
                rules = Grammer.INLINE_GFM_RULES;
            }
        } else {
            rules = Grammer.INLINE_RULES;
        }
		
		options.extensions().forEach((extension) -> {
			rules = extension.enhanceRules(rules);
		});

        ParserContext context = new ParserContext(src, links, rules);
        StringBuilder out = new StringBuilder();

        while(context.nextToken() != null){
            out.append(tok(context));
        }

        return out.toString();
    }

    protected String parseText(ParserContext context){
        StringBuilder body = new StringBuilder(((TextToken) context.currentToken()).getText());
        while(true){
            Token p = context.peekToken();
            if(p == null || !p.getType().equals("TextToken")){
                break;
            }
            body.append("\n" + ((TextToken) context.nextToken()).getText());
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
                    Token n = context.nextToken();
                    if(n == null || n.getType().equals("BlockquoteEndToken")){
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
                    Token n = context.nextToken();
                    if(n == null || n.getType().equals("ListEndToken")){
                        break;
                    }
                    out.append(tok(context));
                }
                return renderer.list(out.toString(), t.isOrderd());
            }
            case "ListItemStartToken": {
                StringBuilder out = new StringBuilder();
                while(true){
                    Token n = context.nextToken();
                    if(n == null || n.getType().equals("ListItemEndToken")){
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
                    Token n = context.nextToken();
                    if(n == null || n.getType().equals("ListItemEndToken")){
                        break;
                    }
                    out.append(tok(context));
                }
                return renderer.listitem(out.toString());
            }
            case "HtmlToken": {
                HtmlToken t = (HtmlToken) context.currentToken();
                return renderer.html(context.getInlineLexer().output(t.getText()));
            }
            case "ParagraphToken": {
                ParagraphToken t = (ParagraphToken) context.currentToken();
                return renderer.paragraph(context.getInlineLexer().output(t.getText()));
            }
            case "TextToken": {
                return renderer.paragraph(parseText(context));
            }
            default: {
				// try to find extension
				String tokenType = context.currentToken().getType();
				Optional<Extension>	extension = options.extensions().stream().filter(ext -> ext.handlesToken(tokenType)).findFirst();
				if (extension.isPresent()) {
					return extension.get().parse(context, this::tok);
				}
                throw new RuntimeException("Unexpected token: " + context.currentToken());
            }
        }
    }

    public class ParserContext {

        private Stack<Token> tokens = new Stack<>();
        private Token token = null;
        private InlineLexer inline;

        public ParserContext(Stack<Token> src, Map<String, Lexer.Link> links, Map<String, Rule> rules){
            // reverse
            tokens = new Stack<>();
            while(!src.isEmpty()){
                tokens.push(src.pop());
            }

            inline = new InlineLexer(rules, links, options, renderer);
        }

        public Token currentToken(){
            return token;
        }

        public Token nextToken(){
            if(tokens.isEmpty()){
                return null;
            } else {
                token = tokens.pop();
                return token;
            }
        }

        public Token peekToken(){
            if(tokens.isEmpty()){
                return null;
            } else {
                return tokens.get(tokens.size() - 1);
            }
        }

        public InlineLexer getInlineLexer(){
            return inline;
        }
    }
}
