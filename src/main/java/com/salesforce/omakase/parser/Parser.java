/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.observer.Observer;

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
     * @param observers
     *            TODO
     * @return TODO
     */
    boolean parse(Stream stream, Iterable<Observer> observers);
}
