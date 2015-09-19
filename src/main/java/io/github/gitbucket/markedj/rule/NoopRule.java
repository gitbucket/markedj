package io.github.gitbucket.markedj.rule;

import java.util.Collections;
import java.util.List;

public class NoopRule implements Rule {
    public List<String> exec(String src) {
        return Collections.emptyList();
    }
}
