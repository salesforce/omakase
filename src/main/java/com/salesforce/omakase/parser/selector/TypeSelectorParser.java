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

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.selector.TypeSelector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Source;

/**
 * Parses {@link TypeSelector}s.
 *
 * @author nmcwilliams
 * @see TypeSelector
 */
public class TypeSelectorParser extends AbstractParser {

    @Override
    public boolean parse(Source source, Broadcaster broadcaster) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a descendant combinator
        source.collectComments(false);

        // snapshot the current state before parsing
        Source.Snapshot snapshot = source.snapshot();

        // find the name
        Optional<String> name = source.readIdent();
        if (!name.isPresent()) return false;

        // create and broadcast the new selector
        TypeSelector selector = new TypeSelector(snapshot.originalLine, snapshot.originalColumn, name.get());
        selector.comments(source.flushComments());
        broadcaster.broadcast(selector);
        return true;
    }

}
