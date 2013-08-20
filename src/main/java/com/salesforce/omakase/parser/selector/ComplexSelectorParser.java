/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.selector;

import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.ast.selector.Selector;
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
    private static final String MUTUAL = "universal selectors and type selectors cannot both be present within the same sequence";
    private static final String UNIVERSAL_NOT_ALLOWED = "universal selector not allowed here";
    private static final String TYPE_NOT_ALLOWED = "type selector not allowed here";

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        stream.skipWhitepace();

        // setup inner parsers
        Parser combinator = ParserFactory.combinatorParser();
        Parser simpleSelector = ParserFactory.simpleSelectorParser();
        Parser universalSelector = ParserFactory.universalSelectorParser();
        Parser typeSelector = ParserFactory.typeSelectorParser();

        boolean matchedUniversal = false;
        boolean matchedType = false;
        boolean matchedOtherSimple = false;
        boolean matchedPseudoElement = false;
        boolean couldHaveMore = true;

        // it's important not to skip whitespace here (neither should any simple selector parser or pseudo element
        // selector skip whitespace) because a space could be the descendant combinator.
        while (couldHaveMore) {
            // try parsing universal or type simple selectors
            matchedUniversal = universalSelector.parse(stream, broadcaster);
            matchedType = typeSelector.parse(stream, broadcaster);

            // can't have both
            if (matchedUniversal && matchedType) throw new ParserException(stream, MUTUAL);

            // parse all remaining simple selectors
            while (simpleSelector.parse(stream, broadcaster)) {
                matchedOtherSimple = true;
            }

            couldHaveMore = combinator.parse(stream, broadcaster);
        }

        // parse pseudo element -- must be last
        matchedPseudoElement = ParserFactory.pseudoElementSelectorParser().parse(stream, broadcaster);

        // check for known possible errors
        if (!stream.eof()) {
            stream.snapshot();
            if (universalSelector.parse(stream, broadcaster)) {
                stream.rollback(); // for correct error reporting line and column
                throw new ParserException(stream, UNIVERSAL_NOT_ALLOWED);
            } else if (typeSelector.parse(stream, broadcaster)) {
                stream.rollback(); // for correct error reporting line and column
                throw new ParserException(stream, TYPE_NOT_ALLOWED);
            }
        }

        return matchedUniversal || matchedType || matchedOtherSimple || matchedPseudoElement;
    }
}
