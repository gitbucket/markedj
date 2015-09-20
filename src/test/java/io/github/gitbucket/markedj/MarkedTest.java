package io.github.gitbucket.markedj;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * Created by takezoe on 15/09/19.
 */
public class MarkedTest {

    @Test
    public void testMarked() throws Exception {
        String md = loadResourceAsString("ldap_settings.md");
        String result = Marked.marked(md, new Options());
        String expect = loadResourceAsString("ldap_settings.html");
        assertEquals(expect, result);
    }

    @Test
    public void testMarked2() throws Exception {
        String md = loadResourceAsString("gitbucket.md");
        String result = Marked.marked(md, new Options());
        String expect = loadResourceAsString("gitbucket.html");
        assertEquals(expect, result);
    }

    @Test
    public void testMarked3() throws Exception {
        String md = loadResourceAsString("wikilink.md");
        String result = Marked.marked(md, new Options());
        String expect = loadResourceAsString("wikilink.html");
        assertEquals(expect, result);
//        Files.write(Paths.get("wikilink.html"), result.getBytes("UTF-8"));
    }


    @Test
    public void testAutolink() throws Exception {
        String md = Marked.marked("<takezoe@gmail.com>", new Options());
        String result = Marked.marked(md, new Options());
        assertEquals("<p><a href=\"mailto:takezoe@gmail.com\">takezoe@gmail.com</a></p>", result);
    }


    private String loadResourceAsString(String path) throws IOException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024 * 8];
            int length = 0;
            while((length = in.read(buf)) != -1){
                out.write(buf, 0, length);
            }
            return new String(out.toByteArray(), "UTF-8");
        } finally {
            in.close();
        }
    }
}