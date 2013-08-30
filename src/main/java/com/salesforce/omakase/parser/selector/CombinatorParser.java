/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.selector;

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.selector.Combinator;
import com.salesforce.omakase.ast.selector.CombinatorType;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * TESTME Parses {@link Combinator}s.
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
        boolean mightBeDescendant = stream.optionallyPresent(Tokens.SINGLE_SPACE);

        if (mightBeDescendant) {
            // if we already know that a space is present, we must skip past all other whitespace
            stream.skipWhitepace();
        }

        Optional<CombinatorType> type = stream.optionalFromEnum(CombinatorType.class);

        // if no other combinator symbols are present, and we parsed at least one space earlier
        // then it's a descendant combinator
        if (!type.isPresent() && mightBeDescendant) {
            type = Optional.of(CombinatorType.DESCENDANT);
        }

        if (type.isPresent()) {
            // if we have parsed a combinator then we must skip past all subsequent whitespace.
            stream.skipWhitepace();

            // create and broadcast the combinator
            Combinator combinator = new Combinator(line, column, type.get());
            broadcaster.broadcast(combinator);
            return true;
        }
        return false;
    }

}
