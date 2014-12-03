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
    public boolean refine(AtRule atRule, Broadcaster broadcaster, MasterRefiner refiner) {
        for (AtRuleRefiner strategy : STANDARD_AT_RULES) {
            if (strategy.refine(atRule, broadcaster, refiner)) return true;
        }
        return false;
    }

    @Override
    public boolean refine(Selector selector, Broadcaster broadcaster, MasterRefiner refiner) {
        // parse inner content
        Source source = new Source(selector.rawContent(), false);
        ParserFactory.complexSelectorParser().parse(source, broadcaster, refiner);

        // grab orphaned comments
        selector.orphanedComments(source.collectComments().flushComments());

        // there should be nothing left
        if (!source.eof()) throw new ParserException(source, Message.UNPARSABLE_SELECTOR);

        return true;
    }

    @Override
    public boolean refine(Declaration declaration, Broadcaster broadcaster, MasterRefiner refiner) {
        if (declaration.isRefined()) return false;

        // parse inner content
        Source source = new Source(declaration.rawPropertyValue().get());
        ParserFactory.propertyValueParser().parse(source, broadcaster, refiner);

        // grab orphaned comments
        declaration.orphanedComments(source.collectComments().flushComments());

        // there should be nothing left
        if (!source.eof()) throw new ParserException(source, Message.UNPARSABLE_DECLARATION_VALUE, source.remaining());

        return true;
    }

    @Override
    public boolean refine(RawFunction raw, Broadcaster broadcaster, MasterRefiner refiner) {
        for (FunctionRefiner strategy : STANDARD_FUNCTIONS) {
            if (strategy.refine(raw, broadcaster, refiner)) return true;
        }

        GenericFunctionValue generic = new GenericFunctionValue(raw.line(), raw.column(), raw.name(), raw.args());
        generic.comments(raw);
        broadcaster.broadcast(generic);

        return true;
    }
}
