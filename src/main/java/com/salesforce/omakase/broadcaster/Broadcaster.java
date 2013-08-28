/**
 * ADD LICENSE
 */
package com.salesforce.omakase.broadcaster;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.emitter.Emitter;
import com.salesforce.omakase.emitter.SubscriptionType;

/**
 * Responsible for broadcasting {@link Syntax} unit create or change events, ultimately to an {@link Emitter}.
 * 
 * <p>
 * Implementations should follow the decorator pattern, allowing for nesting of different broadcasters (like Reader).
 * 
 * @author nmcwilliams
 */
public interface Broadcaster {
    /**
     * Broadcasts an event indicating that the given syntax unit has been created or changed.
     * 
     * @param <T>
     *            The type of {@link Syntax} unit that was created or changed.
     * @param type
     *            The event type (created or changed).
     * @param syntax
     *            The {@link Syntax} unit instance that was created or changed.
     */
    <T extends Syntax> void broadcast(SubscriptionType type, T syntax);
}
