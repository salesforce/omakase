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

package com.salesforce.omakase.parser.refiner;

import com.google.common.base.Optional;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.GenericAtRuleBlock;
import com.salesforce.omakase.ast.atrule.GenericAtRuleExpression;
import com.salesforce.omakase.ast.selector.KeyframeSelector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.broadcast.QueuingBroadcaster;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.atrule.KeyframeSelectorParser;
import com.salesforce.omakase.parser.atrule.KeyframeSelectorSequenceParser;
import com.salesforce.omakase.util.Prefixes;

/**
 * Refines keyframes at-rules (@keyframes).
 *
 * @author nmcwilliams
 * @see KeyframeSelector
 * @see KeyframeSelectorParser
 * @see KeyframeSelectorSequenceParser
 */
public final class KeyframesRefiner implements AtRuleRefiner {
    private static final String KEYFRAMES = "keyframes";

    @Override
    public Refinement refine(AtRule atRule, Broadcaster broadcaster, MasterRefiner refiner) {
        // @keyframes might be prefixed
        String name = Prefixes.unprefixed(atRule.name());
        if (!name.equals(KEYFRAMES)) return Refinement.NONE;

        // must have a keyframes name
        if (!atRule.rawExpression().isPresent()) {
            throw new ParserException(atRule, Message.KEYFRAME_NAME);
        }

        // parse the keyframes name
        Source source = new Source(atRule.rawExpression().get());

        // name should be a proper ident
        Optional<String> ident = source.readIdent();
        if (!ident.isPresent()) {
            throw new ParserException(atRule, Message.KEYFRAME_NAME);
        }

        // nothing should be left in the expression content
        if (!source.skipWhitepace().eof()) {
            throw new ParserException(source, Message.UNEXPECTED_KEYFRAME_NAME, source.remaining());
        }

        // create and broadcast the expression
        broadcaster.broadcast(new GenericAtRuleExpression(ident.get()));

        // must have a block
        if (!atRule.rawBlock().isPresent()) {
            throw new ParserException(atRule, Message.MISSING_KEYFRAMES_BLOCK);
        }

        // parse the block
        source = new Source(atRule.rawBlock().get());

        QueuingBroadcaster queue = new QueuingBroadcaster(broadcaster).pause();
        QueryableBroadcaster queryable = new QueryableBroadcaster(queue);

        // parse the inner statements
        while (!source.eof()) {
            boolean matched = ParserFactory.keyframeRuleParser().parse(source, queryable, refiner);
            source.skipWhitepace();

            // after parsing there should be nothing left in the source
            if (!matched && !source.eof()) throw new ParserException(source, Message.UNPARSABLE_KEYFRAMES, source.remaining());
        }

        // create and broadcast the block
        broadcaster.broadcast(new GenericAtRuleBlock(queryable.filter(Statement.class), broadcaster));

        // once they are in the syntax collection, now we can let them be broadcasted
        queue.resume();

        return Refinement.FULL;
    }
}
