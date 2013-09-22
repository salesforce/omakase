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
import com.salesforce.omakase.ast.OrphanedComment;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.notification.NotifyStylesheetEnd;
import com.salesforce.omakase.ast.notification.NotifyStylesheetStart;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Parser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Stream;

import java.util.List;

/**
 * Parses a top-level {@link Stylesheet}.
 *
 * @author nmcwilliams
 * @see Stylesheet
 */
public class StylesheetParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // broadcast the start of the stylesheet event
        NotifyStylesheetStart.broadcast(broadcaster);

        Parser parser = ParserFactory.statementParser();

        // continually parse until we get to the end of the stream
        while (!stream.eof()) {
            // parse the next statement
            boolean matched = parser.parse(stream, broadcaster);

            // skip whitespace
            stream.skipWhitepace();

            // after all rules and content is parsed, there should be nothing left in the stream
            if (!matched && !stream.eof()) throw new ParserException(stream, Message.EXTRANEOUS, stream.remaining());
        }

        // orphaned comments, e.g., ".class{color:red} /*orphaned*/"
        List<String> orphaned = stream.collectComments().flushComments();
        for (String comment : orphaned) {
            broadcaster.broadcast(new OrphanedComment(comment, OrphanedComment.Location.STYLESHEET));
        }

        // broadcast the end of the stylesheet event
        NotifyStylesheetEnd.broadcast(broadcaster);

        return true;
    }
}
