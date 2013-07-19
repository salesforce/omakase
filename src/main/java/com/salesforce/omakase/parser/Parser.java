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
    boolean raw(Stream stream, Iterable<Adapter> adapters);

    boolean refined(Stream stream, Iterable<Adapter> adapters);
}
