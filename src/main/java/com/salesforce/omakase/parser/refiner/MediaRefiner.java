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

package com.salesforce.omakase.parser.refiner;

import com.google.common.base.Optional;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.GenericAtRuleBlock;
import com.salesforce.omakase.ast.atrule.MediaQueryList;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.broadcast.QueuingBroadcaster;
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
    public boolean refine(AtRule rule, Broadcaster broadcaster, MasterRefiner refiner) {
        if (!rule.name().equals(MEDIA)) return false;

        boolean refinedExpression = false;
        boolean refinedBlock = false;

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
            rule.expression(list.get());

            // nothing should be left in the expression content
            if (!source.skipWhitepace().eof()) throw new ParserException(source, Message.UNPARSABLE_MEDIA, source.remaining());

            refinedExpression = true;
        }

        // refine the block (unless it was already done)
        if (!rule.hasRefinedBlock()) {
            // must have a block
            if (!rule.rawBlock().isPresent()) throw new ParserException(rule, Message.MEDIA_BLOCK);

            Source source = new Source(rule.rawBlock().get());

            // we want to hold off emitting statements until they get shuffled into a syntax collection. This is so that any plugins
            // that depend on order (appending, prepending, etc...) will work.
            QueuingBroadcaster queue = new QueuingBroadcaster(broadcaster).pause();
            QueryableBroadcaster queryable = new QueryableBroadcaster(queue);

            // parse the inner statements
            while (!source.eof()) {
                boolean matched = ParserFactory.ruleParser().parse(source, queryable, refiner);
                source.skipWhitepace();

                // after parsing there should be nothing left in the source
                if (!matched && !source.eof()) throw new ParserException(source, Message.UNPARSABLE_MEDIA, source.remaining());
            }

            // create and add the block
            GenericAtRuleBlock genericBlock = new GenericAtRuleBlock(queryable.filter(Statement.class), broadcaster);
            rule.block(genericBlock);

            // add orphaned comments
            genericBlock.orphanedComments(source.collectComments().flushComments());

            // once they are in the syntax collection, now we can let them be broadcasted
            queue.resume();

            refinedBlock = true;
        }

        return refinedExpression || refinedBlock;
    }
}
