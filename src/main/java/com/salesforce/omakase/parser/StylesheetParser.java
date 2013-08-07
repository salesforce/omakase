/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import javax.annotation.concurrent.Immutable;

import com.salesforce.omakase.Errors;
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
    public boolean parse(Stream stream, Context context) {
        // continually parse until there is nothing left in the stream
        while (!stream.eof()) {
            boolean matched = statement.parse(stream, context);
            if (!matched && !stream.eof()) Errors.extraneous.send(stream, stream.remaining());
        }

        return true;
    }
}
