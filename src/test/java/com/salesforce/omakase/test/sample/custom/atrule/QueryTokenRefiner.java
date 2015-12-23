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

package com.salesforce.omakase.test.sample.custom.atrule;

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.MediaQueryList;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.SingleInterestBroadcaster;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.AtRuleRefiner;
import com.salesforce.omakase.parser.refiner.MasterRefiner;
import com.salesforce.omakase.parser.refiner.Refinement;
import com.salesforce.omakase.parser.token.CompoundToken;
import com.salesforce.omakase.parser.token.Token;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The refiner handles parsing the query tokens and substituting them in applicable media queries.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("UnusedParameters")
public class QueryTokenRefiner implements AtRuleRefiner {
    private static final Pattern PATTERN = Pattern.compile("[a-zA-Z0-9_\\\\-]+");
    private final Map<String, MediaQueryList> queries = new HashMap<>();

    @Override
    public Refinement refine(AtRule atRule, Broadcaster broadcaster, MasterRefiner refiner) {
        String name = atRule.name();
        if (name.equals("query")) {
            return handleQueryTokenDefinition(atRule, broadcaster, refiner);
        } else if (name.equals("media") && atRule.rawExpression().isPresent()) {
            return handleMediaQuery(atRule, broadcaster, refiner);
        }

        return Refinement.NONE;
    }

    private Refinement handleQueryTokenDefinition(AtRule atRule, Broadcaster broadcaster, MasterRefiner refiner) {
        // ex. @query medium | all and (min-width:800px);

        // must have the expression, which is the name of the query token
        Optional<RawSyntax> expression = atRule.rawExpression();
        if (!expression.isPresent()) throw new ParserException(atRule, "missing query name");

        // read the name
        Source source = new Source(expression.get());
        Optional<String> name = source.skipWhitepace().readIdent();
        if (!name.isPresent()) throw new ParserException(source, "invalid or missing query name");

        // read the pipe
        source.skipWhitepace().expect(Pipe.INSTANCE).skipWhitepace();

        // read the expression. we don't want this actually broadcasted out since we are using it as a template to copy when
        // used later in media queries, so we don't give this broadcaster the original one
        SingleInterestBroadcaster<MediaQueryList> single = new SingleInterestBroadcaster<>(MediaQueryList.class);
        ParserFactory.mediaQueryListParser().parse(source, single, refiner);

        Optional<MediaQueryList> mediaQuery = single.broadcasted();
        if (!mediaQuery.isPresent()) throw new ParserException(source, "did not find a valid media query expression");

        // store off a reference for later
        queries.put(name.get(), mediaQuery.get());

        // we don't want this query at-rule actually output in the source
        atRule.markAsMetadataRule();

        return Refinement.FULL;
    }

    private Refinement handleMediaQuery(AtRule atRule, Broadcaster broadcaster, MasterRefiner refiner) {
        // only do a substitution if the query is a single name that we recognize
        String expression = atRule.rawExpression().get().content().trim();
        if (PATTERN.matcher(expression).matches() && queries.containsKey(expression)) {
            MediaQueryList original = queries.get(expression);

            // clone the original then broadcast it. By broadcasting, it will automatically be associated with the at-rule
            broadcaster.broadcast(original.copy());

            // return "partial" refinement since we only refined the at-rule's expression, not the block
            return Refinement.PARTIAL;
        }

        return Refinement.NONE;
    }

    private static final class Pipe implements Token {
        private static final Pipe INSTANCE = new Pipe();

        @Override
        public boolean matches(char c) {
            return c == '|';
        }

        @Override
        public String description() {
            return "pipe '|'";
        }

        @Override
        public Token or(Token other) {
            return new CompoundToken(this, other);
        }
    }
}
