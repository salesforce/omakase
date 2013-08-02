﻿/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import javax.annotation.concurrent.Immutable;

import com.salesforce.omakase.consumer.Consumer;

/**
 * Used to an aspect of CSS source code.
 * 
 * <p> {@link Parser}s must <em>not</em> maintain any state or persistence from one parse operation to another. They
 * should be immutable objects.
 * 
 * @author nmcwilliams
 */
@Immutable
public interface Parser {
    /**
     * Parse from the current position of the given stream, notifying the given {@link Consumer}s of any applicable
     * events and data.
     * 
     * @param stream
     *            The stream to parse.
     * @param consumers
     *            The consumers to notify.
     * @return true if we parsed <em>something</em> (excluding whitespace), false otherwise. Note that a return value of
     *         true does not indicate that the parsed content was actually valid grammar.
     */
    boolean parse(Stream stream, Iterable<Consumer> consumers);
}
