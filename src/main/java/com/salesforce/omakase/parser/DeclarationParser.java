/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import static com.salesforce.omakase.parser.token.Tokens.*;

import com.salesforce.omakase.adapter.Adapter;
import com.salesforce.omakase.parser.token.Token;
import com.salesforce.omakase.syntax.Declaration;
import com.salesforce.omakase.syntax.impl.RawDeclaration;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class DeclarationParser extends AbstractParser {
    private static final Token END = SEMICOLON.or(CLOSE_BRACKET);
    private static final Token PROPERTY = ALPHA.or(HYPHEN);

    @Override
    public boolean parseRaw(Stream stream, Iterable<Adapter> adapters) {
        stream.skipWhitepace();

        if (!PROPERTY.matches(stream.current())) return false;

        int line = stream.line();
        int column = stream.column();

        String property = stream.chomp(PROPERTY);
        stream.skipWhitepace();
        stream.expect(COLON);
        stream.skipWhitepace();
        String value = stream.until(END);

        Declaration d = new RawDeclaration(line, column, property, value);
        for (Adapter adapter : adapters) {
            adapter.declaration(d);
        }

        return true;
    }

    @Override
    public boolean parseRefined(Stream stream, Iterable<Adapter> adapters) {
        // TODO Auto-generated method stub
        return false;
    }

}
