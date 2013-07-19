/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import static com.salesforce.omakase.parser.token.Tokens.ALPHA;
import static com.salesforce.omakase.parser.token.Tokens.OPEN_BRACKET;
import static com.salesforce.omakase.parser.token.Tokens.STAR;

import com.salesforce.omakase.adapter.Adapter;
import com.salesforce.omakase.syntax.impl.RawSelector;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class SelectorParser implements Parser {

    @Override
    public boolean raw(Stream stream, Iterable<Adapter> adapters) {
        stream.skipWhitepace();

        if (!ALPHA.or(STAR).matches(stream.peek())) return false;

        RawSelector rs = new RawSelector(stream.line(), stream.column(), stream.until(OPEN_BRACKET));
        for (Adapter adapter : adapters) {
            adapter.selector(rs);
        }
        return true;
    }

    @Override
    public boolean refined(Stream stream, Iterable<Adapter> adapters) {
        // TODO Auto-generated method stub
        return false;
    }

}
