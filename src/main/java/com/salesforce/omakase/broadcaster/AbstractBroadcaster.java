/**
 * ADD LICENSE
 */
package com.salesforce.omakase.broadcaster;

import com.salesforce.omakase.ast.Syntax;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public abstract class AbstractBroadcaster implements Broadcaster {
    /** TODO */
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
     * TODO Description
     * 
     * @return TODO
     */
    protected boolean hasRelay() {
        return relay != null;
    }
}
