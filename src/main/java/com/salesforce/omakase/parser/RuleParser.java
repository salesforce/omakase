/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import static com.salesforce.omakase.parser.token.Tokens.CLOSE_BRACKET;
import static com.salesforce.omakase.parser.token.Tokens.OPEN_BRACKET;
import static com.salesforce.omakase.parser.token.Tokens.SEMICOLON;

import com.salesforce.omakase.observer.Observer;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class RuleParser extends AbstractParser {
    private static final SelectorParser selector = new SelectorParser();
    private static final DeclarationParser declaration = new DeclarationParser();

    @Override
    public boolean parse(Stream stream, Iterable<Observer> observers) {
        boolean matched;

        // selector
        stream.skipWhitepace();
        matched = selector.parse(stream, observers);

        // if there wasn't a selector then we aren't at a rule
        if (!matched) return false;

        // declaration block
        stream.skipWhitepace();

        stream.expect(OPEN_BRACKET);

        do {
            stream.skipWhitepace();
            declaration.parse(stream, observers);
            stream.skipWhitepace();
        } while (stream.optional(SEMICOLON));

        stream.expect(CLOSE_BRACKET);

        return true;
    }
}
