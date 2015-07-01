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

import com.google.common.base.Optional;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.extended.Conditional;
import com.salesforce.omakase.ast.extended.ConditionalAtRuleBlock;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.AtRuleRefiner;
import com.salesforce.omakase.parser.refiner.MasterRefiner;
import com.salesforce.omakase.parser.refiner.Refinement;
import com.salesforce.omakase.parser.token.Tokens;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses {@link AtRule} objects that are {@link ConditionalAtRuleBlock}s.
 *
 * @author nmcwilliams
 * @see ConditionalAtRuleBlock
 * @see Conditionals
 */
public final class ConditionalsRefiner implements AtRuleRefiner {
    private static final String IF = "if";
    private final ConditionalsConfig config;

    /**
     * Creates a new {@link ConditionalsRefiner} instance.
     *
     * @param config
     *     The {@link ConditionalsConfig} instance, to be passed all new {@link ConditionalAtRuleBlock} instances.
     */
    public ConditionalsRefiner(ConditionalsConfig config) {
        this.config = config;
    }

    @Override
    public Refinement refine(AtRule atRule, Broadcaster broadcaster, MasterRefiner refiner) {
        // must be named  "if"
        if (!atRule.name().equals(IF)) return Refinement.NONE;

        // the at-rule must have an expression
        if (!atRule.rawExpression().isPresent()) {
            throw new ParserException(atRule, Message.MISSING_CONDITIONAL_EXPRESSION);
        }

        // the at-rule must have a block
        if (!atRule.rawBlock().isPresent()) {
            throw new ParserException(atRule, Message.MISSING_CONDITIONAL_BLOCK);
        }

        // parse the condition(s), lower-case for comparison purposes. Conditions can be preceded be a logical negation operator
        // (!) and multiple conditions can be separated by logical OR operators (||)

        List<Conditional> conditionals = new ArrayList<>(3); // if changing from a list check unit tests

        RawSyntax rawExpression = atRule.rawExpression().get();
        Source source = new Source(rawExpression.content(), rawExpression.line(), rawExpression.column(), false);

        source.expect(Tokens.OPEN_PAREN);
        source.skipWhitepace();

        // find the first condition
        boolean isNegated = source.optionallyPresent(Tokens.EXCLAMATION);
        Optional<String> condition = source.readIdent();
        if (!condition.isPresent()) throw new ParserException(source, Message.CONDITION_NAME);
        conditionals.add(new Conditional(condition.get().toLowerCase(), isNegated));

        // find extra conditions after logical ORs
        source.skipWhitepace();
        while (source.optionallyPresent(Tokens.PIPE)) {
            source.expect(Tokens.PIPE);
            source.skipWhitepace();

            isNegated = source.optionallyPresent(Tokens.EXCLAMATION);
            condition = source.readIdent();
            if (!condition.isPresent()) throw new ParserException(source, Message.CONDITION_NAME);
            conditionals.add(new Conditional(condition.get().toLowerCase(), isNegated));

            source.skipWhitepace();
        }

        source.expect(Tokens.CLOSE_PAREN);

        // nothing should be left
        source.skipWhitepace();
        if (!source.eof()) throw new ParserException(source, Message.UNPARSABLE_CONDITIONAL_CONTENT, source.remaining());

        // setup stuff for parsing inner statements
        source = new Source(atRule.rawBlock().get());
        QueryableBroadcaster queryable = new QueryableBroadcaster(broadcaster);

        // parse the inner statements
        while (!source.eof()) {
            boolean matched = ParserFactory.ruleParser().parse(source, queryable, refiner);
            source.skipWhitepace();

            // after parsing there should be nothing left in the source
            if (!matched && !source.eof()) {
                throw new ParserException(source, Message.UNPARSABLE_CONDITIONAL_CONTENT, source.remaining());
            }
        }

        // create the new conditional node and broadcast it
        ConditionalAtRuleBlock block = new ConditionalAtRuleBlock(atRule.line(), atRule.column(), conditionals,
            queryable.filter(Statement.class), config, broadcaster);
        broadcaster.broadcast(block);

        // don't print out the name of the at-rule
        atRule.shouldWriteName(false);

        return Refinement.FULL;
    }
}
