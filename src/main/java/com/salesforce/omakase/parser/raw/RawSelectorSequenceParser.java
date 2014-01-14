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
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.GenericRefiner;

/**
 * Parses a sequence of comma-separated selectors.
 *
 * @author nmcwilliams
 */
public final class RawSelectorSequenceParser extends AbstractParser {
    @Override
    public boolean parse(Source source, Broadcaster broadcaster, GenericRefiner refiner) {
        source.collectComments();

        // check if the next character is a valid first character for a selector
        if (!tokenFactory().selectorBegin().matches(source.current())) return false;

        boolean foundDelimiter = false;
        boolean foundSelector = false;

        do {
            // try to parse a selector
            source.skipWhitepace();
            foundSelector = ParserFactory.rawSelectorParser().parse(source, broadcaster, refiner);

            if (foundDelimiter && !foundSelector) {
                throw new ParserException(source, Message.EXPECTED_SELECTOR, tokenFactory().selectorDelimiter().description());
            }

            // try to parse a delimiter (e.g., comma)
            foundDelimiter = source.skipWhitepace().optionallyPresent(tokenFactory().selectorDelimiter());
        } while (foundDelimiter);

        return true;
    }
}
