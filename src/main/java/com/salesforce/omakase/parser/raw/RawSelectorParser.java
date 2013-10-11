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

package com.salesforce.omakase.parser.raw;

import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.Refiner;

/**
 * Parses a {@link Selector}.
 *
 * @author nmcwilliams
 * @see Selector
 */
public class RawSelectorParser extends AbstractParser {

    @Override
    public boolean parse(Source source, Broadcaster broadcaster, Refiner refiner) {
        source.skipWhitepace();
        source.collectComments();

        if (!tokenFactory().selectorBegin().matches(source.current())) return false;

        // snapshot the current state before parsing
        Source.Snapshot snapshot = source.snapshot();

        // grab everything until the end of the selector
        String content = source.until(tokenFactory().selectorEnd());
        RawSyntax raw = new RawSyntax(snapshot.originalLine, snapshot.originalColumn, content.trim());

        // create selector and associate comments
        Selector selector = new Selector(raw, refiner);
        selector.comments(source.flushComments());

        // notify listeners of new selector
        broadcaster.broadcast(selector);
        return true;
    }

}
