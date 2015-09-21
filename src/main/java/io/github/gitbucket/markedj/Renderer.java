package io.github.gitbucket.markedj;

import java.util.Optional;
import static io.github.gitbucket.markedj.Utils.*;

public class Renderer {

    protected Options options;

    public Renderer(Options options){
        this.options = options;
    }

    public String code(String code, Optional<String> lang, boolean escaped){
        return lang.map(l -> {
            StringBuilder sb = new StringBuilder();
            sb.append("<pre><code class=\"" + options.getLangPrefix() + escape(l, true) + "\">");
            if(escaped){
                sb.append(code);
            } else {
                sb.append(escape(code, true));
            }
            sb.append("\n</code></pre>\n");
            return sb.toString();
        }).orElseGet(() -> {
            StringBuilder sb = new StringBuilder();
            sb.append("<pre><code>");
            if(escaped){
                sb.append(code);
            } else {
                sb.append(escape(code, true));
            }
            sb.append("\n</code></pre>\n");
            return sb.toString();
        });
    }

    public String blockquote(String quote){
        return "<blockquote>\n" + quote + "</blockquote>\n";
    }

    public String html(String html){
        return html;
    }

    public String heading(String text, int level, String raw){
        return "<h" + level + " id=\"" + options.getHeaderPrefix() +
                raw.toLowerCase().replaceAll("[^\\w]+", "-") + "\">" + text + "</h" + level + ">\n";
    }

    public String hr() {
        if (options.isXhtml()){
            return "<hr/>\n";
        } else {
            return "<hr>\n";
        }
    }

    public String list(String body, boolean ordered){
        String listType;
        if(ordered){
            listType = "ol";
        } else {
            listType = "ul";
        }
        return "<" + listType + ">\n" + body + "</" + listType + ">\n";
    }

    public String listitem(String text){
        return "<li>" + text + "</li>\n";
    }

    public String paragraph(String text){
        return "<p>" + text + "</p>\n";
    }

    public String table(String header, String body){
        return "<table>\n<thead>\n" + header + "</thead>\n<tbody>\n" + body + "</tbody>\n</table>\n";
    }

    public String tablerow(String content){
        return "<tr>\n" + content + "</tr>\n";
    }

    public String tablecell(String content, TableCellFlags flags){
        String cellType;
        if(flags.isHeader()){
            cellType = "th";
        } else {
            cellType = "td";
        }
        String tag = flags.getAlign()
                .map(align    -> "<" + cellType + " style=\"text-align: " + align + "\">")
                .orElseGet(() -> "<" + cellType + ">");

        return tag + content + "</" + cellType + ">\n";
    }

    public String strong(String text){
        return "<strong>" + text + "</strong>";
    }

    public String em(String text){
        return "<em>" + text + "</em>";
    }

    public String codespan(String text){
        return "<code>" + text + "</code>";
    }

    public String br(){
        if(options.isXhtml()){
            return "<br/>";
        } else {
            return "<br>";
        }
    }

    public String del(String text){
        return "<del>" + text + "</del>";
    }

    public String link(String href, Optional<String> title, String text){
        if(options.isSanitize()){
            // TODO
        }
        String titleAttr = title.map(t -> " title=\"" + t + "\"").orElseGet(() -> "");
        return "<a href=\"" + href + "\"" + titleAttr + ">" + text + "</a>";
    }

    public String image(String href, Optional<String> title, String text){
        String titleAttr = title.map(t -> " title=\"" + t + "\"").orElseGet(() -> "");
        if(options.isXhtml()){
            return "<img src=\"" + href + "\" alt=\"" + text + "\"" + titleAttr + "/>";
        } else {
            return "<img src=\"" + href + "\" alt=\"" + text + "\"" + titleAttr + ">";
        }
    }

    public String nolink(String text){
        return escape(text);
    }

    public String text(String text){
        return text;
    }

    public static class TableCellFlags {
        private boolean header;
        private Optional<String> align;

        public TableCellFlags(boolean header, Optional<String> align){
            this.header = header;
            this.align = align;
        }

        public boolean isHeader() {
            return header;
        }

        public Optional<String> getAlign() {
            return align;
        }
    }

}
