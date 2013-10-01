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
import com.salesforce.omakase.parser.RefinableParser;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.token.Tokens;

import java.util.Set;

/**
 * TODO description
 *
 * @author nmcwilliams
 */
public class ConditionalRefinableStrategy implements RefinableStrategy {
    private static final String IF = "if";
    private final Set<String> trueConditions;

    public ConditionalRefinableStrategy(Set<String> trueConditions) {
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
        Stream stream = new Stream(rawExpression.content(), rawExpression.line(), rawExpression.column(), false);
        String condition = stream.chompEnclosedValue(Tokens.OPEN_PAREN, Tokens.CLOSE_PAREN).trim().toLowerCase();

        // nothing should be left
        stream.skipWhitepace();
        if (!stream.eof()) throw new ParserException(stream, Message.UNPARSABLE_CONDITIONAL_CONTENT, stream.remaining());

        // setup stuff for parsing inner statements
        RawSyntax rawBlock = atRule.rawBlock().get();
        stream = new Stream(rawBlock.content(), rawBlock.line(), rawBlock.column());

        QueuingBroadcaster queue = new QueuingBroadcaster(broadcaster);
        QueryableBroadcaster queryable = new QueryableBroadcaster(queue);
        RefinableParser rule = ParserFactory.ruleParser();

        // we want to hold emitting statements until they get shuffled into a syntax collection. This is so that any plugins
        // that depend on order (appending, prepending, etc...) will work.
        queue.pause();

        // parse the inner statements
        while (!stream.eof()) {
            boolean matched = rule.parse(stream, queryable, refiner);
            stream.skipWhitepace();

            // after parsing there should be nothing left in the stream
            if (!matched && !stream.eof()) {
                throw new ParserException(stream, Message.UNPARSABLE_CONDITIONAL_CONTENT, stream.remaining());
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
