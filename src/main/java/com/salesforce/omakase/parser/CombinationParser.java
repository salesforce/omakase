/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.consumer.Consumer;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class CombinationParser extends AbstractParser {
    private final Parser first;
    private final Parser second;

    /**
     * TODO
     * 
     * @param first
     *            TODO
     * @param second
     *            TODO
     */
    public CombinationParser(Parser first, Parser second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean parse(Stream stream, Iterable<Consumer> workers) {
        boolean matched = first.parse(stream, workers);
        return matched ? true : second.parse(stream, workers);
    }
}
