/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import static com.salesforce.omakase.parser.token.Tokens.*;

import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.observer.Observer;
import com.salesforce.omakase.parser.token.Token;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class DeclarationParser extends AbstractParser {
    /** expected characters in a property */
    private static final Token PROPERTY = ALPHA.or(HYPHEN);

    /** characters that indicate the end of the declaration */
    private static final Token DECLARATION_END = SEMICOLON.or(CLOSE_BRACKET);

    @Override
    public boolean parse(Stream stream, Iterable<Observer> observers) {
        stream.skipWhitepace();

        if (!PROPERTY.matches(stream.current())) return false;

        int line = stream.line();
        int column = stream.column();

        String property = stream.chomp(PROPERTY);
        stream.skipWhitepace();
        stream.expect(COLON);
        stream.skipWhitepace();
        String value = stream.until(DECLARATION_END);

        Declaration declaration = factory().declaration()
            .property(property)
            .value(value)
            .line(line)
            .column(column)
            .build();

        announce(declaration, observers);

        return true;
    }
}
