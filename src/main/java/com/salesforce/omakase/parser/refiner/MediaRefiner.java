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
import com.salesforce.omakase.ast.atrule.MediaQueryList;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.broadcast.SingleInterestBroadcaster;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.atrule.MediaQueryListParser;

/**
 * Refines media query at-rules (@media).
 *
 * @author nmcwilliams
 * @see MediaQueryList
 * @see MediaQueryListParser
 */
public final class MediaRefiner implements AtRuleRefiner {
    private static final String MEDIA = "media";

    @Override
    public Refinement refine(AtRule rule, Broadcaster broadcaster, MasterRefiner refiner) {
        if (!rule.name().equals(MEDIA)) return Refinement.NONE;

        // refine the expression (unless it was already done)
        if (!rule.hasRefinedExpression()) {
            // must have an expression
            if (!rule.rawExpression().isPresent()) throw new ParserException(rule, Message.MEDIA_EXPR);

            // parse the media query expression
            Source source = new Source(rule.rawExpression().get());

            SingleInterestBroadcaster<MediaQueryList> single = SingleInterestBroadcaster.of(MediaQueryList.class, broadcaster);
            ParserFactory.mediaQueryListParser().parse(source, single, refiner);
            Optional<MediaQueryList> list = single.broadcasted();

            // must have found a media query list
            if (!list.isPresent()) throw new ParserException(source, Message.DIDNT_FIND_MEDIA_LIST);

            // nothing should be left in the expression content
            if (!source.skipWhitepace().eof()) throw new ParserException(source, Message.UNPARSABLE_MEDIA, source.remaining());
        }

        // refine the block (unless it was already done)
        if (!rule.hasRefinedBlock()) {
            // must have a block
            if (!rule.rawBlock().isPresent()) throw new ParserException(rule, Message.MEDIA_BLOCK);

            Source source = new Source(rule.rawBlock().get());

            QueryableBroadcaster queryable = new QueryableBroadcaster(broadcaster);

            // parse the inner statements
            while (!source.eof()) {
                boolean matched = ParserFactory.ruleParser().parse(source, queryable, refiner);
                source.skipWhitepace();

                // after parsing there should be nothing left in the source
                if (!matched && !source.eof()) throw new ParserException(source, Message.UNPARSABLE_MEDIA, source.remaining());
            }

            // create and add the block
            GenericAtRuleBlock genericBlock = new GenericAtRuleBlock(queryable.filter(Statement.class), broadcaster);
            broadcaster.broadcast(genericBlock);

            // add orphaned comments
            genericBlock.orphanedComments(source.collectComments().flushComments());
        }

        return Refinement.FULL;
    }
}
