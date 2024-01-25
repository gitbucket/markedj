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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author t.marx
 */
public class GFMAlertOptions {

	private final Map<GFMAlerts.Alert, String> titles = new HashMap<>();
	
	/**
	 * Creates the options with default titles.
	 */
	public GFMAlertOptions () {
		titles.put(GFMAlerts.Alert.TIP, "Tip");
		titles.put(GFMAlerts.Alert.NOTE, "Note");
		titles.put(GFMAlerts.Alert.IMPORTANT, "Important");
		titles.put(GFMAlerts.Alert.WARNING, "Warning");
		titles.put(GFMAlerts.Alert.CAUTION, "Caution");
	}
	
	public String getTitle (GFMAlerts.Alert alert) {
		return titles.get(alert);
	}
	
	public void setTitle (GFMAlerts.Alert alert, String title) {
		titles.put(alert, title);
	}

}
