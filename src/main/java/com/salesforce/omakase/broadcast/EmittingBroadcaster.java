/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.salesforce.omakase.broadcast;

import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.emitter.Emitter;
import com.salesforce.omakase.broadcast.emitter.SubscriptionPhase;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.plugin.Plugin;

/**
 * The main {@link Broadcaster}, this emits the broadcasted events to registered {@link Plugin} methods.
 * <p/>
 * Any particular {@link Syntax} unit is actually emitted at most once per phase, based on {@link Syntax#status()}.
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
    public void broadcast(Broadcastable broadcastable) {
        SubscriptionPhase phase = emitter.phase();
        Status status = broadcastable.status();

        if (status.shouldBroadcastForPhase(phase)) {
            // set the status to broadcasting
            broadcastable.status(Status.EMITTING);

            // send to listeners
            emitter.emit(broadcastable, em);

            // update the status
            if (broadcastable.status() != Status.NEVER_EMIT) {
                broadcastable.status(Status.nextStatusAfterPhase(phase));
            }

            // pass to relays
            if (relay != null) {
                relay.broadcast(broadcastable);
            }
        }
    }
}
