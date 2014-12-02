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

package com.salesforce.omakase.test.sample.customselector;

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.MasterRefiner;
import com.salesforce.omakase.parser.refiner.SelectorRefiner;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO description
 *
 * @author nmcwilliams
 */
@SuppressWarnings("ALL")
public class PlaceholderSelectorRefiner implements SelectorRefiner {
    private Map<String, Selector> placeholders = new HashMap<>();

    @Override
    public boolean refine(Selector selector, Broadcaster broadcaster, MasterRefiner refiner) {
        Source source = new Source(selector.rawContent());
        source.skipWhitepace();

        if (!source.optionallyPresent(PlaceholderTokens.PERCENTAGE)) return false;

        // parse the placeholder name
        Optional<String> name = source.readIdent();
        if (!name.isPresent()) throw new ParserException(source, "Expected to find a valid placeholder selector name");

        placeholders.put(name.get(), selector);
        return true;
    }
}
