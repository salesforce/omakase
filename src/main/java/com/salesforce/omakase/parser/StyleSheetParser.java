/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.Errors;
import com.salesforce.omakase.observer.Observer;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class StyleSheetParser extends AbstractParser {
    private static final Parser child = new AtRuleParser().or(new RuleParser());

    @Override
    public boolean parse(Stream stream, Iterable<Observer> observers) {
        // continually parse until there is nothing left in the stream
        while (!stream.eof()) {
            boolean matched = child.parse(stream, observers);
            if (!matched && !stream.eof()) Errors.extraneous.send(stream, stream.remaining());
        }

        return true;
    }
}
