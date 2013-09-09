/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.raw;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Parser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Stream;

/**
 * Parses a top-level {@link Stylesheet}.
 *
 * @author nmcwilliams
 * @see Stylesheet
 */
public class StylesheetParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        Parser parser = ParserFactory.statementParser();

        // continually parse until we get to the end of the stream
        while (!stream.eof()) {
            // parse the next statement
            boolean matched = parser.parse(stream, broadcaster);

            // skip whitespace
            stream.skipWhitepace();

            // after all rules and content is parsed, there should be nothing left in the stream
            if (!matched && !stream.eof()) throw new ParserException(stream, Message.EXTRANEOUS, stream.remaining());
        }

        return true;
    }
}
