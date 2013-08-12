/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Selector;
import com.salesforce.omakase.emitter.SubscriptionType;

/**
 * Parses a {@link Selector}.
 * 
 * @author nmcwilliams
 */
public class SelectorParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        stream.skipWhitepace();

        if (!tokenFactory().selectorBegin().matches(stream.current())) return false;

        // line and columns must be calculated before content
        int line = stream.line();
        int column = stream.column();

        // grab everything until the end of the selector
        String content = stream.until(tokenFactory().selectorEnd());
        RawSyntax rawContent = new RawSyntax(line, column, content.trim());

        // notify listeners of new selector
        broadcaster.broadcast(SubscriptionType.CREATED, new Selector(rawContent, broadcaster));

        return true;
    }
}
