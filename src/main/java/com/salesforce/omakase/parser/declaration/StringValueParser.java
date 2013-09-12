/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.declaration;

import com.salesforce.omakase.ast.declaration.value.StringValue;
import com.salesforce.omakase.ast.declaration.value.StringValue.QuotationMode;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link StringValue}.
 *
 * @author nmcwilliams
 * @see StringValue
 */
public class StringValueParser extends AbstractParser {
    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a space operator
        stream.collectComments();

        // snapshot the current state before parsing
        Stream.Snapshot snapshot = stream.snapshot();

        QuotationMode mode;
        String value;

        if (Tokens.SINGLE_QUOTE.matches(stream.current())) {
            mode = QuotationMode.SINGLE;
            value = stream.chompEnclosedValue(Tokens.SINGLE_QUOTE, Tokens.SINGLE_QUOTE);
        } else if (Tokens.DOUBLE_QUOTE.matches(stream.current())) {
            mode = QuotationMode.DOUBLE;
            value = stream.chompEnclosedValue(Tokens.DOUBLE_QUOTE, Tokens.DOUBLE_QUOTE);
        } else {
            return false;
        }

        StringValue string = new StringValue(snapshot.line, snapshot.column, mode, value);
        string.comments(stream.flushComments());
        broadcaster.broadcast(string);

        return true;
    }
}
