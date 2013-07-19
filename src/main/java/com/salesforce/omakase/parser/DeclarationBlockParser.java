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
public class DeclarationBlockParser extends AbstractParser {

    @Override
    public boolean raw(Stream stream, Iterable<Adapter> adapters) {
        stream.skipWhitepace();
        stream.expect(OPEN_BRACKET);
        do {
            stream.skipWhitepace();
            new DeclarationParser().raw(stream, adapters);
            stream.skipWhitepace();
        } while (stream.optional(SEMICOLON));
        stream.expect(CLOSE_BRACKET);
        return true;
    }

    @Override
    public boolean refined(Stream stream, Iterable<Adapter> adapters) {
        // TODO Auto-generated method stub
        return false;
    }

}
