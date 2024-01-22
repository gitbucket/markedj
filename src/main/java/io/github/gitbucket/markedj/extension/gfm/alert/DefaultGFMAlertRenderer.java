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
package io.github.gitbucket.markedj.extension.gfm.alert;

import java.util.Locale;
import java.util.Map;

/**
 *
 * @author t.marx
 */
public class DefaultGFMAlertRenderer implements GFMAlertRenderer {

	@Override
	public String render(Map<GFMAlerts.Alert, String> titles, String message, GFMAlerts.Alert alert) {
		if (!message.startsWith("<p>")) {
			message = String.format("<p>%s</p>", message);
		}
		
        return String.format("<div class=\"markdown-alert markdown-alert-%s\"><p class=\"markdown-alert-title\">%s</p>\n%s</div>", 
				alert.name().toLowerCase(Locale.ENGLISH), 
				titles.get(alert),
				message
		);
	}
	
}
