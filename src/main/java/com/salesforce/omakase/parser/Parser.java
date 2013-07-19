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
public interface Parser {
    boolean parseRaw(Stream stream, Iterable<Adapter> adapters);

    boolean parseRefined(Stream stream, Iterable<Adapter> adapters);
}
