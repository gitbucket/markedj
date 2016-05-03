package io.github.gitbucket.markedj;

public class Notifications {
    public static String exceptGivenNotificationClass(String notificationClassCharacter) {
        switch (notificationClassCharacter) {
            case "":
                return "[xv!]";
            case "x":
                return "[v!]?";
            case "v":
                return "[x!]?";
            case "!":
                return "[xv]?";
        }
        throw new IllegalArgumentException("unknown character [" + notificationClassCharacter+ "] to define a notification charcater class");
    }
    
    public enum Notification {
        INFO, SUCCESS, WARNING, ERROR;
        
        public static Notification fromString(String s) {
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
