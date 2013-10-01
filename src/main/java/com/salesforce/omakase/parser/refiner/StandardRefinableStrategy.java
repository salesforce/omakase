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
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.OrphanedComment;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.value.PropertyValue;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Stream;

/**
 * Standard {@link RefinableStrategy} implementation.
 *
 * @author nmcwilliams
 */
public class StandardRefinableStrategy implements RefinableStrategy {
    @Override
    public boolean refineAtRule(AtRule atRule, Broadcaster broadcaster, Refiner refiner) {
        // do nothing -- there's no default refinement for at-rules
        return false;
    }

    @Override
    public boolean refineSelector(Selector selector, Broadcaster broadcaster, Refiner refiner) {
        QueryableBroadcaster qb = new QueryableBroadcaster(broadcaster);
        Stream stream = new Stream(selector.rawContent(), false);

        // parse the contents
        ParserFactory.complexSelectorParser().parse(stream, qb);

        // there should be nothing left
        if (!stream.eof()) {
            throw new ParserException(stream, Message.UNPARSABLE_SELECTOR);
        }

        // store the parsed selector parts
        selector.appendAll(qb.filter(SelectorPart.class));

        // check for orphaned comments
        for (OrphanedComment comment : qb.filter(OrphanedComment.class)) {
            selector.orphanedComment(comment);
        }

        return true;
    }

    @Override
    public boolean refineDeclaration(Declaration declaration, Broadcaster broadcaster, Refiner refiner) {
        QueryableBroadcaster qb = new QueryableBroadcaster(broadcaster);
        Stream stream = new Stream(declaration.rawPropertyValue().content(), declaration.line(), declaration.column());

        // parse the contents
        ParserFactory.termListParser().parse(stream, qb);

        // there should be nothing left
        if (!stream.eof()) throw new ParserException(stream, Message.UNPARSABLE_VALUE);

        // store the parsed value
        Optional<PropertyValue> first = qb.find(PropertyValue.class);
        if (!first.isPresent()) throw new ParserException(stream, Message.EXPECTED_VALUE);
        declaration.propertyValue(first.get());

        // check for orphaned comments
        for (OrphanedComment comment : qb.filter(OrphanedComment.class)) {
            declaration.orphanedComment(comment);
        }

        return true;
    }
}
