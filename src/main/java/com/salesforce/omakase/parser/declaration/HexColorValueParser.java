/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.declaration;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.declaration.value.HexColorValue;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * TESTME Parses a {@link HexColorValue}.
 * 
 * @author nmcwilliams
 */
public class HexColorValueParser extends AbstractParser {

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

            // TODO move this to a validator
            // check for a valid length
            if (color.length() != 6 && color.length() != 3) throw new ParserException(stream, Message.INVALID_HEX, color);

            HexColorValue value = new HexColorValue(line, column, color);

            broadcaster.broadcast(value);
            return true;
        }

        return false;
    }

}
