/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.ast.SimpleSelector;

/**
 * Parsers a sequence of {@link SimpleSelector}s.
 * 
 * @author nmcwilliams
 */
public class SimpleSelectorSequenceParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        stream.skipWhitepace();

        // if a universal or type selector is present, it must be the first one
        boolean matched = ParserFactory.simpleSelectorStartParser().parse(stream, broadcaster);

        // parse all remaining regular simple selectors
        Parser simpleSelectorParser = ParserFactory.simpleSelectorParser();

        boolean simpleSelectorMatched = true;
        while (simpleSelectorMatched) {
            // it's important not to skip whitespace here (neither should any simple selector parser skip whitespace)
            // because a space could be the descendant combinator
            simpleSelectorMatched = simpleSelectorParser.parse(stream, broadcaster);
            if (simpleSelectorMatched) matched = true;
        }

        // FIXME pseudo element

        return matched;
    }

}
