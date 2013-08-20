/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.selector;

import com.google.common.base.Optional;
import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.ast.selector.PseudoClassSelector;
import com.salesforce.omakase.ast.selector.PseudoElementSelector;
import com.salesforce.omakase.emitter.SubscriptionType;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses both {@link PseudoClassSelector}s and {@link PseudoElementSelector}.
 * 
 * @author nmcwilliams
 */
public class PseudoClassSelectorParser extends AbstractParser {
    private static final String MSG = "expected to find a valid pseudo class name ([-_0-9a-zA-Z], cannot start with a number)";

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        stream.snapshot();

        // gather the line and column before advancing the stream
        int line = stream.line();
        int column = stream.column();

        // first character must be a colon
        if (!stream.optionallyPresent(Tokens.COLON)) return stream.rollback();

        // if there is another colon immediately after then it's a pseudo element selector instead.
        if (Tokens.COLON.matches(stream.current())) return stream.rollback();

        // read the name
        Optional<String> name = stream.readIdent();

        // some pseudo elements look like pseudo classes. we don't want to parse those
        if (name.isPresent() && PseudoElementSelectorParser.POSERS.contains(name.get())) return stream.rollback();

        // name must be present
        if (!name.isPresent()) throw new ParserException(stream, MSG);

        // FIXME pseudo elements with functions

        // create the selector and broadcast it
        PseudoClassSelector selector = new PseudoClassSelector(line, column, name.get());
        broadcaster.broadcast(SubscriptionType.CREATED, selector);
        return true;
    }
}
