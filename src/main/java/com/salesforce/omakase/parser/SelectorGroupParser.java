/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import static com.salesforce.omakase.parser.token.Tokens.*;

import javax.annotation.concurrent.Immutable;

import com.salesforce.omakase.ast.selector.SelectorGroup;
import com.salesforce.omakase.consumer.Consumer;
import com.salesforce.omakase.parser.token.Token;

/**
 * Parses a {@link SelectorGroup}.
 * 
 * @author nmcwilliams
 */
@Immutable
public class SelectorGroupParser extends AbstractParser implements Parser {
    private static final Token SELECTOR_START = ALPHA.or(STAR).or(HASH).or(DOT);

    @Override
    public boolean parse(Stream stream, Iterable<Consumer> consumers) {
        stream.skipWhitepace();

        // if the next character is a valid first character for a selector
        if (!SELECTOR_START.matches(stream.current())) return false;

        int line = stream.line();
        int column = stream.column();
        String content = stream.until(OPEN_BRACKET);

        SelectorGroup selectorGroup = factory().selectorGroup()
            .content(content)
            .line(line)
            .column(column)
            .build();

        notify(consumers, selectorGroup);

        return true;
    }
}
