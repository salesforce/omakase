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
public interface Parser {
    /**
     * TODO Description
     * 
     * @param stream
     *            TODO
     * @param workers
     *            TODO
     * @return TODO
     */
    boolean parse(Stream stream, Iterable<Consumer> workers);
}
