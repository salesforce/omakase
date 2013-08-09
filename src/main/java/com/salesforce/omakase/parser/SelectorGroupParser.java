/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import static com.salesforce.omakase.parser.token.Tokens.*;

import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.parser.token.Token;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class SelectorGroupParser extends AbstractParser {

    private static final Token SELECTOR_START = ALPHA.or(STAR).or(HASH).or(DOT);

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        stream.skipWhitepace();

        // if the next character is a valid first character for a selector
        if (!SELECTOR_START.matches(stream.current())) return false;

        // int line = stream.line();
        // int column = stream.column();
        // String content = stream.until(OPEN_BRACKET);
        //
        // SelectorGroup selectorGroup = new SelectorGroup(line, column, content);
        // broadcaster.broadcast(SubscriptionType.CREATED, selectorGroup);
        return true;
    }

}
