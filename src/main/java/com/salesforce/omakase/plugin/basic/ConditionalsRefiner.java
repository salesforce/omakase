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

package com.salesforce.omakase.plugin.basic;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.extended.ConditionalAtRuleBlock;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.broadcast.QueuingBroadcaster;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.AtRuleRefiner;
import com.salesforce.omakase.parser.refiner.Refiner;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses {@link AtRule} objects that are {@link ConditionalAtRuleBlock}s.
 *
 * @author nmcwilliams
 * @see ConditionalAtRuleBlock
 * @see Conditionals
 */
public final class ConditionalsRefiner implements AtRuleRefiner {
    private static final String IF = "if";
    private final ConditionalsManager manager;

    /**
     * Creates a new {@link ConditionalsRefiner} instance.
     *
     * @param manager
     *     The {@link ConditionalsManager} instance, to be passed all new {@link ConditionalAtRuleBlock} instances.
     */
    public ConditionalsRefiner(ConditionalsManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean refine(AtRule atRule, Broadcaster broadcaster, Refiner refiner) {
        // must be named  "if"
        if (!atRule.name().equals(IF)) return false;

        // the at-rule must have an expression
        if (!atRule.rawExpression().isPresent()) {
            throw new ParserException(atRule.line(), atRule.column(), Message.MISSING_CONDITIONAL_EXPRESSION);
        }

        // the at-rule must have a block
        if (!atRule.rawBlock().isPresent()) {
            throw new ParserException(atRule.line(), atRule.column(), Message.MISSING_CONDITIONAL_BLOCK);
        }

        // parse the condition, lower-case for comparison purposes
        RawSyntax rawExpression = atRule.rawExpression().get();
        Source source = new Source(rawExpression.content(), rawExpression.line(), rawExpression.column(), false);
        String condition = source.chompEnclosedValue(Tokens.OPEN_PAREN, Tokens.CLOSE_PAREN).trim().toLowerCase();

        // nothing should be left
        source.skipWhitepace();
        if (!source.eof()) throw new ParserException(source, Message.UNPARSABLE_CONDITIONAL_CONTENT, source.remaining());

        // setup stuff for parsing inner statements
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
            if (!matched && !source.eof()) {
                throw new ParserException(source, Message.UNPARSABLE_CONDITIONAL_CONTENT, source.remaining());
            }
        }

        // once they are in the syntax collection, now we can let them be broadcasted
        queue.resume();

        // create the new conditional node
        ConditionalAtRuleBlock block = new ConditionalAtRuleBlock(atRule.line(), atRule.column(), manager, condition,
            queryable.filter(Statement.class), broadcaster);

        // set and broadcast it
        atRule.block(block);
        broadcaster.broadcast(block);

        // don't print out the name of the at-rule
        atRule.shouldWriteName(false);

        return true;
    }
}
