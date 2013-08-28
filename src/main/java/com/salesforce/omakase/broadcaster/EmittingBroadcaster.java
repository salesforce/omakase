/**
 * ADD LICENSE
 */
package com.salesforce.omakase.broadcaster;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.emitter.Emitter;
import com.salesforce.omakase.emitter.SubscriptionPhase;
import com.salesforce.omakase.emitter.SubscriptionType;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.plugin.Plugin;

/**
 * The main {@link Broadcaster}, this emits the broadcasted events to registered {@link Plugin} methods.
 * 
 * @see Emitter
 * 
 * @author nmcwilliams
 */
public final class EmittingBroadcaster implements Broadcaster {
    private final Emitter emitter = new Emitter();
    private final Broadcaster relay;
    private ErrorManager em;

    /**
     * Constructs a new {@link EmittingBroadcaster} instance that will <em>not</em> relay any events to another
     * {@link Broadcaster}.
     */
    public EmittingBroadcaster() {
        this(null);
    }

    /**
     * Constructs a new {@link EmittingBroadcaster} instance that will relay all broadcasted events to the given
     * {@link Broadcaster}.
     * 
     * @param relay
     *            Wrap (decorate) this broadcaster. All broadcasts will be relayed to this one.
     */
    public EmittingBroadcaster(Broadcaster relay) {
        this.relay = relay;
    }

    /**
     * Specifies the {@link ErrorManager} to use.
     * 
     * @param em
     *            The {@link ErrorManager} instance.
     */
    public void errorManager(ErrorManager em) {
        this.em = em;
    }

    /**
     * See {@link Emitter#register(Object)}.
     * 
     * @param subscriber
     *            The {@link Plugin} class.
     * 
     */
    public void register(Object subscriber) {
        emitter.register(subscriber);
    }

    /**
     * See {@link Emitter#phase(SubscriptionPhase)}.
     * 
     * @param phase
     *            The new {@link SubscriptionPhase}.
     */
    public void phase(SubscriptionPhase phase) {
        emitter.phase(phase);
    }

    @Override
    public <T extends Syntax> void broadcast(SubscriptionType type, T syntax) {
        emitter.emit(type, syntax, em);

        if (relay != null) {
            relay.broadcast(type, syntax);
        }
    }
}
