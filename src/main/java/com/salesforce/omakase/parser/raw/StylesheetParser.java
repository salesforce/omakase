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

package com.salesforce.omakase.parser.raw;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.broadcast.QueuingBroadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.MasterRefiner;

/**
 * Parses a top-level {@link Stylesheet}.
 *
 * @author nmcwilliams
 * @see Stylesheet
 */
public final class StylesheetParser extends AbstractParser {

    @Override
    public boolean parse(Source source, Broadcaster broadcaster, MasterRefiner refiner) {
        // use a queue so that we can group all statements together before sending them out. This makes some plugins that
        // depend on order (isFirst(), etc...) work more smoothly.
        QueuingBroadcaster queue = new QueuingBroadcaster(broadcaster).pause();
        QueryableBroadcaster queryable = new QueryableBroadcaster(queue);

        // parse all statements
        while (true) {
            if (!ParserFactory.statementParser().parse(source, queryable, refiner)) break;
        }

        // collect any orphaned comments and move past trailing space
        source.collectComments();

        // after all rules and content is parsed, there should be nothing left in the source
        if (!source.eof()) throw new ParserException(source, Message.EXTRANEOUS, source.remaining());

        // create the stylesheet
        Stylesheet stylesheet = new Stylesheet(broadcaster);

        // append all parsed statements
        stylesheet.statements().appendAll(queryable.filter(Statement.class));

        // orphaned at end of the stylesheet comments, e.g., ".class{color:red} /*orphaned*/"
        stylesheet.orphanedComments(source.collectComments().flushComments());

        // now that we have all the rules added resume the queue
        queue.resume();

        broadcaster.broadcast(stylesheet);
        return true;
    }

}
