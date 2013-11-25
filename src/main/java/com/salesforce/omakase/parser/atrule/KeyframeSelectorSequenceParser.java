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

package com.salesforce.omakase.parser.atrule;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.selector.KeyframeSelector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.Refiner;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a sequence of comma-separated {@link KeyframeSelector}s.
 *
 * @author nmcwilliams
 * @see KeyframeSelectorParser
 * @see KeyframeSelector
 */
public final class KeyframeSelectorSequenceParser extends AbstractParser {
    @Override
    public boolean parse(Source source, Broadcaster broadcaster, Refiner refiner) {
        source.collectComments();

        boolean found = ParserFactory.keyframeSelectorParser().parse(source, broadcaster, refiner);
        if (!found) return false;

        // check for a comma
        while (source.collectComments().optionallyPresent(tokenFactory().selectorDelimiter())) {
            if (!ParserFactory.keyframeSelectorParser().parse(source, broadcaster, refiner)) {
                throw new ParserException(source, Message.TRAILING, Tokens.COMMA.description());
            }
        }

        return true;
    }
}
