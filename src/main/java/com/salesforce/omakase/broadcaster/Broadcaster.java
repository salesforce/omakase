/**
 * ADD LICENSE
 */
package com.salesforce.omakase.broadcaster;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.emitter.Emitter;

/**
 * Responsible for broadcasting when {@link Syntax} units have been created, ultimately to an {@link Emitter}.
 * 
 * <p>
 * Implementations should follow the decorator pattern, allowing for nesting of different broadcasters (like Reader).
 * 
 * @author nmcwilliams
 */
public interface Broadcaster {
    /**
     * Broadcasts an event indicating that the given syntax unit has been created.
     * 
     * @param <T>
     *            The type of {@link Syntax} unit that was created.
     * @param syntax
     *            The {@link Syntax} unit instance that was created.
     */
    <T extends Syntax> void broadcast(T syntax);
}
