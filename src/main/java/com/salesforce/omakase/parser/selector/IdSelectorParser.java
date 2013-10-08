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
import com.salesforce.omakase.ast.selector.IdSelector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses an {@link IdSelector}.
 * <p/>
 * #rant The spec conflicts itself with ID selectors. In the actual description of ID selectors it says the name must be an
 * identifier (ident), however in the grammar it is "HASH", which is technically just #(name), where "name" is nmchar+ (think like
 * a hex color value). Just another example of the contradictory information all throughout the CSS "spec". #/rant
 *
 * @author nmcwilliams
 * @see IdSelector
 */
public class IdSelectorParser extends AbstractParser {

    @Override
    public boolean parse(Source source, Broadcaster broadcaster) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a descendant combinator
        source.collectComments(false);

        // snapshot the current state before parsing
        Source.Snapshot snapshot = source.snapshot();

        // first character must be a hash
        if (!source.optionallyPresent(Tokens.HASH)) return false;

        // parse the id name
        Optional<String> name = source.readIdent();
        if (!name.isPresent()) throw new ParserException(source, Message.EXPECTED_VALID_ID);

        // broadcast the new id selector
        IdSelector selector = new IdSelector(snapshot.originalLine, snapshot.originalColumn, name.get());
        selector.comments(source.flushComments());
        broadcaster.broadcast(selector);
        return true;
    }

}
