/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.selector;

import com.google.common.base.Optional;
import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.emitter.SubscriptionType;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link ClassSelector}.
 * 
 * @see ClassSelectorParserTest
 * 
 * @author nmcwilliams
 */
public class ClassSelectorParser extends AbstractParser {
    private static final String MSG = "expected to find a valid class name ([-_0-9a-zA-Z], cannot start with a number)";

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a descendant combinator

        // gather the line and column before advancing the stream
        int line = stream.line();
        int column = stream.column();

        // first character must be a dot
        if (!Tokens.DOT.matches(stream.current())) return false;
        stream.next();

        // parse the class name
        Optional<String> name = stream.readIdent();
        if (!name.isPresent()) throw new ParserException(stream, MSG);

        // broadcast the new class selector
        ClassSelector selector = new ClassSelector(line, column, name.get());
        broadcaster.broadcast(SubscriptionType.CREATED, selector);
        return true;
    }
}
