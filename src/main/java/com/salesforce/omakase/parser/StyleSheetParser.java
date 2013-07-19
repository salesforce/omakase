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

    @Override
    public boolean raw(Stream stream, Iterable<Adapter> adapters) {
        // there should be only at-rules or regular rules at the root level
        final Parser child = new AtRuleParser().or(new RuleParser());

        // continually parse until there is nothing left
        while (!stream.eof()) {
            boolean matched = child.raw(stream, adapters);
            if (!matched && !stream.eof()) throw new ParserException("Extraneous input!!", stream);
        }

        return true;
    }

    @Override
    public boolean refined(Stream stream, Iterable<Adapter> adapters) {
        // TODO Auto-generated method stub
        return false;
    }

}
