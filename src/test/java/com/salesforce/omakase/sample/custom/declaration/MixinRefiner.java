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

package com.salesforce.omakase.sample.custom.declaration;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.broadcast.annotation.Refine;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.token.Tokens;
import com.salesforce.omakase.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This handles refining the mixins and the declarations that reference them.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class MixinRefiner implements Plugin {
    private final Map<String, Mixin> mixins = new HashMap<>();

    @Refine("mixin")
    public void refine(AtRule atRule, Grammar grammar, Broadcaster broadcaster) {
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

        Optional<String> param;
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
        grammar.parser().rawDeclarationSequenceParser().parse(source, grammar, queryable);
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
    }

    /** handles mixin references within rules, i.e., the thing that starts with "+" */
    @Refine
    public void refine(Declaration declaration, Grammar grammar, Broadcaster broadcaster) {
        // only check raw declarations
        if (declaration.isRefined()) {
            return;
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
            propertyValue.propagateBroadcast(broadcaster, Status.PARSED); // this handles broadcasting the inner mixin ref too
        }
    }
}
