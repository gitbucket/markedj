/*
 * Copyright 2023 GitBucket.
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
package io.github.gitbucket.markedj.extension.notification;

/**
 *
 * @author t.marx
 */
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
