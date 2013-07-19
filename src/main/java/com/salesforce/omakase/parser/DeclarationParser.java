/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import static com.salesforce.omakase.parser.token.Tokens.*;

import com.salesforce.omakase.adapter.Adapter;
import com.salesforce.omakase.syntax.Declaration;
import com.salesforce.omakase.syntax.impl.RawDeclaration;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class DeclarationParser extends AbstractParser {

    @Override
    public boolean raw(Stream stream, Iterable<Adapter> adapters) {
        stream.skipWhitepace();

        if (!ALPHA.or(HYPHEN).matches(stream.peek())) return false;

        int line = stream.line();
        int column = stream.column();

        String property = stream.chomp(ALPHA.or(HYPHEN));
        stream.skipWhitepace();
        stream.expect(COLON);
        stream.skipWhitepace();
        String value = stream.until(SEMICOLON.or(CLOSE_BRACKET));

        Declaration d = new RawDeclaration(line, column, property, value);
        for (Adapter adapter : adapters) {
            adapter.declaration(d);
        }

        return true;
    }

    @Override
    public boolean refined(Stream stream, Iterable<Adapter> adapters) {
        // TODO Auto-generated method stub
        return false;
    }

}
