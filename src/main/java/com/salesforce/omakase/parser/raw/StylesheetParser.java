/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.raw;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.parser.*;

/**
 * Parses a top-level {@link Stylesheet}.
 * 
 * @see StylesheetParserTest
 * 
 * @author nmcwilliams
 */
public class StylesheetParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // continually parse until we get to the end of the stream
        while (!stream.eof()) {
            // parse the next statement
            boolean matched = ParserFactory.statementParser().parse(stream, broadcaster);

            // skip whitespace
            stream.skipWhitepace();

            // after all rules and content is parsed, there should be nothing left in the stream
            if (!matched && !stream.eof()) throw new ParserException(stream, Message.EXTRANEOUS, stream.remaining());
        }

        return true;
    }
}
