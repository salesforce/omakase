/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.selector;

import com.salesforce.omakase.ast.selector.UniversalSelector;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link UniversalSelector}.
 *
 * @author nmcwilliams
 */
public class UniversalSelectorParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a descendant combinator

        // gather the line and column before advancing the stream
        int line = stream.line();
        int column = stream.column();

        // first character must be a dot
        boolean matched = stream.optionallyPresent(Tokens.STAR);
        if (!matched) return false;

        // broadcast the new selector
        UniversalSelector selector = new UniversalSelector(line, column);
        broadcaster.broadcast(selector);
        return true;
    }
}
