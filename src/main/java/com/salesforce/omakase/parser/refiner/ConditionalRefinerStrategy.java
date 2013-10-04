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
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.collection.StandardSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.extended.ConditionalAtRuleBlock;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.broadcast.QueuingBroadcaster;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.token.Tokens;
import com.salesforce.omakase.plugin.basic.Conditionals;

import java.util.Set;

/**
 * Parses {@link AtRule} objects that are {@link ConditionalAtRuleBlock}s.
 *
 * @author nmcwilliams
 * @see ConditionalAtRuleBlock
 * @see Conditionals
 */
public final class ConditionalRefinerStrategy implements RefinerStrategy {
    private static final String IF = "if";
    private final Set<String> trueConditions;

    /**
     * Creates a new {@link ConditionalRefinerStrategy} instance with the given set of true conditions.
     *
     * @param trueConditions
     *     Set containing the strings that should evaluate to "true" in a {@link ConditionalAtRuleBlock}.
     */
    public ConditionalRefinerStrategy(Set<String> trueConditions) {
        this.trueConditions = trueConditions;
    }

    @Override
    public boolean refineAtRule(AtRule atRule, Broadcaster broadcaster, Refiner refiner) {
        // must be named  "if"
        if (!atRule.name().equals(IF)) return false;

        // the at-rule must have a parent (so don't call this on a dynamically created at-rule not yet inserted into the tree)
        if (!atRule.parent().isPresent()) throw new IllegalArgumentException("atRule must have a parent specified");

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
        RawSyntax rawBlock = atRule.rawBlock().get();
        source = new Source(rawBlock.content(), rawBlock.line(), rawBlock.column());

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

        // add all parsed statements into a new syntax collection
        SyntaxCollection<Stylesheet, Statement> statements = StandardSyntaxCollection.create(atRule.parent().get());
        statements.appendAll(queryable.filter(Statement.class));

        // once they are in the syntax collection, now we can let them be broadcasted
        queue.resume();

        // create the new conditional node
        ConditionalAtRuleBlock block = new ConditionalAtRuleBlock(trueConditions, condition, statements);

        // set and broadcast it
        atRule.block(block);
        broadcaster.broadcast(block);
        return true;
    }

    @Override
    public boolean refineSelector(Selector selector, Broadcaster broadcaster, Refiner refiner) {
        return false;
    }

    @Override
    public boolean refineDeclaration(Declaration declaration, Broadcaster broadcaster, Refiner refiner) {
        return false;
    }
}
