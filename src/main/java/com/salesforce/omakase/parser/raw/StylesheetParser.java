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

package com.salesforce.omakase.parser.raw;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.Comment;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.broadcast.QueuingBroadcaster;
import com.salesforce.omakase.parser.AbstractRefinableParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.RefinableParser;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.Refiner;

/**
 * Parses a top-level {@link Stylesheet}.
 *
 * @author nmcwilliams
 * @see Stylesheet
 */
public class StylesheetParser extends AbstractRefinableParser {

    @Override
    public boolean parse(Source source, Broadcaster broadcaster, Refiner refiner) {
        // use a queue so that we can group all statements together before sending them out. This makes some plugins that
        // depend on order (isFirst(), etc...) work more smoothly.
        QueuingBroadcaster queue = new QueuingBroadcaster(broadcaster).pause();
        QueryableBroadcaster queryable = new QueryableBroadcaster(queue);

        // parse all statements
        RefinableParser statement = ParserFactory.statementParser();
        while (true) {
            if (!statement.parse(source, queryable, refiner)) break;
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
        for (String comment : source.collectComments().flushComments()) {
            stylesheet.orphanedComment(new Comment(comment));
        }

        // now that we have all the rules added resume the queue
        queue.resume();

        broadcaster.broadcast(stylesheet);
        return true;
    }

}
