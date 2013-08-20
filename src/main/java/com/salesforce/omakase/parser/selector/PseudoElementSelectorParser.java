/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.selector;

import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.ast.selector.PseudoElementSelector;
import com.salesforce.omakase.emitter.SubscriptionType;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link PseudoElementSelector}. This includes pseudo elements written with a single colon (e.g., :before).
 * 
 * @author nmcwilliams
 */
public class PseudoElementSelectorParser extends AbstractParser {
    private static final String MSG = "expected to find a valid pseudo element name ([-_0-9a-zA-Z], cannot start with a number)";

    /** these can use pseudo class syntax but are actually pseudo elements */
    static final Set<String> POSERS = Sets.newHashSet("first-line", "first-letter", "before", "after");

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        stream.snapshot();

        // gather the line and column before advancing the stream
        int line = stream.line();
        int column = stream.column();

        // first character must be a colon
        if (!stream.optionallyPresent(Tokens.COLON)) return false;

        boolean twoColons = stream.optionallyPresent(Tokens.COLON);

        // read the name
        Optional<String> name = stream.readIdent();

        // must have two colons, except for the posers
        if (!twoColons && name.isPresent() && !POSERS.contains(name.get())) return stream.rollback();

        // name must be present
        if (!name.isPresent()) throw new ParserException(stream, MSG);

        // FIXME pseudo elements with functions

        // create the selector and broadcast it
        PseudoElementSelector selector = new PseudoElementSelector(line, column, name.get());
        broadcaster.broadcast(SubscriptionType.CREATED, selector);
        return true;
    }
}
