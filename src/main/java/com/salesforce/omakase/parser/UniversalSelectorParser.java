/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.ast.UniversalSelector;
import com.salesforce.omakase.emitter.SubscriptionType;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses {@link UniversalSelector}s.
 * 
 * @author nmcwilliams
 */
public class UniversalSelectorParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // gather the line and column before advancing the stream
        int line = stream.line();
        int column = stream.column();

        // first character must be a dot
        boolean matched = stream.optional(Tokens.STAR);
        if (!matched) return false;

        // broadcast the new selector
        UniversalSelector selector = new UniversalSelector(line, column);
        broadcaster.broadcast(SubscriptionType.CREATED, selector);
        return true;
    }

}
