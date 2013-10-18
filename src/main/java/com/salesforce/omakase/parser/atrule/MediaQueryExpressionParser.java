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
import com.salesforce.omakase.ast.atrule.MediaQueryExpression;
import com.salesforce.omakase.ast.declaration.TermListMember;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.Refiner;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parsers a {@link MediaQueryExpression}.
 * <p/>
 * In the following example:
 * <pre>    {@code@}media all and (min-width: 800px) { ... }</pre>
 * <p/>
 * The expression is {@code (min-width: 800px)}
 *
 * @author nmcwilliams
 * @see MediaQueryExpression
 */
public class MediaQueryExpressionParser extends AbstractParser {
    @Override
    public boolean parse(Source source, Broadcaster broadcaster, Refiner refiner) {
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

            QueryableBroadcaster qb = new QueryableBroadcaster(broadcaster);
            ParserFactory.termSequenceParser().parse(source, qb, refiner);

            Iterable<TermListMember> terms = qb.filter(TermListMember.class);
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
