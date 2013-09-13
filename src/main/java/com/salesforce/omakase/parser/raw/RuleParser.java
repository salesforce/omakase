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

import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Stream;

/**
 * Parses a {@link Rule}.
 *
 * @author nmcwilliams
 * @see Rule
 */
public class RuleParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        stream.skipWhitepace();
        stream.collectComments();

        // if there wasn't a selector then we aren't a rule
        if (!ParserFactory.selectorGroupParser().parse(stream, broadcaster)) return false;

        // skip whitespace after selectors
        stream.skipWhitepace();

        // parse the declaration block
        stream.expect(tokenFactory().declarationBlockBegin());

        // parse all declarations
        do {
            stream.skipWhitepace();
            ParserFactory.rawDeclarationParser().parse(stream, broadcaster);
            stream.skipWhitepace();
        } while (stream.optionallyPresent(tokenFactory().declarationDelimiter()));

        // parse the end of the block
        stream.expect(tokenFactory().declarationBlockEnd());


        // FIXME orphaned comments

        return true;
    }
}
