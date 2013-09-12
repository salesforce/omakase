/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.raw;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Parser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Stream;

/**
 * Parses a group of comma-separated selectors.
 *
 * @author nmcwilliams
 */
public class SelectorGroupParser extends AbstractParser {

    @Override
    @SuppressWarnings("UnusedAssignment")
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        stream.skipWhitepace();
        stream.collectComments();

        // check if the next character is a valid first character for a selector
        if (!tokenFactory().selectorBegin().matches(stream.current())) return false;

        boolean foundDelimiter = false;
        boolean foundSelector = false;
        Parser parser = ParserFactory.rawSelectorParser();

        do {
            // try to parse a selector
            stream.skipWhitepace();
            foundSelector = parser.parse(stream, broadcaster);

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
