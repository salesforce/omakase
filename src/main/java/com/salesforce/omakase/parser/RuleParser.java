/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.ast.Rule;

/**
 * Parses a {@link Rule}.
 * 
 * @author nmcwilliams
 */
public class RuleParser extends AbstractParser {
    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        stream.skipWhitepace();

        // if there wasn't a selector then we aren't at a rule
        if (!ParserFactory.selectorGroupParser().parse(stream, broadcaster)) return false;

        stream.skipWhitepace();

        // parse the declaration block
        stream.expect(tokenFactory().declarationBlockBegin());

        do {
            stream.skipWhitepace();
            ParserFactory.declarationParser().parse(stream, broadcaster);
            stream.skipWhitepace();
        } while (stream.optional(tokenFactory().declarationDelimiter()));

        stream.expect(tokenFactory().declarationBlockEnd());

        return true;
    }
}
