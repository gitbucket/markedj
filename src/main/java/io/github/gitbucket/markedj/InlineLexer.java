package io.github.gitbucket.markedj;

import io.github.gitbucket.markedj.rule.Rule;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.github.gitbucket.markedj.Utils.*;

public class InlineLexer {

    private Map<String, Rule> rules;
    private Options options;
    private Renderer renderer;
    private boolean inLink = false;

    public InlineLexer(Map<String, Rule> rules, Options options, Renderer renderer){
        this.rules = rules;
        this.options = options;
        this.renderer = renderer;
    }

    public String output(String src){
        StringBuilder out = new StringBuilder();
        while(src.length() > 0){
            //escape
            {
                List<String> cap = rules.get("escape").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    out.append(cap.get(1));
                    continue;
                }
            }

            // TODO autolink

            // url (gfm)
            if(!inLink){
                List<String> cap = rules.get("url").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    String text = escape(cap.get(1));
                    String href = text;
                    out.append(renderer.link(text, Optional.empty(), href));
                    continue;
                }
            }

            // tag
            {
                List<String> cap = rules.get("tag").exec(src);
                if(!cap.isEmpty()){
                    if(!inLink && cap.get(0).matches("")){ // TODO
                        inLink = true;
                    } else if(inLink && cap.get(0).matches("")){ // TODO
                        inLink = false;
                    }

                    src = src.substring(cap.get(0).length());
                    if(options.isSanitize()){
                        out.append(escape(cap.get(0)));
                    } else {
                        out.append(cap.get(0));
                    }
                    continue;
                }
            }

            // link
            {
                List<String> cap = rules.get("link").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    inLink = true;
                    out.append(outputLink(cap, new Lexer.Link(cap.get(2), Optional.of(cap.get(3)))));
                    inLink = false;
                    continue;
                }
            }

            // TODO reflink, nolink

            // strong
            {
                List<String> cap = rules.get("strong").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    out.append(renderer.strong(or(cap.get(2), cap.get(1))));
                    continue;
                }
            }

            // em
            {
                List<String> cap = rules.get("em").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    out.append(renderer.em(or(cap.get(2), cap.get(1))));
                    continue;
                }
            }

            // code
            {
                List<String> cap = rules.get("code").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    out.append(renderer.codespan(cap.get(2)));
                    continue;
                }
            }

            // br
            {
                List<String> cap = rules.get("br").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    out.append(renderer.br());
                    continue;
                }
            }

            // del (gfm)
            {
                List<String> cap = rules.get("del").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    out.append(renderer.del(cap.get(1)));
                    continue;
                }
            }

            // text
            {
                List<String> cap = rules.get("text").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    out.append(renderer.text(escape(smartypants(cap.get(0)))));
                    continue;
                }
            }

            // TODO Error
            //println("Infinite loop on byte: " + source.charAt(0).toByte)
        }
        return out.toString();
    }

    protected String outputLink(List<String> cap, Lexer.Link link){
        String href = escape(link.getHref());
        if(cap.get(0).charAt(0) != '!'){
            return renderer.link(href, link.getTitle(), output(cap.get(1)));
        } else {
            return renderer.image(href, link.getTitle(), escape(cap.get(1)));
        }
    }

    protected String smartypants(String text){ // TODO
        return text;
    }

}
