/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import static com.salesforce.omakase.parser.token.Tokens.*;

import com.salesforce.omakase.Context;
import com.salesforce.omakase.ast.Rule;

/**
 * Parses a {@link Rule}.
 * 
 * @author nmcwilliams
 */
public class RuleParser extends AbstractParser {
    private static final SelectorGroupParser selector = new SelectorGroupParser();
    private static final DeclarationParser declaration = new DeclarationParser();

    @Override
    public boolean parse(Stream stream, Context context) {
        boolean matched;

        // selector
        stream.skipWhitepace();
        matched = selector.parse(stream, context);

        // if there wasn't a selector then we aren't at a rule
        if (!matched) return false;

        // declaration block
        stream.skipWhitepace();

        stream.expect(OPEN_BRACKET);

        do {
            stream.skipWhitepace();
            declaration.parse(stream, context);
            stream.skipWhitepace();
        } while (stream.optional(SEMICOLON));

        stream.expect(CLOSE_BRACKET);

        return true;
    }
}
