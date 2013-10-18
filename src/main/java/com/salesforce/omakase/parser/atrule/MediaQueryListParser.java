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

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.atrule.MediaQuery;
import com.salesforce.omakase.ast.atrule.MediaQueryList;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.Refiner;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parsers a {@link MediaQueryList}.
 * <p/>
 * In the following example the media query list is everything until the opening curly brace:
 * <pre>    {@code @}media all and (min-width: 800px), projection and (color) { ... }</pre>
 *
 * @author nmcwilliams
 */
public final class MediaQueryListParser extends AbstractParser {
    @Override
    public boolean parse(Source source, Broadcaster broadcaster, Refiner refiner) {
        source.skipWhitepace();

        int line = source.originalLine();
        int column = source.originalColumn();

        QueryableBroadcaster qb = new QueryableBroadcaster(broadcaster);

        // try parsing a media query
        if (!ParserFactory.mediaQueryParser().parse(source, qb, refiner)) return false;

        // parse the remaining media queries
        while (source.skipWhitepace().optionallyPresent(Tokens.COMMA)) {
            source.skipWhitepace();
            if (!ParserFactory.mediaQueryParser().parse(source, qb, refiner)) {
                throw new ParserException(source, Message.TRAILING, Tokens.COMMA.description());
            }
        }

        // create the list and broadcast it
        MediaQueryList list = new MediaQueryList(line, column, broadcaster);
        list.queries().appendAll(qb.filter(MediaQuery.class));
        broadcaster.broadcast(list);

        return true;
    }
}
