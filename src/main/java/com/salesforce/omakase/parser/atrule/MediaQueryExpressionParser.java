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

package com.salesforce.omakase.parser.atrule;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.atrule.MediaQueryExpression;
import com.salesforce.omakase.ast.declaration.PropertyValueMember;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.MasterRefiner;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parsers a {@link MediaQueryExpression}.
 * <p/>
 * In the following example:
 * <pre>{@code @}media all and (min-width: 800px) { ... }</pre>
 * <p/>
 * The expression is <code>(min-width: 800px)</code>
 *
 * @author nmcwilliams
 * @see MediaQueryExpression
 */
public final class MediaQueryExpressionParser extends AbstractParser {
    @Override
    public boolean parse(Source source, Broadcaster broadcaster, MasterRefiner refiner) {
        source.skipWhitepace();

        // grab the current position before parsing anything
        int line = source.originalLine();
        int column = source.originalColumn();

        // check for the open paren
        if (!source.optionallyPresent(Tokens.OPEN_PAREN)) return false;

        source.skipWhitepace();

        // read the feature name
        Optional<String> feature = source.readIdent();
        if (!feature.isPresent()) throw new ParserException(source, Message.MISSING_FEATURE);

        source.skipWhitepace();

        MediaQueryExpression expression = new MediaQueryExpression(line, column, feature.get());

        // read the optional terms
        if (source.optionallyPresent(Tokens.COLON)) {
            source.skipWhitepace();

            QueryableBroadcaster qb = new QueryableBroadcaster(); // no need to broadcast the terms
            ParserFactory.termSequenceParser().parse(source, qb, refiner);

            Iterable<PropertyValueMember> terms = qb.filter(PropertyValueMember.class);
            if (Iterables.isEmpty(terms)) throw new ParserException(source, Message.MISSING_MEDIA_TERMS);

            expression.terms(terms);
        }

        source.skipWhitepace();

        // parse closing paren
        source.expect(Tokens.CLOSE_PAREN);

        broadcaster.broadcast(expression);
        return true;
    }
}
