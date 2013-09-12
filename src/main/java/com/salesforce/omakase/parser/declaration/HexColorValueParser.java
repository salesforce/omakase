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
 * Parses a {@link HexColorValue}.
 *
 * @author nmcwilliams
 * @see HexColorValue
 */
public class HexColorValueParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a space operator
        stream.collectComments();

        // snapshot the current state before parsing
        Stream.Snapshot snapshot = stream.snapshot();

        // starts with hash and then a valid hex character
        if (Tokens.HASH.matches(stream.current()) && Tokens.HEX_COLOR.matches(stream.peek())) {
            // skip the has mark
            stream.next();

            // get the color value
            String color = stream.chomp(Tokens.HEX_COLOR);

            // check for a valid length
            if (color.length() != 6 && color.length() != 3) throw new ParserException(stream, Message.INVALID_HEX, color);

            HexColorValue value = new HexColorValue(snapshot.line, snapshot.column, color);
            value.comments(stream.flushComments());

            broadcaster.broadcast(value);
            return true;
        }

        return false;
    }
}
