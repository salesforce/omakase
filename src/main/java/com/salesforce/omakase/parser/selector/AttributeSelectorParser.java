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

package com.salesforce.omakase.parser.selector;

import com.google.common.base.Optional;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.selector.AttributeMatchType;
import com.salesforce.omakase.ast.selector.AttributeSelector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses an {@link AttributeSelector}.
 *
 * @author nmcwilliams
 * @see AttributeSelector
 */
public class AttributeSelectorParser extends AbstractParser {
    @Override
    public boolean parse(Source source, Broadcaster broadcaster) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a descendant combinator
        source.collectComments(false);

        // snapshot the current state before parsing
        Source.Snapshot snapshot = source.snapshot();

        // opening bracket [
        if (!source.optionallyPresent(Tokens.OPEN_BRACKET)) return false;

        // skip whitespace after the bracket
        source.skipWhitepace();

        // read the attribute name
        Optional<String> attribute = source.readIdent();
        if (!attribute.isPresent()) throw new ParserException(source, Message.EXPECTED_ATTRIBUTE_NAME);

        // skip whitespace after the name
        source.skipWhitepace();

        // try to parse the optional match type

        Optional<AttributeMatchType> type = source.optionalFromConstantEnum(AttributeMatchType.class);
        Optional<String> value = Optional.absent();

        if (type.isPresent()) {
            // skip whitespace after the match type
            source.skipWhitepace();

            // more performant to try string first as it's more likely to be used
            value = source.readString();

            // if not matched then try an ident token
            if (!value.isPresent()) {
                value = source.readIdent();
            }

            // value must be present since the match type is present
            if (!value.isPresent()) throw new ParserException(source, Message.EXPECTED_ATTRIBUTE_MATCH_VALUE);
        }

        // closing bracket ]
        source.skipWhitepace();
        source.expect(Tokens.CLOSE_BRACKET);

        // create the selector and broadcast it
        AttributeSelector selector = new AttributeSelector(snapshot.originalLine, snapshot.originalColumn, attribute.get());
        if (type.isPresent()) {
            selector.match(type.get(), value.get().trim());
        }
        selector.comments(source.flushComments());

        broadcaster.broadcast(selector);
        return true;
    }
}
