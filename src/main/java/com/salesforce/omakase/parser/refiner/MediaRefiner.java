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
 * TESTME
 * <p/>
 * Refines media query at-rules (@media).
 *
 * @author nmcwilliams
 * @see MediaQueryList
 * @see MediaQueryListParser
 */
public final class MediaRefiner implements AtRuleRefinerStrategy {
    private static final String MEDIA = "media";

    @Override
    public boolean refine(AtRule atRule, Broadcaster broadcaster, Refiner refiner) {
        // TODO only refine media and block if those parts aren't refined yet.
        // must be named media
        if (!atRule.name().equals(MEDIA)) return false;

        // the at-rule must have a parent (so don't call this on a dynamically created at-rule not yet inserted into the tree)
        if (!atRule.parent().isPresent()) throw new IllegalArgumentException("atRule must have a parent specified");

        // must have an expression
        if (!atRule.rawExpression().isPresent()) throw new ParserException(atRule.line(), atRule.column(), Message.MEDIA_EXPR);

        // must have a block
        if (!atRule.rawBlock().isPresent()) throw new ParserException(atRule.line(), atRule.column(), Message.MEDIA_BLOCK);

        // parse the media query expression
        Source source = new Source(atRule.rawExpression().get());

        SingleInterestBroadcaster<MediaQueryList> single = SingleInterestBroadcaster.of(MediaQueryList.class, broadcaster);
        ParserFactory.mediaQueryListParser().parse(source, single, refiner);
        Optional<MediaQueryList> list = single.broadcasted();

        // must have found a media query list
        if (!list.isPresent()) throw new ParserException(source, Message.DIDNT_FIND_MEDIA_LIST);
        atRule.expression(list.get());

        // nothing should be left in the expression content
        source.skipWhitepace();
        if (!source.eof()) throw new ParserException(source, Message.UNPARSABLE_MEDIA, source.remaining());

        // parse the block of statements
        source = new Source(atRule.rawBlock().get());

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
        atRule.block(new GenericAtRuleBlock(atRule.parent().get(), queryable.filter(Statement.class), broadcaster));

        // once they are in the syntax collection, now we can let them be broadcasted
        queue.resume();

        return true;
    }
}
