/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import static com.google.common.base.Preconditions.checkNotNull;

import com.salesforce.omakase.adapter.Adapter;

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
        this.first = checkNotNull(first, "first parser cannot be null");
        this.second = checkNotNull(second, "second parser cannot be null");
    }

    @Override
    public boolean parseRaw(Stream stream, Iterable<Adapter> adapters) {
        boolean matched = first.parseRaw(stream, adapters);
        return matched ? true : second.parseRaw(stream, adapters);
    }

    @Override
    public boolean parseRefined(Stream stream, Iterable<Adapter> adapters) {
        boolean matched = first.parseRefined(stream, adapters);
        return matched ? true : second.parseRefined(stream, adapters);
    }

}
