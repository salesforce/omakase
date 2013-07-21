/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.adapter.Adapter;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class AtRuleParser extends AbstractParser {

    @Override
    public boolean parseRaw(Stream stream, Iterable<Adapter> adapters) {
        stream.skipWhitepace();

        // must begin with '@'
        if (!Tokens.AT_RULE.matches(stream.current())) return false;

        return false; // TODO
    }

    @Override
    public boolean parseRefined(Stream stream, Iterable<Adapter> adapters) {
        // TODO Auto-generated method stub
        return false;
    }

}
