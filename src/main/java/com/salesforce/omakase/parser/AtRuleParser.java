/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.consumer.Consumer;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class AtRuleParser extends AbstractParser {
    @Override
    public boolean parse(Stream stream, Iterable<Consumer> workers) {
        stream.skipWhitepace();

        // must begin with '@'
        if (!Tokens.AT_RULE.matches(stream.current())) return false;

        return false; // TODO
    }
}
