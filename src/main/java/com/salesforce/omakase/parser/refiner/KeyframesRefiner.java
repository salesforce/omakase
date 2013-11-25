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

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.GenericAtRuleBlock;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.broadcast.QueuingBroadcaster;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.util.Prefixes;

import static com.salesforce.omakase.util.Prefixes.PrefixPair;

/**
 * TESTME
 * <p/>
 * TODO description
 *
 * @author nmcwilliams
 */
public class KeyframesRefiner implements AtRuleRefiner {
    private static final String KEYFRAMES = "keyframes";

    @Override
    public boolean refine(AtRule atRule, Broadcaster broadcaster, Refiner refiner) {
        String name = atRule.name();

        if (name.charAt(0) == '-') {
            PrefixPair pair = Prefixes.splitPrefix(name);
            if (pair.prefix().isPresent()) {
                name = pair.unprefixed();
            }
        }

        if (!name.equals(KEYFRAMES)) return false;

        // must have a keyframes name
        if (!atRule.rawExpression().isPresent()) {
            throw new RuntimeException("e");
            //throw new ParserException(atRule.line(), atRule.column(), Message.KEYFRAMES_NAME);
        }

        // parse the keyframes name
        Source source = new Source(atRule.rawExpression().get());

        // name should be a proper ident
        if (!source.readIdent().isPresent()) throw new RuntimeException("TODO: contains an invalid name");

        // nothing should be left in the expression content
        if (!source.skipWhitepace().eof()) throw new ParserException(source, Message.UNPARSABLE_MEDIA, source.remaining());

        // must have a block
        if (!atRule.rawBlock().isPresent()) {
            throw new RuntimeException("e2");
            //throw new ParserException(rule.line(), rule.column(), Message.MEDIA_BLOCK);
        }

        source = new Source(atRule.rawBlock().get());

        QueuingBroadcaster queue = new QueuingBroadcaster(broadcaster).pause();
        QueryableBroadcaster queryable = new QueryableBroadcaster(queue);

        // parse the inner statements
        while (!source.eof()) {
            boolean matched = ParserFactory.ruleParser().parse(source, queryable, refiner);
            source.skipWhitepace();

            // after parsing there should be nothing left in the source
            if (!matched && !source.eof()) {
                throw new RuntimeException("e");
                // throw new ParserException(source, Message.UNPARSABLE_MEDIA, source.remaining());
            }
        }

        // create and add the block
        atRule.block(new GenericAtRuleBlock(queryable.filter(Statement.class), broadcaster));

        // once they are in the syntax collection, now we can let them be broadcasted
        queue.resume();

        return true;
    }
}
