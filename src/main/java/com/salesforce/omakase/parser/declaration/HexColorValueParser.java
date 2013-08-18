/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.declaration;

import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.ast.declaration.value.HexColorValue;
import com.salesforce.omakase.emitter.SubscriptionType;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class HexColorValueParser extends AbstractParser {
    private static final String INVALID_HEX = "Expected a hex color of length 3 or 6, but found '%s'";

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        int line = stream.line();
        int column = stream.column();

        // starts with hash and then a valid hash character
        if (Tokens.HASH.matches(stream.current()) && Tokens.HEX_COLOR.matches(stream.peek())) {
            // skip the has mark
            stream.next();

            // get the color value
            String color = stream.chomp(Tokens.HEX_COLOR);
            if (color.length() != 6 && color.length() != 3) {
                // incorrect length
                throw new ParserException(stream, String.format(INVALID_HEX, color));
            }

            HexColorValue value = new HexColorValue(line, column, color);

            broadcaster.broadcast(SubscriptionType.CREATED, value);
            return true;
        }

        return false;
    }

}
