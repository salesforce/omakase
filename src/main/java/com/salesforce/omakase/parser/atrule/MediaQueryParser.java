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

package com.salesforce.omakase.parser.atrule;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.atrule.MediaQuery;
import com.salesforce.omakase.ast.atrule.MediaQueryExpression;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.Refiner;
import com.salesforce.omakase.parser.token.Tokens;

import com.salesforce.omakase.ast.atrule.MediaRestriction;

/**
 * Parsers a {@link MediaQuery}.
 * <p/>
 * In the following example:
 * <pre>{@code @}media all and (min-width: 800px), projection and (color) { ... }</pre>
 * <p/>
 * There are two media queries,
 * <p/>
 * 1) {@code all and (min-width: 800px)}
 * <p/>
 * 2) {@code projection and (color)}
 *
 * @author nmcwilliams
 * @see MediaQuery
 */
public final class MediaQueryParser extends AbstractParser {
    private static final String AND = "and";

    @Override
    public boolean parse(Source source, Broadcaster broadcaster, Refiner refiner) {
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
