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

import java.util.Optional;

import com.salesforce.omakase.ast.selector.Combinator;
import com.salesforce.omakase.ast.selector.CombinatorType;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.Parser;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses {@link Combinator}s.
 *
 * @author nmcwilliams
 * @see Combinator
 */
public final class CombinatorParser implements Parser {

    @Override
    public boolean parse(Source source, Grammar grammar, Broadcaster broadcaster) {
        source.collectComments(false);

        // snapshot the current state before parsing
        Source.Snapshot snapshot = source.snapshot();

        // the presence of a space *could* be a descendant selector. Or it could just be whitespace around other
        // combinators. We won't know until later.
        boolean mightBeDescendant = source.optionallyPresent(Tokens.WHITESPACE);

        if (mightBeDescendant) {
            // if we already know that a space is present, we must skip past all other whitespace
            source.skipWhitepace();
        }

        Optional<CombinatorType> type = source.optionalFromEnum(CombinatorType.class);

        // if no other combinator symbols are present, and we parsed at least one space earlier
        // then it's a descendant combinator
        if (!type.isPresent() && mightBeDescendant) {
            type = Optional.of(CombinatorType.DESCENDANT);
        }

        if (type.isPresent()) {
            // if we have parsed a combinator then we must skip past all subsequent whitespace.
            source.skipWhitepace();

            // create and broadcast the combinator
            Combinator combinator = new Combinator(snapshot.originalLine, snapshot.originalColumn, type.get());
            broadcaster.broadcast(combinator);
            return true;
        }

        return snapshot.rollback();
    }

}
