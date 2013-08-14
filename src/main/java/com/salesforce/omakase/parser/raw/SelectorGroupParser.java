/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.raw;

import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.ast.selector.SelectorGroup;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Stream;

/**
 * Parses a {@link SelectorGroup}.
 * 
 * @author nmcwilliams
 */
public class SelectorGroupParser extends AbstractParser {
    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        stream.skipWhitepace();

        // if the next character is a valid first character for a selector
        if (!tokenFactory().selectorBegin().matches(stream.current())) return false;

        do {
            stream.skipWhitepace();
            ParserFactory.rawSelectorParser().parse(stream, broadcaster);
            stream.skipWhitepace();
        } while (stream.optionallyPresent(tokenFactory().selectorDelimiter()));

        return true;
    }
}