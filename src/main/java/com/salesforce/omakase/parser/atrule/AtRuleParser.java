/*
 * Copyright (c) 2017, salesforce.com, inc.
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

package com.salesforce.omakase.parser.atrule;

import java.util.List;
import java.util.Optional;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.AtRuleBlock;
import com.salesforce.omakase.ast.atrule.AtRuleExpression;
import com.salesforce.omakase.ast.extended.ConditionalAtRuleBlock;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.ConsumingBroadcaster;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.Parser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.factory.TokenFactory;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses an {@link AtRule}.
 *
 * @author nmcwilliams
 * @see AtRule
 */
public final class AtRuleParser implements Parser {

    @Override
    public boolean parse(Source source, Grammar grammar, Broadcaster broadcaster) {
        return parse(source, grammar, broadcaster, false);
    }
    
    @Override
    public boolean parse(Source source, Grammar grammar, Broadcaster broadcaster, boolean parentIsConditional) {
        TokenFactory tf = grammar.token();

        source.skipWhitepace();
        source.collectComments();

        // save off current line and column
        int startLine = source.originalLine();
        int startColumn = source.originalColumn();

        // must begin with '@'
        if (!source.optionallyPresent(Tokens.AT_RULE)) {
            return false;
        }

        // read the name
        Optional<String> name = source.readIdent();
        if (!name.isPresent()) {
            throw new ParserException(source, Message.MISSING_AT_RULE_NAME);
        } else if(name.get().equalsIgnoreCase("if") && parentIsConditional) {
            throw new ParserException(source, Message.UNEXPECTED_NESTED_CONDITIONAL_AT_RULE);
        }

        // read everything up until the end of the at-rule expression (usually a semicolon or open bracket).
        int line = source.originalLine();
        int column = source.originalColumn();
        String content = source.until(tf.atRuleExpressionEnd()).trim();
        RawSyntax expression = content.isEmpty() ? null : new RawSyntax(line, column, content);

        // skip whitespace after the expression
        source.skipWhitepace();
        List<String> comments = source.flushComments();

        RawSyntax block = null;

        // parse the termination (usually ';' or the start of an at-rule block), then parse the block
        if (!source.optionallyPresent(tf.atRuleTermination()) && tf.atRuleBlockBegin().matches(source.current())) {
            line = source.originalLine();
            column = source.originalColumn();
            content = source.chompEnclosedValue(tf.atRuleBlockBegin(), tf.atRuleBlockEnd()).trim();
            block = content.isEmpty() ? null : new RawSyntax(line, column, content);
        }

        // expression content must be present
        if (expression == null && block == null) {
            throw new ParserException(source, Message.MISSING_AT_RULE_VALUE);
        }

        source.flushComments(); // ignore any comments that were in the block, the block itself will handle them

        // create and broadcast the new rule
        AtRule atRule = new AtRule(startLine, startColumn, name.get(), expression, block);
        atRule.comments(comments);

        broadcaster.chainBroadcast(atRule,
            new ConsumingBroadcaster<>(AtRuleExpression.class, atRule::expression, t -> !atRule.isConditional()),
            new ConsumingBroadcaster<>(AtRuleBlock.class, atRule::block, t -> {
                if(atRule.isConditional()) {
                    return (t instanceof ConditionalAtRuleBlock);
                }
                return !(t instanceof ConditionalAtRuleBlock);
            }));

        return true;
    }

}
