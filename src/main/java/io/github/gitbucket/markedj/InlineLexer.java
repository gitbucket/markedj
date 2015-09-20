package io.github.gitbucket.markedj;

import io.github.gitbucket.markedj.rule.Rule;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static io.github.gitbucket.markedj.Utils.*;

public class InlineLexer {

    protected Map<String, Rule> rules;
    protected Options options;
    protected Renderer renderer;
    protected boolean inLink = false;
    protected Map<String, Lexer.Link> links;

    public InlineLexer(Map<String, Rule> rules, Map<String, Lexer.Link> links, Options options, Renderer renderer){
        this.rules = rules;
        this.links = links;
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

            // autolink
            {
                List<String> cap = rules.get("autolink").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    String text;
                    String href;
                    if(cap.get(2).equals("@")){
                        if(cap.get(1).startsWith("mailto:")){
                            text = cap.get(1).substring(7);
                        } else {
                            text = cap.get(1);
                        }
                        href = "mailto:" + text;
                    } else {
                        text = escape(cap.get(1));
                        href = text;
                    }
                    out.append(renderer.link(href, Optional.empty(), text));
                    continue;
                }
            }

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
                    if(!inLink && Pattern.compile("^<a ").matcher(cap.get(0)).find()){
                        inLink = true;
                    } else if(inLink && Pattern.compile("^</a>").matcher(cap.get(0)).find()){
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
                    out.append(outputLink(cap, new Lexer.Link(cap.get(2), Optional.ofNullable(cap.get(3)))));
                    inLink = false;
                    continue;
                }
            }

            // reflink, nolink
            {
                List<String> cap = rules.get("reflink").exec(src);
                if(cap.isEmpty()){
                    cap = rules.get("nolink").exec(src);
                }
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    String key;
                    if(cap.size() > 2){
                        key = cap.get(2).replaceAll("\\s+", "");
                    } else {
                        key = cap.get(1).replaceAll("\\s+", "");
                    }
                    Lexer.Link link = links.get(key);
                    if(link == null || isEmpty(link.getHref())){
                        out.append(renderer.nolink(cap.get(0)));
                        continue;
                    }
                    inLink = true;
                    out.append(outputLink(cap, link));
                    inLink = false;
                    continue;
                }
            }

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
                    // TODO smartypants
                    //out.append(renderer.text(escape(smartypants(cap.get(0)))));
                    out.append(renderer.text(escape(cap.get(0))));
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

//    protected String smartypants(String text){ // TODO
//        return text;
//    }

}
