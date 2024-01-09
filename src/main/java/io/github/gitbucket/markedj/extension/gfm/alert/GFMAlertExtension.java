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
package io.github.gitbucket.markedj.extension.gfm.alert;

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
import java.util.function.Function;

/**
 *
 * @author t.marx
 */
public class GFMAlertExtension implements Extension {

	public static String EXPRESSION = "(?s)(?m)\\A^> \\[!(NOTE|TIP|IMPORTANT|WARNING|CAUTION)\\](.+?)(^\n|\\Z)";

	private static final Rule RULE = new FindFirstRule(EXPRESSION);
	
	private final Map<GFMAlerts.Alert, String> titles = new HashMap<>();
	
	private final Map<GFMAlerts.Alert, String> icons = new HashMap<>();

	/**
	 * Creates the extension with default titles and icons.
	 */
	public GFMAlertExtension () {
		titles.put(GFMAlerts.Alert.TIP, "Tip");
		titles.put(GFMAlerts.Alert.NOTE, "Note");
		titles.put(GFMAlerts.Alert.IMPORTANT, "Important");
		titles.put(GFMAlerts.Alert.WARNING, "Warning");
		titles.put(GFMAlerts.Alert.CAUTION, "Caution");
		
		icons.put(GFMAlerts.Alert.TIP, GFMAlerts.Icons.TIP);
		icons.put(GFMAlerts.Alert.NOTE, GFMAlerts.Icons.NOTE);
		icons.put(GFMAlerts.Alert.IMPORTANT, GFMAlerts.Icons.IMPORTANT);
		icons.put(GFMAlerts.Alert.WARNING, GFMAlerts.Icons.WARNING);
		icons.put(GFMAlerts.Alert.CAUTION, GFMAlerts.Icons.CAUTION);
	}
	
	/**
	 * Adds the title for an alert.
	 * 
	 * @param alert
	 * @param title 
	 */
	public void addTitle (final GFMAlerts.Alert alert, final String title ) {
		titles.put(alert, title);
	}
	
	/**
	 * Adds a avg icon for a alert.
	 * @param alert
	 * @param icon 
	 */
	public void addIcon (final GFMAlerts.Alert alert, final String icon ) {
		icons.put(alert, icon);
	}
	
	@Override
	public LexResult lex(String source, final Lexer.LexerContext context, final TokenConsumer consumer) {
		List<String> cap = RULE.exec(source);
		boolean tokenFound = false;
		if (!cap.isEmpty()) {
			// we have detected several contiguous lines of notifications
			// ensure that all are of same kind
			String allNotificationsLines = cap.get(0);
			
			String content = cap.get(2);
			
			content = content.replaceAll("(?m)^ *> ?", "");

			source = source.substring(allNotificationsLines.length());
			context.pushToken(new GFMAlertStartToken(cap.get(1)));
			consumer.token(content, false, false, context);
			context.pushToken(new GFMAlertEndToken());
			
			tokenFound = true;
		}
		return new LexResult(source, tokenFound);
	}

	@Override
	public boolean handlesToken(String token) {
		return GFMAlertStartToken.TYPE.equals(token);
	}

	@Override
	public String parse(Parser.ParserContext context, Function<Parser.ParserContext, String> tok) {
		GFMAlertStartToken t = (GFMAlertStartToken) context.currentToken();
		StringBuilder body = new StringBuilder();
		while (true) {
			Token n = context.nextToken();
			if (n == null || n.getType().equals(GFMAlertEndToken.TYPE)) {
				break;
			}
			body.append(tok.apply(context));
		}
		return render(body.toString(), t.getNotification());
	}
	
	private String render(String message, GFMAlerts.Alert alert) {
		
		if (!message.startsWith("<p>")) {
			message = String.format("<p>%s</p>", message);
		}
		
        return String.format("<div class=\"markdown-alert markdown-alert-%s\"><p class=\"markdown-alert-title\">%s%s</p>\n%s</div>", 
				alert.name().toLowerCase(Locale.ENGLISH), 
				icons.get(alert),
				titles.get(alert),
				message
		);
    }
}
