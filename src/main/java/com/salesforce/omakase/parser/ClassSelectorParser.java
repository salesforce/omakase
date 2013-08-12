/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.google.common.base.Strings;
import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.ast.ClassSelector;
import com.salesforce.omakase.emitter.SubscriptionType;
import com.salesforce.omakase.parser.token.IdentSequence;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class ClassSelectorParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        stream.skipWhitepace();

        int line = stream.line();
        int column = stream.column();

        if (!Tokens.DOT.matches(stream.current())) return false;
        stream.next();

        String name = stream.read(new IdentSequence());
        if (Strings.isNullOrEmpty(name)) { throw new ParserException(stream, "expected to find class name"); }

        ClassSelector selector = new ClassSelector(line, column, name);
        broadcaster.broadcast(SubscriptionType.CREATED, selector);
        return true;
    }

}
