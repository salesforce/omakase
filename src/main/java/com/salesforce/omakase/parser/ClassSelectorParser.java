/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.google.common.base.Strings;
import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.ast.ClassSelector;
import com.salesforce.omakase.emitter.SubscriptionType;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link ClassSelector}.
 * 
 * @author nmcwilliams
 */
public class ClassSelectorParser extends AbstractParser {
    private static final String MSG = "expected to find a valid class name ([-_0-9a-zA-Z], cannot start with a number)";

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // gather the line and column before advancing the stream
        int line = stream.line();
        int column = stream.column();

        // first character must be a dot
        if (!Tokens.DOT.matches(stream.current())) return false;
        stream.next();

        // parse the class name
        String name = stream.read(ParserFactory.ident());
        if (Strings.isNullOrEmpty(name)) throw new ParserException(stream, MSG);

        // broadcast the new class selector
        ClassSelector selector = new ClassSelector(line, column, name);
        broadcaster.broadcast(SubscriptionType.CREATED, selector);
        return true;
    }
}
