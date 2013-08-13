/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.google.common.base.Strings;
import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.ast.TypeSelector;
import com.salesforce.omakase.emitter.SubscriptionType;

/**
 * Parses {@link TypeSelector}s.
 * 
 * @author nmcwilliams
 */
public class TypeSelectorParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // save off the line and column before parsing anything
        int line = stream.line();
        int column = stream.column();

        // find the name
        String name = stream.read(ParserFactory.ident());

        if (Strings.isNullOrEmpty(name)) return false;

        // create and broadcast the new selector
        TypeSelector selector = new TypeSelector(line, column, name);
        broadcaster.broadcast(SubscriptionType.CREATED, selector);
        return true;
    }

}
