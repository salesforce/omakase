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

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.Comment;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.GenericFunctionValue;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.declaration.RawFunction;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.broadcast.QueuingBroadcaster;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;

import java.util.Set;

/**
 * Standard {@link RefinerStrategy} implementation.
 *
 * @author nmcwilliams
 */
public final class StandardRefinerStrategy implements AtRuleRefinerStrategy, SelectorRefinerStrategy,
    DeclarationRefinerStrategy, FunctionRefinerStrategy {

    private static final Set<AtRuleRefinerStrategy> STANDARD_AT_RULES = ImmutableSet.<AtRuleRefinerStrategy>of(
        new MediaRefinerStrategy()
    );

    private static final Set<FunctionRefinerStrategy> STANDARD_FUNCTIONS = ImmutableSet.<FunctionRefinerStrategy>of(
        new UrlFunctionRefinerStrategy()
    );

    @Override
    public boolean refine(AtRule atRule, Broadcaster broadcaster, Refiner refiner) {
        for (AtRuleRefinerStrategy strategy : STANDARD_AT_RULES) {
            if (strategy.refine(atRule, broadcaster, refiner)) return true;
        }
        return false;
    }

    @Override
    public boolean refine(Selector selector, Broadcaster broadcaster, Refiner refiner) {
        if (selector.isRefined()) return false;

        // use a queue so that we can hold off on broadcasting the individual parts until we have them all. This makes rework
        // plugins that utilize order (#isFirst(), etc...) work smoothly.
        QueuingBroadcaster queue = new QueuingBroadcaster(broadcaster).pause();
        QueryableBroadcaster queryable = new QueryableBroadcaster(queue);
        Source source = new Source(selector.rawContent(), false);

        // parse the contents
        ParserFactory.complexSelectorParser().parse(source, queryable, refiner);

        // grab orphaned comments
        for (String comment : source.collectComments().flushComments()) {
            selector.orphanedComment(new Comment(comment));
        }

        // there should be nothing left
        if (!source.eof()) throw new ParserException(source, Message.UNPARSABLE_SELECTOR);

        // store the parsed selector parts
        selector.appendAll(queryable.filter(SelectorPart.class));

        // once they are all added we're good to send them out
        queue.resume();

        return true;
    }

    @Override
    public boolean refine(Declaration declaration, Broadcaster broadcaster, Refiner refiner) {
        if (declaration.isRefined()) return false;

        QueryableBroadcaster qb = new QueryableBroadcaster(broadcaster);
        Source source = new Source(declaration.rawPropertyValue().get().content(), declaration.line(), declaration.column());

        // parse the contents
        ParserFactory.termListParser().parse(source, qb, refiner);

        // grab orphaned comments
        for (String comment : source.collectComments().flushComments()) {
            declaration.orphanedComment(new Comment(comment));
        }

        // there should be nothing left in the source
        if (!source.eof()) throw new ParserException(source, Message.UNPARSABLE_DECLARATION_VALUE);

        // store the parsed value
        Optional<PropertyValue> first = qb.find(PropertyValue.class);
        if (!first.isPresent()) throw new ParserException(source, Message.EXPECTED_VALUE);

        declaration.propertyValue(first.get());

        return true;
    }

    @Override
    public boolean refine(RawFunction raw, Broadcaster broadcaster, Refiner refiner) {
        for (FunctionRefinerStrategy strategy : STANDARD_FUNCTIONS) {
            if (strategy.refine(raw, broadcaster, refiner)) return true;
        }

        GenericFunctionValue generic = new GenericFunctionValue(raw.line(), raw.column(), raw.name(), raw.args());
        generic.directComments(raw.comments());
        broadcaster.broadcast(generic);

        return true;
    }
}
