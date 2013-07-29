/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import static com.salesforce.omakase.parser.token.Tokens.*;

import com.salesforce.omakase.ast.impl.StandardSelectorGroup;
import com.salesforce.omakase.ast.selector.SelectorGroup;
import com.salesforce.omakase.observer.Observer;
import com.salesforce.omakase.parser.token.Token;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class SelectorParser implements Parser {
    private static final Token SELECTOR_START = ALPHA.or(STAR).or(HASH).or(DOT);

    @Override
    public boolean parse(Stream stream, Iterable<Observer> observers) {
        stream.skipWhitepace();

        // if the next character is a valid first character for a selector
        if (!SELECTOR_START.matches(stream.current())) return false;

        // create our raw selector. Note that we have no idea if the selector is valid at this point
        SelectorGroup selectors = new StandardSelectorGroup(stream.line(), stream.column(), stream.until(OPEN_BRACKET));

        // notify all observers of the selector
        for (Observer observer : observers) {
            observer.selectorGroup(selectors);
        }

        return true;
    }
}
