/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.ast.Declaration;

/**
 * Parses a {@link Declaration}.
 * 
 * @author nmcwilliams
 */
public class DeclarationParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // skip whitespace
        stream.skipWhitepace();

        // the first non comment or space character must match the beginning of a declaration
        if (!tokenFactory().declarationBegin().matches(stream.current())) return false;

        // line and columns must be calculated before content
        // int line = stream.line();
        // int column = stream.column();

        // take everything until the end of declaration token
        // String content = stream.until(tokenFactory().declarationEnd());

        // broadcaster.broadcast(SubscriptionType.CREATED, new Declaration(line, column, content));

        return true;
    }
}
