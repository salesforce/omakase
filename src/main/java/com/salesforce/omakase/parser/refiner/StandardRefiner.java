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

package com.salesforce.omakase.parser.refiner;

import com.google.common.collect.ImmutableSet;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.GenericFunctionValue;
import com.salesforce.omakase.ast.declaration.RawFunction;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;

import java.util.Set;

/**
 * Standard {@link Refiner} implementation.
 *
 * @author nmcwilliams
 */
final class StandardRefiner implements AtRuleRefiner, SelectorRefiner,
    DeclarationRefiner, FunctionRefiner {

    private static final Set<AtRuleRefiner> STANDARD_AT_RULES = ImmutableSet.of(
        new MediaRefiner(),
        new KeyframesRefiner(),
        new FontFaceRefiner()
    );

    private static final Set<FunctionRefiner> STANDARD_FUNCTIONS = ImmutableSet.of(
        new UrlRefiner(),
        new LinearGradientRefiner()
    );

    @Override
    public Refinement refine(AtRule atRule, Broadcaster broadcaster, MasterRefiner refiner) {
        Refinement refinement = Refinement.NONE;

        for (AtRuleRefiner strategy : STANDARD_AT_RULES) {
            Refinement result = strategy.refine(atRule, broadcaster, refiner);
            if (result == Refinement.FULL) {
                return Refinement.FULL;
            } else if (result == Refinement.PARTIAL) {
                refinement = Refinement.PARTIAL;
            }
        }
        return refinement;
    }

    @Override
    public Refinement refine(Selector selector, Broadcaster broadcaster, MasterRefiner refiner) {
        // parse inner content
        Source source = new Source(selector.raw().get(), false);
        ParserFactory.complexSelectorParser().parse(source, broadcaster, refiner);

        // grab orphaned comments
        selector.orphanedComments(source.collectComments().flushComments());

        // there should be nothing left
        if (!source.eof()) throw new ParserException(source, Message.UNPARSABLE_SELECTOR);

        return Refinement.FULL;
    }

    @Override
    public Refinement refine(Declaration declaration, Broadcaster broadcaster, MasterRefiner refiner) {
        // parse inner content
        Source source = new Source(declaration.rawPropertyValue().get());
        ParserFactory.propertyValueParser().parse(source, broadcaster, refiner);

        // grab orphaned comments
        declaration.orphanedComments(source.collectComments().flushComments());

        // there should be nothing left
        if (!source.eof()) throw new ParserException(source, Message.UNPARSABLE_DECLARATION_VALUE, source.remaining());

        return Refinement.FULL;
    }

    @Override
    public Refinement refine(RawFunction raw, Broadcaster broadcaster, MasterRefiner refiner) {
        for (FunctionRefiner strategy : STANDARD_FUNCTIONS) {
            Refinement result = strategy.refine(raw, broadcaster, refiner);
            assert result != Refinement.PARTIAL : "Partial refinement of RawFunctions is not supported";

            if (result == Refinement.FULL) {
                return Refinement.FULL;
            }
        }

        GenericFunctionValue generic = new GenericFunctionValue(raw.line(), raw.column(), raw.name(), raw.args());
        generic.comments(raw);
        broadcaster.broadcast(generic);

        return Refinement.FULL;
    }
}
