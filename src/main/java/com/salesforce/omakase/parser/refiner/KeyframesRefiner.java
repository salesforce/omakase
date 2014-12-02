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
    public boolean refine(AtRule atRule, Broadcaster broadcaster, MasterRefiner refiner) {
        // @keyframes might be prefixed
        String name = Prefixes.unprefixed(atRule.name());
        if (!name.equals(KEYFRAMES)) return false;

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

        atRule.expression(new GenericAtRuleExpression(ident.get()));

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

        // create and add the block
        atRule.block(new GenericAtRuleBlock(queryable.filter(Statement.class), broadcaster));

        // once they are in the syntax collection, now we can let them be broadcasted
        queue.resume();

        return true;
    }
}
