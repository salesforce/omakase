/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.raw;

import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Stream;

/**
 * Parses a {@link Selector}.
 *
 * @author nmcwilliams
 * @see Selector
 */
public class RawSelectorParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        stream.skipWhitepace();

        if (!tokenFactory().selectorBegin().matches(stream.current())) return false;

        // line and columns must be calculated before content
        int line = stream.line();
        int column = stream.column();

        // grab the comments now so we can ignore comments at the end of the selector group (see SelectorGroupParser)
        Iterable<String> comments = stream.flushComments();

        // grab everything until the end of the selector
        String content = stream.until(tokenFactory().selectorEnd());
        RawSyntax raw = new RawSyntax(line, column, content.trim());

        // create selector and associate comments
        Selector selector = new Selector(raw, broadcaster);
        selector.comments(comments);

        // notify listeners of new selector
        broadcaster.broadcast(selector);
        return true;
    }
}
