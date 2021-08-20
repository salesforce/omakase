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

package com.salesforce.omakase.plugin.syntax;

import java.util.Optional;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.GenericAtRuleBlock;
import com.salesforce.omakase.ast.atrule.GenericAtRuleExpression;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.broadcast.annotation.Refine;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.Parser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.util.Prefixes;

/**
 * Refines keyframes at-rules (@keyframes).
 *
 * @author nmcwilliams
 * @see KeyframeSelector
 * @see KeyframeSelectorParser
 * @see KeyframeSelectorSequenceParser
 */
public final class KeyframesPlugin implements Plugin {

    /**
     * Refines the keyframes at-rule.
     * <p>
     * If refinement is successful the new {@link AtRuleExpression} and {@link AtRuleBlock} will be broadcasted via the given
     * {@link Broadcaster}.
     *
     * @param atRule
     *     The atRule to refine.
     * @param grammar
     *     The grammar.
     * @param broadcaster
     *     The broadcaster.
     */
    @Refine
    public void refine(AtRule atRule, Grammar grammar, Broadcaster broadcaster) {
        // @keyframes might be prefixed
        String name = Prefixes.unprefixed(atRule.name());
        if (!name.equals("keyframes")) return;

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

        // create and broadcast the expression
        GenericAtRuleExpression expression = new GenericAtRuleExpression(ident.get());
        broadcaster.broadcast(expression);

        // must have a block
        if (!atRule.rawBlock().isPresent()) {
            throw new ParserException(atRule, Message.MISSING_KEYFRAMES_BLOCK);
        }

        // parse the block
        source = new Source(atRule.rawBlock().get());

        QueryableBroadcaster queryable = new QueryableBroadcaster(broadcaster);

        // parse the inner statements
        Parser keyframeRuleParser = grammar.parser().keyframeRuleParser();
        while (!source.eof()) {
            boolean matched = keyframeRuleParser.parse(source, grammar, queryable);
            source.skipWhitepace();

            // after parsing there should be nothing left in the source
            if (!matched && !source.eof()) throw new ParserException(source, Message.UNPARSABLE_KEYFRAMES, source.remaining());
        }

        // create and broadcast the block
        GenericAtRuleBlock block = new GenericAtRuleBlock(queryable.filter(Statement.class));
        broadcaster.broadcast(block);
    }
}
