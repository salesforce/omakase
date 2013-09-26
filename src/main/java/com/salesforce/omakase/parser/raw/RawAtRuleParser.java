/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.salesforce.omakase.parser.raw;

import com.google.common.base.Optional;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractRefinableParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.refiner.Refiner;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.token.TokenFactory;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses an {@link AtRule}.
 *
 * @author nmcwilliams
 * @see AtRule
 */
public class RawAtRuleParser extends AbstractRefinableParser {
    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster, Refiner refiner) {
        TokenFactory tf = tokenFactory();

        stream.skipWhitepace();
        stream.collectComments();

        // snapshot the current state before parsing
        Stream.Snapshot snapshot = stream.snapshot();

        // must begin with '@'
        if (!stream.optionallyPresent(Tokens.AT_RULE)) return false;

        // read the name
        Optional<String> name = stream.readIdent();
        if (!name.isPresent()) throw new ParserException(stream, Message.MISSING_AT_RULE_NAME);

        // read everything up until the end of the at-rule expression (usually a semicolon or open bracket).
        int line = stream.line();
        int column = stream.column();
        String content = stream.until(tf.atRuleExpressionEnd()).trim();
        RawSyntax expression = content.isEmpty() ? null : new RawSyntax(line, column, content);

        // skip whitespace after the expression
        stream.skipWhitepace();

        RawSyntax block = null;

        // parse the termination (usually ';' or the at-rule block)
        if (!stream.optionallyPresent(tf.atRuleTermination()) && tf.atRuleBlockBegin().matches(stream.current())) {
            line = stream.line();
            column = stream.column();
            content = stream.chompEnclosedValue(tf.atRuleBlockBegin(), tf.atRuleBlockEnd()).trim();
            block = content.isEmpty() ? null : new RawSyntax(line, column, content);
        }

        // expression content must be present
        if (expression == null && block == null) throw new ParserException(stream, Message.MISSING_AT_RULE_VALUE);

        // create and broadcast the new rule
        AtRule rule = new AtRule(snapshot.line, snapshot.column, name.get(), expression, block, refiner);
        rule.comments(stream.flushComments());
        broadcaster.broadcast(rule);

        return true;
    }
}
