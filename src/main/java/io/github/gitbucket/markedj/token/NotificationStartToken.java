package io.github.gitbucket.markedj.token;

import io.github.gitbucket.markedj.Notifications.Notification;

public class NotificationStartToken implements Token {
    private Notification notification;

    public NotificationStartToken(String type) {
        this.notification = Notification.fromString(type);
    }

    @Override
    public String getType() {
        return "NotificationStartToken";
    }
    
    public Notification getNotification() {
        return notification;
    }
}
