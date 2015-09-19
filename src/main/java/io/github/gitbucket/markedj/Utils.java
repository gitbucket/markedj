package io.github.gitbucket.markedj;

public class Utils {

    public static String escape(String html){
        return escape(html, false);
    }

    public static String escape(String html, boolean encode){
        if(!encode){
            html = html.replaceAll("&(?!#?\\w+;)", "&amp;");
        } else {
            html = html.replace("&", "&amp");
        }
        return html.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }

    public static String or(String str1, String str2){
        if(str1 != null && str1.length() > 0){
            return str1;
        } else {
            return str2;
        }
    }

    public static boolean isNumber(String str){
        return (str.length() > 0 && str.matches("^[0-9]+$"));
    }

}
