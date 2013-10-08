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

package com.salesforce.omakase.parser.declaration;

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.declaration.value.KeywordValue;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Source;

/**
 * Parses a {@link KeywordValue}.
 *
 * @author nmcwilliams
 * @see KeywordValue
 */
public class KeywordValueParser extends AbstractParser {

    @Override
    public boolean parse(Source source, Broadcaster broadcaster) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a space operator
        source.collectComments(false);

        // snapshot the current state before parsing
        Source.Snapshot snapshot = source.snapshot();

        // read the keyword
        Optional<String> keyword = source.readIdent();
        if (!keyword.isPresent()) return false;

        KeywordValue value = new KeywordValue(snapshot.originalLine, snapshot.originalColumn, keyword.get());
        value.comments(source.flushComments());
        broadcaster.broadcast(value);

        return true;
    }

}
