package io.github.gitbucket.markedj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.gitbucket.markedj.rule.Rule;
import io.github.gitbucket.markedj.token.BlockquoteEndToken;
import io.github.gitbucket.markedj.token.BlockquoteStartToken;
import io.github.gitbucket.markedj.token.CodeToken;
import io.github.gitbucket.markedj.token.HeadingToken;
import io.github.gitbucket.markedj.token.HrToken;
import io.github.gitbucket.markedj.token.HtmlToken;
import io.github.gitbucket.markedj.token.ListEndToken;
import io.github.gitbucket.markedj.token.ListItemEndToken;
import io.github.gitbucket.markedj.token.ListItemStartToken;
import io.github.gitbucket.markedj.token.ListStartToken;
import io.github.gitbucket.markedj.token.LooseItemStartToken;
import io.github.gitbucket.markedj.token.NotificationEndToken;
import io.github.gitbucket.markedj.token.NotificationStartToken;
import io.github.gitbucket.markedj.token.ParagraphToken;
import io.github.gitbucket.markedj.token.SpaceToken;
import io.github.gitbucket.markedj.token.TableToken;
import io.github.gitbucket.markedj.token.TextToken;
import io.github.gitbucket.markedj.token.Token;

public class Lexer {

    protected Options options;
    protected Map<String, Rule> rules = null;

    public Lexer(Options options){
        this.options = options;
        if(!options.isGfm()){
            this.rules = Grammer.BLOCK_RULES;
        } else if(options.isTables()){
            this.rules = Grammer.BLOCK_TABLE_RULES;
        } else {
            this.rules = Grammer.BLOCK_GFM_RULES;
        }
        
        if (options.useNotifications()) {
            this.rules = Grammer.enhanceRulesWithNotifications(this.rules);
        }
    }

    public LexerResult lex(String src){
        LexerContext context = new LexerContext();

        token(src
            .replace("\r\n", "\n")
            .replace("\r", "\n")
            .replace("\t", "    ")
            .replace("\u00a0", " ")
            .replace("\u2424", "\n"),
             true, false, context);

        return new LexerResult(context.getTokens(), context.getLinks());
    }

    protected void token(String src, boolean top, boolean bq, LexerContext context){
        while(src.length() > 0){
            // newline
            {
                List<String> cap = rules.get("newline").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    if(cap.get(0).length() > 1){
                        context.pushToken(new SpaceToken());
                    }
                }
            }

            // code
            {
                List<String> cap = rules.get("code").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    String code = cap.get(0).replaceAll("(?m)^ {4}", "");
                    if(!options.isPedantic()){
                        context.pushToken(new CodeToken(code.replaceAll("\\n+$", ""), null, false));
                    } else {
                        context.pushToken(new CodeToken(code, null, false));
                    }
                    continue;
                }
            }

