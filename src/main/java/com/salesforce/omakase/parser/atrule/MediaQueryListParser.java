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

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.atrule.MediaQuery;
import com.salesforce.omakase.ast.atrule.MediaQueryList;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.Parser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parsers a {@link MediaQueryList}.
 * <p>
 * In the following example:
 * <pre>{@code @}media all and (min-width: 800px), projection and (color) { ... }</pre>
 *
 * The media query list comprises of the content {@code all and (min-width: 800px), projection and (color)}.
 *
 * @author nmcwilliams
 */
public final class MediaQueryListParser implements Parser {

    @Override
    public boolean parse(Source source, Grammar grammar, Broadcaster broadcaster) {
        source.skipWhitepace();

        int line = source.originalLine();
        int column = source.originalColumn();

        QueryableBroadcaster queryable = new QueryableBroadcaster(broadcaster);

        // try parsing a media query
        Parser mediaQueryParser = grammar.parser().mediaQueryParser();
        if (!mediaQueryParser.parse(source, grammar, queryable)) return false;

        // parse the remaining media queries
        while (source.skipWhitepace().optionallyPresent(Tokens.COMMA)) {
            source.skipWhitepace();
            if (!mediaQueryParser.parse(source, grammar, queryable)) {
                throw new ParserException(source, Message.TRAILING, Tokens.COMMA.description());
            }
        }

        // create the list and broadcast it
        MediaQueryList list = new MediaQueryList(line, column);
        list.queries().appendAll(queryable.filter(MediaQuery.class));
        broadcaster.broadcast(list);

        return true;
    }

}
