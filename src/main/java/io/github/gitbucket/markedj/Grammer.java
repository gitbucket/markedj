package io.github.gitbucket.markedj;

import io.github.gitbucket.markedj.rule.FindFirstRule;
import io.github.gitbucket.markedj.rule.FindAllRule;
import io.github.gitbucket.markedj.rule.NoopRule;
import io.github.gitbucket.markedj.rule.Rule;

import java.util.HashMap;
import java.util.Map;

public class Grammer {

    public static String BULLET            = "(?:[*+-]|\\d+\\.)";
    public static String COMMENT           = "<!--[\\s\\S]*?-->";
    public static String TAG               = "(?!(?:a|em|strong|small|s|cite|q|dfn|abbr|data|time|code|var|samp|kbd|sub|sup|i|b|u|mark|ruby|rt|rp|bdi|bdo|span|br|wbr|ins|del|img)\\b)\\w+(?!:/|[^\\w\\s@]*@)\\b";
    public static String CLOSED            = "<(" + TAG + ")[\\s\\S]+?<\\/\\1>";
    public static String CLOSING           = "<" + TAG + "(?:\"[^\"]*\"|'[^']*'|[^'\">])*?>";
    public static String HR                = "\\n+(?=\\1?(?:[-*_] *){3,}(?:\\n+|$))";
    public static String DEF               = "^ *\\[([^\\]]+)\\]: *<?([^\\s>]+)>?(?: +[\"(]([^\\n]+)[\")])? *(?:\\n+|$)";
    public static String BLOCK_HR          = "^( *[-*_]){3,} *(?:\\n+|$)";
    public static String BLOCK_HEADING     = "^ *(#{1,6}) *([^\\n]+?) *#* *(?:\\n+|$)";
    public static String BLOCK_LHEADING    = "^([^\\n]+)\\n *(=|-){2,} *(?:\\n+|$)";
    public static String BLOCK_BLOCKQUOTE  = "^( *>[^\\n]+(\\n(?!" + removeLineStart(DEF) + ")[^\\n]+)*\\n*)+";
    public static String BLOCK_NOTIFICATION  = "^( *(\\![vx!]?)[^\\n]+(\\n(?!" + removeLineStart(DEF) + ")[^\\n]+)*\\n*)+";
    //public static String BLOCK_LIST        = "^( *)(" + BULLET + ") [\\s\\S]+?(?:" + HR + "|\\n+(?=" + removeLineStart(DEF) + ")|\\n{2,}(?! )(?!\\1" + BULLET + " )\\n*|\\s*$)";
    public static String BLOCK_LIST        = "^( *)(" + BULLET + ") [\\s\\S]+?(?:\\n{2,}(?! )(?!\\1" + BULLET + " )\\n*|\\s*$)";
    public static String BLOCK_DEF         = "^ *\\[([^\\]]+)\\]: *<?([^\\s>]+)>?(?: +[\"(]([^\\n]+)[\")])? *(?:\\n+|$)";
    public static String BLOCK_PARAGRAPH   = "^((?:[^\\n]+\\n?(?!" + removeLineStart(BLOCK_HR) + "|" + removeLineStart(BLOCK_HEADING) + "|" + removeLineStart(BLOCK_LHEADING) + "|" + removeLineStart(BLOCK_BLOCKQUOTE) + "|<" + TAG + "|" + removeLineStart(BLOCK_DEF) + "))+)\\n*";
    public static String BLOCK_GFM_FENCES  = "^ *(`{3,}|~{3,})[ \\.]*(\\S+)? *\\n([\\s\\S]*?)\\s*\\1 *(?:\\n+|$)";

    public static Map<String, Rule> BLOCK_RULES = new HashMap<>();
    public static Map<String, Rule> BLOCK_GFM_RULES = new HashMap<>();
    public static Map<String, Rule> BLOCK_TABLE_RULES = new HashMap<>();

    static {
        BLOCK_RULES.put("newline", new FindFirstRule("^\n+"));
        BLOCK_RULES.put("code", new FindFirstRule("^( {4}[^\n]+\n*)+"));
        BLOCK_RULES.put("fences", new NoopRule());
        BLOCK_RULES.put("hr", new FindFirstRule(BLOCK_HR));
        BLOCK_RULES.put("heading", new FindFirstRule(BLOCK_HEADING));
        BLOCK_RULES.put("nptable", new NoopRule());
        BLOCK_RULES.put("lheading", new FindFirstRule(BLOCK_LHEADING));
        BLOCK_RULES.put("blockquote", new FindFirstRule(BLOCK_BLOCKQUOTE));
        BLOCK_RULES.put("notification", new FindFirstRule(BLOCK_NOTIFICATION));
        BLOCK_RULES.put("list", new FindFirstRule(BLOCK_LIST));
        BLOCK_RULES.put("html", new FindFirstRule("^ *(?:" + COMMENT + " *(?:\\n|\\s*$)|" + CLOSED + " *(?:\\n{2,}|\\s*$)|" + CLOSING + " *(?:\\n{2,}|\\s*$))"));
        BLOCK_RULES.put("def", new FindFirstRule(BLOCK_DEF));
        BLOCK_RULES.put("table", new NoopRule());
        BLOCK_RULES.put("paragraph", new FindFirstRule(BLOCK_PARAGRAPH));
        BLOCK_RULES.put("text", new FindFirstRule("^[^\n]+"));
        BLOCK_RULES.put("item", new FindAllRule(("(?m)^( *)(" + BULLET + ") [^\\n]*(?:\\n(?!\\1" + BULLET + " )[^\\n]*)*")));

        BLOCK_GFM_RULES.putAll(BLOCK_RULES);
        BLOCK_GFM_RULES.put("fences", new FindFirstRule(BLOCK_GFM_FENCES));
        BLOCK_GFM_RULES.put("paragraph", new FindFirstRule(BLOCK_PARAGRAPH.replace("(?!", "(?!" + removeLineStart(BLOCK_GFM_FENCES).replace("\\1", "\\2") + "|" + removeLineStart(BLOCK_LIST).replace("\\1", "\\3") + "|")));
        BLOCK_GFM_RULES.put("heading", new FindFirstRule("^ *(#{1,6}) +([^\\n]+?) *#* *(?:\\n+|$)"));
// TODO
//  block.gfm.paragraph = replace(block.paragraph)
//    ('(?!', '(?!'
//      + block.gfm.fences.source.replace('\\1', '\\2') + '|'
//      + block.list.source.replace('\\1', '\\3') + '|')
//    ();

        BLOCK_TABLE_RULES.putAll(BLOCK_GFM_RULES);
        BLOCK_TABLE_RULES.put("nptable", new FindFirstRule("^ *(\\S.*\\|.*)\\n *([-:]+ *\\|[-| :]*)\\n((?:.*\\|.*(?:\\n|$))*)\\n*"));
        BLOCK_TABLE_RULES.put("table", new FindFirstRule("^ *\\|(.+)\\n *\\|( *[-:]+[-| :]*)\\n((?: *\\|.*(?:\\n|$))*)\\n*"));
    }

