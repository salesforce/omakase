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

    public CombinationParser(Parser first, Parser second) {
        this.first = checkNotNull(first, "first parser cannot be null");
        this.second = checkNotNull(second, "second parser cannot be null");
    }

    @Override
    public boolean raw(Stream stream, Iterable<Adapter> adapters) {
        boolean matched = first.raw(stream, adapters);
        return matched ? true : second.raw(stream, adapters);
    }

    @Override
    public boolean refined(Stream stream, Iterable<Adapter> adapters) {
        boolean matched = first.refined(stream, adapters);
        return matched ? true : second.refined(stream, adapters);
    }

}
