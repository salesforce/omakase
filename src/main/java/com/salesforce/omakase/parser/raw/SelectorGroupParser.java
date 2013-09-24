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

import com.salesforce.omakase.Message;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractRefinableParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.RefinableParser;
import com.salesforce.omakase.parser.Refiner;
import com.salesforce.omakase.parser.Stream;

/**
 * Parses a group of comma-separated selectors.
 *
 * @author nmcwilliams
 */
public class SelectorGroupParser extends AbstractRefinableParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster, Refiner refiner) {
        stream.skipWhitepace();
        stream.collectComments();

        // check if the next character is a valid first character for a selector
        if (!tokenFactory().selectorBegin().matches(stream.current())) return false;

        boolean foundDelimiter = false;
        boolean foundSelector = false;
        RefinableParser parser = ParserFactory.rawSelectorParser();

        do {
            // try to parse a selector
            stream.skipWhitepace();
            foundSelector = parser.parse(stream, broadcaster, refiner);

            if (foundDelimiter && !foundSelector) {
                throw new ParserException(stream, Message.EXPECTED_SELECTOR, tokenFactory().selectorDelimiter().description());
            }

            stream.skipWhitepace();

            // try to parse a delimiter (e.g., comma)
            foundDelimiter = stream.optionallyPresent(tokenFactory().selectorDelimiter());
        } while (foundDelimiter);

        return true;
    }
}
