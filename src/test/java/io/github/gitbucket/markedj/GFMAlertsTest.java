/*
 * Copyright 2024 GitBucket.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.gitbucket.markedj;

import static io.github.gitbucket.markedj.Resources.loadResourceAsString;
import io.github.gitbucket.markedj.extension.gfm.alert.GFMAlertExtension;
import io.github.gitbucket.markedj.extension.gfm.alert.GFMAlerts;
import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 *
 * @author t.marx
 */
public class GFMAlertsTest {
	
	
	private Options createOptions () {
		Options options = new Options();
		options.addExtension(new GFMAlertExtension());
		return options;
	}
	
    @Test
    public void testNoteAlert() throws Exception {
        String md = loadResourceAsString("gfm/alerts/note.md");
		String result = Marked.marked(md, createOptions());
        String expect = loadResourceAsString("gfm/alerts/note.html");
        Assertions.assertThat(result).isEqualToIgnoringWhitespace(expect);
    }
	
	@Test
    public void testTipAlert() throws Exception {
        String md = loadResourceAsString("gfm/alerts/tip.md");
		String result = Marked.marked(md, createOptions());
        String expect = loadResourceAsString("gfm/alerts/tip.html");
        Assertions.assertThat(result).isEqualToIgnoringWhitespace(expect);
    }
	
	@Test
    public void testImportantAlert() throws Exception {
        String md = loadResourceAsString("gfm/alerts/important.md");
		String result = Marked.marked(md, createOptions());
        String expect = loadResourceAsString("gfm/alerts/important.html");
        Assertions.assertThat(result).isEqualToIgnoringWhitespace(expect);
    }
	
	@Test
    public void testCautionAlert() throws Exception {
        String md = loadResourceAsString("gfm/alerts/caution.md");
		String result = Marked.marked(md, createOptions());
        String expect = loadResourceAsString("gfm/alerts/caution.html");
        Assertions.assertThat(result).isEqualToIgnoringWhitespace(expect);
    }
	
	@Test
    public void testWarningAlert() throws Exception {
        String md = loadResourceAsString("gfm/alerts/warning.md");
		String result = Marked.marked(md, createOptions());
        String expect = loadResourceAsString("gfm/alerts/warning.html");
        Assertions.assertThat(result).isEqualToIgnoringWhitespace(expect);
    }
	
	@Test
    public void testWithCustomTitle() throws Exception {
        String md = loadResourceAsString("gfm/alerts/warning.md");
		
		Options options = new Options();
		final GFMAlertExtension gfmAlertExtension = new GFMAlertExtension();
		gfmAlertExtension.addTitle(GFMAlerts.Alert.WARNING, "Attention!!!");
		options.addExtension(gfmAlertExtension);
		
		String result = Marked.marked(md, options);
        String expect = loadResourceAsString("gfm/alerts/warning_custom_title.html");
        Assertions.assertThat(result).isEqualToIgnoringWhitespace(expect);
    }
	
	@Test
    public void testWithCustomIcon() throws Exception {
        String md = loadResourceAsString("gfm/alerts/warning.md");
		
		Options options = new Options();
		final GFMAlertExtension gfmAlertExtension = new GFMAlertExtension();
		gfmAlertExtension.addIcon(GFMAlerts.Alert.WARNING, "");
		options.addExtension(gfmAlertExtension);
		
		String result = Marked.marked(md, options);
        String expect = loadResourceAsString("gfm/alerts/warning_custom_icon.html");
        Assertions.assertThat(result).isEqualToIgnoringWhitespace(expect);
    }
}
