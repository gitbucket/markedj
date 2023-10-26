package io.github.gitbucket.markedj;

import static io.github.gitbucket.markedj.Resources.loadResourceAsString;
import io.github.gitbucket.markedj.extension.notification.NotificationExtension;
import org.assertj.core.api.Assertions;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NotificationsTest {
    @Test
    public void testInfoNotification() throws Exception {
        String md = loadResourceAsString("notifications/info.md");
        String result = Marked.marked(md, new Options().extension("notifications", new NotificationExtension()));
        String expect = loadResourceAsString("notifications/info.html");
        Assertions.assertThat(result).isEqualToIgnoringWhitespace(expect);
    }
    
    @Test
    public void testSuccessNotification() throws Exception {
        String md = loadResourceAsString("notifications/success.md");
        String result = Marked.marked(md, new Options().extension("notifications", new NotificationExtension()));
        String expect = loadResourceAsString("notifications/success.html");
        Assertions.assertThat(result).isEqualToIgnoringWhitespace(expect);
    }
    
    @Test
    public void testWarningNotification() throws Exception {
        String md = loadResourceAsString("notifications/warning.md");
        String result = Marked.marked(md, new Options().extension("notifications", new NotificationExtension()));
        String expect = loadResourceAsString("notifications/warning.html");
        Assertions.assertThat(result).isEqualToIgnoringWhitespace(expect);
    }
    
    @Test
    public void testErrorNotification() throws Exception {
        String md = loadResourceAsString("notifications/error.md");
        String result = Marked.marked(md, new Options().extension("notifications", new NotificationExtension()));
        String expect = loadResourceAsString("notifications/error.html");
        Assertions.assertThat(result).isEqualToIgnoringWhitespace(expect);
    }
    
    @Test
    public void testMultipleNotifications1() throws Exception {
        String md = loadResourceAsString("notifications/multiple_notifications_1.md");
        String result = Marked.marked(md, new Options().extension("notifications", new NotificationExtension()));
        String expect = loadResourceAsString("notifications/multiple_notifications_1.html");
        Assertions.assertThat(result).isEqualToIgnoringWhitespace(expect);
    }
    
    @Test
    public void testMultipleNotifications2() throws Exception {
        String md = loadResourceAsString("notifications/multiple_notifications_2.md");
        String result = Marked.marked(md, new Options().extension("notifications", new NotificationExtension()));
        String expect = loadResourceAsString("notifications/multiple_notifications_2.html");
        Assertions.assertThat(result).isEqualToIgnoringWhitespace(expect);
    }
    
    @Test
    public void testMultipleNotifications3() throws Exception {
        String md = loadResourceAsString("notifications/multiple_notifications_3.md");
        String result = Marked.marked(md, new Options().extension("notifications", new NotificationExtension()));
        String expect = loadResourceAsString("notifications/multiple_notifications_3.html");
        Assertions.assertThat(result).isEqualToIgnoringWhitespace(expect);
    }
    
    @Test
    public void testMultipleNotificationsWithTypeChange() throws Exception {
        String md = loadResourceAsString("notifications/multiple_notifications_type_change.md");
        String result = Marked.marked(md, new Options().extension("notifications", new NotificationExtension()));
        String expect = loadResourceAsString("notifications/multiple_notifications_type_change.html");
		
		System.out.println(result);
		System.out.println("---");
		System.out.println(expect);
		
        Assertions.assertThat(result).isEqualToIgnoringWhitespace(expect);
    }
    
    @Test
    public void testEmbeddedNotifications() throws Exception {
        String md = loadResourceAsString("notifications/embedded_notifications.md");
        String result = Marked.marked(md, new Options().extension("notifications", new NotificationExtension()));
        String expect = loadResourceAsString("notifications/embedded_notifications.html");
        Assertions.assertThat(result).isEqualToIgnoringWhitespace(expect);
    }
}
