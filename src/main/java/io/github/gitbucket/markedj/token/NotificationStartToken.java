package io.github.gitbucket.markedj.token;

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

    public enum Notification {
        INFO, SUCCESS, WARNING, ERROR;
        
        static Notification fromString(String s) {
            switch (s) {
                case "x":
                    return Notification.ERROR;
                case "!":
                    return Notification.WARNING;
                case "v":
                    return Notification.SUCCESS;
                default:
                    return Notification.INFO;
            }
        }
    }
}
