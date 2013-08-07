/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import javax.annotation.concurrent.Immutable;

import com.salesforce.omakase.consumer.Plugin;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
@Immutable
public class AtRuleParser extends AbstractParser {
    @Override
    public boolean parse(Stream stream, Iterable<Plugin> consumers) {
        stream.skipWhitepace();

        // must begin with '@'
        if (!Tokens.AT_RULE.matches(stream.current())) return false;

        return false; // TODO
    }
}
