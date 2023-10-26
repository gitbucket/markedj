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
package io.github.gitbucket.markedj.extension;

import io.github.gitbucket.markedj.Lexer;
import io.github.gitbucket.markedj.Parser;
import io.github.gitbucket.markedj.rule.Rule;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author t.marx
 */
public interface Extension {
	public Map<String, Rule> enhanceRules(Map<String, Rule> existingRules);
	
	public String lex(String source, final Lexer.LexerContext context, final TokenConsumer consumer);
	
	public boolean handlesToken (final String token);
	
	public String parse (Parser.ParserContext context, Function<Parser.ParserContext, String> tok);
}
