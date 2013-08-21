/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.selector;

import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.ast.selector.AttributeSelector;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Stream;

/**
 * Parses an {@link AttributeSelector}.
 * 
 * @author nmcwilliams
 */
public class AttributeSelectorParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a descendant combinator

        // TODO Auto-generated method stub
        return false;
    }

}
