package io.github.gitbucket.markedj;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static String escape(String html){
        return escape(html, false);
    }

    public static String escape(String html, boolean encode){
        if(!encode){
            html = html.replaceAll("&(?!#?\\w+;)", "&amp;");
        } else {
            html = html.replace("&", "&amp;");
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

    public static boolean isEmpty(String str){
        return str == null || str.length() == 0;
    }

    public static void fillList(List<String> list, int length, String value){
        while(list.size() < length){
            list.add(value);
        }
    }

    public static List<String> array2list(String[] array){
        List<String> list = new ArrayList<>(array.length);
        for(String value: array){
            list.add(value);
        }
        return list;
    }

    public static String trim(String str){
        if(str == null){
            return null;
        } else {
            return str.trim();
        }
    }

}
