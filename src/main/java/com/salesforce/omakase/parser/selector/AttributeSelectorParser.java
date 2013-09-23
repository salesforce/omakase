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
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses an {@link AttributeSelector}.
 *
 * @author nmcwilliams
 * @see AttributeSelector
 */
public class AttributeSelectorParser extends AbstractParser {
    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a descendant combinator
        stream.collectComments(false);

        // snapshot the current state before parsing
        Stream.Snapshot snapshot = stream.snapshot();

        // opening bracket [
        if (!stream.optionallyPresent(Tokens.OPEN_BRACKET)) return false;

        // skip whitespace after the bracket
        stream.skipWhitepace();

        // read the attribute name
        Optional<String> attribute = stream.readIdent();
        if (!attribute.isPresent()) throw new ParserException(stream, Message.EXPECTED_ATTRIBUTE_NAME);

        // skip whitespace after the name
        stream.skipWhitepace();

        // try to parse the optional match type

        Optional<AttributeMatchType> type = stream.optionalFromConstantEnum(AttributeMatchType.class);
        Optional<String> value = Optional.absent();

        if (type.isPresent()) {
            // skip whitespace after the match type
            stream.skipWhitepace();

            // more performant to try string first as it's more likely to be used
            value = stream.readString();

            // if not matched then try an ident token
            if (!value.isPresent()) {
                value = stream.readIdent();
            }

            // value must be present since the match type is present
            if (!value.isPresent()) throw new ParserException(stream, Message.EXPECTED_ATTRIBUTE_MATCH_VALUE);
        }

        // closing bracket ]
        stream.skipWhitepace();
        stream.expect(Tokens.CLOSE_BRACKET);

        // create the selector and broadcast it
        AttributeSelector selector = new AttributeSelector(snapshot.line, snapshot.column, attribute.get());
        if (type.isPresent()) {
            selector.match(type.get(), value.get().trim());
        }
        selector.comments(stream.flushComments());

        broadcaster.broadcast(selector);
        return true;
    }
}
