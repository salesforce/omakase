/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
import com.salesforce.omakase.parser.refiner.MasterRefiner;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses an {@link AttributeSelector}.
 *
 * @author nmcwilliams
 * @see AttributeSelector
 */
public final class AttributeSelectorParser extends AbstractParser {
    @Override
    public boolean parse(Source source, Broadcaster broadcaster, MasterRefiner refiner) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a descendant combinator
        source.collectComments(false);

        // grab the current position before parsing
        int line = source.originalLine();
        int column = source.originalColumn();

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
        AttributeSelector selector = new AttributeSelector(line, column, attribute.get());
        if (type.isPresent()) {
            selector.match(type.get(), value.get().trim());
        }
        selector.comments(source.flushComments());

        broadcaster.broadcast(selector);
        return true;
    }
}
