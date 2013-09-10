/**
 * ADD LICENSE
 */
package com.salesforce.omakase.broadcaster;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.emitter.Emitter;

/**
 * Responsible for broadcasting when {@link Syntax} units have been created, ultimately to an {@link Emitter}.
 * <p/>
 * Implementations should follow the decorator pattern, allowing for nesting of different broadcasters (like Reader).
 *
 * @author nmcwilliams
 */
public interface Broadcaster {
    /**
     * Broadcasts an event indicating that the given syntax unit has been created.
     *
     * @param <T>
     *     The type of {@link Syntax} unit that was created.
     * @param syntax
     *     The {@link Syntax} unit instance that was created.
     */
    <T extends Syntax> void broadcast(T syntax);

    /**
     * Broadcasts an event indicating that the given syntax unit has been created.
     * <p/>
     * This also gives the option to <em>propagate</em> the broadcast. Propagation directs the broadcasted unit to also broadcast
     * any of it's child or inner {@link Syntax} unit members. This should usually be specified as true when broadcasting a
     * dynamically created unit (as opposed to one created internally as a result of parsing the source).
     *
     * @param <T>
     *     The type of {@link Syntax} unit that was created.
     * @param syntax
     *     The {@link Syntax} unit instance that was created.
     * @param propagate
     *     If {@link Syntax#propagateBroadcast(Broadcaster)} should be called on the unit.
     *
     * @see Syntax#propagateBroadcast(Broadcaster)
     */
    <T extends Syntax> void broadcast(T syntax, boolean propagate);

    /**
     * Specifies an inner {@link Broadcaster} to wrap around. This {@link Broadcaster} will receive broadcasted events after this
     * one has processed the event.
     *
     * @param relay
     *     The inner {@link Broadcaster}.
     *
     * @return this, for chaining.
     */
    Broadcaster wrap(Broadcaster relay);
}