    private static String removeLineStart(String regex){
        return regex.replaceAll("(^|[^\\[])\\^", "$1");
    }

    public static String INSIDE = "(?:\\[[^\\]]*\\]|[^\\[\\]]|\\](?=[^\\[]*\\]))*";
    public static String HREF   = "\\s*<?([\\s\\S]*?)>?(?:\\s+['\"]([\\s\\S]*?)['\"])?\\s*";

    public static Map<String, Rule> INLINE_RULES = new HashMap<>();
    public static Map<String, Rule> INLINE_GFM_RULES = new HashMap<>();
    public static Map<String, Rule> INLINE_BREAKS_RULES = new HashMap<>();

    public static String INLINE_ESCAPE = "^\\\\([\\\\`*{}\\[\\]()#+\\-.!_>])";
    public static String INLINE_TEXT   = "^[\\s\\S]+?(?=[\\\\<!\\[_*`]| {2,}\\n|$)";
    public static String INLINE_BR     = "^ {2,}\\n(?!\\s*$)";

    static {
        INLINE_RULES.put("escape", new FindFirstRule(INLINE_ESCAPE));
        INLINE_RULES.put("autolink", new FindFirstRule("^<([^ >]+(@|:\\/)[^ >]+)>"));
        INLINE_RULES.put("url", new NoopRule());
        INLINE_RULES.put("tag", new FindFirstRule("^<!--[\\s\\S]*?-->|^<\\/?\\w+(?:\"[^\"]*\"|'[^']*'|[^'\">])*?>"));
        INLINE_RULES.put("link", new FindFirstRule(("^!?\\[(" + INSIDE + ")\\]\\(" + HREF + "\\)")));
        INLINE_RULES.put("reflink", new FindFirstRule(("^!?\\[(" + INSIDE + ")\\]\\s*\\[([^\\]]*)\\]")));
        INLINE_RULES.put("nolink", new FindFirstRule("^!?\\[((?:\\[[^\\]]*\\]|[^\\[\\]])*)\\]"));
        INLINE_RULES.put("strong", new FindFirstRule("^__([\\s\\S]+?)__(?!_)|^\\*\\*([\\s\\S]+?)\\*\\*(?!\\*)"));
        INLINE_RULES.put("em", new FindFirstRule("^\\b_((?:[^_]|__)++)_\\b|^\\*((?:\\*\\*|[\\s\\S])+?)\\*(?!\\*)"));
        INLINE_RULES.put("code", new FindFirstRule("^(`+)\\s*([\\s\\S]*?[^`])\\s*\\1(?!`)"));
        INLINE_RULES.put("br", new FindFirstRule(INLINE_BR));
        INLINE_RULES.put("del", new NoopRule());
        INLINE_RULES.put("text", new FindFirstRule(INLINE_TEXT));

        INLINE_GFM_RULES.putAll(INLINE_RULES);
        INLINE_GFM_RULES.put("escape", new FindFirstRule(INLINE_ESCAPE.replace("])", "~|])")));
        INLINE_GFM_RULES.put("url", new FindFirstRule("^(https?:\\/\\/[^\\s<]+[^<.,:;\"')\\]\\s])"));
        INLINE_GFM_RULES.put("del", new FindFirstRule("^~~(?=\\S)([\\s\\S]*?\\S)~~"));
        INLINE_GFM_RULES.put("text", new FindFirstRule(INLINE_TEXT.replace("]|", "~]|").replace("|", "|https?://|")));

        INLINE_BREAKS_RULES.putAll(INLINE_GFM_RULES);
        INLINE_BREAKS_RULES.put("br", new FindFirstRule(INLINE_BR.replace("{2,}", "*")));
        INLINE_BREAKS_RULES.put("text", new FindFirstRule(INLINE_TEXT.replace("]|", "~]|").replace("|", "|https?://|").replace("{2,}", "*")));
    }

    public static void main(String[] args) {
        System.out.println(removeLineStart(DEF));
        System.out.println(BLOCK_BLOCKQUOTE);
    }
}
