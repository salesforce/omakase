/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.Broadcaster;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class RefinedSelectorParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        return new SimpleSelectorSequenceParser().parse(stream, broadcaster);
    }
}
