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

import static com.salesforce.omakase.ast.atrule.MediaQuery.Restriction;

/**
 * TESTME
 * <p/>
 * Parsers a {@link MediaQuery}.
 * <p/>
 * In the following example:
 * <pre>    {@code@}media all and (min-width: 800px), projection and (color) { ... }</pre>
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
public class MediaQueryParser extends AbstractParser {
    private static final String NOT = "not";
    private static final String ONLY = "only";
    private static final String AND = "and";

    @Override
    public boolean parse(Source source, Broadcaster broadcaster, Refiner refiner) {
        source.skipWhitepace();

        // save off position before parsing anything
        int line = source.originalLine();
        int column = source.originalColumn();

        Restriction restriction = null;
        String type = null;

        Optional<String> ident = source.readIdent();
        if (ident.isPresent()) {
            // the ident could be a restriction (only|not) or it could be the media type
            if (ident.get().equalsIgnoreCase(NOT)) {
                restriction = Restriction.NOT;
            } else if (ident.get().equalsIgnoreCase(ONLY)) {
                restriction = Restriction.ONLY;
            } else {
                type = ident.get();
            }

            // if it was a restriction then the media type is required
            if (restriction != null) {
                source.skipWhitepace();
                ident = source.readIdent();
                if (!ident.isPresent()) throw new ParserException(source, Message.MISSING_MEDIA_TYPE);
                type = ident.get();
            }
        }

        source.skipWhitepace();

        boolean readAnd = type != null && source.readConstant(AND);

        Source.Snapshot snapshot = source.snapshot();
        QueryableBroadcaster qb = new QueryableBroadcaster(broadcaster);

        if (ParserFactory.mediaExpressionParser().parse(source, qb, refiner) && type != null && !readAnd) {
            snapshot.rollback(Message.MISSING_AND);
        }

        source.skipWhitepace();

        while (source.readConstant(AND)) {
            source.skipWhitepace();
            if (!ParserFactory.mediaExpressionParser().parse(source, qb, refiner)) {
                throw new ParserException(source, Message.TRAILING_AND);
            }
            source.skipWhitepace();
        }

        MediaQuery query = new MediaQuery(line, column, broadcaster).restriction(restriction).type(type);
        query.expressions().appendAll(qb.filter(MediaQueryExpression.class));
        broadcaster.broadcast(query);
        return true;
    }
}
