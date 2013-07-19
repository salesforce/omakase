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
public class RuleParser extends AbstractParser {

    @Override
    public boolean raw(Stream stream, Iterable<Adapter> adapters) {
        // selector
        stream.skipWhitepace();
        new SelectorParser().raw(stream, adapters);

        // declaration block
        stream.skipWhitepace();
        new DeclarationBlockParser().raw(stream, adapters);

        for (Adapter adapter : adapters) {
            adapter.endRule();
        }

        return true;
    }

    @Override
    public boolean refined(Stream stream, Iterable<Adapter> adapters) {
        // TODO Auto-generated method stub
        return false;
    }

}
