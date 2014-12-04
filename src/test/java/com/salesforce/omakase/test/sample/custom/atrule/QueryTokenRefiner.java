/*
 * Copyright (C) 2014 salesforce.com, inc.
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
