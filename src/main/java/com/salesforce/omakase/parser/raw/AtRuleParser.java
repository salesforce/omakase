/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.raw;

import com.salesforce.omakase.ast.AtRule;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * TESTME Parses an {@link AtRule}.
 * 
 * @author nmcwilliams
 */
public class AtRuleParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        stream.skipWhitepace();

        // must begin with '@'
        if (!Tokens.AT_RULE.matches(stream.current())) return false;

        return false;
    }

}
