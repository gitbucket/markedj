package io.github.gitbucket.markedj;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Created by takezoe on 15/09/19.
 */
public class MarkedTest {
    @Test
    public void testQuote() throws Exception {
        String md = loadResourceAsString("quote.md");
        String result = Marked.marked(md, new Options());
        String expect = loadResourceAsString("quote.html");
        assertEquals(expect, result);
    }

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
        String result = Marked.marked("<takezoe@gmail.com>", new Options());
        assertEquals("<p><a href=\"mailto:takezoe@gmail.com\">takezoe@gmail.com</a></p>", result);
    }

    @Test
    public void testReflink() throws Exception {
      String result = Marked.marked("[FOO], [bar][Foo], [Bar]\n\n[Foo]: http://example.com");
      assertEquals("<p><a href=\"http://example.com\">FOO</a>, <a href=\"http://example.com\">bar</a>, [Bar]</p>", result);
    }

    @Test
    public void testEm() throws Exception {
        {
            String result = Marked.marked("_aa__a__aa_", new Options());
            assertEquals("<p><em>aa<strong>a</strong>aa</em></p>", result);
        }
        {
            String result = Marked.marked("*aa*o*aa*", new Options());
            assertEquals("<p><em>aa</em>o<em>aa</em></p>", result);
        }
        {
            String result = Marked.marked("_aa__aa_", new Options());
            assertEquals("<p><em>aa__aa</em></p>", result);
        }
    }

    @Test
    public void testStackoverFlow() throws Exception {
        Marked.marked(loadResourceAsString("stackoverflow.txt"), new Options());
    }

    @Test
    public void testStackoverFlow2() throws Exception {
        Marked.marked(loadResourceAsString("stackoverflow2.txt"), new Options());
    }

    @Test
    public void testNptable() throws Exception {
        String result = Marked.marked(loadResourceAsString("nptable.md"), new Options());
        String expect = loadResourceAsString("nptable.html");
        assertEquals(expect, result);
    }

    @Test
    public void testBreaks() throws Exception {
        {
            String md = "first line\nsecond line";
            Options options = new Options();
            //options.setBreaks(false); // default is false
            //options.setGfm(true);     // default is true
            String result = Marked.marked(md, options);

            assertEquals("<p>first line second line</p>", result);
        }
        {
            String md = "first line\nsecond line";
            Options options = new Options();
            options.setBreaks(true);
            //options.setGfm(true); // default is true
            String result = Marked.marked(md, options);

            assertEquals("<p>first line<br>second line</p>", result);
        }
    }

    @Test
    public void testInvalidColumnTable() throws Exception {
        {
            String result = Marked.marked(loadResourceAsString("table.md"), new Options());
            assertEquals(loadResourceAsString("table.html"), result);
        }
    }

    @Test
    public void testCodeBlock() throws Exception {
        String result = Marked.marked(
                "    public class HelloWorld {\n" +
                "    }", new Options());
        assertEquals(
                "<pre><code>public class HelloWorld {\n" +
                "}\n" +
                "</code></pre>", result);
    }

    @Test
    public void testEmptyItemOfList() throws Exception {
        String result = Marked.marked(loadResourceAsString("empty_item_of_list.md"), new Options());
        assertEquals(loadResourceAsString("empty_item_of_list.html"), result);
    }

    @Test
    public void testParagraphSeparation() throws Exception {
        String result = Marked.marked(
                "Message A\n" +
                "- List A\n" +
                "- List B", new Options());

        assertEquals(
                "<p>Message A</p> \n" +
                "<ul> \n" +
                " <li>List A</li> \n" +
                " <li>List B</li> \n" +
                "</ul>", result);
    }

    @Test
    public void testNestedContentOfList() throws Exception {
        String result = Marked.marked(loadResourceAsString("nested_content_of_list.md"), new Options());
        assertEquals(loadResourceAsString("nested_content_of_list.html"), result);
    }

    @Test
    public void testEmptyTableCell() throws Exception {
        String result = Marked.marked(
                "|ID|name|note|\n" +
                "|-|-|-|\n" +
                "|1|foo|This is foo|\n" +
                "|2|bar||");


        assertEquals(
                "<table> \n" +
                        " <thead> \n" +
                        "  <tr> \n" +
                        "   <th>ID</th> \n" +
                        "   <th>name</th> \n" +
                        "   <th>note</th> \n" +
                        "  </tr> \n" +
                        " </thead> \n" +
                        " <tbody> \n" +
                        "  <tr> \n" +
                        "   <td>1</td> \n" +
                        "   <td>foo</td> \n" +
                        "   <td>This is foo</td> \n" +
                        "  </tr> \n" +
                        "  <tr> \n" +
                        "   <td>2</td> \n" +
                        "   <td>bar</td> \n" +
                        "   <td></td> \n" +
                        "  </tr> \n" +
                        " </tbody> \n" +
                        "</table>", result);
    }

    @Test
    public void testSanitize() throws Exception {
        {
            Options options = new Options();
            options.setSanitize(true);
            String result = Marked.marked("<b>bold</b><script>alert('test!');</script>", options);
            assertEquals("<p>&lt;b&gt;bold&lt;/b&gt;&lt;script&gt;alert('test!');&lt;/script&gt;</p>", result);
        }
        {
            Options options = new Options();
            options.setSanitize(false);
            String result = Marked.marked("<b>bold</b><script>alert('test!');</script>", options);
            assertEquals("<p><b>bold</b></p>", result);
        }
        {
            Options options = new Options();
            options.setSanitize(false);
            String result = Marked.marked("- <b>test", options);
            // It's not clean but tag is closed at least.
            assertEquals("<ul> \n" +
                    " <li><b>test</b></li>\n" +
                    " <b> </b>\n" +
                    "</ul>\n" +
                    "<b> </b>", result);
        }
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
