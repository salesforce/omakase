/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.ast.SelectorGroup;

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
            ParserFactory.selectorParser().parse(stream, broadcaster);
            stream.skipWhitepace();
        } while (stream.optional(tokenFactory().selectorDelimiter()));

        return true;
    }
}
