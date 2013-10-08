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
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link ClassSelector}.
 *
 * @author nmcwilliams
 * @see ClassSelector
 */
public class ClassSelectorParser extends AbstractParser {

    @Override
    public boolean parse(Source source, Broadcaster broadcaster) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a descendant combinator
        source.collectComments(false);

        // snapshot the current state before parsing
        Source.Snapshot snapshot = source.snapshot();

        // first character must be a dot
        if (!source.optionallyPresent(Tokens.DOT)) return snapshot.rollback();

        // parse the class name
        Optional<String> name = source.readIdent();
        if (!name.isPresent()) throw new ParserException(source, Message.EXPECTED_VALID_CLASS);

        // broadcast the new class selector
        ClassSelector selector = new ClassSelector(snapshot.originalLine, snapshot.originalColumn, name.get());
        selector.comments(source.flushComments());
        broadcaster.broadcast(selector);

        return true;
    }

}
