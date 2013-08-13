/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.ast.Selector;

/**
 * Parses refined {@link Selector}s, as opposed to {@link RawSelectorParser}.
 * 
 * @author nmcwilliams
 */
public class RefinedSelectorParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        stream.skipWhitepace();

        Parser sequenceParser = ParserFactory.simpleSelectorSequenceParser();
        Parser combinatorParser = ParserFactory.combinatorParser();

        boolean matched = false;
        boolean couldHaveMore = true;
        while (couldHaveMore) {
            if (sequenceParser.parse(stream, broadcaster)) {
                matched = true;
            }
            couldHaveMore = combinatorParser.parse(stream, broadcaster);
        }

        return matched;
    }
}
