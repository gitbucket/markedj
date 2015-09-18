package io.github.gitbucket.markedj.rule;

import java.util.List;
import java.util.Optional;

public interface Rule {
    Optional<List<String>> exec(String src);
}
