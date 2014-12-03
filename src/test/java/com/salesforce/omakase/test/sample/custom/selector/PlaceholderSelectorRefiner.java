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

package com.salesforce.omakase.test.sample.custom.selector;

import com.google.common.base.Optional;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.MasterRefiner;
import com.salesforce.omakase.parser.refiner.Refinement;
import com.salesforce.omakase.parser.refiner.SelectorRefiner;

import java.util.HashMap;
import java.util.Map;

/**
 * This handles refining the placeholder selectors and the other selectors that reference them.
 *
 * @author nmcwilliams
 */
public class PlaceholderSelectorRefiner implements SelectorRefiner {
    private final Map<String, PlaceholderSelector> placeholders = new HashMap<>();

    @Override
    public Refinement refine(Selector selector, Broadcaster broadcaster, MasterRefiner refiner) {
        String content = selector.rawContent().content();
        Source source = new Source(content);
        source.skipWhitepace();

        if (source.optionallyPresent(PlaceholderTokens.PERCENTAGE)) {
            // PLACEHOLDER `%name`

            // parse the placeholder name
            Optional<String> name = source.readIdent();
            if (!name.isPresent()) throw new ParserException(source, "Expected to find a valid placeholder selector name");

            // nothing else should be after the placeholder selector name
            if (!source.skipWhitepace().eof()) throw new ParserException(source, Message.UNPARSABLE_SELECTOR);

            // create and broadcast our new placeholder selector
            PlaceholderSelector placeholder = new PlaceholderSelector(name.get());
            placeholders.put(placeholder.name(), placeholder);
            broadcaster.broadcast(placeholder);

            return Refinement.FULL;
        } else if (content.contains(PlaceholderTokens.PIPE.symbol())) {
            // PLACEHOLDER REF `.selector|name`

            // parse the normal selectors, they will automatically be broadcasted
            ParserFactory.complexSelectorParser().parse(source, broadcaster, refiner);

            // parse the reference
            source.expect(PlaceholderTokens.PIPE);
            Optional<String> name = source.readIdent();
            if (!name.isPresent()) throw new ParserException(source, "Expected to find a valid placeholder selector name");

            // there should be nothing left
            if (!source.skipWhitepace().eof()) throw new ParserException(source, Message.UNPARSABLE_SELECTOR);

            PlaceholderSelector placeholder = placeholders.get(name.get());
            if (placeholder == null) throw new ParserException(source, "Unknown placeholder selector '" + name.get() + "'");
            placeholder.addReference(selector);
            return Refinement.FULL;
        }

        return Refinement.NONE;
    }
}
