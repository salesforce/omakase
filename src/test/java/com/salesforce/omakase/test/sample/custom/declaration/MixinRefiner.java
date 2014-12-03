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

package com.salesforce.omakase.test.sample.custom.declaration;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.AtRuleRefiner;
import com.salesforce.omakase.parser.refiner.DeclarationRefiner;
import com.salesforce.omakase.parser.refiner.MasterRefiner;
import com.salesforce.omakase.parser.refiner.Refinement;
import com.salesforce.omakase.parser.token.Tokens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This handles refining the mixins and the declarations that reference them.
 *
 * @author nmcwilliams
 */
public class MixinRefiner implements AtRuleRefiner, DeclarationRefiner {
    private final Map<String, Mixin> mixins = new HashMap<>();

    /** handles mixin definitions (at-rule) */
    @Override
    public Refinement refine(AtRule atRule, Broadcaster broadcaster, MasterRefiner refiner) {
        if (atRule.name().equals("mixin")) {
            // must have an expression, which contains the name and params definition
            if (!atRule.rawExpression().isPresent()) throw new ParserException(atRule, "missing mixin name and args");

            // parse the expression
            Source source = new Source(atRule.rawExpression().get());
            source.skipWhitepace();

            // parse the name
            Optional<String> name = source.readIdent();
            if (!name.isPresent()) throw new ParserException(source, "Expected to find a valid mixin name");

            // parse the params
            List<String> params = new ArrayList<>();
            source.expect(Tokens.OPEN_PAREN);

            Optional<String> param = Optional.absent();
            do {
                param = source.skipWhitepace().readIdent();
                if (param.isPresent()) {
                    params.add(param.get());
                }
            } while (param.isPresent() && source.skipWhitepace().optionallyPresent(Tokens.COMMA));

            source.expect(Tokens.CLOSE_PAREN);

            // should be nothing left in the expression
            if (!source.skipWhitepace().eof()) throw new ParserException(source, "Unexpected content in mixin def");

            // must have the at-rule block containing the declarations
            if (!atRule.rawBlock().isPresent()) throw new ParserException(atRule, "missing mixin block");

            // parse the template declarations
            source = new Source(atRule.rawBlock().get());

            // we don't want the template declarations broadcasted past this point, so we use a custom broadcaster that doesn't
            // wrap the original one
            QueryableBroadcaster queryable = new QueryableBroadcaster();
            ParserFactory.rawDeclarationSequenceParser().parse(source, queryable, refiner);
            Iterable<Declaration> declarations = queryable.filter(Declaration.class);

            // we don't want anything from the at-rule to actually be written out
            atRule.markAsMetadataRule();

            // create the new mixin instance (a custom at-rule expression)
            Mixin mixin = new Mixin(name.get(), params, declarations);

            // save off a reference for later
            String key = name.get() + params.size();
            mixins.put(key, mixin);

            // broadcast the mixin (and by doing so it will automatically be associated with the at-rule)
            broadcaster.broadcast(mixin);

            return Refinement.FULL;
        }
        return Refinement.NONE;
    }

    /** handles mixin references within rules, i.e., the thing that starts with "+" */
    @Override
    public Refinement refine(Declaration declaration, Broadcaster broadcaster, MasterRefiner refiner) {
        // only check raw declarations
        if (!declaration.rawPropertyName().isPresent() || !declaration.rawPropertyValue().isPresent()) {
            return Refinement.NONE;
        }

        RawSyntax rawName = declaration.rawPropertyName().get();

        if (Tokens.PLUS.matches(rawName.content().charAt(0))) {
            // find the mixin name
            Source source = new Source(rawName);
            source.expect(Tokens.PLUS);
            Optional<String> name = source.readIdent();
            if (!name.isPresent()) throw new ParserException(declaration, "expected to find a valid mixin name");

            // find the params
            String paramsString = declaration.rawPropertyValue().get().content();
            List<String> params = ImmutableList.copyOf(Splitter.on(',').trimResults().split(paramsString));

            if (params.size() == 1 && params.get(0).equals("true")) {
                params = ImmutableList.of(); // silly way of handling no-arg mixins
            }

            // find the mixin
            String key = name.get() + params.size();
            Mixin mixin = mixins.get(key);
            if (mixin == null) {
                String msg = "Unknown mixin with name '%s' and number of args '%s'";
                throw new ParserException(declaration.rawPropertyValue().get(), String.format(msg, name.get(), params.size()));
            }

            // map the params from key to specified value
            Map<String, String> mappedParams = new HashMap<>();
            List<String> paramKeys = mixin.params();
            for (int i = 0; i < paramKeys.size(); i++) {
                mappedParams.put(paramKeys.get(i), params.get(i));
            }

            // create and broadcast a PropertyValue containing a sole MixinReference instance (a custom AST Term object). By
            // broadcasting the PropertyValue it will automatically get associated with the declaration...
            // ...ok, this is certainly a bit of a hack. Declaration certainly needs to be brought up to snuff.
            MixinReference mixinRef = new MixinReference(mixin, mappedParams);
            PropertyValue propertyValue = PropertyValue.of(mixinRef);
            propertyValue.propagateBroadcast(broadcaster); // this handles broadcasting the inner mixin ref too

            return Refinement.FULL;
        }
        return Refinement.NONE;
    }
}
