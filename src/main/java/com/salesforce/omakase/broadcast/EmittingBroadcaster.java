/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
 * <p>
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
