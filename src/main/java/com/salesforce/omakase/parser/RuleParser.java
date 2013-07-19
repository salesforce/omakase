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
    private static final SelectorParser selector = new SelectorParser();
    private static final DeclarationBlockParser declarationBlock = new DeclarationBlockParser();

    @Override
    public boolean parseRaw(Stream stream, Iterable<Adapter> adapters) {
        boolean matched;

        // selector
        stream.skipWhitepace();
        matched = selector.parseRaw(stream, adapters);

        // if there wasn't a selector then we aren't at a rule
        if (!matched) return false;

        // declaration block
        stream.skipWhitepace();
        matched = declarationBlock.parseRaw(stream, adapters);

        // if there was a selector then there must be a declaration block
        if (!matched) error(stream, "Expected to find a declaration block");

        for (Adapter adapter : adapters) {
            adapter.endRule();
        }

        return true;
    }

    @Override
    public boolean parseRefined(Stream stream, Iterable<Adapter> adapters) {
        // TODO Auto-generated method stub
        return false;
    }

}
