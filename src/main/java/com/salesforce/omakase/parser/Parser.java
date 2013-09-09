/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.plugin.Plugin;

/**
 * Used to parse an aspect of CSS source code.
 * <p/>
 * {@link Parser}s must <em>not</em> maintain any state or persistence from one parse operation to another. They should be
 * immutable objects.
 *
 * @author nmcwilliams
 */
public interface Parser {
    /**
     * Parse from the current position of the given stream, notifying the given {@link Plugin}s of any applicable events and
     * data.
     *
     * @param stream
     *     The stream to parse.
     * @param broadcaster
     *     The {@link Broadcaster} to receive any events from the parser.
     *
     * @return true if we parsed <em>something</em> (excluding whitespace), false otherwise. Note that a return value of true does
     *         not indicate that the parsed content was actually valid syntax.
     */
    boolean parse(Stream stream, Broadcaster broadcaster);

    /**
     * Utility for creating a {@link CombinationParser}.
     *
     * @param other
     *     The other {@link Parser} in addition to this one to use for creating the {@link CombinationParser}.
     *
     * @return The {@link CombinationParser}.
     */
    Parser or(Parser other);
}
