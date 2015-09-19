package io.github.gitbucket.markedj;

import io.github.gitbucket.markedj.rule.Rule;
import io.github.gitbucket.markedj.token.*;

import java.util.*;
import java.util.regex.Pattern;

import static io.github.gitbucket.markedj.Utils.*;

public class Lexer {

    private Options options;
    private Stack<Token> tokens = new Stack<>();
    private List<Link> links = new ArrayList<>();
    private Map<String, Rule> rules = null;

    public Lexer(Options options){
        this.options = options;
        if(!options.isGfm()){
            this.rules = Grammer.BLOCK_RULES;
        } else if(options.isTables()){
            this.rules = Grammer.BLOCK_TABLE_RULES;
        } else {
            this.rules = Grammer.BLOCK_GFM_RULES;
        }
    }

    public Stack<Token> lex(String src){
        token(src
            .replace("\r\n", "\n")
            .replace("\r", "\n")
            .replace("\t", "    ")
            .replace("\u00a0", " ")
            .replace("\u2424", "\n"),
             true, false);

        return tokens;
    }

    protected void token(String src, boolean top, boolean bq){
        while(src.length() > 0){
            // newline
            {
                List<String> cap = rules.get("newline").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    if(cap.get(0).length() > 1){
                        tokens.push(new SpaceToken());
                    }
                    // TODO continue??
                }
            }

            // code
            {
                List<String> cap = rules.get("code").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    String code = cap.get(0).replaceAll("(?m)^ {4}", "");
                    if(!options.isPedantic()){
                        tokens.push(new CodeToken(code.replaceAll("\\n+$", ""), Optional.empty(), false));
                    } else {
                        tokens.push(new CodeToken(code, Optional.empty(), false));
                    }
                    continue;
                }
            }

