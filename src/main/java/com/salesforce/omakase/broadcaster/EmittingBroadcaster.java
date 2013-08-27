/**
 * ADD LICENSE
 */
package com.salesforce.omakase.broadcaster;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.emitter.Emitter;
import com.salesforce.omakase.emitter.SubscriptionType;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class EmittingBroadcaster implements Broadcaster {
    private final Emitter emitter = new Emitter();
    private final Broadcaster relay;

    /**
     * TODO
     */
    public EmittingBroadcaster() {
        this(null);
    }

    /**
     * TODO
     * 
     * @param relay
     *            TODO
     */
    public EmittingBroadcaster(Broadcaster relay) {
        this.relay = relay;
    }

    /**
     * TODO Description
     * 
     * @param subscriber
     *            TODO
     */
    public void register(Object subscriber) {
        emitter.register(subscriber);
    }

    @Override
    public <T extends Syntax> void broadcast(SubscriptionType type, T syntax) {
        emitter.emit(type, syntax);

        if (relay != null) {
            relay.broadcast(type, syntax);
        }
    }
}
