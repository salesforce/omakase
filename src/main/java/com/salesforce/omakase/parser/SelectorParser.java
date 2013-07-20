/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import static com.salesforce.omakase.parser.token.Tokens.*;

import com.salesforce.omakase.adapter.Adapter;
import com.salesforce.omakase.parser.token.Token;
import com.salesforce.omakase.syntax.impl.RawSelector;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class SelectorParser implements Parser {
    private static final Token SELECTOR_START = ALPHA.or(STAR).or(HASH).or(DOT);

    @Override
    public boolean parseRaw(Stream stream, Iterable<Adapter> adapters) {
        stream.skipWhitepace();

        // if the next character is a valid first character for a selector
        if (!SELECTOR_START.matches(stream.peek())) return false;

        // create our raw selector. Note that we have no idea if the selector is valid at this point
        RawSelector rs = new RawSelector(stream.line(), stream.column(), stream.until(OPEN_BRACKET));

        // notify all adapters of the selector
        for (Adapter adapter : adapters) {
            adapter.selector(rs);
        }

        return true;
    }

    @Override
    public boolean parseRefined(Stream stream, Iterable<Adapter> adapters) {
        // TODO Auto-generated method stub
        return false;
    }

}
