package io.github.gitbucket.markedj.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindAllRule implements Rule {

    private Pattern pattern;

    public FindAllRule(String regex){
        this.pattern = Pattern.compile(regex);
    }

    public Optional<List<String>> exec(String src) {
        Matcher matcher = pattern.matcher(src);
        List<String> result = new ArrayList<>();
        while(matcher.find()){
            result.add(matcher.group(0));
        }
        if(!result.isEmpty()){
            return Optional.of(result);
        } else {
            return Optional.empty();
        }
    }
}
