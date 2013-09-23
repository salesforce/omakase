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

package com.salesforce.omakase.parser.selector;

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.selector.Combinator;
import com.salesforce.omakase.ast.selector.CombinatorType;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses {@link Combinator}s.
 *
 * @author nmcwilliams
 * @see Combinator
 */
public class CombinatorParser extends AbstractParser {
    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        stream.collectComments(false);

        // snapshot the current state before parsing
        Stream.Snapshot snapshot = stream.snapshot();

        // the presence of a space *could* be a descendant selector. Or it could just be whitespace around other
        // combinators. We won't know until later.
        boolean mightBeDescendant = stream.optionallyPresent(Tokens.WHITESPACE);

        if (mightBeDescendant) {
            // if we already know that a space is present, we must skip past all other whitespace
            stream.skipWhitepace();
        }

        Optional<CombinatorType> type = stream.optionalFromEnum(CombinatorType.class);

        // if no other combinator symbols are present, and we parsed at least one space earlier
        // then it's a descendant combinator
        if (!type.isPresent() && mightBeDescendant) {
            type = Optional.of(CombinatorType.DESCENDANT);
        }

        if (type.isPresent()) {
            // if we have parsed a combinator then we must skip past all subsequent whitespace.
            stream.skipWhitepace();

            // create and broadcast the combinator
            Combinator combinator = new Combinator(snapshot.line, snapshot.column, type.get());
            broadcaster.broadcast(combinator);
            return true;
        }

        return snapshot.rollback();
    }
}
