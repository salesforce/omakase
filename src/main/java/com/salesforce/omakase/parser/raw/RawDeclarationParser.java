/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.raw;

import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.emitter.SubscriptionType;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Stream;

/**
 * Parses a {@link Declaration}.
 * 
 * @author nmcwilliams
 */
public class RawDeclarationParser extends AbstractParser {
    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        stream.skipWhitepace();

        // the first non comment or space character must match the beginning of a declaration
        if (!tokenFactory().declarationBegin().matches(stream.current())) return false;

        // get the property, which is everything up to the delimiter
        int line = stream.line();
        int column = stream.column();
        String content = stream.until(tokenFactory().propertyNameEnd());
        RawSyntax property = new RawSyntax(line, column, content.trim());

        stream.skipWhitepace();
        stream.expect(tokenFactory().propertyNameEnd());
        stream.skipWhitepace();

        // get the value, which is everything until the end of the declaration
        line = stream.line();
        column = stream.column();
        content = stream.until(tokenFactory().declarationEnd());
        RawSyntax value = new RawSyntax(line, column, content.trim());

        // notifier listeners of new declaration
        broadcaster.broadcast(SubscriptionType.CREATED, new Declaration(property, value, broadcaster));

        return true;
    }
}
