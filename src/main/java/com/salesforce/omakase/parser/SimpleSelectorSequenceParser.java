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
public class SimpleSelectorSequenceParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        stream.skipWhitepace();

        boolean s = new TypeSelectorParser().or(new UniversalSelectorParser()).parse(stream, broadcaster);

        Parser p = new IdSelectorParser().or(new ClassSelectorParser()).or(new AttributeSelectorParser())
            .or(new PseudoSelectorParser()).or(new NegationSelectorParser());

        boolean h = true;
        do {
            stream.skipWhitepace();
            h = p.parse(stream, broadcaster);
            if (h) s = true;
        } while (h);

        return s;
    }
}
