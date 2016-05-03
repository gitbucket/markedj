package io.github.gitbucket.markedj;

import static io.github.gitbucket.markedj.Resources.loadResourceAsString;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BlockquotesTest {
    
    @Test
    public void testMultipleBlockQuotes1() throws Exception {
        String md = loadResourceAsString("multiple_blockquotes_1.md");
        String result = Marked.marked(md, new Options());
        String expect = loadResourceAsString("multiple_blockquotes_1.html");
        assertEquals(expect, result);
    }
    
    @Test
    public void testMultipleBlockQuotes2() throws Exception {
        String md = loadResourceAsString("multiple_blockquotes_2.md");
        String result = Marked.marked(md, new Options());
        String expect = loadResourceAsString("multiple_blockquotes_2.html");
        assertEquals(expect, result);
    }
}
