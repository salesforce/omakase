/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.raw;

import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.emitter.SubscriptionType;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Stream;

/**
 * Parses a {@link Selector}.
 * 
 * @author nmcwilliams
 */
public class RawSelectorParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        stream.skipWhitepace();

        if (!tokenFactory().selectorBegin().matches(stream.current())) return false;

        // line and columns must be calculated before content
        int line = stream.line();
        int column = stream.column();

        // grab everything until the end of the selector
        String content = stream.until(tokenFactory().selectorEnd());
        RawSyntax raw = new RawSyntax(line, column, content.trim());

        // create selector and associate comments
        Selector selector = new Selector(raw, broadcaster);
        selector.comments(stream.flushComments());

        // notify listeners of new selector
        broadcaster.broadcast(SubscriptionType.CREATED, selector);
        return true;
    }

}
