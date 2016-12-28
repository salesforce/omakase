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

package com.salesforce.omakase.parser.selector;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.selector.Combinator;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.InterestBroadcaster;
import com.salesforce.omakase.broadcast.QueuingBroadcaster;
import com.salesforce.omakase.broadcast.SingleInterestBroadcaster;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.Parser;
import com.salesforce.omakase.parser.Source;

import java.util.Optional;

import static com.salesforce.omakase.ast.selector.SelectorPartType.DESCENDANT_COMBINATOR;

/**
 * Parses refined {@link Selector}s, as opposed to {@link SelectorParser}.
 * <p>
 * This attempts to conform to Selectors level 3 (http://www.w3.org/TR/css3-selectors). Yes, attempts, because the spec is
 * inconsistent, contradictory, and malformed.
 *
 * @author nmcwilliams
 * @see Selector
 */
public final class ComplexSelectorParser implements Parser {

    @Override
    public boolean parse(Source source, Grammar grammar, Broadcaster broadcaster) {
        source.skipWhitepace();

        // snapshot the current state before parsing
        Source.Snapshot snapshot = source.snapshot();

        // setup inner parsers
        Parser combinator = grammar.parser().combinatorParser();
        Parser repeatableSelector = grammar.parser().repeatableSelector();
        Parser typeOrUniversal = grammar.parser().typeOrUniversaleSelectorParser();

        // we queue the broadcasts because we don't want the last unit to be a trailing descendant combinator.
        InterestBroadcaster<Combinator> interest = SingleInterestBroadcaster.of(Combinator.class);
        QueuingBroadcaster queue = interest.chain(new QueuingBroadcaster(broadcaster).pause());

        boolean matchedAnything = false;
        boolean matchedThisTime;

        do {
            matchedThisTime = false;

            // try parsing a universal or type selector
            if (typeOrUniversal.parse(source, grammar, queue)) {
                matchedAnything = true;
                matchedThisTime = true;
            }

            // parse remaining selectors in the sequence
            while (repeatableSelector.parse(source, grammar, queue)) {
                matchedAnything = true;
                matchedThisTime = true;
            }

            // check for trailing combinators. If it is a descendant combinator then it was just an extra space so remove it.
            if (matchedAnything && !matchedThisTime) {
                // find the last combinator
                Optional<Combinator> lastCombinator = interest.one();
                if (lastCombinator.isPresent()) {
                    if (lastCombinator.get().type() == DESCENDANT_COMBINATOR) {
                        queue.reject(lastCombinator.get());
                    } else {
                        snapshot.rollback(Message.TRAILING_COMBINATOR, lastCombinator.get().type());
                    }
                }
            } else {
                // so that if there is a trailing combinator error the source points to the right location
                snapshot = source.snapshot();
            }
        } while (combinator.parse(source, grammar, interest.reset()));

        // check for known possible errors
        if (!source.eof()) {
            snapshot = source.snapshot();
            if (typeOrUniversal.parse(source, grammar, queue)) {
                snapshot.rollback(Message.NAME_SELECTORS_NOT_ALLOWED);
            }
        }

        // we're good, send out all queued broadcasts
        queue.resume();

        return matchedAnything;
    }

}
