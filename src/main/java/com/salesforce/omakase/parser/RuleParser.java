/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import static com.salesforce.omakase.parser.token.Tokens.CLOSE_BRACKET;
import static com.salesforce.omakase.parser.token.Tokens.OPEN_BRACKET;
import static com.salesforce.omakase.parser.token.Tokens.SEMICOLON;

import com.salesforce.omakase.adapter.Adapter;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class RuleParser extends AbstractParser {
    private static final SelectorParser selector = new SelectorParser();

    @Override
    public boolean parseRaw(Stream stream, Iterable<Adapter> adapters) {
        boolean matched;

        // selector
        stream.skipWhitepace();
        matched = selector.parseRaw(stream, adapters);

        // if there wasn't a selector then we aren't at a rule
        if (!matched) return false;

        // declaration block
        stream.skipWhitepace();
        stream.expect(OPEN_BRACKET);

        do {
            stream.skipWhitepace();
            new DeclarationParser().parseRaw(stream, adapters);
            stream.skipWhitepace();
        } while (stream.optional(SEMICOLON));

        stream.expect(CLOSE_BRACKET);

        for (Adapter adapter : adapters) {
            adapter.endRule();
        }

        return true;
    }

    @Override
    public boolean parseRefined(Stream stream, Iterable<Adapter> adapters) {
        // TODO Auto-generated method stub
        return false;
    }

}