            // fences (gfm)
            {
                List<String> cap = rules.get("fences").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    tokens.push(new CodeToken(cap.get(3), Optional.of(cap.get(2)), false));
                    continue;
                }
            }

            // heading
            {
                List<String> cap = rules.get("heading").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    tokens.push(new HeadingToken(cap.get(1).length(), cap.get(2)));
                    continue;
                }
            }

            // table no leading pipe (gfm)
            if(top){
                List<String> cap = rules.get("nptable").exec(src);
                src = src.substring(cap.get(0).length());

                String[] header = cap.get(1).replaceAll("^ *| *\\| *$", "").split(" *\\| *");
                String[] align  = cap.get(2).replaceAll("^ *|\\| *$", "").split(" *\\| *");
                String[] cells  = cap.get(3).replaceAll("\\n$", "").split(" \\n");

                List<String> header2 = Arrays.asList(header);

                List<Optional<String>> align2 = new ArrayList<>();
                for (String s : align) {
                    if(s.matches("^ *-+: *$")){
                        align2.add(Optional.of("right"));
                    } else if(s.matches("^ *:-+: *$")){
                        align2.add(Optional.of("center"));
                    } else if(s.matches("^ *:-+ *$")){
                        align2.add(Optional.of("left"));
                    } else {
                        align2.add(Optional.empty());
                    }
                }

                List<List<String>> cells2 = new ArrayList<>();
                for (String cell : cells) {
                    cells2.add(Arrays.asList(cell.split(" *\\| *")));
                }

                tokens.push(new TableToken(header2, align2, cells2));
                continue;
            }

            // lheading
            {
                List<String> cap = rules.get("lheading").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    if(cap.get(2).equals("=")){
                        tokens.push(new HeadingToken(1, cap.get(1)));
                    } else {
                        tokens.push(new HeadingToken(2, cap.get(1)));
                    }
                    continue;
                }
            }

            // hr
            {
                List<String> cap = rules.get("he").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    tokens.push(new HrToken());
                    continue;
                }
            }

            // blockquote
            {
                List<String> cap = rules.get("blockquote").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    tokens.push(new BlockquoteStartToken());
                    token(cap.get(0).replaceAll("(?m) *> ?", ""), top, true);
                    tokens.push(new BlockquoteEndToken());
                    continue;
                }
            }

            // list
            {
                List<String> cap = rules.get("list").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    String bull = cap.get(2);

                    tokens.push(new ListStartToken(isNumber(bull)));
                    boolean next = false;

                    cap = rules.get("item").exec(src);
                    if(!cap.isEmpty()){
                        for(int i = 0; i < cap.size();){
                            String item = cap.get(i);

                            // Remove the list item's bullet
                            // so it is seen as the next token.
                            int space = item.length();
                            item = item.replaceAll("^ *([*+-]|\\d+\\.) +", "");

                            // Outdent whatever the
                            // list item contains. Hacky.
                            if(item.indexOf("\n ") > 0){
                                space = space - item.length();
                                if(!options.isPedantic()){
                                    item = item.replaceAll("(?m)^ {1," + space + "}", "");
                                } else {
                                    item = item.replaceAll("(?m)^ {1,4}", "");
                                }
                            }

                            // Determine whether the next list item belongs here.
                            // Backpedal if it does not belong in this list.
                            if(options.isSmartLists() && i != cap.size() - 1){
                                Pattern p = Pattern.compile(Grammer.BULLET);
                                if(p.matcher(cap.get(i + 1)).find()){
                                    src = String.join("\n", cap.subList(i + 1, cap.size())) + src;
                                    i = i - 1;
                                }
                            }

                            // Determine whether item is loose or not.
                            // Use: /(^|\n)(?! )[^\n]+\n\n(?!\s*$)/
                            // for discount behavior.
                            boolean loose = next || item.matches("\\n\\n(?!\\s*$)");
                            if(i != cap.size() - 1){
                                next = item.charAt(item.length() - 1) == '\n';
                                if(!loose) {
                                    loose = next;
                                }
                            }

                            if(loose){
                                tokens.push(new LooseItemStartToken());
                            } else {
                                tokens.push(new ListItemStartToken());
                            }

                            token(item, false, bq);
                            tokens.push(new ListItemEndToken());
                        }
                    }
                    tokens.push(new ListEndToken());
                    continue;
                }
            }

            // html
            {
                List<String> cap = rules.get("html").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    if(options.isSanitize()){
                        tokens.push(new ParagraphToken(cap.get(0)));
                    } else {
                        tokens.push(new HtmlToken(cap.get(0),
                                !options.isSanitize() && (cap.get(0).equals("pre") || cap.get(0).equals("script") || cap.get(0).equals("style"))));
                    }
                    continue;
                }
            }

            // def
            if(top){
                List<String> cap = rules.get("def").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    links.add(new Link(cap.get(2), Optional.of(cap.get(3)))); // TODO Stack??
                    continue;
                }
            }

            // table (gfm)
            if(top){
                List<String> cap = rules.get("table").exec(src);
                src = src.substring(cap.get(0).length());

                String[] header = cap.get(1).replaceAll("^ *| *\\| *$", "").split(" *\\| *");
                String[] align  = cap.get(2).replaceAll("^ *|\\| *$", "").split(" *\\| *");
                String[] cells  = cap.get(3).replaceAll("(?: *\\| *)?\\n$", "").split(" \\n");

                List<String> header2 = Arrays.asList(header);

                List<Optional<String>> align2 = new ArrayList<>();
                for (String s : align) {
                    if(s.matches("^ *-+: *$")){
                        align2.add(Optional.of("right"));
                    } else if(s.matches("^ *:-+: *$")){
                        align2.add(Optional.of("center"));
                    } else if(s.matches("^ *:-+ *$")){
                        align2.add(Optional.of("left"));
                    } else {
                        align2.add(Optional.empty());
                    }
                }

                List<List<String>> cells2 = new ArrayList<>();
                for (String cell : cells) {
                    cells2.add(Arrays.asList(cell.replaceAll("^ *\\| *| *\\| *$", "").split(" *\\| *")));
                }

                tokens.push(new TableToken(header2, align2, cells2));
                continue;
            }

            // top-level paragraph
            if(top){
                List<String> cap = rules.get("paragraph").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    if(cap.get(1).charAt(cap.get(1).length() - 1) == '\n'){
                        tokens.push(new ParagraphToken(cap.get(1).substring(0, cap.get(1).length() - 1)));
                    } else {
                        tokens.push(new ParagraphToken(cap.get(1)));
                    }
                    continue;
                }
            }

            // text
            {
                List<String> cap = rules.get("text").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    tokens.push(new TextToken((cap.get(0))));
                    continue;
                }
            }

            // TODO Error
            //println("Infinite loop on byte: " + source.charAt(0).toByte)
        }
    }

    public static class Link {
        private String href;
        private Optional<String> title;

        public Link(String href, Optional<String> title){
            this.href = href;
            this.title = title;
        }

        public String getHref() {
            return href;
        }

        public Optional<String> getTitle() {
            return title;
        }
    }

}
