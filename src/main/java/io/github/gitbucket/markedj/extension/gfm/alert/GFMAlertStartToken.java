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

import io.github.gitbucket.markedj.token.Token;

/**
 *
 * @author t.marx
 */
public class GFMAlertStartToken implements Token {
	protected static String TYPE = "GFMAlertStartToken";
    
	private GFMAlerts.Alert alert;

    public GFMAlertStartToken(String type) {
        this.alert = GFMAlerts.Alert.fromString(type);
    }

    @Override
    public String getType() {
        return TYPE;
    }
    
    public GFMAlerts.Alert getNotification() {
        return alert;
    }
}
