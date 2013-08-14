/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.raw;

import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Stream;

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
            ParserFactory.rawDeclarationParser().parse(stream, broadcaster);
            stream.skipWhitepace();
        } while (stream.optionallyPresent(tokenFactory().declarationDelimiter()));

        stream.expect(tokenFactory().declarationBlockEnd());

        return true;
    }
}
