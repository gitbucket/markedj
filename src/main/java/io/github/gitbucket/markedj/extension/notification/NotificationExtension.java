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

import io.github.gitbucket.markedj.Lexer;
import io.github.gitbucket.markedj.Parser;
import io.github.gitbucket.markedj.extension.Extension;
import io.github.gitbucket.markedj.extension.TokenConsumer;
import io.github.gitbucket.markedj.rule.FindFirstRule;
import io.github.gitbucket.markedj.rule.Rule;
import io.github.gitbucket.markedj.token.Token;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author t.marx
 */
public class NotificationExtension implements Extension {

	public static String BLOCK_NOTIFICATION = "^((?:(!([xv!]?))[^\n]*(?!^!)\n?)+)";

	@Override
	public Map<String, Rule> enhanceRules(Map<String, Rule> existingRules) {
		Map<String, Rule> rulesWithNotifications = new HashMap<>(existingRules);
		rulesWithNotifications.put("notification", notificationRule());
		return rulesWithNotifications;
	}

	private Rule notificationRule() {
		return new FindFirstRule(BLOCK_NOTIFICATION);
	}

	@Override
	public String lex(String source, final Lexer.LexerContext context, final TokenConsumer consumer) {
		List<String> cap = notificationRule().exec(source);
		if (!cap.isEmpty()) {
			// we have detected several contiguous lines of notifications
			// ensure that all are of same kind
			String allNotificationsLines = cap.get(0);

			// if other kind of notifications lines are detected
			// let's split them so that they are handled separately
			String findOtherLinesPattern = "(?m)^(!" + Notifications.exceptGivenNotificationClass(cap.get(3)) + " .*)";
			Matcher otherLinesMatcher = Pattern.compile(findOtherLinesPattern).matcher(cap.get(1));

			if (otherLinesMatcher.find()) {
				String otherLinesSeparated = otherLinesMatcher.replaceAll("\n$1\n");

				// change the source to parse
				// replace all the notifications lines with separated notifications lines
				// and reparse the string
				source = otherLinesSeparated + source.substring(allNotificationsLines.length());
			} else {
				source = source.substring(allNotificationsLines.length());
				context.pushToken(new NotificationStartToken(cap.get(3)));
				consumer.token(allNotificationsLines.replaceAll("(?m)^" + cap.get(2) + "[ ]?", ""), false, false, context);
				context.pushToken(new NotificationEndToken());
			}
		}
		return source;
	}

	@Override
	public boolean handlesToken(String token) {
		return NotificationStartToken.TYPE.equals(token);
	}

	@Override
	public String parse(Parser.ParserContext context, Function<Parser.ParserContext, String> tok) {
		NotificationStartToken t = (NotificationStartToken) context.currentToken();
		StringBuilder body = new StringBuilder();
		while (true) {
			Token n = context.nextToken();
			if (n == null || n.getType().equals("NotificationEndToken")) {
				break;
			}
			body.append(tok.apply(context));
		}
		return render(body.toString(), t.getNotification());
	}
	
	private String render(String info, Notifications.Notification notification) {
        return String.format("<div class=\"notification_%s\">\n%s</div>\n", notification.name().toLowerCase(Locale.ENGLISH), info);
    }
}
