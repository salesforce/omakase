/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.selector;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.parser.*;
import com.salesforce.omakase.parser.raw.RawSelectorParser;

/**
 * Parses refined {@link Selector}s, as opposed to {@link RawSelectorParser}.
 * 
 * <p>
 * This attempts to conform to Selectors level 3 (http://www.w3.org/TR/css3-selectors). Yes, attempts, because the spec
 * is inconsistent, contradictory, and malformed.
 * 
 * @author nmcwilliams
 */
public class ComplexSelectorParser extends AbstractParser {
    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        stream.skipWhitepace();
        stream.rejectComments();

        // setup inner parsers
        Parser combinator = ParserFactory.combinatorParser();
        Parser repeatableSelector = ParserFactory.repeatableSelector();
        Parser typeOrUniversalSelector = ParserFactory.typeOrUniversaleSelectorParser();

        boolean matchedTypeOrUniversal = false;
        boolean matchedOther = false;
        boolean couldHaveMore = true;

        // it's important not to skip whitespace here (neither should any simple selector parser or pseudo element
        // selector skip whitespace) because a space could be the descendant combinator.
        while (couldHaveMore) {
            // try parsing universal or type simple selector
            matchedTypeOrUniversal = typeOrUniversalSelector.parse(stream, broadcaster);

            // parse remaining selectors in the sequence
            while (repeatableSelector.parse(stream, broadcaster)) {
                matchedOther = true;
            }

            // parse combinator
            couldHaveMore = combinator.parse(stream, broadcaster);
        }

        // check for known possible errors
        if (!stream.eof()) {
            stream.snapshot();
            if (typeOrUniversalSelector.parse(stream, broadcaster)) {
                stream.rollback(); // for correct error reporting line and column
                throw new ParserException(stream, Message.NAME_SELECTORS_NOT_ALLOWED);
            }
        }

        // allow comments again
        stream.enableComments();

        return matchedTypeOrUniversal || matchedOther;
    }
}
