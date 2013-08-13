/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.google.common.base.Strings;
import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.ast.IdSelector;
import com.salesforce.omakase.emitter.SubscriptionType;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses an {@link IdSelector}.
 * 
 * <p>
 * #rant The spec conflicts itself with ID selectors. In the actual description of ID selectors it says the name must be
 * an identifier (ident), however in the grammar it is "HASH", which is technically just #(name), where "name" is
 * nmchar+ (think like a hex color value). Just another example of the contradictory information all throughout the CSS
 * "spec". #/rant
 * 
 * @author nmcwilliams
 */
public class IdSelectorParser extends AbstractParser {
    private static final String MSG = "expected to find a valid id name ([-_0-9a-zA-Z], cannot start with a number)";

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // gather the line and column before advancing the stream
        int line = stream.line();
        int column = stream.column();

        // first character must be a hash
        if (!Tokens.HASH.matches(stream.current())) return false;
        stream.next();

        // parse the id name.
        String name = stream.read(ParserFactory.ident());
        if (Strings.isNullOrEmpty(name)) throw new ParserException(stream, MSG);

        // broadcast the new class selector
        IdSelector selector = new IdSelector(line, column, name);
        broadcaster.broadcast(SubscriptionType.CREATED, selector);
        return true;
    }
}
