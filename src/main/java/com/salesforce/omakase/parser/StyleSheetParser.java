/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.adapter.Adapter;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class StyleSheetParser extends AbstractParser {
    private static final Parser child = new AtRuleParser().or(new RuleParser());
    private static final String msg = "Extraneous input! %s";

    @Override
    public boolean parseRaw(Stream stream, Iterable<Adapter> adapters) {
        // continually parse until there is nothing left in the stream
        while (!stream.eof()) {
            boolean matched = child.parseRaw(stream, adapters);
            if (!matched && !stream.eof()) error(stream, msg, stream.remaining());
        }

        return true;
    }

    @Override
    public boolean parseRefined(Stream stream, Iterable<Adapter> adapters) {
        // TODO Auto-generated method stub
        return false;
    }

}
