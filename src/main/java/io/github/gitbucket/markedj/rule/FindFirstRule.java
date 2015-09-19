package io.github.gitbucket.markedj.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindFirstRule implements Rule {

    private Pattern pattern;

    public FindFirstRule(String regex){
        this.pattern = Pattern.compile(regex);
    }

    public List<String> exec(String src) {
        Matcher matcher = pattern.matcher(src);
        if(matcher.find()){
            List<String> result = new ArrayList<>();
            for(int i = 0; i < matcher.groupCount(); i++){
                result.add(matcher.group(i));
            }
            return result;
        } else {
            return Collections.emptyList();
        }
    }
}
