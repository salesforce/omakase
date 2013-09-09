/**
 * ADD LICENSE
 */
package com.salesforce.omakase.broadcaster;

import com.salesforce.omakase.ast.Syntax;

/**
 * Base {@link Broadcaster} class.
 *
 * @author nmcwilliams
 */
public abstract class AbstractBroadcaster implements Broadcaster {
    /** inner {@link Broadcaster} */
    protected Broadcaster relay;

    @Override
    public <T extends Syntax> void broadcast(T syntax, boolean propagate) {
        broadcast(syntax);
        if (propagate) syntax.propagateBroadcast(this);
    }

    @Override
    public Broadcaster wrap(Broadcaster relay) {
        this.relay = relay;
        return this;
    }

    /**
     * Gets whether this {@link Broadcaster} is wrapped around a child {@link Broadcaster}.
     *
     * @return True if there is an inner {@link Broadcaster} specified.
     */
    protected boolean hasRelay() {
        return relay != null;
    }
}
