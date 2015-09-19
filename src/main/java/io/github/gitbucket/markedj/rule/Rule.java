package io.github.gitbucket.markedj.rule;

import java.util.List;

public interface Rule {
    List<String> exec(String src);
}
