/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import javax.annotation.concurrent.Immutable;

import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.ast.Stylesheet;

/**
 * Parses a top-level {@link Stylesheet}
 * 
 * @author nmcwilliams
 */
@Immutable
public class StylesheetParser extends AbstractParser {
    private static final Parser statement = new AtRuleParser().or(new RuleParser());

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // continually parse until there is nothing left in the stream
        while (!stream.eof()) {
            boolean matched = statement.parse(stream, null);
            if (!matched && !stream.eof()) {
                String msg = "Extraneous text found at the end of the source '%s'";
                throw new ParserException(stream, String.format(msg, stream.remaining()));
            }
        }

        return true;
    }
}
