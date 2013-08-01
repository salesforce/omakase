/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import static com.salesforce.omakase.parser.token.Tokens.*;

import com.salesforce.omakase.ast.selector.SelectorGroup;
import com.salesforce.omakase.observer.Observer;
import com.salesforce.omakase.parser.token.Token;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class SelectorParser extends AbstractParser implements Parser {
    private static final Token SELECTOR_START = ALPHA.or(STAR).or(HASH).or(DOT);

    @Override
    public boolean parse(Stream stream, Iterable<Observer> observers) {
        stream.skipWhitepace();

        // if the next character is a valid first character for a selector
        if (!SELECTOR_START.matches(stream.current())) return false;

        String content = stream.until(OPEN_BRACKET);
        int line = stream.line();
        int column = stream.column();

        SelectorGroup selectorGroup = factory().selectorGroup()
            .content(content)
            .line(line)
            .column(column)
            .build();

        announce(selectorGroup, observers);

        return true;
    }
}
