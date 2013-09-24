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

import com.salesforce.omakase.ast.OrphanedComment;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.notification.NotifyDeclarationBlockEnd;
import com.salesforce.omakase.notification.NotifyDeclarationBlockStart;
import com.salesforce.omakase.parser.AbstractRefinableParser;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Refiner;
import com.salesforce.omakase.parser.Stream;

import java.util.List;

/**
 * Parses a {@link Rule}.
 *
 * @author nmcwilliams
 * @see Rule
 */
public class RawRuleParser extends AbstractRefinableParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster, Refiner refiner) {
        stream.skipWhitepace();
        stream.collectComments();

        // if there wasn't a selector then we aren't a rule
        if (!ParserFactory.selectorGroupParser().parse(stream, broadcaster, refiner)) return false;

        // skip whitespace after selectors
        stream.skipWhitepace();

        // parse the declaration block
        stream.expect(tokenFactory().declarationBlockBegin());

        // broadcast the beginning of the declaration block event
        NotifyDeclarationBlockStart.broadcast(broadcaster);

        // parse all declarations
        do {
            stream.skipWhitepace();
            ParserFactory.rawDeclarationParser().parse(stream, broadcaster, refiner);
            stream.skipWhitepace();
        } while (stream.optionallyPresent(tokenFactory().declarationDelimiter()));

        // orphaned comments e.g., ".class{color:red; /*orphaned*/}"
        List<String> orphaned = stream.collectComments().flushComments();
        for (String comment : orphaned) {
            broadcaster.broadcast(new OrphanedComment(comment, OrphanedComment.Location.RULE));
        }

        // parse the end of the block
        stream.expect(tokenFactory().declarationBlockEnd());

        // broadcast the end of the declaration block event
        NotifyDeclarationBlockEnd.broadcast(broadcaster);

        return true;
    }

}