            // fences (gfm)
            {
                List<String> cap = rules.get("fences").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    context.pushToken(new CodeToken(cap.get(3), cap.get(2), false));
                    continue;
                }
            }

            // heading
            {
                List<String> cap = rules.get("heading").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    context.pushToken(new HeadingToken(cap.get(1).length(), cap.get(2)));
                    continue;
                }
            }

            // table no leading pipe (gfm)
            if(top){
                List<String> cap = rules.get("nptable").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());

                    String[] header = cap.get(1).replaceAll("^ *| *\\| *$", "").split(" *\\| *");
                    String[] align  = cap.get(2).replaceAll("^ *|\\| *$", "").split(" *\\| *");
                    String[] cells  = cap.get(3).replaceAll("\n$", "").split("\n");

                    List<String> header2 = Arrays.asList(header);

                    List<String> align2 = new ArrayList<>();
                    for (String s : align) {
                        if(s.matches("^ *-+: *$")){
                            align2.add("right");
                        } else if(s.matches("^ *:-+: *$")){
                            align2.add("center");
                        } else if(s.matches("^ *:-+ *$")){
                            align2.add("left");
                        } else {
                            align2.add(null);
                        }
                    }

                    List<List<String>> cells2 = new ArrayList<>();
                    for (String cell : cells) {
                        cells2.add(Arrays.asList(cell.split(" *\\| *")));
                    }

                    context.pushToken(new TableToken(header2, align2, cells2));
                    continue;
                }
            }

            // lheading
            {
                List<String> cap = rules.get("lheading").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    if(cap.get(2).equals("=")){
                        context.pushToken(new HeadingToken(1, cap.get(1)));
                    } else {
                        context.pushToken(new HeadingToken(2, cap.get(1)));
                    }
                    continue;
                }
            }

            // hr
            {
                List<String> cap = rules.get("hr").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    context.pushToken(new HrToken());
                    continue;
                }
            }

            // blockquote
            {
                List<String> cap = rules.get("blockquote").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    context.pushToken(new BlockquoteStartToken());
                    token(cap.get(0).replaceAll("(?m) *> ?", ""), top, true, context);
                    context.pushToken(new BlockquoteEndToken());
                    continue;
                }
            }

            // notification info
            if (options.useNotifications()) {
                List<String> cap = rules.get("notification").exec(src);
                if(!cap.isEmpty()) {
                    // we have detected several contiguous lines of notifications
                    // ensure that all are of same kind
                    String allNotificationsLines = cap.get(0);
                    
                    // if other kind of notifications lines are detected
                    // let's split them so that they are handled separately
                    String findOtherLinesPattern = "(?m)^(!" + Notifications.exceptGivenNotificationClass(cap.get(3)) + " .*)";
                    Matcher otherLinesMatcher = Pattern.compile(findOtherLinesPattern).matcher(cap.get(1));
                    
                    if (otherLinesMatcher.find()) {
                        String otherLinesSeparated = otherLinesMatcher.replaceAll("\n$1\n");
                        
                        // change the source to parse
                        // replace all the notifications lines with separated notifications lines
                        // and reparse the string
                        src = otherLinesSeparated + src.substring(allNotificationsLines.length());
                    } else {
                        src = src.substring(allNotificationsLines.length());
                        context.pushToken(new NotificationStartToken(cap.get(3)));
                        token(allNotificationsLines.replaceAll("(?m)^" + cap.get(2) + "[ ]?", ""), false, false, context);
                        context.pushToken(new NotificationEndToken());
                    }
                    
                    continue;
                }
            }

            // list
            {
                List<String> cap = rules.get("list").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    String bull = cap.get(2);

                    context.pushToken(new ListStartToken(bull.matches("^[0-9]+\\.$")));
                    boolean next = false;

                    // Get each top-level item.
                    cap = rules.get("item").exec(cap.get(0));
                    if(!cap.isEmpty()){
                        for(int i = 0; i < cap.size(); i++){
                            String item = cap.get(i);

                            // Remove the list item's bullet
                            // so it is seen as the nextToken token.
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

//                            // Determine whether the nextToken list item belongs here.
//                            // Backpedal if it does not belong in this list.
//                            if(options.isSmartLists() && i != cap.size() - 1){
//                                Pattern p = Pattern.compile(Grammer.BULLET);
//                                if(p.matcher(cap.get(i + 1)).find()){
//                                    src = String.join("\n", cap.subList(i + 1, cap.size())) + src;
//                                    i = i - 1;
//                                }
//                            }

                            // Determine whether item is loose or not.
                            // Use: /(^|\n)(?! )[^\n]+\n\n(?!\s*$)/
                            // for discount behavior.
                            boolean loose = next || item.matches("\\n\\n(?!\\s*$)");
                            if(i != cap.size() - 1){
                                next = !item.isEmpty() && item.charAt(item.length() - 1) == '\n';
                                if(!loose) {
                                    loose = next;
                                }
                            }

                            if(loose){
                                context.pushToken(new LooseItemStartToken());
                            } else {
                                context.pushToken(new ListItemStartToken());
                            }

                            token(item, false, bq, context);
                            context.pushToken(new ListItemEndToken());
                        }
                    }
                    context.pushToken(new ListEndToken());
                    continue;
                }
            }

            // html
            {
                List<String> cap = rules.get("html").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    if(options.isSanitize()){
                        context.pushToken(new ParagraphToken(cap.get(0)));
                    } else {
                        context.pushToken(new HtmlToken(cap.get(0),
                                !options.isSanitize() && (cap.get(0).equals("pre") || cap.get(0).equals("script") || cap.get(0).equals("style"))));
                    }
                    continue;
                }
            }

            // def
            if(!bq && top){
                List<String> cap = rules.get("def").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    context.defineLink(cap.get(1).toLowerCase(), new Link(cap.get(2), cap.get(3)));
                    continue;
                }
            }

            // table (gfm)
            if(top){
                List<String> cap = rules.get("table").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());

                    String[] header = cap.get(1).replaceAll("^ *| *\\| *$", "").split(" *\\| *");
                    String[] align  = cap.get(2).replaceAll("^ *|\\| *$", "").split(" *\\| *");
                    String[] cells  = cap.get(3).replaceAll("(?: *\\| *)?\\n$", "").split("\\n");

                    List<String> header2 = Arrays.asList(header);

                    List<String> align2 = new ArrayList<>();
                    for (String s : align) {
                        if(s.matches("^ *-+: *$")){
                            align2.add("right");
                        } else if(s.matches("^ *:-+: *$")){
                            align2.add("center");
                        } else if(s.matches("^ *:-+ *$")){
                            align2.add("left");
                        } else {
                            align2.add(null);
                        }
                    }

                    List<List<String>> cells2 = new ArrayList<>();
                    for (String cell : cells) {
                        cells2.add(Arrays.asList(cell.replaceAll("^ *\\| *| *\\| *$", "").split(" *\\| *")));
                    }

                    context.pushToken(new TableToken(header2, align2, cells2));
                    continue;
                }
            }

            // top-level paragraph
            if(top){
                List<String> cap = rules.get("paragraph").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    if(cap.get(1).charAt(cap.get(1).length() - 1) == '\n'){
                        context.pushToken(new ParagraphToken(cap.get(1).substring(0, cap.get(1).length() - 1)));
                    } else {
                        context.pushToken(new ParagraphToken(cap.get(1)));
                    }
                    continue;
                }
            }

            // text
            {
                List<String> cap = rules.get("text").exec(src);
                if(!cap.isEmpty()){
                    src = src.substring(cap.get(0).length());
                    context.pushToken(new TextToken((cap.get(0))));
                    continue;
                }
            }

            // TODO Error
            //println("Infinite loop on byte: " + source.charAt(0).toByte)
        }
    }

    public static class LexerContext {
        private Stack<Token> tokens = new Stack<>();
        private Map<String, Link> links = new HashMap<>();

        public void pushToken(Token token){
            this.tokens.push(token);
        }

        public void defineLink(String key, Link link){
            this.links.put(key, link);
        }

        public Stack<Token> getTokens() {
            return tokens;
        }

        public Map<String, Link> getLinks() {
            return links;
        }
    }

    public static class LexerResult {
        private Stack<Token> tokens;
        private Map<String, Link> links = new HashMap<>();

        public LexerResult(Stack<Token> tokens, Map<String, Link> links){
            this.tokens = tokens;
            this.links = links;
        }

        public Stack<Token> getTokens() {
            return tokens;
        }

        public Map<String, Link> getLinks() {
            return links;
        }
    }


    public static class Link {
        private String href;
        private String title;

        public Link(String href, String title){
            this.href = href;
            this.title = title;
        }

        public String getHref() {
            return href;
        }

        public String getTitle() {
            return title;
        }
    }

}
