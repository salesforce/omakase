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

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.AtRuleBlock;
import com.salesforce.omakase.ast.atrule.AtRuleExpression;
import com.salesforce.omakase.ast.atrule.GenericAtRuleBlock;
import com.salesforce.omakase.ast.atrule.MediaQuery;
import com.salesforce.omakase.ast.atrule.MediaQueryList;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.InterestBroadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.broadcast.SingleInterestBroadcaster;
import com.salesforce.omakase.broadcast.annotation.Refine;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.Parser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.atrule.MediaQueryListParser;
import com.salesforce.omakase.plugin.Plugin;

import java.util.Optional;

/**
 * Refines media query at-rules (@media).
 * <p>
 * In custom refiner plugins, you can reuse the logic from this class to parse declarations with the {@link
 * #delegateRefinement(AtRule, Grammar, Broadcaster)} method.
 *
 * @author nmcwilliams
 * @see MediaQueryList
 * @see MediaQueryListParser
 */
public final class MediaPlugin implements Plugin {
    private static final MediaPlugin DELEGATE = new MediaPlugin();

    /**
     * Refines {@link MediaQuery}s.
     * <p>
     * If refinement is successful the new {@link AtRuleExpression} and {@link AtRuleBlock} will be broadcasted via the given
     * {@link Broadcaster}.
     *
     * @param rule
     *     The at-rule.
     * @param grammar
     *     The grammar.
     * @param broadcaster
     *     The broadcaster.
     */
    @Refine("media")
    public void refine(AtRule rule, Grammar grammar, Broadcaster broadcaster) {
        // refine the expression (unless it was already done)
        if (!rule.expression().isPresent()) {
            // must have an expression
            if (!rule.rawExpression().isPresent()) throw new ParserException(rule, Message.MEDIA_EXPR);

            // parse the media query expression
            Source source = new Source(rule.rawExpression().get());

            InterestBroadcaster<MediaQueryList> interest = SingleInterestBroadcaster.of(MediaQueryList.class);
            interest.chain(broadcaster);

            grammar.parser().mediaQueryListParser().parse(source, grammar, interest);
            Optional<MediaQueryList> list = interest.one();

            // must have found a media query list
            if (!list.isPresent()) throw new ParserException(source, Message.DIDNT_FIND_MEDIA_LIST);

            // nothing should be left in the expression content
            if (!source.skipWhitepace().eof()) throw new ParserException(source, Message.UNPARSABLE_MEDIA, source.remaining());

            // broadcast the expression
            broadcaster.broadcast(list.get());
        }

        // refine the block (unless it was already done)
        if (!rule.block().isPresent()) {
            // must have a block
            if (!rule.rawBlock().isPresent()) throw new ParserException(rule, Message.MEDIA_BLOCK);

            Source source = new Source(rule.rawBlock().get());

            QueryableBroadcaster queryable = new QueryableBroadcaster(broadcaster);

            // parse the inner statements
            Parser ruleParser = grammar.parser().ruleParser();
            while (!source.eof()) {
                boolean matched = ruleParser.parse(source, grammar, queryable);
                source.skipWhitepace();

                // after parsing there should be nothing left in the source
                if (!matched && !source.eof()) throw new ParserException(source, Message.UNPARSABLE_MEDIA, source.remaining());
            }

            // create and add the block
            GenericAtRuleBlock genericBlock = new GenericAtRuleBlock(queryable.filter(Statement.class));

            // add orphaned comments
            genericBlock.orphanedComments(source.collectComments().flushComments());

            // broadcast the block
            broadcaster.broadcast(genericBlock);
        }
    }

    /**
     * A convenience method to delegate refinement of an {@link AtRule} to this class.
     * <p>
     * This is mainly used by {@link Refine} subscription methods.
     * <p>
     * If refinement is successful the new {@link AtRuleExpression} and {@link AtRuleBlock} will be broadcasted via the given
     * {@link Broadcaster}.
     *
     * @param atRule
     *     The AtRule.
     * @param grammar
     *     The grammar.
     * @param broadcaster
     *     The broadcaster.
     */
    public static void delegateRefinement(AtRule atRule, Grammar grammar, Broadcaster broadcaster) {
        DELEGATE.refine(atRule, grammar, broadcaster);
    }
}
