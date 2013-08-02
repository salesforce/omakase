/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.Errors;
import com.salesforce.omakase.consumer.Consumer;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class StylesheetParser extends AbstractParser {
    private static final Parser statement = new AtRuleParser().or(new RuleParser());

    @Override
    public boolean parse(Stream stream, Iterable<Consumer> workers) {
        // continually parse until there is nothing left in the stream
        while (!stream.eof()) {
            boolean matched = statement.parse(stream, workers);
            if (!matched && !stream.eof()) Errors.extraneous.send(stream, stream.remaining());
        }

        return true;
    }
}
