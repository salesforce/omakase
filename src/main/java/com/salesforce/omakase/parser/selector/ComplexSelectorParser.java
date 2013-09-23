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

import com.google.common.collect.Iterables;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.OrphanedComment;
import com.salesforce.omakase.ast.selector.Combinator;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueuingBroadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Parser;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.raw.RawSelectorParser;

import java.util.List;

import static com.salesforce.omakase.ast.selector.SelectorPartType.DESCENDANT_COMBINATOR;

/**
 * Parses refined {@link Selector}s, as opposed to {@link RawSelectorParser}.
 * <p/>
 * This attempts to conform to Selectors level 3 (http://www.w3.org/TR/css3-selectors). Yes, attempts, because the spec is
 * inconsistent, contradictory, and malformed.
 *
 * @author nmcwilliams
 * @see Selector
 */
public class ComplexSelectorParser extends AbstractParser {
    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        stream.skipWhitepace();

        // snapshot the current state before parsing
        Stream.Snapshot snapshot = stream.snapshot();

        // setup inner parsers
        Parser combinator = ParserFactory.combinatorParser();
        Parser repeatableSelector = ParserFactory.repeatableSelector();
        Parser typeOrUniversal = ParserFactory.typeOrUniversaleSelectorParser();

        // we queue the broadcasts because we don't want the last unit to be a trailing descendant combinator.
        QueuingBroadcaster queue = new QueuingBroadcaster(broadcaster);
        queue.pause(); // don't actually broadcast anything until we can do some checking later

        boolean matchedAnything = false;
        boolean matchedThisTime = false;

        do {
            matchedThisTime = false;

            // try parsing a universal or type selector
            if (typeOrUniversal.parse(stream, queue)) {
                matchedAnything = true;
                matchedThisTime = true;
            }

            // parse remaining selectors in the sequence
            while (repeatableSelector.parse(stream, queue)) {
                matchedAnything = true;
                matchedThisTime = true;
            }

            // check for trailing combinators. If it is a descendant combinator then it was just an extra space so remove it.
            if (matchedAnything && !matchedThisTime) {
                // find the last combinator
                Combinator lastCombinator = Iterables.getLast(Iterables.filter(queue.all(), Combinator.class));
                if (lastCombinator.type() == DESCENDANT_COMBINATOR) {
                    queue.reject(lastCombinator);
                } else {
                    snapshot.rollback(Message.TRAILING_COMBINATOR, lastCombinator.type());
                }
            } else {
                // so that if there is a trailing combinator error the stream points to the right location
                snapshot = stream.snapshot();
            }
        } while (combinator.parse(stream, queue));

        // check for known possible errors
        if (!stream.eof()) {
            snapshot = stream.snapshot();
            if (typeOrUniversal.parse(stream, queue)) {
                snapshot.rollback(Message.NAME_SELECTORS_NOT_ALLOWED);
            }
        }

        // orphaned comments, e.g., ".class, #id /*orphaned*/ {}"
        List<String> orphaned = stream.collectComments().flushComments();
        for (String comment : orphaned) {
            queue.broadcast(new OrphanedComment(comment, OrphanedComment.Location.SELECTOR));
        }

        // we're good, send out all queued broadcasts
        queue.resume();

        return matchedAnything;
    }
}
