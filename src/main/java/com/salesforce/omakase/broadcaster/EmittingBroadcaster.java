/**
 * ADD LICENSE
 */
package com.salesforce.omakase.broadcaster;

import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.emitter.Emitter;
import com.salesforce.omakase.emitter.SubscriptionPhase;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.plugin.Plugin;

/**
 * The main {@link Broadcaster}, this emits the broadcasted events to registered {@link Plugin} methods.
 * <p/>
 * TODO note about broadcast status and phases
 *
 * @author nmcwilliams
 * @see Emitter
 */
public final class EmittingBroadcaster extends AbstractBroadcaster {
    private final Emitter emitter = new Emitter();
    private ErrorManager em;

    /**
     * Constructs a new {@link EmittingBroadcaster} instance that will <em>not</em> relay any events to another {@link
     * Broadcaster}.
     */
    public EmittingBroadcaster() {
        this(null);
    }

    /**
     * Constructs a new {@link EmittingBroadcaster} instance that will relay all broadcasted events to the given {@link
     * Broadcaster}.
     *
     * @param relay
     *     Wrap (decorate) this broadcaster. All broadcasts will be relayed to this one.
     */
    public EmittingBroadcaster(Broadcaster relay) {
        wrap(relay);
    }

    /**
     * Specifies the {@link ErrorManager} to use.
     *
     * @param em
     *     The {@link ErrorManager} instance.
     */
    public void errorManager(ErrorManager em) {
        this.em = em;
    }

    /**
     * See {@link Emitter#register(Object)}.
     *
     * @param subscriber
     *     The {@link Plugin} class.
     */
    public void register(Object subscriber) {
        emitter.register(subscriber);
    }

    /**
     * See {@link Emitter#phase(SubscriptionPhase)}.
     *
     * @param phase
     *     The new {@link SubscriptionPhase}.
     */
    public void phase(SubscriptionPhase phase) {
        emitter.phase(phase);
    }

    @Override
    public <T extends Syntax> void broadcast(T syntax) {
        SubscriptionPhase phase = emitter.phase();
        Status status = syntax.status();

        if (status.shouldBroadcastForPhase(phase)) {
            syntax.status(Status.BROADCASTING);

            // send to listeners
            emitter.emit(syntax, em);

            // update the status
            syntax.status(status.nextStatus(phase));

            // pass to relays
            if (hasRelay()) {
                relay.broadcast(syntax);
            }
        }
    }
}
