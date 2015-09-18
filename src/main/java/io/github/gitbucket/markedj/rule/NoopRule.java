package io.github.gitbucket.markedj.rule;

import java.util.List;
import java.util.Optional;

public class NoopRule implements Rule {
    public Optional<List<String>> exec(String src) {
        return Optional.empty();
    }
}
