package io.github.gitbucket.markedj;

import static io.github.gitbucket.markedj.Resources.loadResourceAsString;
import org.assertj.core.api.Assertions;

import org.junit.Test;

public class BlockquotesTest {
    
    @Test
    public void testMultipleBlockQuotes1() throws Exception {
        String md = loadResourceAsString("multiple_blockquotes_1.md");
        String result = Marked.marked(md, new Options());
        String expect = loadResourceAsString("multiple_blockquotes_1.html");
        Assertions.assertThat(result).isEqualToIgnoringWhitespace(expect);
		
    }
    
    @Test
    public void testMultipleBlockQuotes2() throws Exception {
        String md = loadResourceAsString("multiple_blockquotes_2.md");
        String result = Marked.marked(md, new Options());
        String expect = loadResourceAsString("multiple_blockquotes_2.html");
        Assertions.assertThat(result).isEqualToIgnoringWhitespace(expect);
    }
}
