/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.declaration;

import com.salesforce.omakase.ast.declaration.value.StringValue;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.emitter.SubscriptionType;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link StringValue}.
 * 
 * @author nmcwilliams
 */
public class StringValueParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        int line = stream.line();
        int column = stream.column();

        String value;

        if (Tokens.DOUBLE_QUOTE.matches(stream.current())) {
            value = stream.chompEnclosedValue(Tokens.DOUBLE_QUOTE, Tokens.DOUBLE_QUOTE);
        } else if (Tokens.SINGLE_QUOTE.matches(stream.current())) {
            value = stream.chompEnclosedValue(Tokens.SINGLE_QUOTE, Tokens.SINGLE_QUOTE);
        } else {
            return false;
        }

        StringValue string = new StringValue(line, column, value);
        broadcaster.broadcast(SubscriptionType.CREATED, string);
        return true;
    }

}
