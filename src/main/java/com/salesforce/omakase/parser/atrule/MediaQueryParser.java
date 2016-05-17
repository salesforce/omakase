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
import com.salesforce.omakase.ast.atrule.MediaQuery;
import com.salesforce.omakase.ast.atrule.MediaQueryExpression;
import com.salesforce.omakase.ast.atrule.MediaRestriction;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.MasterRefiner;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parsers a {@link MediaQuery}.
 * <p>
 * In the following example:
 * <pre>{@code @}media all and (min-width: 800px), projection and (color) { ... }</pre>
 *
 * There are two media queries,
 *
 * 1) {@code all and (min-width: 800px)}
 *
 * 2) {@code projection and (color)}
 *
 * @author nmcwilliams
 * @see MediaQuery
 */
public final class MediaQueryParser extends AbstractParser {
    private static final String AND = "and";

    @Override
    public boolean parse(Source source, Broadcaster broadcaster, MasterRefiner refiner) {
        source.skipWhitepace();

        // save off position before parsing anything
        int line = source.originalLine();
        int column = source.originalColumn();

        // read the optional restriction and type
        Optional<MediaRestriction> restriction = source.optionalFromConstantEnum(MediaRestriction.class);
        Source.Snapshot snapshot = source.skipWhitepace().snapshot();
        Optional<String> type = source.readIdent();

        // if restriction is present then there must be a type ('and' is not a type)
        if (restriction.isPresent() && (!type.isPresent() || type.get().equalsIgnoreCase(AND))) {
            snapshot.rollback(Message.MISSING_MEDIA_TYPE);
        }

        source.skipWhitepace();

        // 'and' is required before an expression if the type is present
        boolean hasAndAfterType = type.isPresent() && source.readConstantCaseInsensitive(AND);
        if (hasAndAfterType) {
            source.expect(Tokens.WHITESPACE);// space required after and
        }

        snapshot = source.snapshot();
        QueryableBroadcaster qb = new QueryableBroadcaster(broadcaster);

        // try reading one expression. if there was a type then we must have parsed an 'and' beforehand
        if (ParserFactory.mediaExpressionParser().parse(source, qb, refiner) && type.isPresent() && !hasAndAfterType) {
            snapshot.rollback(Message.MISSING_AND);
        }

        // read the rest of the expressions
        while (source.skipWhitepace().readConstantCaseInsensitive(AND)) {
            source.expect(Tokens.WHITESPACE).skipWhitepace();
            if (!ParserFactory.mediaExpressionParser().parse(source, qb, refiner)) {
                throw new ParserException(source, Message.TRAILING_AND);
            }
        }

        Iterable<MediaQueryExpression> expressions = qb.filter(MediaQueryExpression.class);
        boolean hasExpressions = !Iterables.isEmpty(expressions);

        // check for a trailing 'and'
        if (!hasExpressions && hasAndAfterType) throw new ParserException(source, Message.TRAILING_AND);

        // if we haven't parsed a type (and thus no restriction either) and no expressions, return false
        if (!type.isPresent() && !hasExpressions) return false;

        // create and broadcast the media query
        MediaQuery query = new MediaQuery(line, column, broadcaster);
        query.type(type.orNull()).restriction(restriction.orNull()).expressions().appendAll(expressions);
        broadcaster.broadcast(query);
        return true;
    }
}
