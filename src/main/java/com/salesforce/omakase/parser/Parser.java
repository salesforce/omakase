/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import javax.annotation.concurrent.Immutable;

import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.plugin.Plugin;

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
     * Parse from the current position of the given stream, notifying the given {@link Plugin}s of any applicable events
     * and data.
     * 
     * @param stream
     *            The stream to parse.
     * @param broadcaster
     *            TODO
     * 
     * @return true if we parsed <em>something</em> (excluding whitespace), false otherwise. Note that a return value of
     *         true does not indicate that the parsed content was actually valid grammar.
     */
    boolean parse(Stream stream, Broadcaster broadcaster);

    /**
     * TODO Description
     * 
     * @param other
     * @return TODO
     */
    Parser or(Parser other);
}
