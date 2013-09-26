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
import com.salesforce.omakase.parser.AbstractRefinableParser;
import com.salesforce.omakase.parser.refiner.Refiner;
import com.salesforce.omakase.parser.Stream;

/**
 * Parses a {@link Selector}.
 *
 * @author nmcwilliams
 * @see Selector
 */
public class RawSelectorParser extends AbstractRefinableParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster, Refiner refiner) {
        stream.skipWhitepace();
        stream.collectComments();

        if (!tokenFactory().selectorBegin().matches(stream.current())) return false;

        // snapshot the current state before parsing
        Stream.Snapshot snapshot = stream.snapshot();

        // grab everything until the end of the selector
        String content = stream.until(tokenFactory().selectorEnd());
        RawSyntax raw = new RawSyntax(snapshot.line, snapshot.column, content.trim());

        // create selector and associate comments
        Selector selector = new Selector(raw, refiner);
        selector.comments(stream.flushComments());

        // notify listeners of new selector
        broadcaster.broadcast(selector);
        return true;
    }

}
