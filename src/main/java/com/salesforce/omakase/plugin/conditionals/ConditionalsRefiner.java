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

package com.salesforce.omakase.plugin.conditionals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.extended.Conditional;
import com.salesforce.omakase.ast.extended.ConditionalAtRuleBlock;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.broadcast.annotation.Refine;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.Parser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.token.Tokens;
import com.salesforce.omakase.plugin.Plugin;

/**
 * Parses {@link AtRule} objects that are {@link ConditionalAtRuleBlock}s.
 *
 * @author nmcwilliams
 * @see ConditionalAtRuleBlock
 * @see Conditionals
 */
public final class ConditionalsRefiner implements Plugin {
    private final ConditionalsConfig config;

    /**
     * Creates a new {@link ConditionalsRefiner}.
     *
     * @param config
     *     The {@link ConditionalsConfig} instance, to be passed all new {@link ConditionalAtRuleBlock} instances.
     */
    public ConditionalsRefiner(ConditionalsConfig config) {
        this.config = config;
    }

    /**
     * The refiner method.
     *
     * @param atRule
     *     The atRule to refine.
     * @param grammar
     *     The grammar.
     * @param broadcaster
     *     The broadcaster.
     */
    @Refine("if")
    public void refine(AtRule atRule, Grammar grammar, Broadcaster broadcaster) {
        // the at-rule must have an expression
        if (!atRule.rawExpression().isPresent()) {
            throw new ParserException(atRule, Message.MISSING_CONDITIONAL_EXPRESSION);
        }

        // the at-rule must have a block
        if (!atRule.rawBlock().isPresent()) {
            throw new ParserException(atRule, Message.MISSING_CONDITIONAL_BLOCK);
        }
        
        atRule.setConditional(true);

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
        if (!condition.isPresent()) {
            throw new ParserException(source, Message.CONDITION_NAME);
        }
        conditionals.add(new Conditional(condition.get().toLowerCase(), isNegated));

        // find extra conditions after logical ORs
        source.skipWhitepace();
        while (source.optionallyPresent(Tokens.PIPE)) {
            source.expect(Tokens.PIPE);
            source.skipWhitepace();

            isNegated = source.optionallyPresent(Tokens.EXCLAMATION);
            condition = source.readIdent();
            if (!condition.isPresent()) {
                throw new ParserException(source, Message.CONDITION_NAME);
            }
            conditionals.add(new Conditional(condition.get().toLowerCase(), isNegated));

            source.skipWhitepace();
        }

        source.expect(Tokens.CLOSE_PAREN);

        // nothing should be left
        source.skipWhitepace();
        if (!source.eof()) {
            throw new ParserException(source, Message.UNPARSABLE_CONDITIONAL_CONTENT, source.remaining());
        }

        // setup stuff for parsing inner statements
        source = new Source(atRule.rawBlock().get());
        QueryableBroadcaster queryable = new QueryableBroadcaster(broadcaster);

        // parse the inner statements
        Parser rule = grammar.parser().statementParser();
        while (!source.eof()) {
            boolean matched = rule.parse(source, grammar, queryable, true);
            source.skipWhitepace();

            // after parsing there should be nothing left in the source
            if (!matched && !source.eof()) {
                throw new ParserException(source, Message.UNPARSABLE_CONDITIONAL_CONTENT, source.remaining());
            }
        }
        
        // create the new conditional node and broadcast it
        ConditionalAtRuleBlock block = new ConditionalAtRuleBlock(atRule.line(), atRule.column(), conditionals,
            queryable.filter(Statement.class), config);
        broadcaster.broadcast(block);

        // don't print out the name of the at-rule (the '@if' part. the block will print it out when it's
        // needed in passthrough mode. A little wonky but heh...)
        atRule.shouldWriteName(false);
    }
}
