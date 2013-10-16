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

package com.salesforce.omakase.parser.selector;

import com.salesforce.omakase.ast.selector.UniversalSelector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.Refiner;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link UniversalSelector}.
 *
 * @author nmcwilliams
 */
public final class UniversalSelectorParser extends AbstractParser {

    @Override
    public boolean parse(Source source, Broadcaster broadcaster, Refiner refiner) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a descendant combinator
        source.collectComments(false);

        // snapshot the current state before parsing
        Source.Snapshot snapshot = source.snapshot();

        // first character must be a dot
        boolean matched = source.optionallyPresent(Tokens.STAR);
        if (!matched) return false;

        // broadcast the new selector
        UniversalSelector selector = new UniversalSelector(snapshot.originalLine, snapshot.originalColumn);
        selector.comments(source.flushComments());
        broadcaster.broadcast(selector);
        return true;
    }

}
