/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.ast.Combinator;
import com.salesforce.omakase.ast.CombinatorType;
import com.salesforce.omakase.emitter.SubscriptionType;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses {@link Combinator}s.
 * 
 * @author nmcwilliams
 */
public class CombinatorParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // save off the line and column before parsing anything
        int line = stream.line();
        int column = stream.column();

        // the presence of a space *could* be a descendant selector. Or it could just be whitespace around other
        // combinators. We won't know until later.
        boolean mightBeDescendant = stream.optional(Tokens.SINGLE_SPACE);

        if (mightBeDescendant) {
            // if we already know that a space is present, we must skip past all other whitespace
            stream.skipWhitepace();
        }

        CombinatorType type = null;

        // try parsing the other combinator symbols
        if (stream.optional(Tokens.PLUS)) {
            type = CombinatorType.ADJACENT_SIBLING;
        } else if (stream.optional(Tokens.GREATER_THAN)) {
            type = CombinatorType.CHILD;
        } else if (stream.optional(Tokens.TILDE)) {
            type = CombinatorType.GENERAL_SIBLING;
        }

        // if no other combinator symbols are present, and we parsed at least one space earlier
        // then it's a descendant combinator
        if (type == null && mightBeDescendant) {
            type = CombinatorType.DESCENDANT;
        }

        if (type != null) {
            // if we have parsed a combinator then we must skip past all subsequent whitespace.
            stream.skipWhitepace();

            // create and broadcast the combinator
            Combinator combinator = new Combinator(line, column, type);
            broadcaster.broadcast(SubscriptionType.CREATED, combinator);
            return true;
        }
        return false;
    }

}
